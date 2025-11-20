package com.example.aplicaciongrupo7.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartManager(private val context: Context) {
    private val databaseHelper = AppDatabaseHelper(context)

    // StateFlow para observar los cambios del carrito
    private val _cartItemsCount = MutableStateFlow(getCartItemsCount())
    val cartItemsCount: StateFlow<Int> = _cartItemsCount.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(productId: Int, quantity: Int = 1) {
        val db = databaseHelper.writableDatabase

        // Verificar si el producto ya está en el carrito
        val currentQuantity = getProductQuantity(productId)
        val newQuantity = currentQuantity + quantity

        val values = ContentValues().apply {
            put(DatabaseContract.CartEntry.COLUMN_PRODUCT_ID, productId)
            put(DatabaseContract.CartEntry.COLUMN_QUANTITY, newQuantity)
        }

        if (currentQuantity > 0) {
            // Actualizar cantidad existente
            db.update(
                DatabaseContract.CartEntry.TABLE_NAME,
                values,
                "${DatabaseContract.CartEntry.COLUMN_PRODUCT_ID} = ?",
                arrayOf(productId.toString())
            )
        } else {
            // Insertar nuevo producto
            db.insert(DatabaseContract.CartEntry.TABLE_NAME, null, values)
        }

        db.close()
        updateCartState()
    }

    fun removeFromCart(productId: Int) {
        val db = databaseHelper.writableDatabase
        db.delete(
            DatabaseContract.CartEntry.TABLE_NAME,
            "${DatabaseContract.CartEntry.COLUMN_PRODUCT_ID} = ?",
            arrayOf(productId.toString())
        )
        db.close()
        updateCartState()
    }

    fun updateQuantity(productId: Int, newQuantity: Int) {
        if (newQuantity > 0) {
            val db = databaseHelper.writableDatabase
            val values = ContentValues().apply {
                put(DatabaseContract.CartEntry.COLUMN_QUANTITY, newQuantity)
            }
            db.update(
                DatabaseContract.CartEntry.TABLE_NAME,
                values,
                "${DatabaseContract.CartEntry.COLUMN_PRODUCT_ID} = ?",
                arrayOf(productId.toString())
            )
            db.close()
        } else {
            removeFromCart(productId)
        }
        updateCartState()
    }

    fun getProductQuantity(productId: Int): Int {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseContract.CartEntry.TABLE_NAME,
            arrayOf(DatabaseContract.CartEntry.COLUMN_QUANTITY),
            "${DatabaseContract.CartEntry.COLUMN_PRODUCT_ID} = ?",
            arrayOf(productId.toString()),
            null, null, null
        )

        val quantity = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.CartEntry.COLUMN_QUANTITY))
        } else {
            0
        }

        cursor.close()
        db.close()
        return quantity
    }

    fun getCartItems(allProducts: List<Product>): List<CartItem> {
        val db = databaseHelper.readableDatabase
        val cartItems = mutableListOf<CartItem>()

        val cursor = db.query(
            DatabaseContract.CartEntry.TABLE_NAME,
            arrayOf(
                DatabaseContract.CartEntry.COLUMN_PRODUCT_ID,
                DatabaseContract.CartEntry.COLUMN_QUANTITY
            ),
            null, null, null, null, null
        )

        while (cursor.moveToNext()) {
            val productId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.CartEntry.COLUMN_PRODUCT_ID))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.CartEntry.COLUMN_QUANTITY))

            val product = allProducts.find { it.id == productId }
            if (product != null && quantity > 0) {
                cartItems.add(CartItem(product, quantity))
            }
        }

        cursor.close()
        db.close()
        return cartItems
    }

    fun getCartItemsCount(): Int {
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM(${DatabaseContract.CartEntry.COLUMN_QUANTITY}) as total FROM ${DatabaseContract.CartEntry.TABLE_NAME}",
            null
        )

        val total = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow("total"))
        } else {
            0
        }

        cursor.close()
        db.close()
        return total
    }

    fun clearCart() {
        val db = databaseHelper.writableDatabase
        db.delete(DatabaseContract.CartEntry.TABLE_NAME, null, null)
        db.close()
        updateCartState()
    }

    fun isProductInCart(productId: Int): Boolean {
        return getProductQuantity(productId) > 0
    }

    fun getCartTotal(allProducts: List<Product>): Double {
        return getCartItems(allProducts).sumOf { cartItem ->
            val priceString = cartItem.product.price
                .replace("$", "")
                .replace(".", "")
                .replace(",", ".")
            val price = priceString.toDoubleOrNull() ?: 0.0
            price * cartItem.quantity
        }
    }

    fun updateCartItems(allProducts: List<Product>) {
        _cartItems.value = getCartItems(allProducts)
        _cartItemsCount.value = getCartItemsCount()
    }

    private fun updateCartState() {
        _cartItemsCount.value = getCartItemsCount()
    }
}