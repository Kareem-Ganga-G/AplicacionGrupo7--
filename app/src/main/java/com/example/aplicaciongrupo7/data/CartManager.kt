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
    private val _cartItemsCount = MutableStateFlow(0)
    val cartItemsCount: StateFlow<Int> = _cartItemsCount.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Lista de productos para poder actualizar _cartItems
    private var currentProducts: List<Product> = emptyList()

    // Mantener una instancia única de la base de datos
    private val database: SQLiteDatabase by lazy {
        databaseHelper.writableDatabase
    }

    init {
        // Inicializar el estado al crear el manager
        updateCartState()
    }

    fun addToCart(productId: Int, quantity: Int = 1) {
        try {
            // Verificar si el producto ya está en el carrito
            val currentQuantity = getProductQuantity(productId)
            val newQuantity = currentQuantity + quantity

            val values = ContentValues().apply {
                put(DatabaseContract.CartEntry.COLUMN_PRODUCT_ID, productId)
                put(DatabaseContract.CartEntry.COLUMN_QUANTITY, newQuantity)
            }

            if (currentQuantity > 0) {
                // Actualizar cantidad existente
                database.update(
                    DatabaseContract.CartEntry.TABLE_NAME,
                    values,
                    "${DatabaseContract.CartEntry.COLUMN_PRODUCT_ID} = ?",
                    arrayOf(productId.toString())
                )
            } else {
                // Insertar nuevo producto
                database.insert(DatabaseContract.CartEntry.TABLE_NAME, null, values)
            }

            updateCartState()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeFromCart(productId: Int) {
        try {
            database.delete(
                DatabaseContract.CartEntry.TABLE_NAME,
                "${DatabaseContract.CartEntry.COLUMN_PRODUCT_ID} = ?",
                arrayOf(productId.toString())
            )
            updateCartState()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateQuantity(productId: Int, newQuantity: Int) {
        try {
            if (newQuantity > 0) {
                val values = ContentValues().apply {
                    put(DatabaseContract.CartEntry.COLUMN_QUANTITY, newQuantity)
                }
                database.update(
                    DatabaseContract.CartEntry.TABLE_NAME,
                    values,
                    "${DatabaseContract.CartEntry.COLUMN_PRODUCT_ID} = ?",
                    arrayOf(productId.toString())
                )
            } else {
                removeFromCart(productId)
            }
            updateCartState()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getProductQuantity(productId: Int): Int {
        var cursor: Cursor? = null
        return try {
            cursor = database.query(
                DatabaseContract.CartEntry.TABLE_NAME,
                arrayOf(DatabaseContract.CartEntry.COLUMN_QUANTITY),
                "${DatabaseContract.CartEntry.COLUMN_PRODUCT_ID} = ?",
                arrayOf(productId.toString()),
                null, null, null
            )

            if (cursor.moveToFirst()) {
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.CartEntry.COLUMN_QUANTITY))
            } else {
                0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        } finally {
            cursor?.close()
        }
    }

    fun getCartItems(allProducts: List<Product>): List<CartItem> {
        var cursor: Cursor? = null
        return try {
            val cartItems = mutableListOf<CartItem>()

            cursor = database.query(
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

            cartItems
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        } finally {
            cursor?.close()
        }
    }

    fun getCartItemsCount(): Int {
        var cursor: Cursor? = null
        return try {
            cursor = database.rawQuery(
                "SELECT SUM(${DatabaseContract.CartEntry.COLUMN_QUANTITY}) as total FROM ${DatabaseContract.CartEntry.TABLE_NAME}",
                null
            )

            if (cursor.moveToFirst()) {
                cursor.getInt(cursor.getColumnIndexOrThrow("total"))
            } else {
                0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        } finally {
            cursor?.close()
        }
    }

    fun clearCart() {
        try {
            database.delete(DatabaseContract.CartEntry.TABLE_NAME, null, null)
            updateCartState()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isProductInCart(productId: Int): Boolean {
        return getProductQuantity(productId) > 0
    }

    fun getCartTotal(allProducts: List<Product>): Double {
        return try {
            getCartItems(allProducts).sumOf { cartItem ->
                val priceString = cartItem.product.price
                    .replace("$", "")
                    .replace(".", "")
                    .replace(",", ".")
                val price = priceString.toDoubleOrNull() ?: 0.0
                price * cartItem.quantity
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    fun updateCartItems(allProducts: List<Product>) {
        currentProducts = allProducts
        _cartItems.value = getCartItems(allProducts)
        _cartItemsCount.value = getCartItemsCount()
    }

    private fun updateCartState() {
        _cartItemsCount.value = getCartItemsCount()
        // También actualizar _cartItems si tenemos los productos
        if (currentProducts.isNotEmpty()) {
            _cartItems.value = getCartItems(currentProducts)
        }
    }

    // Método para establecer los productos y actualizar el carrito
    fun setProducts(products: List<Product>) {
        currentProducts = products
        updateCartState()
    }

    // Método opcional para cerrar la BD cuando ya no se necesite
    fun close() {
        try {
            database.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}