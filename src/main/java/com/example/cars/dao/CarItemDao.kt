package com.example.cars.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cars.entity.CarItem

@Dao
interface CarItemDao {
    @Insert
    suspend fun insertCarItem(carItem: CarItem)

    @Query("SELECT * FROM car_items WHERE userId = :userId")
    suspend fun getCarItemsByUserId(userId: Int): List<CarItem>

    @Query("SELECT * FROM car_items WHERE carId = :carId")
    suspend fun getCarItemsByCarId(carId: Int): List<CarItem>

    @Query("DELETE FROM car_items WHERE id = :carItemId")
    suspend fun deleteCarItemById(carItemId: Int)

    @Query("UPDATE car_items SET status = :status WHERE id = :carItemId")
    suspend fun updateCarItemStatus(carItemId: Int, status: String)
}
