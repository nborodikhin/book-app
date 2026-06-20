package com.example.bookapp.ui.search

import app.cash.turbine.test
import com.example.bookapp.data.repository.BookRepository
import com.example.bookapp.data.repository.SearchResult
import com.example.bookapp.ui.search.SearchUiState
import com.example.bookapp.ui.search.SearchViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: BookRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = mock()
        whenever(repository.isBookmarked(any())).thenReturn(flowOf(false))
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 10.5 — debounce: rapid input changes produce only one search call after 2s idle
    @Test
    fun `rapid typing produces only one search call after 2s idle`() = runTest(dispatcher) {
        val fakeResults = listOf(SearchResult("OL1W", "Dune", listOf("Frank Herbert"), null))
        whenever(repository.search(any(), any())).thenReturn(fakeResults)

        viewModel.searchQuery.value = "d"
        advanceTimeBy(500)
        viewModel.searchQuery.value = "du"
        advanceTimeBy(500)
        viewModel.searchQuery.value = "dun"
        advanceTimeBy(500)
        viewModel.searchQuery.value = "dune"
        advanceTimeBy(2100) // wait for debounce

        verify(repository, times(1)).search("dune", 1)
    }

    // 10.6 — pagination: next page appended; no request when last page loaded
    @Test
    fun `loadNextPage appends results and respects last page`() = runTest(dispatcher) {
        val page1 = List(20) { SearchResult("OL${it}W", "Book $it", emptyList(), null) }
        val page2 = listOf(SearchResult("OL20W", "Book 20", emptyList(), null)) // < 20 = last page
        whenever(repository.search("dune", 1)).thenReturn(page1)
        whenever(repository.search("dune", 2)).thenReturn(page2)

        viewModel.searchQuery.value = "dune"
        advanceTimeBy(2100)

        viewModel.loadNextPage()
        advanceTimeBy(100)

        viewModel.loadNextPage() // should be no-op (last page)
        advanceTimeBy(100)

        verify(repository, times(1)).search("dune", 1)
        verify(repository, times(1)).search("dune", 2)
        verify(repository, never()).search("dune", 3)

        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Success)
        assertEquals(21, (state as SearchUiState.Success).results.size)
    }

    // 10.7 — loading state during fetch; error state on network failure
    @Test
    fun `loading state emitted during fetch`() = runTest(dispatcher) {
        whenever(repository.search(any(), any())).thenReturn(emptyList())

        viewModel.uiState.test {
            assertEquals(SearchUiState.Idle, awaitItem())
            viewModel.searchQuery.value = "dune"
            advanceTimeBy(2100)
            assertEquals(SearchUiState.Loading, awaitItem())
            assertTrue(awaitItem() is SearchUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `error state emitted on network failure`() = runTest(dispatcher) {
        whenever(repository.search(any(), any())).thenThrow(RuntimeException("Network error"))

        viewModel.searchQuery.value = "dune"
        advanceTimeBy(2100)

        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Error)
    }

    @Test
    fun `initial search error state carries non-null onRetry`() = runTest(dispatcher) {
        whenever(repository.search(any(), any())).thenThrow(RuntimeException("Network error"))

        viewModel.searchQuery.value = "dune"
        advanceTimeBy(2100)

        val state = viewModel.uiState.value as SearchUiState.Error
        assertNotNull(state.onRetry)
    }

    @Test
    fun `retrySearch transitions back to Loading then Success`() = runTest(dispatcher) {
        val fakeResults = listOf(SearchResult("OL1W", "Dune", listOf("Frank Herbert"), null))
        whenever(repository.search(any(), any()))
            .thenThrow(RuntimeException("Network error"))
            .thenReturn(fakeResults)

        viewModel.searchQuery.value = "dune"
        advanceTimeBy(2100)
        assertTrue(viewModel.uiState.value is SearchUiState.Error)

        viewModel.retrySearch()
        advanceTimeBy(100)

        assertTrue(viewModel.uiState.value is SearchUiState.Success)
    }

    @Test
    fun `pagination error keeps existing results and sets paginationError flag`() = runTest(dispatcher) {
        val page1 = List(20) { SearchResult("OL${it}W", "Book $it", emptyList(), null) }
        whenever(repository.search("dune", 1)).thenReturn(page1)
        whenever(repository.search("dune", 2)).thenThrow(RuntimeException("Network error"))

        viewModel.searchQuery.value = "dune"
        advanceTimeBy(2100)

        viewModel.loadNextPage()
        advanceTimeBy(100)

        val state = viewModel.uiState.value as SearchUiState.Success
        assertEquals(20, state.results.size)
        assertTrue(state.paginationError)
    }

    @Test
    fun `loadNextPage after pagination error clears paginationError and sets isLoadingMore`() = runTest(dispatcher) {
        val page1 = List(20) { SearchResult("OL${it}W", "Book $it", emptyList(), null) }
        whenever(repository.search("dune", 1)).thenReturn(page1)
        whenever(repository.search("dune", 2)).thenThrow(RuntimeException("Network error"))

        viewModel.searchQuery.value = "dune"
        advanceTimeBy(2100)
        viewModel.loadNextPage()
        advanceTimeBy(100)
        assertTrue((viewModel.uiState.value as SearchUiState.Success).paginationError)

        // loadNextPage sets isLoadingMore=true and paginationError=false synchronously
        // before launching the fetch coroutine — verify that intermediate state
        viewModel.loadNextPage()

        val loadingState = viewModel.uiState.value as SearchUiState.Success
        assertTrue(loadingState.isLoadingMore)
        assertFalse(loadingState.paginationError)
    }
}
