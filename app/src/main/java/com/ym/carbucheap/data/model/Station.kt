package com.ym.carbucheap.data.model

data class Station(
    val id: String,
    val name: String,
    val address: String,
    val city: String,
    val postalCode: String,
    val latitude: Double,
    val longitude: Double,
    val price: Double,
    val fuelType: String,
    val distance: Double, // in km
    val lastUpdate: String
)

