package com.example.aplicaciongrupo7

import com.example.aplicaciongrupo7.data.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductTest {
    @Test
    fun productModel_storesDataCorrectly() {
        // 1. Crear un producto simulado
        val product = Product(
            id = 99,
            title = "Juego de Prueba",
            genre = "Acción",
            price = "$50.00",
            rating = 4.5f,
            description = "Descripción test",
            imageRes = 100,
            stock = 10
        )

        // 2. Verificar que los datos son accesibles
        assertEquals(99, product.id)
        assertEquals("Juego de Prueba", product.title)
        assertEquals(10, product.stock)
    }
}