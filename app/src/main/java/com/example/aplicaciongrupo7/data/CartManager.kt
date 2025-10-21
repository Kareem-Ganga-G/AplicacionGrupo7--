package com.example.aplicaciongrupo7.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // StateFlow para observar los cambios del carrito
    private val _cartItemsCount = MutableStateFlow(getCartItemsCount())
    val cartItemsCount: StateFlow<Int> = _cartItemsCount.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(productId: Int, quantity: Int = 1) {
        val currentQuantity = getProductQuantity(productId)
        val newQuantity = currentQuantity + quantity

        if (newQuantity > 0) {
            sharedPreferences.edit().putInt("cart_$productId", newQuantity).apply()
        }
        updateCartState()
    }

    fun removeFromCart(productId: Int) {
        sharedPreferences.edit().remove("cart_$productId").apply()
        updateCartState()
    }

    fun updateQuantity(productId: Int, newQuantity: Int) {
        if (newQuantity > 0) {
            sharedPreferences.edit().putInt("cart_$productId", newQuantity).apply()
        } else {
            removeFromCart(productId)
        }
        updateCartState()
    }

    fun getProductQuantity(productId: Int): Int {
        return sharedPreferences.getInt("cart_$productId", 0)
    }

    fun getCartItems(allProducts: List<Product>): List<CartItem> {
        val cartItems = mutableListOf<CartItem>()
        val cartEntries = sharedPreferences.all.filterKeys { it.startsWith("cart_") }

        for ((key, value) in cartEntries) {
            if (value is Int) {
                val productId = key.removePrefix("cart_").toIntOrNull()
                val product = allProducts.find { it.id == productId }
                if (product != null && value > 0) {
                    cartItems.add(CartItem(product, value)) // Cambiado de game a product
                }
            }
        }
        return cartItems
    }

    fun getCartItemsCount(): Int {
        return sharedPreferences.all.values
            .filterIsInstance<Int>()
            .sum()
    }

    fun clearCart() {
        val keysToRemove = sharedPreferences.all.keys.filter { it.startsWith("cart_") }
        val editor = sharedPreferences.edit()
        keysToRemove.forEach { editor.remove(it) }
        editor.apply()
        updateCartState()
    }

    fun isProductInCart(productId: Int): Boolean {
        return getProductQuantity(productId) > 0
    }

    fun getCartTotal(allProducts: List<Product>): Double {
        return getCartItems(allProducts).sumOf { cartItem ->
            val priceString = cartItem.product.price // Cambiado de game a product
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