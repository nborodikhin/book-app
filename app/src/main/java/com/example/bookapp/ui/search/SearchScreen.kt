package com.example.bookapp.ui.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bookapp.data.repository.SearchResult
import com.example.bookapp.ui.components.BookListItem
import com.example.bookapp.ui.theme.BookAppTheme

@Composable
fun SearchScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisible >= totalItems - 5
        }.collect { nearEnd ->
            if (nearEnd) viewModel.loadNextPage()
        }
    }

    SearchScreenContent(
        uiState = uiState,
        query = query,
        onQueryChange = { viewModel.searchQuery.value = it },
        isBookmarked = { workId ->
            viewModel.isBookmarked(workId).collectAsStateWithLifecycle(initialValue = false).value
        },
        onBookmarkToggle = { result, currentlyBookmarked ->
            viewModel.toggleBookmark(result, currentlyBookmarked)
        },
        onNavigateToDetail = onNavigateToDetail,
        onRetryPagination = { viewModel.loadNextPage() },
        listState = listState
    )
}

@Composable
internal fun SearchScreenContent(
    uiState: SearchUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    isBookmarked: @Composable (String) -> Boolean,
    onBookmarkToggle: (SearchResult, Boolean) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onRetryPagination: () -> Unit = {},
    listState: LazyListState = rememberLazyListState()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search books by title or author") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        when (val state = uiState) {
            is SearchUiState.Idle -> Unit

            is SearchUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.semantics { contentDescription = "Loading" })
                }
            }

            is SearchUiState.Success -> {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(state.results, key = { _, item -> item.workId }) { _, result ->
                        val bookmarked = isBookmarked(result.workId)
                        BookListItem(
                            title = result.title,
                            authors = result.authors.joinToString(", "),
                            coverUrl = result.coverUrl,
                            isBookmarked = bookmarked,
                            onBookmarkToggle = { onBookmarkToggle(result, bookmarked) },
                            onClick = { onNavigateToDetail(result.workId) }
                        )
                    }
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    if (state.paginationError) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Failed to load more.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    TextButton(onClick = onRetryPagination) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is SearchUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = state.onRetry,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

private val fakeResults = listOf(
    SearchResult(workId = "OL1W", title = "Dune", authors = listOf("Frank Herbert"), coverUrl = null),
    SearchResult(workId = "OL2W", title = "Foundation", authors = listOf("Isaac Asimov"), coverUrl = null)
)

@Preview(showBackground = true)
@Composable
private fun SearchIdlePreview() {
    BookAppTheme {
        SearchScreenContent(
            uiState = SearchUiState.Idle,
            query = "",
            onQueryChange = {},
            isBookmarked = { false },
            onBookmarkToggle = { _, _ -> },
            onNavigateToDetail = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchLoadingPreview() {
    BookAppTheme {
        SearchScreenContent(
            uiState = SearchUiState.Loading,
            query = "dune",
            onQueryChange = {},
            isBookmarked = { false },
            onBookmarkToggle = { _, _ -> },
            onNavigateToDetail = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchSuccessPreview() {
    BookAppTheme {
        SearchScreenContent(
            uiState = SearchUiState.Success(results = fakeResults),
            query = "dune",
            onQueryChange = {},
            isBookmarked = { workId -> workId == "OL1W" },
            onBookmarkToggle = { _, _ -> },
            onNavigateToDetail = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchErrorPreview() {
    BookAppTheme {
        SearchScreenContent(
            uiState = SearchUiState.Error("Something went wrong.", onRetry = {}),
            query = "dune",
            onQueryChange = {},
            isBookmarked = { false },
            onBookmarkToggle = { _, _ -> },
            onNavigateToDetail = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchPaginationErrorPreview() {
    BookAppTheme {
        SearchScreenContent(
            uiState = SearchUiState.Success(results = fakeResults, paginationError = true),
            query = "dune",
            onQueryChange = {},
            isBookmarked = { false },
            onBookmarkToggle = { _, _ -> },
            onNavigateToDetail = {},
            onRetryPagination = {}
        )
    }
}
