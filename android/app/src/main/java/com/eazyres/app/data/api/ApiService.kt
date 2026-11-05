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

    @POST("auth/google")
    suspend fun googleLogin(
        @Body body: GoogleLoginRequest
    ): Response<AuthResponse>

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

    // Retrieve the logged-in user's saved properties
    @GET("favourites")
    suspend fun getFavourites(
        @Header("Authorization") token: String
    ): Response<FavouritesResponse>

    // Save a property
    @POST("favourites/{propertyId}")
    suspend fun saveFavourite(
        @Header("Authorization") token: String,
        @Path("propertyId") propertyId: String
    ): Response<FavouriteActionResponse>

    // Remove a saved property
    @DELETE("favourites/{propertyId}")
    suspend fun removeFavourite(
        @Header("Authorization") token: String,
        @Path("propertyId") propertyId: String
    ): Response<FavouriteActionResponse>
}
