package net.cleanbin.myapplication.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.cleanbin.myapplication.data.api.RecyclingApiService
import net.cleanbin.myapplication.data.model.RecyclingResult
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

class RecyclingRepository(private val context: Context? = null) {

    companion object {
        private const val BASE_URL = "http://3.38.48.153:8001/"
    }

    private val apiService: RecyclingApiService by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecyclingApiService::class.java)
    }

    suspend fun analyzeImage(imageUri: Uri): Result<RecyclingResult> {
        return withContext(Dispatchers.IO) {
            try {
                context ?: return@withContext Result.failure(Exception("Context is null"))

                // URI를 파일로 변환
                val file = uriToFile(context, imageUri)

                // MultipartBody.Part 생성
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                // model 파라미터
                val modelBody = "gpt-4.1-mini".toRequestBody("text/plain".toMediaTypeOrNull())

                // API 호출
                val response = apiService.analyzeImage(body, modelBody)

                if (response.isSuccessful && response.body() != null) {
                    val visionResponse = response.body()!!
                    val result = visionResponse.result

                    // 카테고리 추론 (materials 기반)
                    val category = inferCategory(result.materials)

                    // image_url이 상대 경로인 경우 전체 URL로 변환
                    val fullImageUrl = result.imageUrl?.let { url ->
                        android.util.Log.d("RecyclingRepository", "Original image_url from server: $url")
                        val converted = if (url.startsWith("http://") || url.startsWith("https://")) {
                            url
                        } else {
                            // 상대 경로인 경우 BASE_URL과 결합
                            BASE_URL.trimEnd('/') + "/" + url.trimStart('/')
                        }
                        android.util.Log.d("RecyclingRepository", "Converted image URL: $converted")
                        converted
                    }

                    // 빈 값이 와도 기본값으로 처리하여 보여줌
                    val recyclingResult = RecyclingResult(
                        category = category,
                        itemName = result.items.firstOrNull()?.takeIf { it.isNotBlank() } ?: "물품 정보 없음",
                        materials = result.materials.takeIf { it.isNotEmpty() } ?: listOf("재질 정보 없음"),
                        details = result.details.takeIf { it.isNotEmpty() } ?: emptyList(),
                        method = result.disposalMethods.takeIf { it.isNotEmpty() }
                            ?.joinToString("\n\n")
                            ?: "분리수거 방법 정보를 확인할 수 없습니다.",
                        tip = result.tips?.firstOrNull() ?: generateTip(result.details),
                        imageUrl = fullImageUrl
                    )

                    android.util.Log.d("RecyclingRepository", "RecyclingResult created with imageUrl: ${recyclingResult.imageUrl}")

                    // 임시 파일 삭제
                    file.delete()

                    Result.success(recyclingResult)
                } else {
                    // API 호출이 실패해도 기본 결과를 반환
                    val defaultResult = RecyclingResult(
                        category = "GENERAL",
                        itemName = "분석 결과를 가져올 수 없음",
                        materials = listOf("정보 없음"),
                        details = emptyList(),
                        method = "서버 응답을 받지 못했습니다. 네트워크 연결을 확인하거나 나중에 다시 시도해주세요.",
                        tip = null
                    )
                    file.delete()
                    Result.success(defaultResult)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 예외가 발생해도 기본 결과를 반환
                val errorResult = RecyclingResult(
                    category = "GENERAL",
                    itemName = "분석 중 오류 발생",
                    materials = listOf("정보 없음"),
                    details = emptyList(),
                    method = "이미지 분석 중 오류가 발생했습니다.\n오류 내용: ${e.message ?: "알 수 없는 오류"}",
                    tip = "다시 시도하거나 다른 이미지를 사용해보세요."
                )
                Result.success(errorResult)
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        android.util.Log.d("RecyclingRepository", "Converting URI to file: $uri")

        val contentResolver = context.contentResolver
        val fileName = getFileName(context, uri)
        val tempFile = File(context.cacheDir, "upload_$fileName")

        // URI에서 직접 InputStream을 열어서 임시 파일로 복사
        try {
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    val bytesRead = input.copyTo(output)
                    android.util.Log.d("RecyclingRepository", "File copied: $bytesRead bytes")
                }
            } ?: throw Exception("InputStream을 열 수 없습니다: $uri")
        } catch (e: Exception) {
            android.util.Log.e("RecyclingRepository", "Failed to read URI: $uri", e)
            throw Exception("이미지를 읽을 수 없습니다: ${e.message}")
        }

        // 파일 크기 확인
        if (!tempFile.exists() || tempFile.length() == 0L) {
            throw Exception("파일이 비어있거나 생성되지 않았습니다")
        }

        android.util.Log.d("RecyclingRepository", "File created successfully: ${tempFile.length()} bytes")
        return tempFile
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var name = "temp_image.jpg"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    private fun inferCategory(materials: List<String>): String {
        val material = materials.firstOrNull()?.lowercase() ?: return "GENERAL"

        return when {
            material.contains("플라스틱") || material.contains("plastic") -> "PLASTIC"
            material.contains("종이") || material.contains("paper") -> "PAPER"
            material.contains("유리") || material.contains("glass") -> "GLASS"
            material.contains("캔") || material.contains("금속") || material.contains("metal") -> "CAN"
            material.contains("비닐") || material.contains("vinyl") -> "VINYL"
            material.contains("스티로폼") || material.contains("styrofoam") -> "STYROFOAM"
            material.contains("음식") || material.contains("food") -> "FOOD"
            else -> "GENERAL"
        }
    }

    private fun generateTip(details: List<String>): String? {
        // details에서 유용한 팁 생성
        val tips = mutableListOf<String>()

        details.forEach { detail ->
            when {
                detail.contains("라벨") || detail.contains("label") ->
                    tips.add("라벨을 제거하면 재활용률이 높아집니다!")
                detail.contains("깨끗") || detail.contains("세척") ->
                    tips.add("깨끗이 세척하여 배출하면 좋습니다!")
                detail.contains("압축") || detail.contains("찌그러짐") ->
                    tips.add("압축하여 배출하면 수거가 용이합니다!")
            }
        }

        return tips.firstOrNull()
    }
}
