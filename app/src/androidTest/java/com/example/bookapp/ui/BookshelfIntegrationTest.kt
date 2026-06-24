package com.example.bookapp.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bookapp.MainActivity
import com.example.bookapp.utils.MockWebServerRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockResponse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

private const val RESULT_TIMEOUT_MS = 10_000L

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

    private fun typeInSearchField(text: String) {
        composeRule.onNode(hasSetTextAction()).performTextInput(text)
    }

    private fun waitForText(text: String) {
        composeRule.waitUntil(timeoutMillis = RESULT_TIMEOUT_MS) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    // 4.2 — search results appear from MockWebServer response
    @Test
    fun searchResultsAppearFromMockResponse() {
        mockWebServerRule.enqueueResponse(searchResponse("Dune", "Foundation"))

        typeInSearchField("dune001")
        waitForText("Dune")

        composeRule.onNodeWithText("Dune").assertIsDisplayed()
        composeRule.onNodeWithText("Foundation").assertIsDisplayed()
    }

    // 4.3 — network error during search shows error message
    @Test
    fun networkErrorDuringSearchShowsErrorMessage() {
        mockWebServerRule.enqueueError()

        typeInSearchField("dune002")
        waitForText("Something went wrong.")

        composeRule.onNodeWithText("Something went wrong.").assertIsDisplayed()
    }

    // 4.4 — Retry after error loads results from second enqueued response
    @Test
    fun retryAfterErrorLoadsResults() {
        mockWebServerRule.enqueueError()
        mockWebServerRule.enqueueResponse(searchResponse("Dune Messiah"))

        typeInSearchField("dune003")
        waitForText("Something went wrong.")
        composeRule.onNodeWithText("Retry").performClick()

        waitForText("Dune Messiah")
        composeRule.onNodeWithText("Dune Messiah").assertIsDisplayed()
    }

    // 4.5 — pagination: second page appended after scrolling to bottom
    @Test
    fun paginationAppendsSecondPage() {
        mockWebServerRule.enqueueResponse(page20Response("P1"))
        val page2Docs = (1..5).joinToString(",") { i ->
            """{"key":"/works/OLP2${i}W","title":"P2 Book $i","author_name":["Author"],"cover_i":null}"""
        }
        mockWebServerRule.enqueueResponse("""{"numFound":25,"docs":[$page2Docs]}""")

        typeInSearchField("paginate01")
        waitForText("P1 Book 1")

        // Scroll to last item of page 1 to trigger pagination
        composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(19)

        // Keep scrolling to index 20; before page 2 loads this clamps to 19 (no-op),
        // and once page 2 loads it brings "P2 Book 1" into view.
        composeRule.waitUntil(timeoutMillis = RESULT_TIMEOUT_MS) {
            composeRule.onNode(hasScrollToIndexAction()).performScrollToIndex(20)
            composeRule.onAllNodesWithText("P2 Book 1").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("P2 Book 1").assertIsDisplayed()
    }

    // 4.6 — bookmark from Search → Bookmarks tab shows the bookmarked book
    @Test
    fun bookmarkFromSearchAppearsOnBookmarksTab() {
        mockWebServerRule.enqueueResponse(searchResponse("Neuromancer"))
        // setBookmarked fetches work detail to store in Room
        mockWebServerRule.enqueueResponse(workDetailResponse("OL1W", "Neuromancer"))

        typeInSearchField("neuro01")
        waitForText("Neuromancer")

        composeRule.onNodeWithContentDescription("Add bookmark").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Bookmarks").performClick()

        waitForText("Neuromancer")
        composeRule.onNodeWithText("Neuromancer").assertIsDisplayed()
    }

    // 4.7 — unbookmark from Bookmarks tab → book disappears
    @Test
    fun unbookmarkFromBookmarksTabRemovesBook() {
        mockWebServerRule.enqueueResponse(searchResponse("Ender's Game"))
        mockWebServerRule.enqueueResponse(workDetailResponse("OL1W", "Ender's Game"))

        typeInSearchField("ender01")
        waitForText("Ender's Game")

        composeRule.onNodeWithContentDescription("Add bookmark").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Bookmarks").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Ender's Game").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Remove bookmark").performClick()
        waitForText("No bookmarks yet")

        composeRule.onNodeWithText("No bookmarks yet").assertIsDisplayed()
    }

    // 4.8 — tab switch Search → Bookmarks → Search preserves results
    @Test
    fun tabSwitchPreservesSearchResults() {
        mockWebServerRule.enqueueResponse(searchResponse("The Left Hand of Darkness"))

        typeInSearchField("ursula01")
        waitForText("The Left Hand of Darkness")

        composeRule.onNodeWithText("Bookmarks").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Search").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("The Left Hand of Darkness").assertIsDisplayed()
    }

    // 4.9 — Back from BookDetailScreen returns to Search with results
    @Test
    fun backFromDetailReturnsToSearch() {
        mockWebServerRule.enqueueResponse(searchResponse("Nineteen Eighty-Four"))
        mockWebServerRule.enqueueResponse(workDetailResponse("OL1W", "Nineteen Eighty-Four"))

        typeInSearchField("orwell01")
        waitForText("Nineteen Eighty-Four")

        composeRule.onNodeWithText("Nineteen Eighty-Four").performClick()
        composeRule.waitForIdle()

        pressBack()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Nineteen Eighty-Four").assertIsDisplayed()
    }

    // 4.10 — navigate away while detail is loading — no crash
    @Test
    fun navigateAwayWhileDetailLoadingNoCrash() {
        mockWebServerRule.enqueueResponse(searchResponse("Fahrenheit 451"))
        // Slow detail response so we can navigate away while it's loading
        mockWebServerRule.server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(workDetailResponse("OL1W", "Fahrenheit 451"))
                .setBodyDelay(2, TimeUnit.SECONDS)
        )

        typeInSearchField("bradbury01")
        waitForText("Fahrenheit 451")

        composeRule.onNodeWithText("Fahrenheit 451").performClick()
        pressBack()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Fahrenheit 451").assertIsDisplayed()
    }
}
