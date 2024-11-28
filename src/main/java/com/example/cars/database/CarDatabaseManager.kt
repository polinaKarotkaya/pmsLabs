import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

// Импортируйте ваши сущности
import com.example.cars.entity.Car
import com.example.cars.entity.User

class CarDatabaseManager(context: Context) {
    private val dbHelper = CarDatabaseHelper(context)
    private val db: SQLiteDatabase = dbHelper.writableDatabase

    fun insertCar(car: Car): Long {
        val values = ContentValues().apply {
            put(CarDatabaseHelper.COLUMN_MAKE, car.make)
            put(CarDatabaseHelper.COLUMN_MODEL, car.model)
            put(CarDatabaseHelper.COLUMN_PRICE, car.price)
            put(CarDatabaseHelper.COLUMN_IMAGE_RES_ID, car.imageResId)
            put(CarDatabaseHelper.COLUMN_OWNER, car.owner)
        }
        return db.insert(CarDatabaseHelper.TABLE_CARS, null, values)
    }

    fun updateCar(car: Car): Int {
        val values = ContentValues().apply {
            put(CarDatabaseHelper.COLUMN_MAKE, car.make)
            put(CarDatabaseHelper.COLUMN_MODEL, car.model)
            put(CarDatabaseHelper.COLUMN_PRICE, car.price)
            put(CarDatabaseHelper.COLUMN_IMAGE_RES_ID, car.imageResId)
            put(CarDatabaseHelper.COLUMN_OWNER, car.owner)
        }
        return db.update(CarDatabaseHelper.TABLE_CARS, values, "${CarDatabaseHelper.COLUMN_ID} = ?", arrayOf(car.id.toString()))
    }

    fun getAllCars(): List<Car> {
        val cars = mutableListOf<Car>()
        val cursor: Cursor = db.query(
            CarDatabaseHelper.TABLE_CARS,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                cars.add(Car(
                    id = getInt(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_ID)),
                    make = getString(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_MAKE)),
                    model = getString(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_MODEL)),
                    price = getDouble(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_PRICE)),
                    imageResId = getInt(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_IMAGE_RES_ID)),
                    owner = getString(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_OWNER))
                ))
            }
        }
        cursor.close()
        return cars
    }

    fun getCarsByOwner(owner: String): List<Car> {
        val cars = mutableListOf<Car>()
        val cursor: Cursor = db.query(
            CarDatabaseHelper.TABLE_CARS,
            null,
            "${CarDatabaseHelper.COLUMN_OWNER} = ?",
            arrayOf(owner),
            null,
            null,
            null
        )

        with(cursor) {
            while (moveToNext()) {
                cars.add(Car(
                    id = getInt(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_ID)),
                    make = getString(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_MAKE)),
                    model = getString(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_MODEL)),
                    price = getDouble(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_PRICE)),
                    imageResId = getInt(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_IMAGE_RES_ID)),
                    owner = getString(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_OWNER))
                ))
            }
        }
        cursor.close()
        return cars
    }

    fun insertUser(user: User): Long {
        val values = ContentValues().apply {
            put(CarDatabaseHelper.COLUMN_NICKNAME, user.nickname)
            put(CarDatabaseHelper.COLUMN_PASSWORD, user.password)
        }
        return db.insert(CarDatabaseHelper.TABLE_USERS, null, values)
    }

    fun getUserByNickname(nickname: String): User? {
        val cursor: Cursor = db.query(
            CarDatabaseHelper.TABLE_USERS,
            null,
            "${CarDatabaseHelper.COLUMN_NICKNAME} = ?",
            arrayOf(nickname),
            null,
            null,
            null
        )

        var user: User? = null
        with(cursor) {
            if (moveToFirst()) {
                user = User(
                    nickname = getString(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_NICKNAME)),
                    password = getString(getColumnIndexOrThrow(CarDatabaseHelper.COLUMN_PASSWORD))
                )
            }
        }
        cursor.close()
        return user
    }

    fun deleteCar(carId: Long): Int {
        return db.delete(CarDatabaseHelper.TABLE_CARS, "${CarDatabaseHelper.COLUMN_ID} = ?", arrayOf(carId.toString()))
    }
}
