package com.example.aplicaciongrupo7.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class CartManager(private val context: Context) {

    private val prefs =
        context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)

    private val products = mutableMapOf<Int, Product>()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _cartItemsCount = MutableStateFlow(0)
    val cartItemsCount: StateFlow<Int> = _cartItemsCount.asStateFlow()

    init {
        loadCart()
    }

    fun setProducts(productList: List<Product>) {
        products.clear()
        productList.forEach { products[it.id] = it }
    }

    fun addToCart(productId: Int, quantity: Int = 1) {
        val product = products[productId] ?: return

        val currentItems = _cartItems.value.toMutableList()
        val existing = currentItems.find { it.product.id == productId }

        if (existing != null) {
            if (existing.quantity + quantity <= product.stock) {
                currentItems[currentItems.indexOf(existing)] =
                    existing.copy(quantity = existing.quantity + quantity)
            }
        } else {
            currentItems.add(CartItem(product, quantity))
        }

        updateCart(currentItems)
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        val items = _cartItems.value.toMutableList()
        val index = items.indexOfFirst { it.product.id == productId }

        if (index != -1) {
            if (quantity <= 0) items.removeAt(index)
            else items[index] = items[index].copy(quantity = quantity)
        }

        updateCart(items)
    }

    fun removeFromCart(productId: Int) {
        updateCart(_cartItems.value.filterNot { it.product.id == productId })
    }

    fun clearCart() {
        updateCart(emptyList())
    }

    private fun updateCart(items: List<CartItem>) {
        _cartItems.value = items
        _cartItemsCount.value = items.sumOf { it.quantity }
        saveCart()
    }

    // 💾 GUARDAR
    private fun saveCart() {
        val json = JSONArray()
        _cartItems.value.forEach {
            val obj = JSONObject()
            obj.put("id", it.product.id)
            obj.put("qty", it.quantity)
            json.put(obj)
        }
        prefs.edit().putString("cart", json.toString()).apply()
    }

    // 🔁 CARGAR
    private fun loadCart() {
        val json = prefs.getString("cart", null) ?: return
        val array = JSONArray(json)
        val loaded = mutableListOf<CartItem>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val product = products[obj.getInt("id")] ?: continue
            loaded.add(CartItem(product, obj.getInt("qty")))
        }

        _cartItems.value = loaded
        _cartItemsCount.value = loaded.sumOf { it.quantity }
    }
}
