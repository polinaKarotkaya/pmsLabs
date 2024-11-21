package com.example.cars.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cars.entity.Car

@Dao
interface CarDao {
    @Insert
    suspend fun insertCar(car: Car)

    @Query("SELECT * FROM cars")
    suspend fun getAllCars(): List<Car>

    @Query("SELECT * FROM cars WHERE id = :carId")
    suspend fun getCarById(carId: Int): Car?

    @Query("DELETE FROM cars WHERE id = :carId")
    suspend fun deleteCar(carId: Int)
}
