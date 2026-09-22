package com.eazyres.app.data.api

import com.eazyres.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit definition of the EazyRes REST API.
 * Matches the endpoints documented in the Planning & Design document
 * (Section 5.2 / 5.3). Point BASE_URL (see RetrofitClient) at your
 * hosted backend, e.g. https://your-app.onrender.com/
 */
interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @PUT("users/settings")
    suspend fun updateSettings(
        @Header("Authorization") token: String,
        @Body body: UpdateSettingsRequest
    ): Response<AuthResponse>

    @GET("properties")
    suspend fun getProperties(
        @Query("city") city: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null
    ): Response<PropertiesResponse>

    @GET("properties/{id}")
    suspend fun getPropertyById(@Path("id") id: String): Response<Property>
}
