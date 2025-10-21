package com.example.aplicaciongrupo7.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Cambia el nombre para evitar conflicto
    private var _currentUser by mutableStateOf<User?>(null)

    // Propiedad pública para acceder al usuario actual
    val currentUser: User?
        get() = _currentUser

    // Guardar múltiples usuarios
    fun saveUser(user: User): Boolean {
        return try {
            val users = getUsers().toMutableList()

            // Verificar si el usuario ya existe
            if (users.any { it.username == user.username }) {
                return false
            }
            if (users.any { it.email.equals(user.email, ignoreCase = true) }) {
                return false
            }

            users.add(user)
            saveUsers(users)
            _currentUser = user
            true
        } catch (e: Exception) {
            false
        }
    }

    // Obtener todos los usuarios
    fun getUsers(): List<User> {
        val usersJson = sharedPreferences.getString("users", "[]")
        val type = object : TypeToken<List<User>>() {}.type
        return gson.fromJson(usersJson, type) ?: emptyList()
    }

    // Login con username
    fun validateLogin(username: String, password: String): Boolean {
        val users = getUsers()
        val user = users.find { it.username == username && it.password == password }
        _currentUser = user
        return user != null
    }

    // Login con email
    fun loginWithEmail(email: String, password: String): Boolean {
        val users = getUsers()
        val user = users.find {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
        _currentUser = user
        return user != null
    }

    // Verificar si usuario existe
    fun userExists(username: String): Boolean {
        return getUsers().any { it.username == username }
    }

    // Verificar si email existe
    fun emailExists(email: String): Boolean {
        return getUsers().any { it.email.equals(email, ignoreCase = true) }
    }

    // Para compatibilidad con tu código existente - usa currentUser directamente
    fun getUser(): User? {
        return _currentUser
    }

    fun isUserRegistered(): Boolean {
        return getUsers().isNotEmpty()
    }

    fun logout() {
        _currentUser = null
    }

    // Función privada para guardar la lista de usuarios
    private fun saveUsers(users: List<User>) {
        val usersJson = gson.toJson(users)
        sharedPreferences.edit().putString("users", usersJson).apply()
    }
}

// Función de validación de email
fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    return email.matches(emailRegex)
}