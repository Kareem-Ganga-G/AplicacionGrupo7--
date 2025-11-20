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
        // Tabla de productos
        val createProductTable = """
            CREATE TABLE ${DatabaseContract.ProductEntry.TABLE_NAME} (
                ${DatabaseContract.ProductEntry.COLUMN_ID} INTEGER PRIMARY KEY,
                ${DatabaseContract.ProductEntry.COLUMN_TITLE} TEXT NOT NULL,
                ${DatabaseContract.ProductEntry.COLUMN_GENRE} TEXT NOT NULL,
                ${DatabaseContract.ProductEntry.COLUMN_PRICE} TEXT NOT NULL,
                ${DatabaseContract.ProductEntry.COLUMN_RATING} REAL NOT NULL,
                ${DatabaseContract.ProductEntry.COLUMN_DESCRIPTION} TEXT,
                ${DatabaseContract.ProductEntry.COLUMN_IMAGE_RES} INTEGER NOT NULL,
                ${DatabaseContract.ProductEntry.COLUMN_STOCK} INTEGER DEFAULT 0
            )
        """.trimIndent()

        // Tabla de usuarios
        val createUserTable = """
            CREATE TABLE ${DatabaseContract.UserEntry.TABLE_NAME} (
                ${android.provider.BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.UserEntry.COLUMN_USERNAME} TEXT UNIQUE NOT NULL,
                ${DatabaseContract.UserEntry.COLUMN_EMAIL} TEXT UNIQUE NOT NULL,
                ${DatabaseContract.UserEntry.COLUMN_PASSWORD} TEXT NOT NULL,
                ${DatabaseContract.UserEntry.COLUMN_IS_ADMIN} INTEGER DEFAULT 0
            )
        """.trimIndent()

        // Tabla del carrito
        val createCartTable = """
            CREATE TABLE ${DatabaseContract.CartEntry.TABLE_NAME} (
                ${android.provider.BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.CartEntry.COLUMN_PRODUCT_ID} INTEGER UNIQUE NOT NULL,
                ${DatabaseContract.CartEntry.COLUMN_QUANTITY} INTEGER NOT NULL,
                ${DatabaseContract.CartEntry.COLUMN_ADDED_DATE} INTEGER DEFAULT (strftime('%s','now'))
            )
        """.trimIndent()

        // Tabla de ventas
        val createSalesTable = """
            CREATE TABLE ${DatabaseContract.SalesEntry.TABLE_NAME} (
                ${android.provider.BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.SalesEntry.COLUMN_PRODUCT_ID} INTEGER NOT NULL,
                ${DatabaseContract.SalesEntry.COLUMN_QUANTITY} INTEGER NOT NULL,
                ${DatabaseContract.SalesEntry.COLUMN_TOTAL} REAL NOT NULL,
                ${DatabaseContract.SalesEntry.COLUMN_SALE_DATE} INTEGER DEFAULT (strftime('%s','now')),
                ${DatabaseContract.SalesEntry.COLUMN_USER_ID} TEXT
            )
        """.trimIndent()

        db.execSQL(createProductTable)
        db.execSQL(createUserTable)
        db.execSQL(createCartTable)
        db.execSQL(createSalesTable)

        // Insertar productos por defecto
        insertDefaultProducts(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.ProductEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.UserEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.CartEntry.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.SalesEntry.TABLE_NAME}")
        onCreate(db)
    }

    private fun insertDefaultProducts(db: SQLiteDatabase) {
        val defaultProducts = listOf(
            Product(1, "AMD Ryzen 9 7950X", "Procesador", "\$699.990", 4.8f, "16 núcleos, 4.5GHz", R.drawable.procesador_amd_ryzen9, 10),
            Product(2, "Intel Core i9-14900K", "Procesador", "\$589.990", 4.6f, "24 núcleos, 6.0GHz", R.drawable.procesador_intel_i9, 10),
            Product(3, "AMD Ryzen 7 7800X3D", "Procesador", "\$449.990", 4.9f, "8 núcleos, 5.0GHz", R.drawable.procesador_amd_ryzen7, 15),
            Product(4, "NVIDIA GeForce RTX 4090", "Tarjeta Gráfica", "\$1599.990", 4.7f, "24GB GDDR6X", R.drawable.gpu_rtx4090, 5),
            Product(5, "AMD Radeon RX 7900 XTX", "Tarjeta Gráfica", "\$999.990", 4.5f, "24GB GDDR6", R.drawable.gpu_amd_radeon, 8),
            Product(6, "NVIDIA GeForce RTX 4070 Ti", "Tarjeta Gráfica", "\$799.990", 4.4f, "12GB GDDR6X", R.drawable.gpu_rtx4070, 12),
            Product(7, "Samsung Odyssey G9", "Monitor", "\$1299.990", 4.8f, "49 Curvo 240Hz", R.drawable.monitor_samsung_odyssey, 7),
            Product(8, "ASUS ROG Swift PG279QM", "Monitor", "\$899.990", 4.6f, "27 1440p 240Hz", R.drawable.monitor_asus_rog, 10),
            Product(9, "Alienware AW3423DW", "Monitor", "\$1199.990", 4.9f, "34 OLED 175Hz", R.drawable.monitor_alienware, 6),
            Product(10, "Corsair Dominator Platinum", "Memoria RAM", "\$249.990", 4.7f, "32GB DDR5 6000MHz", R.drawable.ram_corsair_dominator, 20),
            Product(11, "G.Skill Trident Z5 RGB", "Memoria RAM", "\$199.990", 4.5f, "32GB DDR5 5600MHz", R.drawable.ram_gskill_trident, 25)
        )

        defaultProducts.forEach { product ->
            val values = ContentValues().apply {
                put(DatabaseContract.ProductEntry.COLUMN_ID, product.id)
                put(DatabaseContract.ProductEntry.COLUMN_TITLE, product.title)
                put(DatabaseContract.ProductEntry.COLUMN_GENRE, product.genre)
                put(DatabaseContract.ProductEntry.COLUMN_PRICE, product.price)
                put(DatabaseContract.ProductEntry.COLUMN_RATING, product.rating.toDouble())
                put(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION, product.description)
                put(DatabaseContract.ProductEntry.COLUMN_IMAGE_RES, product.imageRes)
                put(DatabaseContract.ProductEntry.COLUMN_STOCK, product.stock)
            }
            db.insert(DatabaseContract.ProductEntry.TABLE_NAME, null, values)
        }
    }
}