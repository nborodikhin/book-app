package com.example.bookapp.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookapp.R
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.ui.theme.BookAppTheme
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    workId: String,
    onBack: () -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isBookmarked by viewModel.isBookmarked.collectAsStateWithLifecycle()

    var noteText by remember { mutableStateOf("") }
    var noteInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        noteText = viewModel.note.filterNotNull().first()
        noteInitialized = true
    }

    BookDetailScreenContent(
        uiState = uiState,
        isBookmarked = isBookmarked,
        noteText = noteText,
        noteInitialized = noteInitialized,
        onBack = onBack,
        onToggleBookmark = { viewModel.toggleBookmark() },
        onNoteChange = {
            noteText = it
            viewModel.onNoteChange(it)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookDetailScreenContent(
    uiState: DetailUiState,
    isBookmarked: Boolean,
    noteText: String,
    noteInitialized: Boolean,
    onBack: () -> Unit,
    onToggleBookmark: () -> Unit,
    onNoteChange: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is DetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, style = MaterialTheme.typography.bodyMedium)
                }
            }

            is DetailUiState.Success -> {
                val book = state.book
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f / 3f)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(book.coverUrl)
                                    .placeholder(R.drawable.ic_book_placeholder)
                                    .error(R.drawable.ic_error_image)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Cover of ${book.title}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = book.title, style = MaterialTheme.typography.headlineSmall)
                            if (book.authors.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = book.authors,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = book.synopsis.ifBlank { "No description available." },
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (isBookmarked) {
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = noteText,
                                    onValueChange = { onNoteChange(it.take(300)) },
                                    label = { Text("My note") },
                                    supportingText = { Text("${noteText.length}/300") },
                                    enabled = noteInitialized,
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Remove bookmark" else "Add bookmark",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookDetailLoadingPreview() {
    BookAppTheme {
        BookDetailScreenContent(
            uiState = DetailUiState.Loading,
            isBookmarked = false,
            noteText = "",
            noteInitialized = false,
            onBack = {},
            onToggleBookmark = {},
            onNoteChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookDetailErrorPreview() {
    BookAppTheme {
        BookDetailScreenContent(
            uiState = DetailUiState.Error("Could not load book details."),
            isBookmarked = false,
            noteText = "",
            noteInitialized = false,
            onBack = {},
            onToggleBookmark = {},
            onNoteChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookDetailSuccessNotBookmarkedPreview() {
    BookAppTheme {
        BookDetailScreenContent(
            uiState = DetailUiState.Success(
                BookEntity(
                    workId = "OL1W",
                    title = "Dune",
                    authors = "Frank Herbert",
                    synopsis = "A young nobleman flees to a desert planet where he must lead a rebellion.",
                    coverUrl = null
                )
            ),
            isBookmarked = false,
            noteText = "",
            noteInitialized = true,
            onBack = {},
            onToggleBookmark = {},
            onNoteChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookDetailSuccessBookmarkedPreview() {
    BookAppTheme {
        BookDetailScreenContent(
            uiState = DetailUiState.Success(
                BookEntity(
                    workId = "OL1W",
                    title = "Dune",
                    authors = "Frank Herbert",
                    synopsis = "A young nobleman flees to a desert planet where he must lead a rebellion.",
                    coverUrl = null
                )
            ),
            isBookmarked = true,
            noteText = "Great read for the holidays!",
            noteInitialized = true,
            onBack = {},
            onToggleBookmark = {},
            onNoteChange = {}
        )
    }
}
