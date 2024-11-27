package com.example.cars.entity

import android.net.Uri

data class Car(
    val id: Int = 0,
    val make: String,
    val model: String,
    val price: Double,
    val imageResId: Int,
    val owner: String
)
