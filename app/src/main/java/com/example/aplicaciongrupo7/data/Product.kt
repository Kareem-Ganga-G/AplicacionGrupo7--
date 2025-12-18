package com.example.aplicaciongrupo7.data

class Product(
    val id: Int,
    val title: String,
    val genre: String,
    val price: String,
    val rating: Float,
    val description: String = "",
    val imageRes: Int = 0,
    val stock: Int = 0
)