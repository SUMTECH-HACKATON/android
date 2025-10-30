package net.cleanbin.myapplication.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.cleanbin.myapplication.data.model.Achievement
import net.cleanbin.myapplication.data.model.RecyclingResult
import net.cleanbin.myapplication.data.model.UiState
import net.cleanbin.myapplication.data.repository.AchievementRepository
import net.cleanbin.myapplication.data.repository.RecyclingRepository

class RecyclingViewModel : ViewModel() {

    private var repository: RecyclingRepository? = null
    private var achievementRepository: AchievementRepository? = null

    private val _uiState = MutableStateFlow<UiState<RecyclingResult>>(UiState.Idle)
    val uiState: StateFlow<UiState<RecyclingResult>> = _uiState.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _totalAnalysisCount = MutableStateFlow(0)
    val totalAnalysisCount: StateFlow<Int> = _totalAnalysisCount.asStateFlow()

    fun initializeAchievementRepository(context: Context) {
        if (achievementRepository == null) {
            achievementRepository = AchievementRepository(context)
            loadAchievements()
        }
        if (repository == null) {
            repository = RecyclingRepository(context)
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            achievementRepository?.getAchievements()?.collect { achievements ->
                _achievements.value = achievements
            }
        }

        viewModelScope.launch {
            achievementRepository?.getTotalAnalysisCount()?.collect { count ->
                _totalAnalysisCount.value = count
            }
        }
    }

    fun setSelectedImage(uri: Uri) {
        _selectedImageUri.value = uri
    }

    fun analyzeImage(imageUri: Uri, isFromCamera: Boolean = false) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            repository?.analyzeImage(imageUri)
                ?.onSuccess { result ->
                    _uiState.value = UiState.Success(result)

                    // 업적 진행도 업데이트
                    achievementRepository?.let { repo ->
                        repo.incrementAnalysisCount()
                        if (isFromCamera) {
                            repo.incrementCameraUses()
                        } else {
                            repo.incrementGalleryUses()
                        }
                    }
                }
                ?.onFailure { exception ->
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
