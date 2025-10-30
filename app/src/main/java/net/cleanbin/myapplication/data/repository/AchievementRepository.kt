package net.cleanbin.myapplication.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.cleanbin.myapplication.data.model.Achievement
import net.cleanbin.myapplication.data.model.AchievementDefinitions

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "achievements")

class AchievementRepository(private val context: Context) {

    companion object {
        private val TOTAL_ANALYSES_KEY = intPreferencesKey("total_analyses")
        private val CAMERA_USES_KEY = intPreferencesKey("camera_uses")
        private val GALLERY_USES_KEY = intPreferencesKey("gallery_uses")
        private val UNLOCKED_ACHIEVEMENTS_KEY = stringPreferencesKey("unlocked_achievements")
    }

    suspend fun incrementAnalysisCount() {
        context.dataStore.edit { preferences ->
            val current = preferences[TOTAL_ANALYSES_KEY] ?: 0
            preferences[TOTAL_ANALYSES_KEY] = current + 1
        }
    }

    suspend fun incrementCameraUses() {
        context.dataStore.edit { preferences ->
            val current = preferences[CAMERA_USES_KEY] ?: 0
            preferences[CAMERA_USES_KEY] = current + 1
        }
    }

    suspend fun incrementGalleryUses() {
        context.dataStore.edit { preferences ->
            val current = preferences[GALLERY_USES_KEY] ?: 0
            preferences[GALLERY_USES_KEY] = current + 1
        }
    }

    suspend fun unlockAchievement(achievementId: String) {
        context.dataStore.edit { preferences ->
            val unlocked = preferences[UNLOCKED_ACHIEVEMENTS_KEY]?.split(",")?.toMutableSet() ?: mutableSetOf()
            unlocked.add(achievementId)
            preferences[UNLOCKED_ACHIEVEMENTS_KEY] = unlocked.joinToString(",")
        }
    }

    fun getAchievements(): Flow<List<Achievement>> {
        return context.dataStore.data.map { preferences ->
            val totalAnalyses = preferences[TOTAL_ANALYSES_KEY] ?: 0
            val cameraUses = preferences[CAMERA_USES_KEY] ?: 0
            val galleryUses = preferences[GALLERY_USES_KEY] ?: 0
            val unlockedIds = preferences[UNLOCKED_ACHIEVEMENTS_KEY]?.split(",")?.toSet() ?: emptySet()

            AchievementDefinitions.getAllAchievements().map { achievement ->
                val currentProgress = when (achievement.id) {
                    "first_analysis", "explorer", "recycling_master", "earth_guardian", "environment_champion" -> totalAnalyses
                    "photographer" -> cameraUses
                    "album_collector" -> galleryUses
                    else -> 0
                }

                achievement.copy(
                    currentProgress = currentProgress,
                    isUnlocked = unlockedIds.contains(achievement.id) || currentProgress >= achievement.requiredCount
                )
            }
        }
    }

    fun getTotalAnalysisCount(): Flow<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[TOTAL_ANALYSES_KEY] ?: 0
        }
    }
}
