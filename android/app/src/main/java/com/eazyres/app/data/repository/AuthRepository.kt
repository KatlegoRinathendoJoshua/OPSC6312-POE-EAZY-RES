package com.eazyres.app.data.repository

import com.eazyres.app.data.api.RetrofitClient
import com.eazyres.app.data.model.AuthResponse
import com.eazyres.app.data.model.LoginRequest
import com.eazyres.app.data.model.RegisterRequest
import com.eazyres.app.data.model.UpdateSettingsRequest

/**
 * A thin wrapper around the API calls related to authentication.
 * Keeping this separate from the Activities makes the network logic
 * easy to unit test in isolation (see app/src/test).
 */
class AuthRepository {

    private val api = RetrofitClient.api

    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val message: String) : Result<Nothing>()
    }

    suspend fun register(email: String, password: String, fullName: String, phone: String): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(email, password, fullName, phone))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.errorBody()?.string() ?: "Registration failed")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.errorBody()?.string() ?: "Invalid email or password")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun updateSettings(token: String, fullName: String, phone: String): Result<AuthResponse> {
        return try {
            val response = api.updateSettings("Bearer $token", UpdateSettingsRequest(fullName, phone))
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.errorBody()?.string() ?: "Could not update settings")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }
}
