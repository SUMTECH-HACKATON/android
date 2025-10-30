package net.cleanbin.myapplication.data.model

import com.google.gson.annotations.SerializedName

data class RecyclingResult(
    @SerializedName("category")
    val category: String,
    @SerializedName("itemName")
    val itemName: String,
    @SerializedName("method")
    val method: String,
    @SerializedName("tip")
    val tip: String? = null,
    @SerializedName("images")
    val images: List<String> = emptyList()
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
