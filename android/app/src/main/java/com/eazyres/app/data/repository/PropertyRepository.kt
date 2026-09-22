package com.eazyres.app.data.repository

import com.eazyres.app.data.api.RetrofitClient
import com.eazyres.app.data.model.Property

class PropertyRepository {

    private val api = RetrofitClient.api

    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val message: String) : Result<Nothing>()
    }

    suspend fun getProperties(): Result<List<Property>> {
        return try {
            val response = api.getProperties()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.properties)
            } else {
                Result.Error(response.errorBody()?.string() ?: "Could not load properties")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }

    suspend fun getPropertyById(id: String): Result<Property> {
        return try {
            val response = api.getPropertyById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.errorBody()?.string() ?: "Property not found")
            }
        } catch (e: Exception) {
            Result.Error(e.localizedMessage ?: "Network error")
        }
    }
}
