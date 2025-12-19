// Archivo: CartManagerTest.kt (en carpeta 'androidTest')
package com.example.aplicaciongrupo7

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.aplicaciongrupo7.data.CartManager
import com.example.aplicaciongrupo7.data.Product
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartManagerTest {

    @Test
    fun addToCart_updatesFlowState() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val cartManager = CartManager(context)

        // 1. Limpiar carrito antes del test
        cartManager.clearCart()

        // 2. Configurar productos en el manager
        val testProduct = Product(1, "Test Game", "Genre", "100", 5.0f, "", 0, 10)
        cartManager.setProducts(listOf(testProduct))

        // 3. Agregar producto al carrito
        cartManager.addToCart(productId = 1, quantity = 2)

        val currentItems = cartManager.cartItems.first()
        val count = cartManager.cartItemsCount.first()

        // 5. Validaciones
        assertEquals("Debería haber 1 tipo de item en el carrito", 1, currentItems.size)
        assertEquals("La cantidad del producto debería ser 2", 2, currentItems[0].quantity)
        assertEquals("El contador total debería ser 2", 2, count)
    }
}