package com.example.bookapp.ui.bookmarks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookapp.ui.components.BookListItem

@Composable
fun BookmarksScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val books by viewModel.bookmarkedBooks.collectAsStateWithLifecycle()

    if (books.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No bookmarks yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(books, key = { it.workId }) { book ->
                BookListItem(
                    title = book.title,
                    authors = book.authors,
                    coverUrl = book.coverUrl,
                    isBookmarked = true,
                    onBookmarkToggle = { viewModel.toggleBookmark(book, currentlyBookmarked = true) },
                    onClick = { onNavigateToDetail(book.workId) }
                )
            }
        }
    }
}
