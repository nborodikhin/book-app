package com.example.bookapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookapp.R
import com.example.bookapp.ui.theme.BookAppTheme

@Composable
fun BookListItem(
    title: String,
    authors: String,
    coverUrl: String?,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(coverUrl)
                .placeholder(R.drawable.ic_book_placeholder)
                .error(R.drawable.ic_error_image)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.book_cover_description, title),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(56.dp, 80.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (authors.isNotBlank()) {
                Text(
                    text = authors,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        IconButton(onClick = onBookmarkToggle) {
            Icon(
                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = if (isBookmarked) stringResource(R.string.book_bookmark_remove_description) else stringResource(R.string.book_bookmark_add_description),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookListItemBookmarkedPreview() {
    BookAppTheme {
        BookListItem(
            title = "Dune",
            authors = "Frank Herbert",
            coverUrl = null,
            isBookmarked = true,
            onBookmarkToggle = {},
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BookListItemNotBookmarkedPreview() {
    BookAppTheme {
        BookListItem(
            title = "Foundation",
            authors = "Isaac Asimov",
            coverUrl = null,
            isBookmarked = false,
            onBookmarkToggle = {},
            onClick = {}
        )
    }
}
