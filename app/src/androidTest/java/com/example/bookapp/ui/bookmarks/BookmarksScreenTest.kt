package com.example.bookapp.ui.bookmarks

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.ui.theme.BookAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookmarksScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val fakeBooks = listOf(
        BookEntity(workId = "OL1W", title = "Dune", authors = "Frank Herbert", synopsis = "", coverUrl = null),
        BookEntity(workId = "OL2W", title = "Foundation", authors = "Isaac Asimov", synopsis = "", coverUrl = null)
    )

    @Test
    fun emptyStateMessageShownWhenBooksEmpty() {
        composeRule.setContent {
            BookAppTheme {
                BookmarksScreenContent(
                    books = emptyList(),
                    onToggleBookmark = {},
                    onNavigateToDetail = {}
                )
            }
        }
        composeRule.onNodeWithText("No bookmarks yet").assertIsDisplayed()
    }

    @Test
    fun listItemsRenderedWhenBooksNonEmpty() {
        composeRule.setContent {
            BookAppTheme {
                BookmarksScreenContent(
                    books = fakeBooks,
                    onToggleBookmark = {},
                    onNavigateToDetail = {}
                )
            }
        }
        composeRule.onNodeWithText("Dune").assertIsDisplayed()
        composeRule.onNodeWithText("Foundation").assertIsDisplayed()
    }
}
