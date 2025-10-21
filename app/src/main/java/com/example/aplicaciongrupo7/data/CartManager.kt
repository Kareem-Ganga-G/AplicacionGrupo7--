package com.example.aplicaciongrupo7.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addToCart(gameId: Int, quantity: Int = 1) {
        val currentQuantity = getProductQuantity(gameId)
        val newQuantity = currentQuantity + quantity

        if (newQuantity > 0) {
            sharedPreferences.edit().putInt("cart_$gameId", newQuantity).apply()
        }
    }

    fun removeFromCart(gameId: Int) {
        sharedPreferences.edit().remove("cart_$gameId").apply()
    }

    fun updateQuantity(gameId: Int, newQuantity: Int) {
        if (newQuantity > 0) {
            sharedPreferences.edit().putInt("cart_$gameId", newQuantity).apply()
        } else {
            removeFromCart(gameId)
        }
    }

    fun getProductQuantity(gameId: Int): Int {
        return sharedPreferences.getInt("cart_$gameId", 0)
    }

    fun getCartItems(allGames: List<Product>): List<CartItem> {
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
    }

    fun isProductInCart(gameId: Int): Boolean {
        return getProductQuantity(gameId) > 0
    }

    fun getCartTotal(allGames: List<Product>): Double {
        return getCartItems(allGames).sumOf { cartItem ->
            val priceString = cartItem.game.price
                .replace("$", "")
                .replace(".", "")
                .replace(",", ".")
            val price = priceString.toDoubleOrNull() ?: 0.0
            price * cartItem.quantity
        }
    }
}

data class CartItem(
    val game: Product,
    val quantity: Int
)