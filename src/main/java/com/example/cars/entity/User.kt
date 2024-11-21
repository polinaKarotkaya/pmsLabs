package com.example.cars.entity

data class User(
    val id: Int = 0,
    val nickname: String,
    val password: String,
    val role: String
)