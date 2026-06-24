package com.example.bookapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.example.bookapp.data.local.BookDao
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.data.network.OpenLibraryApi
import com.example.bookapp.data.network.models.SearchDoc
import com.example.bookapp.data.network.models.SearchResponse
import com.example.bookapp.data.network.models.WorkDetailResponse
import com.example.bookapp.data.repository.BookRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BookRepositoryTest {

    @get:Rule
    val tmpFolder = TemporaryFolder()

    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)

    private lateinit var api: OpenLibraryApi
    private lateinit var bookDao: BookDao
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: BookRepository

    @Before
    fun setup() {
        api = mock()
        bookDao = mock()
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_prefs.preferences_pb") }
        )
        repository = BookRepository(api, bookDao, dataStore)
    }

    // 10.1 — cache hit skips network; cache miss calls API and stores result
    @Test
    fun `search returns cached result without network call`() = testScope.runTest {
        val doc = SearchDoc(key = "/works/OL1W", title = "Dune", authorName = listOf("Frank Herbert"), coverId = null)
        whenever(api.searchBooks(any(), any(), any(), any())).thenReturn(SearchResponse(1, listOf(doc)))

        repository.search("dune", 1)
        repository.search("dune", 1)

        verify(api, times(1)).searchBooks(any(), any(), any(), any())
    }

    @Test
    fun `search cache miss calls API`() = testScope.runTest {
        val doc = SearchDoc(key = "/works/OL1W", title = "Dune")
        whenever(api.searchBooks(any(), any(), any(), any())).thenReturn(SearchResponse(1, listOf(doc)))

        val results = repository.search("dune", 1)
        assertEquals(1, results.size)
        assertEquals("OL1W", results[0].workId)
        verify(api, times(1)).searchBooks(any(), any(), any(), any())
    }

    @Test
    fun `getBookDetail uses authors from search index when work API has none`() = testScope.runTest {
        val doc = SearchDoc(key = "/works/OL1W", title = "Dune", authorName = listOf("Frank Herbert"), coverId = 123L)
        whenever(api.searchBooks(any(), any(), any(), any())).thenReturn(SearchResponse(1, listOf(doc)))
        whenever(bookDao.getBook("OL1W")).thenReturn(null)
        whenever(api.getWork("OL1W")).thenReturn(WorkDetailResponse(title = "Dune", key = "/works/OL1W"))

        repository.search("dune", 1)
        repository.getBookDetail("OL1W")

        verify(bookDao).insert(org.mockito.kotlin.argThat { authors == "Frank Herbert" })
    }

    @Test
    fun `getBookDetail returns null when network call throws`() = testScope.runTest {
        whenever(bookDao.getBook("OL1W")).thenReturn(null)
        whenever(api.getWork("OL1W")).thenThrow(RuntimeException("Network error"))

        val result = repository.getBookDetail("OL1W")

        assertEquals(null, result)
    }

    // 10.2 — setBookmarked(true) on new book fetches work detail and inserts into Room
    @Test
    fun `setBookmarked true on new book fetches detail and inserts into Room`() = testScope.runTest {
        val workId = "OL1W"
        whenever(bookDao.getBook(workId)).thenReturn(null)
        whenever(api.getWork(workId)).thenReturn(WorkDetailResponse(title = "Dune", key = "/works/$workId"))

        repository.setBookmarked(workId, true, "Dune", listOf("Frank Herbert"))

        verify(api, times(1)).getWork(workId)
        verify(bookDao, times(1)).insert(any())
    }

    @Test
    fun `setBookmarked true falls back to search metadata when network call throws`() = testScope.runTest {
        val workId = "OL1W"
        whenever(bookDao.getBook(workId)).thenReturn(null)
        whenever(api.getWork(workId)).thenThrow(RuntimeException("Network error"))

        repository.setBookmarked(workId, true, "Dune", listOf("Frank Herbert"))

        verify(bookDao, times(1)).insert(org.mockito.kotlin.argThat {
            this.workId == workId && title == "Dune" && authors == "Frank Herbert"
        })
    }

    @Test
    fun `setBookmarked true on existing book does not re-fetch`() = testScope.runTest {
        val workId = "OL1W"
        val entity = BookEntity(workId, "Dune", "Frank Herbert", "synopsis", null)
        whenever(bookDao.getBook(workId)).thenReturn(entity)

        repository.setBookmarked(workId, true, "Dune", listOf("Frank Herbert"))

        verify(api, never()).getWork(any())
        verify(bookDao, never()).insert(any())
    }

    // 10.3 — setBookmarked(false) clears bookmark flag but does not delete Room entity
    @Test
    fun `setBookmarked false clears DataStore flag but retains Room entity`() = testScope.runTest {
        val workId = "OL1W"

        repository.setBookmarked(workId, true, "Dune", listOf("Frank Herbert"))
        repository.setBookmarked(workId, false)

        repository.isBookmarked(workId).test {
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        // Entity is not deleted; verify insert was never called with an empty title
        verify(bookDao, never()).insert(
            org.mockito.kotlin.argThat<BookEntity> { entity -> entity.workId == workId && entity.title.isEmpty() }
        )
    }

    // 10.4 — getNote / setNote round-trip; note cleared on unbookmark
    @Test
    fun `getNote and setNote round-trip through DataStore`() = testScope.runTest {
        val workId = "OL1W"
        repository.setNote(workId, "My favourite book")

        repository.getNote(workId).test {
            assertEquals("My favourite book", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `note is cleared when book is unbookmarked`() = testScope.runTest {
        val workId = "OL1W"
        repository.setNote(workId, "Some note")
        repository.setBookmarked(workId, false)

        repository.getNote(workId).test {
            assertEquals("", awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
