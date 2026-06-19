package com.example.bookapp.ui.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.data.repository.BookRepository
import com.example.bookapp.ui.detail.BookDetailViewModel
import com.example.bookapp.ui.detail.DetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class BookDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: BookRepository

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repository = mock()
        whenever(repository.isBookmarked(any())).thenReturn(flowOf(false))
        whenever(repository.getNote(any())).thenReturn(flowOf(""))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(workId: String) = BookDetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf("workId" to workId)),
        repository = repository
    )

    // 10.8 — local-first: bookmarked book served from Room without network call
    @Test
    fun `local book data served from Room without network call`() = runTest(dispatcher) {
        val entity = BookEntity("OL1W", "Dune", "Frank Herbert", "A desert planet.", null)
        whenever(repository.getBookDetail("OL1W")).thenReturn(entity)

        val vm = viewModel("OL1W")
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is DetailUiState.Success)
        assertEquals("Dune", (state as DetailUiState.Success).book.title)
    }

    // 10.9 — non-local book triggers network fetch and shows loading state
    @Test
    fun `non-local book triggers network fetch and loading state first`() = runTest(dispatcher) {
        val entity = BookEntity("OL2W", "Foundation", "Asimov", "Empire.", null)
        whenever(repository.getBookDetail("OL2W")).thenReturn(entity)

        val vm = viewModel("OL2W")

        vm.uiState.test {
            assertEquals(DetailUiState.Loading, awaitItem())
            advanceUntilIdle()
            assertTrue(awaitItem() is DetailUiState.Success)
            cancelAndIgnoreRemainingEvents()
        }
    }

    // 10.10 — note capped at 300 chars
    @Test
    fun `note input beyond 300 chars is truncated`() = runTest(dispatcher) {
        val entity = BookEntity("OL3W", "Book", "Author", "", null)
        whenever(repository.getBookDetail("OL3W")).thenReturn(entity)

        val vm = viewModel("OL3W")
        advanceUntilIdle()

        val longNote = "A".repeat(350)
        vm.onNoteChange(longNote)
        advanceUntilIdle()

        verify(repository).setNote("OL3W", "A".repeat(300))
    }
}
