package net.cleanbin.myapplication.data.model

import com.google.gson.annotations.SerializedName

// API 응답 전체 구조
data class VisionAnalyzeResponse(
    @SerializedName("result")
    val result: AnalysisResult
)

// 분석 결과
data class AnalysisResult(
    @SerializedName("items")
    val items: List<String>,
    @SerializedName("materials")
    val materials: List<String>,
    @SerializedName("details")
    val details: List<String>,
    @SerializedName("disposal_methods")
    val disposalMethods: List<String>
)

// UI에서 사용할 RecyclingResult (호환성 유지)
data class RecyclingResult(
    val category: String,
    val itemName: String,
    val materials: List<String>,
    val details: List<String>,
    val method: String,
    val tip: String? = null
)

enum class RecyclingCategory(val displayName: String, val color: Long) {
    PAPER("종이류", 0xFF4CAF50),
    PLASTIC("플라스틱", 0xFF2196F3),
    GLASS("유리", 0xFFFF9800),
    CAN("캔/금속", 0xFFF44336),
    VINYL("비닐", 0xFF9C27B0),
    STYROFOAM("스티로폼", 0xFF00BCD4),
    GENERAL("일반쓰레기", 0xFF757575),
    FOOD("음식물", 0xFF8BC34A)
}
