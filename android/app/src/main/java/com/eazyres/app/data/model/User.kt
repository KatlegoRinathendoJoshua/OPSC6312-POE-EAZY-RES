package com.eazyres.app.data.model

/**
 * Represents a user as returned by the API. Note: the password is NEVER
 * part of this model — the backend hashes it with bcrypt and never sends
 * it back, and the app never stores it in plain form either.
 */
data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val phone: String?,
    val userType: String // "student" or "landlord"
)
