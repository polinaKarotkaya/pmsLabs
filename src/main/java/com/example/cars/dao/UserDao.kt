package com.example.cars.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cars.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE nickname = :nickname AND password = :password")
    suspend fun getUser(nickname: String, password: String): User?

    @Query("SELECT role FROM users WHERE nickname = :nickname")
    suspend fun getUserRole(nickname: String): String?

    @Query("SELECT id FROM users WHERE nickname = :nickname")
    suspend fun getUserIdByNickname(nickname: String): Int?
}
