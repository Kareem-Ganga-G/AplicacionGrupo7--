package com.example.aplicaciongrupo7.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class UserManager(private val context: Context) {
    private val databaseHelper = AppDatabaseHelper(context)

    private var _currentUser by mutableStateOf<User?>(null)
    val currentUser: User?
        get() = _currentUser

    init {
        createAdminUserIfNeeded()
    }

    private fun createAdminUserIfNeeded() {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseContract.UserEntry.TABLE_NAME,
            null,
            "${DatabaseContract.UserEntry.COLUMN_EMAIL} = ?",
            arrayOf("p.lopez@duocuc.cl"),
            null, null, null
        )

        val adminExists = cursor.count > 0
        cursor.close()
        db.close()

        if (!adminExists) {
            val adminUser = User(
                username = "p.lopez",
                password = "admin",
                email = "p.lopez@duocuc.cl",
                isAdmin = true
            )
            saveUser(adminUser)
        }
    }

    fun saveUser(user: User): Boolean {
        val db = databaseHelper.writableDatabase

        // Verificar si usuario ya existe
        if (userExists(user.username) || emailExists(user.email)) {
            db.close()
            return false
        }

        val values = ContentValues().apply {
            put(DatabaseContract.UserEntry.COLUMN_USERNAME, user.username)
            put(DatabaseContract.UserEntry.COLUMN_EMAIL, user.email)
            put(DatabaseContract.UserEntry.COLUMN_PASSWORD, user.password)
            put(DatabaseContract.UserEntry.COLUMN_IS_ADMIN, if (user.isAdmin) 1 else 0)
        }

        val result = db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, values)
        db.close()

        if (result != -1L) {
            _currentUser = user
            return true
        }
        return false
    }

    fun deleteUser(username: String): Boolean {
        val db = databaseHelper.writableDatabase

        // No permitir eliminar admin principal
        if (username == "p.lopez") {
            db.close()
            return false
        }

        val result = db.delete(
            DatabaseContract.UserEntry.TABLE_NAME,
            "${DatabaseContract.UserEntry.COLUMN_USERNAME} = ?",
            arrayOf(username)
        )
        db.close()

        if (result > 0 && _currentUser?.username == username) {
            _currentUser = null
        }
        return result > 0
    }

    fun getUsers(): List<User> {
        val db = databaseHelper.readableDatabase
        val users = mutableListOf<User>()

        val cursor = db.query(
            DatabaseContract.UserEntry.TABLE_NAME,
            null, null, null, null, null,
            "${DatabaseContract.UserEntry.COLUMN_USERNAME} ASC"
        )

        while (cursor.moveToNext()) {
            val user = cursorToUser(cursor)
            users.add(user)
        }

        cursor.close()
        db.close()
        return users
    }

    fun validateLogin(username: String, password: String): Boolean {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseContract.UserEntry.TABLE_NAME,
            null,
            "${DatabaseContract.UserEntry.COLUMN_USERNAME} = ? AND ${DatabaseContract.UserEntry.COLUMN_PASSWORD} = ?",
            arrayOf(username, password),
            null, null, null
        )

        val user = if (cursor.moveToFirst()) {
            cursorToUser(cursor)
        } else {
            null
        }

        cursor.close()
        db.close()

        _currentUser = user
        return user != null
    }

    fun loginWithEmail(email: String, password: String): Boolean {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseContract.UserEntry.TABLE_NAME,
            null,
            "${DatabaseContract.UserEntry.COLUMN_EMAIL} = ? AND ${DatabaseContract.UserEntry.COLUMN_PASSWORD} = ?",
            arrayOf(email, password),
            null, null, null
        )

        val user = if (cursor.moveToFirst()) {
            cursorToUser(cursor)
        } else {
            null
        }

        cursor.close()
        db.close()

        _currentUser = user
        return user != null
    }

    fun userExists(username: String): Boolean {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseContract.UserEntry.TABLE_NAME,
            null,
            "${DatabaseContract.UserEntry.COLUMN_USERNAME} = ?",
            arrayOf(username),
            null, null, null
        )

        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun emailExists(email: String): Boolean {
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            DatabaseContract.UserEntry.TABLE_NAME,
            null,
            "${DatabaseContract.UserEntry.COLUMN_EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )

        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun isAdminEmail(email: String): Boolean {
        return email.endsWith("@duocuc.cl", ignoreCase = true)
    }

    fun getUser(): User? {
        return _currentUser
    }

    fun isUserRegistered(): Boolean {
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseContract.UserEntry.TABLE_NAME}",
            null
        )

        val count = if (cursor.moveToFirst()) {
            cursor.getInt(0)
        } else {
            0
        }

        cursor.close()
        db.close()
        return count > 0
    }

    fun logout() {
        _currentUser = null
    }

    private fun cursorToUser(cursor: Cursor): User {
        return User(
            username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_USERNAME)),
            password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_PASSWORD)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_EMAIL)),
            isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_IS_ADMIN)) == 1
        )
    }
}

// Función de validación de email (se mantiene igual)
fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    return email.matches(emailRegex)
}