package com.example.aplicaciongrupo7.data

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import androidx.test.filters.SmallTest
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
@SmallTest
class DatabaseContractTest {

    private lateinit var database: SQLiteDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = SQLiteDatabase.openOrCreateDatabase(
            context.getDatabasePath(DatabaseContract.DATABASE_NAME),
            null
        )
        createTables()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createTables() {
        // Crear tabla users - VERSIÓN CORREGIDA
        database.execSQL("""
            CREATE TABLE ${DatabaseContract.UserEntry.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.UserEntry.COLUMN_USERNAME} TEXT UNIQUE NOT NULL,
                ${DatabaseContract.UserEntry.COLUMN_EMAIL} TEXT UNIQUE NOT NULL,
                ${DatabaseContract.UserEntry.COLUMN_PASSWORD} TEXT NOT NULL,
                ${DatabaseContract.UserEntry.COLUMN_IS_ADMIN} INTEGER DEFAULT 0
            )
        """)

        // Crear tabla products
        database.execSQL("""
            CREATE TABLE ${DatabaseContract.ProductEntry.TABLE_NAME} (
                ${DatabaseContract.ProductEntry.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.ProductEntry.COLUMN_TITLE} TEXT NOT NULL,
                ${DatabaseContract.ProductEntry.COLUMN_GENRE} TEXT,
                ${DatabaseContract.ProductEntry.COLUMN_PRICE} REAL NOT NULL,
                ${DatabaseContract.ProductEntry.COLUMN_RATING} REAL DEFAULT 0,
                ${DatabaseContract.ProductEntry.COLUMN_DESCRIPTION} TEXT,
                ${DatabaseContract.ProductEntry.COLUMN_IMAGE_RES} TEXT,
                ${DatabaseContract.ProductEntry.COLUMN_STOCK} INTEGER DEFAULT 0
            )
        """)

        // Crear tabla cart - VERSIÓN CORREGIDA
        database.execSQL("""
            CREATE TABLE ${DatabaseContract.CartEntry.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.CartEntry.COLUMN_PRODUCT_ID} INTEGER NOT NULL,
                ${DatabaseContract.CartEntry.COLUMN_QUANTITY} INTEGER DEFAULT 1,
                ${DatabaseContract.CartEntry.COLUMN_ADDED_DATE} DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """)

        // Crear tabla sales - VERSIÓN CORREGIDA
        database.execSQL("""
            CREATE TABLE ${DatabaseContract.SalesEntry.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${DatabaseContract.SalesEntry.COLUMN_PRODUCT_ID} INTEGER NOT NULL,
                ${DatabaseContract.SalesEntry.COLUMN_QUANTITY} INTEGER NOT NULL,
                ${DatabaseContract.SalesEntry.COLUMN_TOTAL} REAL NOT NULL,
                ${DatabaseContract.SalesEntry.COLUMN_SALE_DATE} DATETIME DEFAULT CURRENT_TIMESTAMP,
                ${DatabaseContract.SalesEntry.COLUMN_USER_ID} INTEGER NOT NULL
            )
        """)
    }

    // El resto de tus pruebas permanecen igual...
    @Test
    fun testUsersTable_InsertAndRetrieve() {
        val userValues = ContentValues().apply {
            put(DatabaseContract.UserEntry.COLUMN_USERNAME, "testuser")
            put(DatabaseContract.UserEntry.COLUMN_EMAIL, "test@example.com")
            put(DatabaseContract.UserEntry.COLUMN_PASSWORD, "password123")
            put(DatabaseContract.UserEntry.COLUMN_IS_ADMIN, 0)
        }

        val userId = database.insert(DatabaseContract.UserEntry.TABLE_NAME, null, userValues)
        assertTrue("User should be inserted successfully", userId != -1L)

        val cursor = database.query(
            DatabaseContract.UserEntry.TABLE_NAME,
            null,
            "${BaseColumns._ID} = ?", // ← USAR BaseColumns._ID aquí también
            arrayOf(userId.toString()),
            null, null, null
        )

        assertTrue("Cursor should have user data", cursor.moveToFirst())
        assertEquals("testuser", cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_USERNAME)))
        cursor.close()
    }

    // ... resto de las pruebas
}