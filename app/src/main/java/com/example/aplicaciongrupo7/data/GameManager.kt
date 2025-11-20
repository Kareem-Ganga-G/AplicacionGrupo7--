package com.example.aplicaciongrupo7.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.aplicaciongrupo7.R

class GameManager(private val context: Context) {
    private val databaseHelper = AppDatabaseHelper(context)

    fun getGames(): List<Product> {
        val db = databaseHelper.readableDatabase
        val products = mutableListOf<Product>()

        val cursor = db.query(
            DatabaseContract.ProductEntry.TABLE_NAME,
            null, null, null, null, null,
            "${DatabaseContract.ProductEntry.COLUMN_ID} ASC"
        )

        while (cursor.moveToNext()) {
            val product = cursorToProduct(cursor)
            products.add(product)
        }

        cursor.close()
        db.close()
        return products
    }

    fun addGame(game: Product) {
        val db = databaseHelper.writableDatabase

        // Obtener el siguiente ID disponible
        val nextId = getNextProductId(db)
        val newGame = game.copy(id = nextId)

        val values = ContentValues().apply {
            put(DatabaseContract.ProductEntry.COLUMN_ID, newGame.id)
            put(DatabaseContract.ProductEntry.COLUMN_TITLE, newGame.title)
            put(DatabaseContract.ProductEntry.COLUMN_GENRE, newGame.genre)
            put(DatabaseContract.ProductEntry.COLUMN_PRICE, newGame.price)
            put(DatabaseContract.ProductEntry.COLUMN_RATING, newGame.rating.toDouble())
            put(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION, newGame.description)
            put(DatabaseContract.ProductEntry.COLUMN_IMAGE_RES, newGame.imageRes)
            put(DatabaseContract.ProductEntry.COLUMN_STOCK, newGame.stock)
        }

        db.insert(DatabaseContract.ProductEntry.TABLE_NAME, null, values)
        db.close()
    }

    fun updateGame(updatedGame: Product) {
        val db = databaseHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseContract.ProductEntry.COLUMN_TITLE, updatedGame.title)
            put(DatabaseContract.ProductEntry.COLUMN_GENRE, updatedGame.genre)
            put(DatabaseContract.ProductEntry.COLUMN_PRICE, updatedGame.price)
            put(DatabaseContract.ProductEntry.COLUMN_RATING, updatedGame.rating.toDouble())
            put(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION, updatedGame.description)
            put(DatabaseContract.ProductEntry.COLUMN_IMAGE_RES, updatedGame.imageRes)
            put(DatabaseContract.ProductEntry.COLUMN_STOCK, updatedGame.stock)
        }

        db.update(
            DatabaseContract.ProductEntry.TABLE_NAME,
            values,
            "${DatabaseContract.ProductEntry.COLUMN_ID} = ?",
            arrayOf(updatedGame.id.toString())
        )
        db.close()
    }

    fun deleteGame(gameId: Int) {
        val db = databaseHelper.writableDatabase
        db.delete(
            DatabaseContract.ProductEntry.TABLE_NAME,
            "${DatabaseContract.ProductEntry.COLUMN_ID} = ?",
            arrayOf(gameId.toString())
        )
        db.close()
    }

    fun getGameById(gameId: Int): Product? {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseContract.ProductEntry.TABLE_NAME,
            null,
            "${DatabaseContract.ProductEntry.COLUMN_ID} = ?",
            arrayOf(gameId.toString()),
            null, null, null
        )

        val product = if (cursor.moveToFirst()) {
            cursorToProduct(cursor)
        } else {
            null
        }

        cursor.close()
        db.close()
        return product
    }

    fun updateStock(gameId: Int, newStock: Int) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseContract.ProductEntry.COLUMN_STOCK, newStock)
        }

        db.update(
            DatabaseContract.ProductEntry.TABLE_NAME,
            values,
            "${DatabaseContract.ProductEntry.COLUMN_ID} = ?",
            arrayOf(gameId.toString())
        )
        db.close()
    }

    private fun getNextProductId(db: SQLiteDatabase): Int {
        val cursor = db.rawQuery(
            "SELECT MAX(${DatabaseContract.ProductEntry.COLUMN_ID}) FROM ${DatabaseContract.ProductEntry.TABLE_NAME}",
            null
        )

        val maxId = if (cursor.moveToFirst() && !cursor.isNull(0)) {
            cursor.getInt(0)
        } else {
            0
        }

        cursor.close()
        return maxId + 1
    }

    private fun cursorToProduct(cursor: Cursor): Product {
        return Product(
            id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_ID)),
            title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_TITLE)),
            genre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_GENRE)),
            price = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_PRICE)),
            rating = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_RATING)),
            description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_DESCRIPTION)),
            imageRes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_IMAGE_RES)),
            stock = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.ProductEntry.COLUMN_STOCK))
        )
    }
}