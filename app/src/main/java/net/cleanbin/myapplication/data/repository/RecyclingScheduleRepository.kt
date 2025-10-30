package net.cleanbin.myapplication.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.cleanbin.myapplication.data.model.RecyclingType
import java.time.DayOfWeek

private val Context.scheduleDataStore: DataStore<Preferences> by preferencesDataStore(name = "recycling_schedule")

class RecyclingScheduleRepository(private val context: Context) {

    companion object {
        private val GENERAL_DAYS = stringPreferencesKey("general_days")
        private val RECYCLABLE_DAYS = stringPreferencesKey("recyclable_days")
        private val FOOD_DAYS = stringPreferencesKey("food_days")
        private val LARGE_DAYS = stringPreferencesKey("large_days")
        private val NOTIFICATION_ENABLED = stringPreferencesKey("notification_enabled")
        private val NOTIFICATION_TIME = stringPreferencesKey("notification_time")
    }

    suspend fun saveDaysForType(type: RecyclingType, days: List<DayOfWeek>) {
        val key = when (type) {
            RecyclingType.GENERAL -> GENERAL_DAYS
            RecyclingType.RECYCLABLE -> RECYCLABLE_DAYS
            RecyclingType.FOOD -> FOOD_DAYS
            RecyclingType.LARGE -> LARGE_DAYS
        }

        context.scheduleDataStore.edit { preferences ->
            preferences[key] = days.joinToString(",") { it.value.toString() }
        }
    }

    fun getDaysForType(type: RecyclingType): Flow<List<DayOfWeek>> {
        val key = when (type) {
            RecyclingType.GENERAL -> GENERAL_DAYS
            RecyclingType.RECYCLABLE -> RECYCLABLE_DAYS
            RecyclingType.FOOD -> FOOD_DAYS
            RecyclingType.LARGE -> LARGE_DAYS
        }

        return context.scheduleDataStore.data.map { preferences ->
            val daysString = preferences[key] ?: ""
            if (daysString.isEmpty()) {
                emptyList()
            } else {
                daysString.split(",").mapNotNull { dayValue ->
                    DayOfWeek.of(dayValue.toIntOrNull() ?: return@mapNotNull null)
                }
            }
        }
    }

    suspend fun setNotificationEnabled(enabled: Boolean) {
        context.scheduleDataStore.edit { preferences ->
            preferences[NOTIFICATION_ENABLED] = enabled.toString()
        }
    }

    fun isNotificationEnabled(): Flow<Boolean> {
        return context.scheduleDataStore.data.map { preferences ->
            preferences[NOTIFICATION_ENABLED]?.toBoolean() ?: false
        }
    }

    suspend fun setNotificationTime(hour: Int, minute: Int) {
        context.scheduleDataStore.edit { preferences ->
            preferences[NOTIFICATION_TIME] = "$hour:$minute"
        }
    }

    fun getNotificationTime(): Flow<Pair<Int, Int>> {
        return context.scheduleDataStore.data.map { preferences ->
            val timeString = preferences[NOTIFICATION_TIME] ?: "20:00"
            val parts = timeString.split(":")
            Pair(parts[0].toInt(), parts[1].toInt())
        }
    }
}
