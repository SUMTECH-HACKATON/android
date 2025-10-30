package net.cleanbin.myapplication.data.model

import java.time.DayOfWeek

data class RecyclingSchedule(
    val days: Map<RecyclingType, List<DayOfWeek>> = emptyMap()
)

enum class RecyclingType(val displayName: String, val emoji: String) {
    GENERAL("일반 쓰레기", "🗑️"),
    RECYCLABLE("재활용", "♻️"),
    FOOD("음식물", "🥬"),
    LARGE("대형 폐기물", "📦")
}
