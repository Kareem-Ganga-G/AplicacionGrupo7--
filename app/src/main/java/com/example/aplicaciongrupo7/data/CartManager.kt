package com.example.aplicaciongrupo7.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class CartManager(private val context: Context) {

    // Preferencias compartidas para guardar el carrito de forma persistente
    private val prefs =
        context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)

    // Mapa local de productos para acceso rápido por ID
    private val products = mutableMapOf<Int, Product>()

    // Estado observable de los items del carrito (para la UI)
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Estado observable del contador total de items
    private val _cartItemsCount = MutableStateFlow(0)
    val cartItemsCount: StateFlow<Int> = _cartItemsCount.asStateFlow()

    init {
        // ⚠️ IMPORTANTE: No cargamos el carrito aquí (init)
        // porque aún no tenemos la lista de productos cargada.
        // Lo haremos dentro de setProducts.
    }

    /**
     * Recibe la lista completa de productos desde la base de datos (GameManager).
     * Una vez recibida, carga el carrito guardado cruzando los IDs.
     */
    fun setProducts(productList: List<Product>) {
        products.clear()
        productList.forEach { products[it.id] = it }

        // ✅ AHORA SÍ: Cargamos el carrito porque ya tenemos los productos (imágenes, títulos, etc.)
        loadCart()
    }

    /**
     * Agrega un producto al carrito o incrementa su cantidad.
     */
    fun addToCart(productId: Int, quantity: Int = 1) {
        val product = products[productId] ?: return

        val currentItems = _cartItems.value.toMutableList()
        val existing = currentItems.find { it.product.id == productId }

        if (existing != null) {
            // Verificar stock antes de sumar
            if (existing.quantity + quantity <= product.stock) {
                val index = currentItems.indexOf(existing)
                currentItems[index] = existing.copy(quantity = existing.quantity + quantity)
            }
        } else {
            // Producto nuevo en el carrito
            currentItems.add(CartItem(product, quantity))
        }

        updateCart(currentItems)
    }

    /**
     * Actualiza la cantidad de un item específico.
     * Si la cantidad es 0 o menor, se elimina el item.
     */
    fun updateQuantity(productId: Int, quantity: Int) {
        val items = _cartItems.value.toMutableList()
        val index = items.indexOfFirst { it.product.id == productId }

        if (index != -1) {
            if (quantity <= 0) {
                items.removeAt(index)
            } else {
                items[index] = items[index].copy(quantity = quantity)
            }
        }

        updateCart(items)
    }

    /**
     * Elimina un producto del carrito completamente.
     */
    fun removeFromCart(productId: Int) {
        updateCart(_cartItems.value.filterNot { it.product.id == productId })
    }

    /**
     * Vacía todo el carrito.
     */
    fun clearCart() {
        updateCart(emptyList())
    }

    /**
     * Método centralizado para actualizar el estado (Flow) y guardar en persistencia.
     */
    private fun updateCart(items: List<CartItem>) {
        _cartItems.value = items
        _cartItemsCount.value = items.sumOf { it.quantity }
        saveCart()
    }

    // ==========================================
    // PERSISTENCIA (GUARDAR Y CARGAR)
    // ==========================================

    /**
     * Guarda la lista de items en SharedPreferences como un JSON String.
     * Solo guardamos ID y Cantidad para ahorrar espacio.
     */
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

    /**
     * Lee el JSON de SharedPreferences y reconstruye la lista de objetos CartItem.
     * Requiere que el mapa 'products' esté ya lleno.
     */
    private fun loadCart() {
        val jsonString = prefs.getString("cart", null) ?: return

        try {
            val array = JSONArray(jsonString)
            val loaded = mutableListOf<CartItem>()

            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.getInt("id")
                val qty = obj.getInt("qty")

                // Buscamos el producto real en el mapa para tener la IMAGEN y TÍTULO
                val product = products[id]

                if (product != null) {
                    loaded.add(CartItem(product, qty))
                }
            }

            _cartItems.value = loaded
            _cartItemsCount.value = loaded.sumOf { it.quantity }

        } catch (e: Exception) {
            e.printStackTrace()
            // Si hay error leyendo el JSON, limpiamos para evitar crashes futuros
            clearCart()
        }
    }
}