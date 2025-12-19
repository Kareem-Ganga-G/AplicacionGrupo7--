package com.example.aplicaciongrupo7

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.aplicaciongrupo7.data.GameManager
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class GameManagerTest {

    @Test
    fun getGames_returnsPopulatedList() {
        // 1. Obtener el contexto de la aplicación
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 2. Instanciar el GameManager
        val gameManager = GameManager(context)

        // 3. Obtener los juegos
        val games = gameManager.getGames()

        // 4. Verificar que la lista NO esté vacía (gracias a los datos por defecto)
        Assert.assertTrue("La base de datos debería tener productos iniciales", games.isNotEmpty())

        // Opcional: Verificar que existe un producto específico
        val rtxProduct = games.find { it.title.contains("NVIDIA") }
        Assert.assertTrue("Debería existir la tarjeta NVIDIA", rtxProduct != null)
    }
}