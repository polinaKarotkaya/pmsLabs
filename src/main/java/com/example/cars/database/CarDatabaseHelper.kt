import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CarDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "car_database.db"
        const val DATABASE_VERSION = 1

        const val TABLE_CARS = "cars"
        const val COLUMN_ID = "id"
        const val COLUMN_MAKE = "make"
        const val COLUMN_MODEL = "model"
        const val COLUMN_PRICE = "price"
        const val COLUMN_IMAGE_RES_ID = "imageResId"
        const val COLUMN_OWNER = "owner"

        const val TABLE_USERS = "users"
        const val COLUMN_NICKNAME = "nickname"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createCarsTable = ("CREATE TABLE $TABLE_CARS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_MAKE TEXT,"
                + "$COLUMN_MODEL TEXT,"
                + "$COLUMN_PRICE REAL,"
                + "$COLUMN_IMAGE_RES_ID INTEGER,"
                + "$COLUMN_OWNER TEXT)")

        val createUsersTable = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_NICKNAME TEXT PRIMARY KEY,"
                + "$COLUMN_PASSWORD TEXT)")

        db.execSQL(createCarsTable)
        db.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CARS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }
}
