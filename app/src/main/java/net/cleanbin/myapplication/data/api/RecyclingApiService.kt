package net.cleanbin.myapplication.data.api

import net.cleanbin.myapplication.data.model.RecyclingResult
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RecyclingApiService {

    @Multipart
    @POST("api/analyze")
    suspend fun analyzeImage(
        @Part image: MultipartBody.Part
    ): Response<RecyclingResult>
}
