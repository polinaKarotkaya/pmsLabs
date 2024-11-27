package com.example.cars.entity

data class CarItem(
    val id: Int = 0,
    val userId: Int,
    val carId: Int,
    val status: String
)