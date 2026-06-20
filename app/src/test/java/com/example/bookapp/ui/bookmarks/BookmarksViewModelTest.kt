package com.example.bookapp.ui.bookmarks

import app.cash.turbine.test
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.data.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarksViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: BookRepository
    private lateinit var viewModel: BookmarksViewModel

    private val dune = BookEntity(workId = "OL1W", title = "Dune", authors = "Frank Herbert", synopsis = "", coverUrl = null)
    private val foundation = BookEntity(workId = "OL2W", title = "Foundation", authors = "Isaac Asimov", synopsis = "", coverUrl = null)
    private val hyperion = BookEntity(workId = "OL3W", title = "Hyperion", authors = "Dan Simmons", synopsis = "", coverUrl = null)

    private val booksFlow = MutableStateFlow(listOf(dune, foundation, hyperion))

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = mock()
        whenever(repository.getBookmarkedBooks()).thenReturn(booksFlow)
        viewModel = BookmarksViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `empty query returns all books`() = runTest(dispatcher) {
        viewModel.filteredBooks.test {
            advanceUntilIdle()
            assertEquals(listOf(dune, foundation, hyperion), expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter by title returns matching books`() = runTest(dispatcher) {
        viewModel.filteredBooks.test {
            viewModel.updateFilter("dune")
            advanceUntilIdle()
            assertEquals(listOf(dune), expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter by author returns matching books`() = runTest(dispatcher) {
        viewModel.filteredBooks.test {
            viewModel.updateFilter("asimov")
            advanceUntilIdle()
            assertEquals(listOf(foundation), expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter is case insensitive`() = runTest(dispatcher) {
        viewModel.filteredBooks.test {
            viewModel.updateFilter("FRANK")
            advanceUntilIdle()
            assertEquals(listOf(dune), expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filter with no matches returns empty list`() = runTest(dispatcher) {
        viewModel.filteredBooks.test {
            viewModel.updateFilter("zzznomatch")
            advanceUntilIdle()
            assertEquals(emptyList<BookEntity>(), expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearing filter restores all books`() = runTest(dispatcher) {
        viewModel.filteredBooks.test {
            viewModel.updateFilter("dune")
            advanceUntilIdle()
            viewModel.updateFilter("")
            advanceUntilIdle()
            assertEquals(listOf(dune, foundation, hyperion), expectMostRecentItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
