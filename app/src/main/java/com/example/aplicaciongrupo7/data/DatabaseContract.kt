package com.example.aplicaciongrupo7.data

import android.provider.BaseColumns

object DatabaseContract {
    const val DATABASE_NAME = "app_database.db"
    const val DATABASE_VERSION = 4 // INCREMENTADO: Nueva versión por cambios en schema

    object ProductEntry {
        const val TABLE_NAME = "products"
        const val COLUMN_ID = "_id"  // Cambiar a _id
        const val COLUMN_TITLE = "title"
        const val COLUMN_GENRE = "genre"
        const val COLUMN_PRICE = "price"
        const val COLUMN_RATING = "rating"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_IMAGE_RES = "image_res"
        const val COLUMN_STOCK = "stock"
    }

    object UserEntry : BaseColumns {
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "_id" // NUEVO: Para compatibilidad con BaseColumns
        const val COLUMN_USER_ID = "user_id" // NUEVO: ID único adicional
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_IS_ADMIN = "is_admin"
        const val COLUMN_NAME = "name" // NUEVO: Nombre completo
        const val COLUMN_PHONE = "phone" // NUEVO: Teléfono
        const val COLUMN_CREATED_AT = "created_at" // NUEVO: Fecha de creación
    }

    object CartEntry : BaseColumns {
        const val TABLE_NAME = "cart"
        const val COLUMN_PRODUCT_ID = "product_id"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_ADDED_DATE = "added_date"
    }

    object SalesEntry : BaseColumns {
        const val TABLE_NAME = "sales"
        const val COLUMN_PRODUCT_ID = "product_id"
        const val COLUMN_QUANTITY = "quantity"
        const val COLUMN_TOTAL = "total"
        const val COLUMN_SALE_DATE = "sale_date"
        const val COLUMN_USER_ID = "user_id"
    }
}