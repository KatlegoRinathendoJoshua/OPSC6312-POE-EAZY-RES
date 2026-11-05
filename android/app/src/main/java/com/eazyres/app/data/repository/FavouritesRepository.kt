package com.eazyres.app.data.repository

import com.eazyres.app.data.api.RetrofitClient
import com.eazyres.app.data.local.SessionManager

class FavouriteRepository(
    private val sessionManager: SessionManager
) {

    private val api = RetrofitClient.api

    private fun getAuthToken(): String? {
        val token = sessionManager.getToken()
        return token?.let { "Bearer $it" }
    }

    suspend fun getFavourites(): PropertyRepository.Result<List<String>> {
        return try {
            val token = getAuthToken()
                ?: return PropertyRepository.Result.Error("Please log in first")

            val response = api.getFavourites(token)

            if (response.isSuccessful && response.body() != null) {
                PropertyRepository.Result.Success(
                    response.body()!!.propertyIds
                )
            } else {
                PropertyRepository.Result.Error("Could not load saved properties")
            }
        } catch (e: Exception) {
            PropertyRepository.Result.Error(
                e.localizedMessage ?: "Network error"
            )
        }
    }

    suspend fun saveFavourite(
        propertyId: String
    ): PropertyRepository.Result<String> {
        return try {
            val token = getAuthToken()
                ?: return PropertyRepository.Result.Error("Please log in first")

            val response = api.saveFavourite(token, propertyId)

            if (response.isSuccessful && response.body() != null) {
                PropertyRepository.Result.Success(response.body()!!.message)
            } else {
                val serverMessage = response.errorBody()?.string()
                PropertyRepository.Result.Error(
                    "Save failed (${response.code()}): ${serverMessage ?: "No details returned"}"
                )
            }
        } catch (e: Exception) {
            PropertyRepository.Result.Error(
                e.localizedMessage ?: "Network error"
            )
        }
    }

    suspend fun removeFavourite(
        propertyId: String
    ): PropertyRepository.Result<String> {
        return try {
            val token = getAuthToken()
                ?: return PropertyRepository.Result.Error("Please log in first")

            val response = api.removeFavourite(token, propertyId)

            if (response.isSuccessful && response.body() != null) {
                PropertyRepository.Result.Success(response.body()!!.message)
            } else {
                val serverMessage = response.errorBody()?.string()
                PropertyRepository.Result.Error(
                    "Save failed (${response.code()}): ${serverMessage ?: "No details returned"}"
                )
            }
        } catch (e: Exception) {
            PropertyRepository.Result.Error(
                e.localizedMessage ?: "Network error"
            )
        }
    }
}