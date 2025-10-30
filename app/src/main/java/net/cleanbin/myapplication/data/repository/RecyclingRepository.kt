package net.cleanbin.myapplication.data.repository

import android.net.Uri
import kotlinx.coroutines.delay
import net.cleanbin.myapplication.data.model.RecyclingResult

class RecyclingRepository {

    // 현재는 Mock 데이터를 반환, 나중에 실제 API 호출로 교체
    suspend fun analyzeImage(imageUri: Uri): Result<RecyclingResult> {
        return try {
            // 네트워크 호출 시뮬레이션
            delay(2000)

            // Mock 데이터
            val mockResult = RecyclingResult(
                category = "PLASTIC",
                itemName = "페트병",
                method = "1. 내용물을 비우고 물로 헹굽니다.\n" +
                        "2. 라벨을 제거합니다.\n" +
                        "3. 뚜껑을 분리합니다.\n" +
                        "4. 압축하여 플라스틱 전용 수거함에 배출합니다.",
                tip = "라벨과 뚜껑은 분리하여 배출하면 재활용률이 높아집니다!",
                images = listOf(
                    "https://via.placeholder.com/300x200/2196F3/FFFFFF?text=Step+1",
                    "https://via.placeholder.com/300x200/2196F3/FFFFFF?text=Step+2",
                    "https://via.placeholder.com/300x200/2196F3/FFFFFF?text=Step+3"
                )
            )

            Result.success(mockResult)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
