package com.example.bookapp.ui.detail

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookapp.R
import com.example.bookapp.data.local.BookEntity
import com.example.bookapp.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val book: BookEntity) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

private const val NOTE_MAX_LENGTH = 300

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: BookRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val workId: String = checkNotNull(savedStateHandle["workId"])

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    val isBookmarked: StateFlow<Boolean> = repository.isBookmarked(workId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val note: StateFlow<String?> = repository.getNote(workId)
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        loadDetail()
    }

    // 8.1 — loads local data first, fetches from network if absent
    private fun loadDetail() {
        viewModelScope.launch {
            val detail = repository.getBookDetail(workId)
            _uiState.value = if (detail != null) {
                DetailUiState.Success(detail)
            } else {
                DetailUiState.Error(context.getString(R.string.detail_error_message))
            }
        }
    }

    // 8.4 — bookmark toggle
    fun toggleBookmark() {
        viewModelScope.launch {
            val current = isBookmarked.value
            val book = (_uiState.value as? DetailUiState.Success)?.book
            repository.setBookmarked(
                workId = workId,
                bookmarked = !current,
                title = book?.title ?: "",
                authors = book?.authorList ?: emptyList(),
                coverUrl = book?.coverUrl
            )
        }
    }

    // 8.3 — enforce 300-char max and auto-save
    fun onNoteChange(newNote: String) {
        viewModelScope.launch { repository.setNote(workId, newNote.take(NOTE_MAX_LENGTH)) }
    }
}
