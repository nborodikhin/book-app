package com.example.bookapp.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bookapp.MainActivity
import com.example.bookapp.utils.MockWebServerRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.SocketPolicy
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

private const val DEBOUNCE_MS = 2100L

private fun searchResponse(vararg titles: String): String {
    val docs = titles.mapIndexed { i, t ->
        """{"key":"/works/OL${i + 1}W","title":"$t","author_name":["Author"],"cover_i":null}"""
    }.joinToString(",")
    return """{"numFound":${titles.size},"docs":[$docs]}"""
}

private fun page20Response(prefix: String): String {
    val docs = (1..20).joinToString(",") { i ->
        """{"key":"/works/OL${prefix}${i}W","title":"$prefix Book $i","author_name":["Author"],"cover_i":null}"""
    }
    return """{"numFound":25,"docs":[$docs]}"""
}

private fun workDetailResponse(workId: String, title: String) =
    """{"key":"/works/$workId","title":"$title","description":"A great book.","covers":null}"""

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class BookshelfIntegrationTest {

    @get:Rule(order = 0)
    val mockWebServerRule = MockWebServerRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    // 4.2 — search results appear from MockWebServer response
    @Test
    fun searchResultsAppearFromMockResponse() {
        mockWebServerRule.enqueueResponse(searchResponse("Dune", "Foundation"))

        composeRule.onNodeWithText("Search books by title or author").performClick()
        composeRule.onNodeWithText("Search books by title or author").performTextInput("dune001")
        Thread.sleep(DEBOUNCE_MS)
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Dune").assertIsDisplayed()
        composeRule.onNodeWithText("Foundation").assertIsDisplayed()
    }

    // 4.3 — network error during search shows error message
    @Test
    fun networkErrorDuringSearchShowsErrorMessage() {
        mockWebServerRule.enqueueError()

        composeRule.onNodeWithText("Search books by title or author").performClick()
        composeRule.onNodeWithText("Search books by title or author").performTextInput("dune002")
        Thread.sleep(DEBOUNCE_MS)
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Something went wrong.").assertIsDisplayed()
    }

    // 4.4 — Retry after error loads results from second enqueued response
    @Test
    fun retryAfterErrorLoadsResults() {
        mockWebServerRule.enqueueError()
        mockWebServerRule.enqueueResponse(searchResponse("Dune Messiah"))

        composeRule.onNodeWithText("Search books by title or author").performClick()
        composeRule.onNodeWithText("Search books by title or author").performTextInput("dune003")
        Thread.sleep(DEBOUNCE_MS)
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Something went wrong.").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Dune Messiah").assertIsDisplayed()
    }

    // 4.5 — pagination: second page appended after scrolling to bottom
    @Test
    fun paginationAppendsSecondPage() {
        mockWebServerRule.enqueueResponse(page20Response("P1"))
        // page 2 with fewer items (signals last page)
        val page2Docs = (1..5).joinToString(",") { i ->
            """{"key":"/works/OLP2${i}W","title":"P1 Page2 Book $i","author_name":["Author"],"cover_i":null}"""
        }
        mockWebServerRule.enqueueResponse("""{"numFound":25,"docs":[$page2Docs]}""")

        composeRule.onNodeWithText("Search books by title or author").performClick()
        composeRule.onNodeWithText("Search books by title or author").performTextInput("paginate01")
        Thread.sleep(DEBOUNCE_MS)
        composeRule.waitForIdle()

        composeRule.onNodeWithText("P1 Book 1").assertIsDisplayed()

        // Scroll to near the end of page 1 to trigger pagination
        composeRule.onNodeWithText("P1 Book 20").performScrollTo()
        composeRule.waitForIdle()
        Thread.sleep(1500) // wait for page 2 network call

        composeRule.onNodeWithText("P1 Page2 Book 1").performScrollTo()
        composeRule.onNodeWithText("P1 Page2 Book 1").assertIsDisplayed()
    }

    // 4.6 — bookmark from Search → Bookmarks tab shows the bookmarked book
    @Test
    fun bookmarkFromSearchAppearsOnBookmarksTab() {
        mockWebServerRule.enqueueResponse(searchResponse("Neuromancer"))
        // setBookmarked calls getWork()
        mockWebServerRule.enqueueResponse(workDetailResponse("OL1W", "Neuromancer"))

        composeRule.onNodeWithText("Search books by title or author").performClick()
        composeRule.onNodeWithText("Search books by title or author").performTextInput("neuro01")
        Thread.sleep(DEBOUNCE_MS)
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Neuromancer").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Add bookmark").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Bookmarks").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Neuromancer").assertIsDisplayed()
    }

    // 4.7 — unbookmark from Bookmarks tab → book disappears
    @Test
    fun unbookmarkFromBookmarksTabRemovesBook() {
        mockWebServerRule.enqueueResponse(searchResponse("Ender's Game"))
        // setBookmarked calls getWork()
        mockWebServerRule.enqueueResponse(workDetailResponse("OL1W", "Ender's Game"))

        composeRule.onNodeWithText("Search books by title or author").performClick()
        composeRule.onNodeWithText("Search books by title or author").performTextInput("ender01")
        Thread.sleep(DEBOUNCE_MS)
        composeRule.waitForIdle()

        composeRule.onNodeWithContentDescription("Add bookmark").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Bookmarks").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Ender's Game").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Remove bookmark").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("No bookmarks yet").assertIsDisplayed()
    }

    // 4.8 — tab switch Search → Bookmarks → Search preserves results
    @Test
    fun tabSwitchPreservesSearchResults() {
        mockWebServerRule.enqueueResponse(searchResponse("The Left Hand of Darkness"))

        composeRule.onNodeWithText("Search books by title or author").performClick()
        composeRule.onNodeWithText("Search books by title or author").performTextInput("ursula01")
        Thread.sleep(DEBOUNCE_MS)
        composeRule.waitForIdle()

        composeRule.onNodeWithText("The Left Hand of Darkness").assertIsDisplayed()

        composeRule.onNodeWithText("Bookmarks").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Search").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("The Left Hand of Darkness").assertIsDisplayed()
    }

    // 4.9 — Back from BookDetailScreen returns to Search with results
    @Test
    fun backFromDetailReturnsToSearch() {
        mockWebServerRule.enqueueResponse(searchResponse("1984"))
        mockWebServerRule.enqueueResponse(workDetailResponse("OL1W", "1984"))

        composeRule.onNodeWithText("Search books by title or author").performClick()
        composeRule.onNodeWithText("Search books by title or author").performTextInput("orwell01")
        Thread.sleep(DEBOUNCE_MS)
        composeRule.waitForIdle()

        composeRule.onNodeWithText("1984").performClick()
        composeRule.waitForIdle()

        pressBack()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("1984").assertIsDisplayed()
    }

    // 4.10 — navigate away while detail is loading — no crash
    @Test
    fun navigateAwayWhileDetailLoadingNoCrash() {
        mockWebServerRule.enqueueResponse(searchResponse("Fahrenheit 451"))
        // Enqueue a slow detail response so we can navigate away while it's loading
        mockWebServerRule.server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(workDetailResponse("OL1W", "Fahrenheit 451"))
                .setBodyDelay(5, TimeUnit.SECONDS)
        )

        composeRule.onNodeWithText("Search books by title or author").performClick()
        composeRule.onNodeWithText("Search books by title or author").performTextInput("bradbury01")
        Thread.sleep(DEBOUNCE_MS)
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Fahrenheit 451").performClick()
        // Navigate back immediately while detail is still loading
        pressBack()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Search books by title or author").assertIsDisplayed()
    }
}
