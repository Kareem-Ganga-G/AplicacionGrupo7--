package com.example.aplicaciongrupo7.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

/**
 * GameManager.kt
 * Maneja operaciones CRUD sobre la tabla products
 */

class GameManager(private val context: Context) {

    private val databaseHelper = AppDatabaseHelper(context)

    /**
     * Obtener todos los productos
     */
    fun getGames(): List<Product> {
        val db = databaseHelper.readableDatabase
        val products = mutableListOf<Product>()

        val cursor = db.query(
            DatabaseContract.ProductEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseContract.ProductEntry.COLUMN_ID} ASC"
        )

        while (cursor.moveToNext()) {
            products.add(cursorToProduct(cursor))
        }

        cursor.close()
        db.close()
        return products
    }

    /**
     * Agregar producto
     */
    fun addGame(product: Product) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.ProductEntry.COLUMN_TITLE, product.title)
            put(DatabaseContract.ProductEntry.COLUMN_GENRE, product.genre)
            put(DatabaseContract.ProductEntry.COLUMN_PRICE, product.price)
            put(DatabaseContract.ProductEntry.COLUMN_RATING, product.rating.toDouble())
            put(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION, product.description)
            put(DatabaseContract.ProductEntry.COLUMN_IMAGE_RES, product.imageRes)
            put(DatabaseContract.ProductEntry.COLUMN_STOCK, product.stock)
        }

        db.insert(DatabaseContract.ProductEntry.TABLE_NAME, null, values)
        db.close()
    }

    /**
     * Actualizar producto completo
     */
    fun updateGame(product: Product) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.ProductEntry.COLUMN_TITLE, product.title)
            put(DatabaseContract.ProductEntry.COLUMN_GENRE, product.genre)
            put(DatabaseContract.ProductEntry.COLUMN_PRICE, product.price)
            put(DatabaseContract.ProductEntry.COLUMN_RATING, product.rating.toDouble())
            put(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION, product.description)
            put(DatabaseContract.ProductEntry.COLUMN_IMAGE_RES, product.imageRes)
            put(DatabaseContract.ProductEntry.COLUMN_STOCK, product.stock)
        }

        db.update(
            DatabaseContract.ProductEntry.TABLE_NAME,
            values,
            "${DatabaseContract.ProductEntry.COLUMN_ID} = ?",
            arrayOf(product.id.toString())
        )

        db.close()
    }

    /**
     * Eliminar producto
     */
    fun deleteGame(id: Int) {
        val db = databaseHelper.writableDatabase
        db.delete(
            DatabaseContract.ProductEntry.TABLE_NAME,
            "${DatabaseContract.ProductEntry.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        db.close()
    }

    /**
     * 🔥 DESCONTAR STOCK AL COMPRAR
     * Se usa desde CartScreen
     */
    fun decreaseStock(productId: Int, quantity: Int) {
        val db = databaseHelper.writableDatabase

        db.execSQL(
            """
            UPDATE ${DatabaseContract.ProductEntry.TABLE_NAME}
            SET ${DatabaseContract.ProductEntry.COLUMN_STOCK} =
                ${DatabaseContract.ProductEntry.COLUMN_STOCK} - ?
            WHERE ${DatabaseContract.ProductEntry.COLUMN_ID} = ?
            """.trimIndent(),
            arrayOf(quantity, productId)
        )

        db.close()
    }

    /**
     * Convertir cursor a Product
     */
    private fun cursorToProduct(cursor: Cursor): Product {
        return Product(
            id = cursor.getInt(
                cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_ID)
            ),
            title = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_TITLE)
            ),
            genre = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_GENRE)
            ),
            price = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_PRICE)
            ),
            rating = cursor.getFloat(
                cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_RATING)
            ),
            description = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION)
            ),
            imageRes = cursor.getInt(
                cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_IMAGE_RES)
            ),
            stock = cursor.getInt(
                cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_STOCK)
            )
        )
    }
}
