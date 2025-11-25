package com.example.aplicaciongrupo7.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.aplicaciongrupo7.R


class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DatabaseContract.DATABASE_NAME,
    null,
    DatabaseContract.DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        val createUsersTable = """
    CREATE TABLE ${DatabaseContract.UserEntry.TABLE_NAME} (
        ${DatabaseContract.ProductEntry.COLUMN_ID} INTEGER PRIMARY KEY,
        ${DatabaseContract.UserEntry.COLUMN_USERNAME} TEXT NOT NULL,
        ${DatabaseContract.UserEntry.COLUMN_EMAIL} TEXT NOT NULL UNIQUE,
        ${DatabaseContract.UserEntry.COLUMN_PASSWORD} TEXT NOT NULL,
        ${DatabaseContract.UserEntry.COLUMN_IS_ADMIN} INTEGER NOT NULL DEFAULT 0
    )
""".trimIndent()

        db.execSQL(createUsersTable)

        // Tabla de productos
        val createProductsTable = """
            CREATE TABLE ${DatabaseContract.ProductEntry.TABLE_NAME} (
                ${DatabaseContract.ProductEntry.COLUMN_ID} INTEGER PRIMARY KEY,
                ${DatabaseContract.ProductEntry.COLUMN_TITLE} TEXT,
                ${DatabaseContract.ProductEntry.COLUMN_GENRE} TEXT,
                ${DatabaseContract.ProductEntry.COLUMN_PRICE} TEXT,
                ${DatabaseContract.ProductEntry.COLUMN_RATING} REAL,
                ${DatabaseContract.ProductEntry.COLUMN_DESCRIPTION} TEXT,
                ${DatabaseContract.ProductEntry.COLUMN_IMAGE_RES} INTEGER,
                ${DatabaseContract.ProductEntry.COLUMN_STOCK} INTEGER
            )
        """.trimIndent()

        db.execSQL(createProductsTable)

        // Insertar productos por defecto
        insertDefaultProducts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.ProductEntry.TABLE_NAME}")
        onCreate(db)
    }

    private fun insertDefaultProducts(db: SQLiteDatabase) {
        // Lista de productos por defecto (ejemplos). Asegúrate que estos drawables existan en res/drawable
        val defaultProducts = listOf(
            Product(1, "AMD Ryzen 9 7950X", "Procesador", "$699.99", 4.8f, "16 núcleos, 4.5GHz", R.drawable.procesador_amd_ryzen9, 10),
            Product(2, "Intel Core i9-14900K", "Procesador", "$699.99", 4.7f, "24 núcleos, 6.0GHz", R.drawable.procesador_intel_i9, 10),
            Product(3, "AMD Ryzen 7 7800X3D", "Procesador", "$449.99", 4.6f, "8 núcleos, 5.0GHz", R.drawable.procesador_amd_ryzen7, 15),
            Product(4, "NVIDIA GeForce RTX 4090", "Tarjeta Gráfica", "$1599.99", 4.7f, "24GB GDDR6X", R.drawable.gpu_rtx4090, 5),
            Product(5, "AMD Radeon RX 7900 XTX", "Tarjeta Gráfica", "$999.99", 4.5f, "24GB GDDR6", R.drawable.gpu_amd_radeon, 8),
            Product(6, "NVIDIA GeForce RTX 4070 Ti", "Tarjeta Gráfica", "$799.99", 4.4f, "12GB GDDR6X", R.drawable.gpu_rtx4070, 12),
            Product(7, "Samsung Odyssey G9", "Monitor", "$1299.99", 4.8f, "49 Curvo 240Hz", R.drawable.monitor_samsung_odyssey, 7),
            Product(8, "ASUS ROG Swift PG279QM", "Monitor", "$899.99", 4.6f, "27 1440p 240Hz", R.drawable.monitor_asus_rog, 10),
            Product(9, "Alienware AW3423DW", "Monitor", "$1199.99", 4.9f, "34 OLED 175Hz", R.drawable.monitor_alienware, 6),
            Product(10, "Corsair Dominator Platinum", "Memoria RAM", "$299.99", 4.7f, "32GB DDR5 6000MHz", R.drawable.ram_corsair_dominator, 20),
            Product(11, "G.Skill Trident Z5 RGB", "Memoria RAM", "$249.99", 4.5f, "32GB DDR5 5600MHz", R.drawable.ram_gskill_trident, 25)
        )

        defaultProducts.forEach { product ->
            val values = ContentValues().apply {
                put(DatabaseContract.ProductEntry.COLUMN_ID, product.id)
                put(DatabaseContract.ProductEntry.COLUMN_TITLE, product.title)
                put(DatabaseContract.ProductEntry.COLUMN_GENRE, product.genre)
                put(DatabaseContract.ProductEntry.COLUMN_PRICE, product.price)
                put(DatabaseContract.ProductEntry.COLUMN_RATING, product.rating.toDouble())
                put(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION, product.description)
                put(DatabaseContract.ProductEntry.COLUMN_IMAGE_RES, product.imageRes) // <-- Int (R.drawable.xxx)
                put(DatabaseContract.ProductEntry.COLUMN_STOCK, product.stock)
            }
            db.insert(DatabaseContract.ProductEntry.TABLE_NAME, null, values)
        }
    }
}
