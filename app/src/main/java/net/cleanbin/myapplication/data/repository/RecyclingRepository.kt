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
            .baseUrl("http://3.38.48.153:8001/")
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

                    // RecyclingResult로 변환
                    val recyclingResult = RecyclingResult(
                        category = category,
                        itemName = result.items.firstOrNull() ?: "알 수 없는 물품",
                        materials = result.materials,
                        details = result.details,
                        method = result.disposalMethods.joinToString("\n\n"),
                        tip = generateTip(result.details)
                    )

                    // 임시 파일 삭제
                    file.delete()

                    Result.success(recyclingResult)
                } else {
                    Result.failure(Exception("API 호출 실패: ${response.code()} ${response.message()}"))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(Exception("이미지 분석 중 오류가 발생했습니다: ${e.message}"))
            }
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        // FileProvider URI인 경우 (카메라로 찍은 사진)
        if (uri.scheme == "content" && uri.authority?.contains("fileprovider") == true) {
            // URI의 path에서 실제 파일 경로 추출
            val path = uri.path
            if (path != null) {
                // FileProvider URI는 이미 캐시 디렉토리의 파일을 가리킴
                val fileName = path.substringAfterLast('/')
                val file = File(context.cacheDir, fileName)
                if (file.exists() && file.length() > 0) {
                    android.util.Log.d("RecyclingRepository", "Using existing camera file: ${file.length()} bytes")
                    return file
                }
            }
        }

        // 일반 content URI (갤러리 등)
        val contentResolver = context.contentResolver
        val fileName = getFileName(context, uri)
        val tempFile = File(context.cacheDir, "temp_$fileName")

        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                val bytesRead = input.copyTo(output)
                android.util.Log.d("RecyclingRepository", "File copied: $bytesRead bytes to ${tempFile.absolutePath}")
            }
        }

        // 파일 크기 확인
        if (!tempFile.exists() || tempFile.length() == 0L) {
            throw Exception("파일을 읽을 수 없습니다. URI: $uri, 파일 크기: ${tempFile.length()}")
        }

        android.util.Log.d("RecyclingRepository", "Final file size: ${tempFile.length()} bytes")

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
