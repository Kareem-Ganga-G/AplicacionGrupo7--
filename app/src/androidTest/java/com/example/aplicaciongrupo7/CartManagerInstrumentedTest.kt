package com.example.aplicaciongrupo7

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.aplicaciongrupo7.data.CartManager
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartManagerInstrumentedTest {

    private lateinit var context: Context
    private lateinit var cartManager: CartManager

    // Productos de prueba: formato de precio esperado por la lógica del proyecto:
    // - incluye el signo '$'
    // - usa la coma como separador decimal (ej: "$10,50")
    // - puede tener puntos como separador de miles (ej: "$1.000,00")
    private val products = listOf(
        Product(id = 1, title = "Producto1", genre = "G1", price = "$10,00", rating = 0.0f, description = "", imageRes = 0, stock = 10),
        Product(id = 2, title = "Producto2", genre = "G2", price = "$5,50", rating = 0.0f, description = "", imageRes = 0, stock = 5)
    )

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        cartManager = CartManager(context)
        // Usar los productos y limpiar carrito
        cartManager.setProducts(products)
        cartManager.clearCart()
    }

    @Test
    fun testAddRemoveAndTotal() {
        // Añadir 2 unidades del producto 1 y 1 unidad del producto 2
        cartManager.addToCart(productId = 1, quantity = 2)
        cartManager.addToCart(productId = 2, quantity = 1)

        // Comprobar cantidad total en carrito (2 + 1 = 3)
        val count = cartManager.getCartItemsCount()
        assertEquals("La suma de cantidades en el carrito debe ser 3", 3, count)

        // Calcular total esperado: 2 * 10.00 + 1 * 5.50 = 25.50
        val total = cartManager.getCartTotal(products)
        // comparar con un margen pequeño por si acaso
        assertEquals(25.50, total, 0.001)

        // Eliminar producto 1 y comprobar total/contador
        cartManager.removeFromCart(productId = 1)
        val newCount = cartManager.getCartItemsCount()
        assertEquals("Luego de eliminar el producto 1, debe quedar 1 item en cantidad total", 1, newCount)

        val newTotal = cartManager.getCartTotal(products)
        assertEquals(5.50, newTotal, 0.001)
    }

    @After
    fun teardown() {
        cartManager.clearCart()
        cartManager.close()
    }
}
