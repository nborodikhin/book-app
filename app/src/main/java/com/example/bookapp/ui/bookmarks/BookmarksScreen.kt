package com.example.bookapp.ui.bookmarks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bookapp.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.ui.components.BookListItem
import com.example.bookapp.ui.theme.BookAppTheme

@Composable
fun BookmarksScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val books by viewModel.filteredBooks.collectAsStateWithLifecycle()
    val allBooks by viewModel.bookmarkedBooks.collectAsStateWithLifecycle()
    val filterQuery by viewModel.filterQuery.collectAsStateWithLifecycle()

    BookmarksScreenContent(
        books = books,
        allBooksEmpty = allBooks.isEmpty(),
        filterQuery = filterQuery,
        onFilterChange = viewModel::updateFilter,
        onToggleBookmark = { book -> viewModel.toggleBookmark(book, currentlyBookmarked = true) },
        onNavigateToDetail = onNavigateToDetail
    )
}

@Composable
internal fun BookmarksScreenContent(
    books: List<BookEntity>,
    allBooksEmpty: Boolean,
    filterQuery: String,
    onFilterChange: (String) -> Unit,
    onToggleBookmark: (BookEntity) -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = filterQuery,
            onValueChange = onFilterChange,
            placeholder = { Text(stringResource(R.string.bookmarks_filter_placeholder)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        when {
            allBooksEmpty -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.bookmarks_empty_no_bookmarks),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            books.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.bookmarks_empty_no_results, filterQuery),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(books, key = { it.workId }) { book ->
                        BookListItem(
                            title = book.title,
                            authors = book.authors,
                            coverUrl = book.coverUrl,
                            isBookmarked = true,
                            onBookmarkToggle = { onToggleBookmark(book) },
                            onClick = { onNavigateToDetail(book.workId) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarksEmptyPreview() {
    BookAppTheme {
        BookmarksScreenContent(
            books = emptyList(),
            allBooksEmpty = true,
            filterQuery = "",
            onFilterChange = {},
            onToggleBookmark = {},
            onNavigateToDetail = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarksNoResultsPreview() {
    BookAppTheme {
        BookmarksScreenContent(
            books = emptyList(),
            allBooksEmpty = false,
            filterQuery = "xyz",
            onFilterChange = {},
            onToggleBookmark = {},
            onNavigateToDetail = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarksNonEmptyPreview() {
    BookAppTheme {
        BookmarksScreenContent(
            books = listOf(
                BookEntity(workId = "OL1W", title = "Dune", authors = "Frank Herbert", synopsis = "", coverUrl = null),
                BookEntity(workId = "OL2W", title = "Foundation", authors = "Isaac Asimov", synopsis = "", coverUrl = null)
            ),
            allBooksEmpty = false,
            filterQuery = "",
            onFilterChange = {},
            onToggleBookmark = {},
            onNavigateToDetail = {}
        )
    }
}
