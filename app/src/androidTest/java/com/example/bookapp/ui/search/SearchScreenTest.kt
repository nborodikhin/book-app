package com.example.bookapp.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import com.example.bookapp.data.repository.SearchResult
import com.example.bookapp.ui.theme.BookAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val fakeResults = listOf(
        SearchResult(workId = "OL1W", title = "Dune", authors = listOf("Frank Herbert"), coverUrl = null),
        SearchResult(workId = "OL2W", title = "Foundation", authors = listOf("Isaac Asimov"), coverUrl = null)
    )

    @Test
    fun loadingSpinnerVisibleDuringLoadingState() {
        composeRule.setContent {
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
        composeRule.onNodeWithContentDescription("Loading").assertIsDisplayed()
    }

    @Test
    fun errorMessageVisibleDuringErrorState() {
        composeRule.setContent {
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
        composeRule.onNodeWithText("Something went wrong.").assertIsDisplayed()
    }

    @Test
    fun errorStateShowsRetryButtonAndItIsClickable() {
        var retryClicked = false
        composeRule.setContent {
            BookAppTheme {
                SearchScreenContent(
                    uiState = SearchUiState.Error("Something went wrong.", onRetry = { retryClicked = true }),
                    query = "dune",
                    onQueryChange = {},
                    isBookmarked = { false },
                    onBookmarkToggle = { _, _ -> },
                    onNavigateToDetail = {}
                )
            }
        }
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()
        assertTrue(retryClicked)
    }

    @Test
    fun paginationErrorShowsInlineBannerAndRetryButton() {
        var retryPaginationClicked = false
        composeRule.setContent {
            BookAppTheme {
                SearchScreenContent(
                    uiState = SearchUiState.Success(results = fakeResults, paginationError = true),
                    query = "dune",
                    onQueryChange = {},
                    isBookmarked = { false },
                    onBookmarkToggle = { _, _ -> },
                    onNavigateToDetail = {},
                    onRetryPagination = { retryPaginationClicked = true }
                )
            }
        }
        composeRule.onNodeWithText("Failed to load more.").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()
        assertTrue(retryPaginationClicked)
    }

    @Test
    fun resultsListVisibleDuringSuccessState() {
        composeRule.setContent {
            BookAppTheme {
                SearchScreenContent(
                    uiState = SearchUiState.Success(results = fakeResults),
                    query = "dune",
                    onQueryChange = {},
                    isBookmarked = { false },
                    onBookmarkToggle = { _, _ -> },
                    onNavigateToDetail = {}
                )
            }
        }
        composeRule.onNodeWithText("Dune").assertIsDisplayed()
        composeRule.onNodeWithText("Foundation").assertIsDisplayed()
    }

    @Test
    fun bookmarkIconFilledWhenItemIsBookmarked() {
        composeRule.setContent {
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
        composeRule.onNodeWithContentDescription("Remove bookmark").assertIsDisplayed()
    }

    @Test
    fun bookmarkIconOutlineWhenItemIsNotBookmarked() {
        composeRule.setContent {
            BookAppTheme {
                SearchScreenContent(
                    uiState = SearchUiState.Success(results = fakeResults),
                    query = "dune",
                    onQueryChange = {},
                    isBookmarked = { false },
                    onBookmarkToggle = { _, _ -> },
                    onNavigateToDetail = {}
                )
            }
        }
        composeRule.onAllNodesWithContentDescription("Add bookmark")[0].assertIsDisplayed()
    }
}
