package com.example.aplicaciongrupo7.data

data class User(
    val username: String,
    val password: String,
    val email: String,
    val isAdmin: Boolean = false
)

data class Game(
    val id: Int,
    val title: String,
    val genre: String,
    val price: String,
    val rating: Float,
    val description: String = ""
)