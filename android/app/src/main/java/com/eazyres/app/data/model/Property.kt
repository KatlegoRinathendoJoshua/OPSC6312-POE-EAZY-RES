package com.eazyres.app.data.model

data class Property(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val price: Double,
    val propertyType: String,
    val description: String,
    val amenities: List<String>,
    val photoUrl: String?,
    val ratingAvg: Double,
    val isVerified: Boolean,
    val isNsfasApproved: Boolean,
    val availableRooms: Int
)
