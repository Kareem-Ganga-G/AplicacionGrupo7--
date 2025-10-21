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

    fun addToCart(gameId: Int, quantity: Int = 1) {
        val currentQuantity = getProductQuantity(gameId)
        val newQuantity = currentQuantity + quantity

        if (newQuantity > 0) {
            sharedPreferences.edit().putInt("cart_$gameId", newQuantity).apply()
        }
        // Actualizar StateFlow
        updateCartState()
    }

    fun removeFromCart(gameId: Int) {
        sharedPreferences.edit().remove("cart_$gameId").apply()
        // Actualizar StateFlow
        updateCartState()
    }

    fun updateQuantity(gameId: Int, newQuantity: Int) {
        if (newQuantity > 0) {
            sharedPreferences.edit().putInt("cart_$gameId", newQuantity).apply()
        } else {
            removeFromCart(gameId)
        }
        // Actualizar StateFlow
        updateCartState()
    }

    fun getProductQuantity(gameId: Int): Int {
        return sharedPreferences.getInt("cart_$gameId", 0)
    }

    fun getCartItems(allGames: List<Game>): List<CartItem> {
        val cartItems = mutableListOf<CartItem>()
        val cartEntries = sharedPreferences.all.filterKeys { it.startsWith("cart_") }

        for ((key, value) in cartEntries) {
            if (value is Int) {
                val gameId = key.removePrefix("cart_").toIntOrNull()
                val game = allGames.find { it.id == gameId }
                if (game != null && value > 0) {
                    cartItems.add(CartItem(game, value))
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
        // Actualizar StateFlow
        updateCartState()
    }

    fun isProductInCart(gameId: Int): Boolean {
        return getProductQuantity(gameId) > 0
    }

    fun getCartTotal(allGames: List<Game>): Double {
        return getCartItems(allGames).sumOf { cartItem ->
            val priceString = cartItem.game.price
                .replace("$", "")
                .replace(".", "")
                .replace(",", ".")
            val price = priceString.toDoubleOrNull() ?: 0.0
            price * cartItem.quantity
        }
    }

    // Función para actualizar el StateFlow cuando cambie el carrito
    fun updateCartItems(allGames: List<Game>) {
        _cartItems.value = getCartItems(allGames)
        _cartItemsCount.value = getCartItemsCount()
    }

    // Función privada para actualizar el estado
    private fun updateCartState() {
        _cartItemsCount.value = getCartItemsCount()
        // Nota: _cartItems se actualiza cuando se llama updateCartItems con la lista de juegos
    }
}