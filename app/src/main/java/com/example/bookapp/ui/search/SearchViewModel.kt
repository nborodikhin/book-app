package com.example.bookapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookapp.data.repository.BookRepository
import com.example.bookapp.data.repository.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Success(
        val results: List<SearchResult>,
        val isLoadingMore: Boolean = false,
        val paginationError: Boolean = false
    ) : SearchUiState
    data class Error(val message: String, val onRetry: () -> Unit) : SearchUiState
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private var isLastPage = false
    private val accumulatedResults = mutableListOf<SearchResult>()

    init {
        observeQuery()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeQuery() {
        viewModelScope.launch {
            searchQuery
                .debounce(2000L)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        accumulatedResults.clear()
                        currentPage = 1
                        isLastPage = false
                        _uiState.value = SearchUiState.Idle
                    } else {
                        resetAndSearch(query)
                    }
                }
        }
    }

    private suspend fun resetAndSearch(query: String) {
        currentPage = 1
        isLastPage = false
        accumulatedResults.clear()
        _uiState.value = SearchUiState.Loading
        fetchPage(query, currentPage)
    }

    // 6.2 — pagination logic
    fun loadNextPage() {
        if (isLastPage) return
        val current = _uiState.value
        if (current is SearchUiState.Success && !current.isLoadingMore) {
            val query = searchQuery.value
            if (query.isBlank()) return
            _uiState.value = current.copy(isLoadingMore = true, paginationError = false)
            viewModelScope.launch { fetchPage(query, currentPage + 1) }
        }
    }

    fun retrySearch() {
        val query = searchQuery.value
        if (query.isBlank()) return
        viewModelScope.launch { resetAndSearch(query) }
    }

    private suspend fun fetchPage(query: String, page: Int) {
        try {
            val results = repository.search(query, page)
            if (results.size < 20) isLastPage = true
            if (page == 1) accumulatedResults.clear()
            accumulatedResults.addAll(results)
            currentPage = page
            _uiState.value = SearchUiState.Success(accumulatedResults.toList())
        } catch (e: Exception) {
            if (page == 1) {
                _uiState.value = SearchUiState.Error("Something went wrong.", ::retrySearch)
            } else {
                val current = _uiState.value
                val existing = if (current is SearchUiState.Success) current.results else accumulatedResults.toList()
                _uiState.value = SearchUiState.Success(existing, paginationError = true)
            }
        }
    }

    // 6.6 — bookmark toggle
    fun toggleBookmark(result: SearchResult, currentlyBookmarked: Boolean) {
        viewModelScope.launch {
            repository.setBookmarked(
                workId = result.workId,
                bookmarked = !currentlyBookmarked,
                title = result.title,
                authors = result.authors,
                coverUrl = result.coverUrl
            )
        }
    }

    fun isBookmarked(workId: String) = repository.isBookmarked(workId)
}
