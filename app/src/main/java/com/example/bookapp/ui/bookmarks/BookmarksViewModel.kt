package com.example.bookapp.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    val bookmarkedBooks: StateFlow<List<BookEntity>> =
        repository.getBookmarkedBooks()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val filterQuery = MutableStateFlow("")

    val filteredBooks: StateFlow<List<BookEntity>> =
        combine(bookmarkedBooks, filterQuery) { books, query ->
            if (query.isBlank()) books
            else books.filter { book ->
                book.title.contains(query, ignoreCase = true) ||
                    book.authors.contains(query, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun updateFilter(query: String) {
        filterQuery.value = query
    }

    fun toggleBookmark(book: BookEntity, currentlyBookmarked: Boolean) {
        viewModelScope.launch {
            repository.setBookmarked(
                workId = book.workId,
                bookmarked = !currentlyBookmarked,
                title = book.title,
                authors = book.authors.split(", ").filter { it.isNotBlank() },
                coverUrl = book.coverUrl
            )
        }
    }
}
