package net.cleanbin.myapplication.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.cleanbin.myapplication.data.model.RecyclingResult
import net.cleanbin.myapplication.data.model.UiState
import net.cleanbin.myapplication.data.repository.RecyclingRepository

class RecyclingViewModel(
    private val repository: RecyclingRepository = RecyclingRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<RecyclingResult>>(UiState.Idle)
    val uiState: StateFlow<UiState<RecyclingResult>> = _uiState.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    fun setSelectedImage(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun analyzeImage(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository.analyzeImage(imageUri)
                .onSuccess { result ->
                    _uiState.value = UiState.Success(result)
                }
                .onFailure { exception ->
                    _uiState.value = UiState.Error(
                        exception.message ?: "알 수 없는 오류가 발생했습니다."
                    )
                }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Idle
        _selectedImageUri.value = null
    }
}
