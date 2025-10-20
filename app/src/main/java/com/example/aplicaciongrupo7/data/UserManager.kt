package com.example.aplicaciongrupo7.data

import android.content.Context

class UserManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        with(sharedPreferences.edit()) {
            putString("username", user.username)
            putString("password", user.password)
            putString("email", user.email)
            putBoolean("isAdmin", user.isAdmin)
            apply()
        }
    }

    fun getUser(): User? {
        val username = sharedPreferences.getString("username", null)
        val password = sharedPreferences.getString("password", null)
        val email = sharedPreferences.getString("email", null)
        val isAdmin = sharedPreferences.getBoolean("isAdmin", false)

        return if (username != null && password != null && email != null) {
            User(username, password, email, isAdmin)
        } else {
            null
        }
    }

    fun validateLogin(username: String, password: String): Boolean {
        val savedUser = getUser()
        return savedUser?.username == username && savedUser.password == password
    }

    fun isUserRegistered(): Boolean {
        return getUser() != null
    }

    fun getCurrentUser(): User? {
        return getUser()
    }

    fun logout() {

    }
}