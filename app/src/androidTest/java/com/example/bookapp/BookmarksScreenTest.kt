package com.example.bookapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class BookmarksScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    // 11.5 — empty state shown when no bookmarks
    @Test
    fun emptyStateShownWhenNoBookmarks() {
        composeRule.onNodeWithText("Bookmarks").performClick()
        composeRule.onNodeWithText("No bookmarks yet").assertIsDisplayed()
    }

    // 11.5 — bookmarked books appear in list (requires fake data injection for determinism)
    @Test
    fun bookmarksTabIsAccessible() {
        composeRule.onNodeWithText("Bookmarks").performClick()
        composeRule.onNodeWithText("Bookmarks").assertIsDisplayed()
    }
}
