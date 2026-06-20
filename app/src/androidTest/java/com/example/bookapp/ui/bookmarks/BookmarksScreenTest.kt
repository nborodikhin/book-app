package com.example.bookapp.ui.bookmarks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
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
                    allBooksEmpty = true,
                    filterQuery = "",
                    onFilterChange = {},
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
                    allBooksEmpty = false,
                    filterQuery = "",
                    onFilterChange = {},
                    onToggleBookmark = {},
                    onNavigateToDetail = {}
                )
            }
        }
        composeRule.onNodeWithText("Dune").assertIsDisplayed()
        composeRule.onNodeWithText("Foundation").assertIsDisplayed()
    }

    @Test
    fun filterFieldIsVisible() {
        composeRule.setContent {
            BookAppTheme {
                BookmarksScreenContent(
                    books = fakeBooks,
                    allBooksEmpty = false,
                    filterQuery = "",
                    onFilterChange = {},
                    onToggleBookmark = {},
                    onNavigateToDetail = {}
                )
            }
        }
        composeRule.onNodeWithText("Filter by title or author").assertIsDisplayed()
    }

    @Test
    fun filterNarrowsListByTitle() {
        var query by mutableStateOf("")
        composeRule.setContent {
            BookAppTheme {
                val filtered = if (query.isBlank()) fakeBooks
                    else fakeBooks.filter { it.title.contains(query, ignoreCase = true) || it.authors.contains(query, ignoreCase = true) }
                BookmarksScreenContent(
                    books = filtered,
                    allBooksEmpty = false,
                    filterQuery = query,
                    onFilterChange = { query = it },
                    onToggleBookmark = {},
                    onNavigateToDetail = {}
                )
            }
        }
        composeRule.onNodeWithText("Filter by title or author").performTextInput("Dune")
        composeRule.onNodeWithText("Dune").assertIsDisplayed()
        composeRule.onNodeWithText("Foundation").assertDoesNotExist()
    }

    @Test
    fun noResultsMessageShownWhenFilterMatchesNothing() {
        composeRule.setContent {
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
        composeRule.onNodeWithText("No results for \"xyz\"").assertIsDisplayed()
    }
}
