package com.example.bookapp.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.ui.theme.BookAppTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val sampleBook = BookEntity(
        workId = "OL1W",
        title = "Dune",
        authors = "Frank Herbert",
        synopsis = "A desert planet saga.",
        coverUrl = null
    )

    @Test
    fun noteFieldVisibleWhenBookmarked() {
        composeRule.setContent {
            BookAppTheme {
                BookDetailScreenContent(
                    uiState = DetailUiState.Success(sampleBook),
                    isBookmarked = true,
                    noteText = "",
                    noteInitialized = true,
                    onBack = {},
                    onToggleBookmark = {},
                    onNoteChange = {}
                )
            }
        }
        composeRule.onNodeWithText("My note").assertIsDisplayed()
    }

    @Test
    fun noteFieldHiddenWhenNotBookmarked() {
        composeRule.setContent {
            BookAppTheme {
                BookDetailScreenContent(
                    uiState = DetailUiState.Success(sampleBook),
                    isBookmarked = false,
                    noteText = "",
                    noteInitialized = true,
                    onBack = {},
                    onToggleBookmark = {},
                    onNoteChange = {}
                )
            }
        }
        composeRule.onNodeWithText("My note").assertIsNotDisplayed()
    }

    @Test
    fun characterCounterShowsInitialCount() {
        composeRule.setContent {
            BookAppTheme {
                BookDetailScreenContent(
                    uiState = DetailUiState.Success(sampleBook),
                    isBookmarked = true,
                    noteText = "",
                    noteInitialized = true,
                    onBack = {},
                    onToggleBookmark = {},
                    onNoteChange = {}
                )
            }
        }
        composeRule.onNodeWithText("0/300").assertIsDisplayed()
    }

    @Test
    fun characterCounterUpdatesAsTextChanges() {
        var note by mutableStateOf("")
        composeRule.setContent {
            BookAppTheme {
                BookDetailScreenContent(
                    uiState = DetailUiState.Success(sampleBook),
                    isBookmarked = true,
                    noteText = note,
                    noteInitialized = true,
                    onBack = {},
                    onToggleBookmark = {},
                    onNoteChange = { note = it }
                )
            }
        }
        composeRule.onNodeWithText("0/300").assertIsDisplayed()
        composeRule.onNodeWithText("My note").performTextInput("Hello")
        composeRule.onNodeWithText("5/300").assertIsDisplayed()
    }

    @Test
    fun inputCappedAt300Characters() {
        var note by mutableStateOf("")
        composeRule.setContent {
            BookAppTheme {
                BookDetailScreenContent(
                    uiState = DetailUiState.Success(sampleBook),
                    isBookmarked = true,
                    noteText = note,
                    noteInitialized = true,
                    onBack = {},
                    onToggleBookmark = {},
                    onNoteChange = { note = it }
                )
            }
        }
        composeRule.onNodeWithText("My note").performTextInput("A".repeat(310))
        composeRule.onNodeWithText("300/300").assertIsDisplayed()
    }

    @Test
    fun bookmarkIconTogglesOnTap() {
        var isBookmarked by mutableStateOf(false)
        composeRule.setContent {
            BookAppTheme {
                BookDetailScreenContent(
                    uiState = DetailUiState.Success(sampleBook),
                    isBookmarked = isBookmarked,
                    noteText = "",
                    noteInitialized = true,
                    onBack = {},
                    onToggleBookmark = { isBookmarked = !isBookmarked },
                    onNoteChange = {}
                )
            }
        }
        composeRule.onNodeWithContentDescription("Add bookmark").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Add bookmark").performClick()
        composeRule.onNodeWithContentDescription("Remove bookmark").assertIsDisplayed()
    }

    @Test
    fun loadingStateRendersCorrectly() {
        composeRule.setContent {
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
        composeRule.onNodeWithText("My note").assertIsNotDisplayed()
    }

    @Test
    fun errorStateRendersCorrectly() {
        composeRule.setContent {
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
        composeRule.onNodeWithText("Could not load book details.").assertIsDisplayed()
    }
}
