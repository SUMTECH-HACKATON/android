package net.cleanbin.myapplication.data.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val requiredCount: Int,
    val isUnlocked: Boolean = false,
    val currentProgress: Int = 0
) {
    val progress: Float
        get() = if (requiredCount > 0) currentProgress.toFloat() / requiredCount.toFloat() else 0f
}

enum class AchievementType {
    FIRST_ANALYSIS,           // 첫 분석
    EXPLORER,                 // 5번 분석
    RECYCLING_MASTER,         // 10번 분석
    EARTH_GUARDIAN,           // 20번 분석
    ENVIRONMENT_CHAMPION,     // 50번 분석
    PHOTOGRAPHER,             // 카메라로 5번 촬영
    ALBUM_COLLECTOR          // 갤러리에서 5번 선택
}

object AchievementDefinitions {
    fun getAllAchievements(): List<Achievement> = listOf(
        Achievement(
            id = "first_analysis",
            title = "🌱 첫 걸음",
            description = "첫 번째 분석을 완료했어요!",
            icon = "🌱",
            requiredCount = 1
        ),
        Achievement(
            id = "explorer",
            title = "🔍 탐험가",
            description = "5번의 분석을 완료했어요!",
            icon = "🔍",
            requiredCount = 5
        ),
        Achievement(
            id = "recycling_master",
            title = "♻️ 재활용 마스터",
            description = "10번의 분석을 완료했어요!",
            icon = "♻️",
            requiredCount = 10
        ),
        Achievement(
            id = "earth_guardian",
            title = "🌍 지구 지킴이",
            description = "20번의 분석을 완료했어요!",
            icon = "🌍",
            requiredCount = 20
        ),
        Achievement(
            id = "environment_champion",
            title = "🏆 환경 챔피언",
            description = "50번의 분석을 완료했어요!",
            icon = "🏆",
            requiredCount = 50
        ),
        Achievement(
            id = "photographer",
            title = "📸 사진작가",
            description = "카메라로 5번 촬영했어요!",
            icon = "📸",
            requiredCount = 5
        ),
        Achievement(
            id = "album_collector",
            title = "📱 앨범 수집가",
            description = "갤러리에서 5번 선택했어요!",
            icon = "📱",
            requiredCount = 5
        )
    )
}
