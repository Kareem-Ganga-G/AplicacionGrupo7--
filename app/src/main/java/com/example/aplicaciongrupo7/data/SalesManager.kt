package com.example.aplicaciongrupo7.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.text.SimpleDateFormat
import java.util.*

class SalesManager(private val context: Context) {
    private val databaseHelper = AppDatabaseHelper(context)

    fun addSale(sale: Sale) {
        val db = databaseHelper.writableDatabase

        // Insertar cada item de la venta
        sale.items.forEach { cartItem ->
            val values = ContentValues().apply {
                put(DatabaseContract.SalesEntry.COLUMN_PRODUCT_ID, cartItem.product.id)
                put(DatabaseContract.SalesEntry.COLUMN_QUANTITY, cartItem.quantity)
                put(DatabaseContract.SalesEntry.COLUMN_TOTAL, cartItem.product.price
                    .replace("$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .toDoubleOrNull() ?: 0.0 * cartItem.quantity)
                put(DatabaseContract.SalesEntry.COLUMN_SALE_DATE, System.currentTimeMillis())
                put(DatabaseContract.SalesEntry.COLUMN_USER_ID, sale.userId)
            }
            db.insert(DatabaseContract.SalesEntry.TABLE_NAME, null, values)
        }

        db.close()
    }

    fun getSales(): List<Sale> {
        val db = databaseHelper.readableDatabase
        val salesMap = mutableMapOf<Long, MutableList<CartItem>>()
        val sales = mutableListOf<Sale>()

        val cursor = db.query(
            DatabaseContract.SalesEntry.TABLE_NAME,
            null, null, null, null, null,
            "${DatabaseContract.SalesEntry.COLUMN_SALE_DATE} DESC"
        )

        // CORRECCIÓN: Usar GameManager correctamente
        val gameManager = GameManager(context)
        val allProducts = gameManager.getGames()

        while (cursor.moveToNext()) {
            val saleId = cursor.getLong(cursor.getColumnIndexOrThrow(android.provider.BaseColumns._ID))
            val productId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.SalesEntry.COLUMN_PRODUCT_ID))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.SalesEntry.COLUMN_QUANTITY))
            val total = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.SalesEntry.COLUMN_TOTAL))
            val saleDate = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseContract.SalesEntry.COLUMN_SALE_DATE))
            val userId = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.SalesEntry.COLUMN_USER_ID))

            val product = allProducts.find { it.id == productId }
            if (product != null) {
                val cartItem = CartItem(product, quantity)

                if (!salesMap.containsKey(saleDate)) {
                    salesMap[saleDate] = mutableListOf()
                }
                salesMap[saleDate]?.add(cartItem)
            }
        }

        cursor.close()
        db.close()

        // Convertir el mapa a lista de ventas
        salesMap.forEach { (saleDate, items) ->
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateString = dateFormatter.format(Date(saleDate))

            val total = items.sumOf { cartItem ->
                cartItem.product.price
                    .replace("$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .toDoubleOrNull() ?: 0.0 * cartItem.quantity
            }

            // Obtener el userId para esta venta
            val userIdCursor = db.query(
                DatabaseContract.SalesEntry.TABLE_NAME,
                arrayOf(DatabaseContract.SalesEntry.COLUMN_USER_ID),
                "${DatabaseContract.SalesEntry.COLUMN_SALE_DATE} = ?",
                arrayOf(saleDate.toString()),
                null, null, null,
                "1" // Limitar a 1 resultado
            )

            val saleUserId = if (userIdCursor.moveToFirst()) {
                userIdCursor.getString(userIdCursor.getColumnIndexOrThrow(DatabaseContract.SalesEntry.COLUMN_USER_ID))
            } else {
                "unknown"
            }
            userIdCursor.close()

            sales.add(Sale(
                id = saleDate,
                date = dateString,
                items = items,
                total = total,
                userId = saleUserId
            ))
        }

        return sales
    }

    fun getSalesByUser(userId: String): List<Sale> {
        return getSales().filter { it.userId == userId }
    }

    fun getTotalSales(): Double {
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(${DatabaseContract.SalesEntry.COLUMN_TOTAL}) FROM ${DatabaseContract.SalesEntry.TABLE_NAME}",
            null
        )

        val total = if (cursor.moveToFirst()) {
            cursor.getDouble(0)
        } else {
            0.0
        }

        cursor.close()
        db.close()
        return total
    }

    // NUEVO: Método para limpiar ventas (útil para testing)
    fun clearSales() {
        val db = databaseHelper.writableDatabase
        db.delete(DatabaseContract.SalesEntry.TABLE_NAME, null, null)
        db.close()
    }
}