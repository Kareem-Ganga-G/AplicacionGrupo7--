package com.example.aplicaciongrupo7.data

data class User(
    val username: String,
    val password: String,
    val email: String,
    val isAdmin: Boolean = false
)

data class Product(
    val id: Int,
    val title: String,
    val genre: String,
    val price: String,
    val rating: Float,
    val description: String = "",
    val imageRes: Int,
    val stock: Int = 0,
)

data class CartItem(
    val product: Product,
    val quantity: Int = 1
)
data class Sale(
    val id: Long,
    val date: String,
    val items: List<CartItem>,
    val total: Double,
    val userId: String
)
