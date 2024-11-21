package com.example.cars.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cars.dao.CarItemDao
import com.example.cars.dao.UserDao
import com.example.cars.dao.CarDao
import com.example.cars.entity.CarItem
import com.example.cars.entity.User
import com.example.cars.entity.Car

@Database(entities = [CarItem::class, User::class, Car::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carItemDao(): CarItemDao
    abstract fun userDao(): UserDao
    abstract fun carDao(): CarDao
}
