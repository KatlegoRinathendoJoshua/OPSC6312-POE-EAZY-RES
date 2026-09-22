package com.eazyres.app.data.model

// --- Requests ---

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val userType: String = "student"
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UpdateSettingsRequest(
    val fullName: String,
    val phone: String
)

// --- Responses ---

data class AuthResponse(
    val status: String,
    val token: String,
    val user: User
)

data class ApiMessageResponse(
    val status: String,
    val message: String? = null
)

data class PropertiesResponse(
    val status: String,
    val properties: List<Property>
)
