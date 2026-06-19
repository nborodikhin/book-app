package com.example.bookapp.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
