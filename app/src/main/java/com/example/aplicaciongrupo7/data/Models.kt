package com.example.aplicaciongrupo7.data


data class User(
    val username: String,
    val password: String,
    val email: String,
    val isAdmin: Boolean = false
)



data class Sale(
    val id: Long,
    val date: String,
    val items: List<CartItem>,
    val total: Double,
    val userId: String
)
