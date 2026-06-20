package com.example.bookapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.bookapp.data.local.BookDao
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.data.network.OpenLibraryApi
import com.example.bookapp.data.network.models.SearchDoc
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val api: OpenLibraryApi,
    private val bookDao: BookDao,
    private val dataStore: DataStore<Preferences>
) {
    private val searchCache = mutableMapOf<String, List<SearchResult>>()
    // Secondary index populated by search(); lets getBookDetail() fill author/cover
    // for books not yet in Room, since the work-detail API doesn't return authors.
    private val workIdIndex = mutableMapOf<String, SearchResult>()

    companion object {
        private val BOOKMARKED_IDS = stringSetPreferencesKey("bookmarked_ids")
        private fun noteKey(workId: String) = stringPreferencesKey("note_$workId")
    }

    // 4.2 — checks cache first, falls back to network
    suspend fun search(query: String, page: Int): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        val key = "$query:$page"
        searchCache[key]?.let { return it }
        val response = api.searchBooks(query = query, page = page, limit = 20)
        val results = response.docs.map { it.toSearchResult() }
        searchCache[key] = results
        results.forEach { workIdIndex[it.workId] = it }
        return results
    }

    // 4.3 — checks Room first, falls back to network
    suspend fun getBookDetail(workId: String): BookEntity? {
        bookDao.getBook(workId)?.let { return it }
        val cached = workIdIndex[workId]
        return try {
            val detail = api.getWork(workId)
            val entity = BookEntity(
                workId = workId,
                title = detail.title.ifBlank { cached?.title ?: "" },
                authors = cached?.authors?.joinToString(", ") ?: "",
                synopsis = detail.synopsis(),
                coverUrl = detail.coverUrl() ?: cached?.coverUrl
            )
            bookDao.insert(entity)
            bookDao.getBook(workId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    // 4.4 — updates DataStore; fetches and stores work detail in Room on first bookmark
    suspend fun setBookmarked(
        workId: String,
        bookmarked: Boolean,
        title: String = "",
        authors: List<String> = emptyList(),
        coverUrl: String? = null
    ) {
        if (bookmarked) {
            dataStore.edit { prefs ->
                val current = prefs[BOOKMARKED_IDS] ?: emptySet()
                prefs[BOOKMARKED_IDS] = current + workId
            }
            // fetch and store work detail if not already in Room
            if (bookDao.getBook(workId) == null) {
                try {
                    val detail = api.getWork(workId)
                    val entity = BookEntity(
                        workId = workId,
                        title = detail.title.ifBlank { title },
                        authors = authors.joinToString(", "),
                        synopsis = detail.synopsis(),
                        coverUrl = detail.coverUrl() ?: coverUrl
                    )
                    bookDao.insert(entity)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // store with search metadata if network fails
                    bookDao.insert(
                        BookEntity(
                            workId = workId,
                            title = title,
                            authors = authors.joinToString(", "),
                            synopsis = "",
                            coverUrl = coverUrl
                        )
                    )
                }
            }
        } else {
            dataStore.edit { prefs ->
                val current = prefs[BOOKMARKED_IDS] ?: emptySet()
                prefs[BOOKMARKED_IDS] = current - workId
                prefs.remove(noteKey(workId))
            }
        }
    }

    // 4.5 — isBookmarked from DataStore
    fun isBookmarked(workId: String): Flow<Boolean> =
        dataStore.data.map { prefs ->
            (prefs[BOOKMARKED_IDS] ?: emptySet()).contains(workId)
        }

    // 4.6 — getNote/setNote via DataStore
    fun getNote(workId: String): Flow<String> =
        dataStore.data.map { prefs -> prefs[noteKey(workId)] ?: "" }

    suspend fun setNote(workId: String, note: String) {
        dataStore.edit { prefs -> prefs[noteKey(workId)] = note }
    }

    // 4.7 — bookmarked books combining DataStore set + Room
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getBookmarkedBooks(): Flow<List<BookEntity>> =
        dataStore.data
            .map { prefs -> prefs[BOOKMARKED_IDS] ?: emptySet() }
            .flatMapLatest { ids ->
                if (ids.isEmpty()) flowOf(emptyList())
                else bookDao.getAllBooks().map { books -> books.filter { it.workId in ids } }
            }

    private fun SearchDoc.toSearchResult(): SearchResult {
        val workId = key.removePrefix("/works/")
        val coverUrl = coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
        return SearchResult(
            workId = workId,
            title = title,
            authors = authorName ?: emptyList(),
            coverUrl = coverUrl
        )
    }
}
