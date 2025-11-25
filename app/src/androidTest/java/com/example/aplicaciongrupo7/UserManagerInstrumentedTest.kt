package com.example.aplicaciongrupo7

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.aplicaciongrupo7.data.User
import com.example.aplicaciongrupo7.data.UserManager
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserManagerInstrumentedTest {

    private lateinit var context: Context
    private lateinit var userManager: UserManager

    private val testUsername = "testuser"
    private val testEmail = "testuser@example.com"
    private val testPassword = "password123"

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        userManager = UserManager(context)
        // Asegurarnos de partir limpio (elimino si existiera)
        try { userManager.deleteUser(testUsername) } catch (_: Exception) {}
        try { userManager.deleteUser("otheruser") } catch (_: Exception) {}
    }

    @Test
    fun testRegisterAndDuplicatePrevention() {
        val user = User(username = testUsername, password = testPassword, email = testEmail, isAdmin = false)

        // Registro exitoso la primera vez
        val savedFirst = userManager.saveUser(user)
        assertTrue("El primer registro debe devolver true", savedFirst)

        // Registrar mismo usuario debe fallar (username duplicado)
        val savedSecond = userManager.saveUser(user)
        assertFalse("Registrar el mismo usuario debe devolver false", savedSecond)

        // Registrar diferente username pero mismo email debe fallar (email duplicado)
        val another = User(username = "otheruser", password = "x", email = testEmail, isAdmin = false)
        val savedThird = userManager.saveUser(another)
        assertFalse("Registrar distinto username con email duplicado debe devolver false", savedThird)
    }

    @Test
    fun testLoginWithEmail() {
        // Asegurar que el usuario de test exista
        val user = User(username = testUsername, password = testPassword, email = testEmail, isAdmin = false)
        if (!userManager.saveUser(user)) {
            // si ya existe, ok — seguimos
        }

        // Login correcto
        val ok = userManager.loginWithEmail(testEmail, testPassword)
        assertTrue("Login con credenciales correctas debe devolver true", ok)
        assertNotNull("currentUser no debe ser null después de login exitoso", userManager.currentUser)
        assertEquals("El email del currentUser debe coincidir", testEmail, userManager.currentUser?.email)

        // Login con contraseña incorrecta falla
        val bad = userManager.loginWithEmail(testEmail, "wrongpass")
        assertFalse("Login con contraseña incorrecta debe devolver false", bad)
    }

    @Test
    fun testIsValidEmailFunction() {
        assertTrue(com.example.aplicaciongrupo7.data.isValidEmail("demo@dominio.com"))
        assertFalse(com.example.aplicaciongrupo7.data.isValidEmail("invalido@@dominio"))
        assertFalse(com.example.aplicaciongrupo7.data.isValidEmail("sin-arroba.com"))
    }

    @After
    fun teardown() {
        // Limpieza: eliminar usuarios de prueba
        try { userManager.deleteUser(testUsername) } catch (_: Exception) {}
        try { userManager.deleteUser("otheruser") } catch (_: Exception) {}
    }
}
