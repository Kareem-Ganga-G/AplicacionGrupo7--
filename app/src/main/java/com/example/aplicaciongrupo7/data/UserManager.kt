package com.example.aplicaciongrupo7.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.util.Log

class UserManager(private val context: Context) {
    private val databaseHelper = AppDatabaseHelper(context)

    private var _currentUser by mutableStateOf<User?>(null)
    val currentUser: User?
        get() = _currentUser

    init {
        createAdminUserIfNeeded()
    }

    private fun createAdminUserIfNeeded() {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = databaseHelper.writableDatabase
            cursor = db.query(
                DatabaseContract.UserEntry.TABLE_NAME,
                null,
                "${DatabaseContract.UserEntry.COLUMN_EMAIL} = ?",
                arrayOf("p.lopez@duocuc.cl"),
                null, null, null
            )

            val adminExists = cursor.count > 0

            if (!adminExists) {
                val adminUser = User(
                    username = "p.lopez",
                    password = "admin123", // Contraseña más segura
                    email = "p.lopez@duocuc.cl",
                    isAdmin = true
                )
                saveUserInternal(adminUser, db)
                Log.d("UserManager", "Usuario admin creado: ${adminUser.email}")
            } else {
                Log.d("UserManager", "Usuario admin ya existe")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    // NUEVO: Verificar si usuario está logueado
    fun isUserLoggedIn(): Boolean {
        return _currentUser != null
    }

    // NUEVO: Obtener tipo de usuario
    fun getUserType(): String {
        return if (_currentUser?.isAdmin == true) "admin" else "user"
    }

    // NUEVO: Método para recuperar contraseña (versión simplificada)
    fun recoverPassword(email: String): Boolean {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = databaseHelper.readableDatabase

            // Verificar si el email existe
            cursor = db.query(
                DatabaseContract.UserEntry.TABLE_NAME,
                null,
                "${DatabaseContract.UserEntry.COLUMN_EMAIL} = ?",
                arrayOf(email),
                null, null, null
            )

            val exists = cursor.count > 0
            if (exists) {
                cursor.moveToFirst()
                val username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_USERNAME))
                Log.d("UserManager", "Solicitud de recuperación para: $email (usuario: $username)")
                // En una app real, aquí enviarías un email
                return true
            }

            return false
        } catch (e: Exception) {
            Log.e("UserManager", "Error en recoverPassword", e)
            return false
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    // NUEVO: Método para cambiar contraseña (versión simplificada)
    fun changePassword(email: String, oldPassword: String, newPassword: String): Boolean {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = databaseHelper.writableDatabase

            // Primero verificar que la contraseña actual sea correcta
            cursor = db.query(
                DatabaseContract.UserEntry.TABLE_NAME,
                null,
                "${DatabaseContract.UserEntry.COLUMN_EMAIL} = ? AND ${DatabaseContract.UserEntry.COLUMN_PASSWORD} = ?",
                arrayOf(email, oldPassword),
                null, null, null
            )

            if (cursor.count == 0) {
                Log.d("UserManager", "Credenciales inválidas para: $email")
                return false
            }

            // Actualizar la contraseña
            val values = ContentValues().apply {
                put(DatabaseContract.UserEntry.COLUMN_PASSWORD, newPassword)
            }

            val rowsAffected = db.update(
                DatabaseContract.UserEntry.TABLE_NAME,
                values,
                "${DatabaseContract.UserEntry.COLUMN_EMAIL} = ?",
                arrayOf(email)
            )

            val success = rowsAffected > 0
            if (success) {
                Log.d("UserManager", "Contraseña actualizada para: $email")
                // Actualizar usuario actual si es el mismo
                if (_currentUser?.email == email) {
                    _currentUser = _currentUser?.copy(password = newPassword)
                }
            }

            return success
        } catch (e: Exception) {
            Log.e("UserManager", "Error en changePassword", e)
            return false
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    // NUEVO: Método para actualizar perfil (versión simplificada - solo email)
    fun updateProfile(username: String, newEmail: String? = null): Boolean {
        if (newEmail == null) {
            return true // No hay cambios
        }

        var db: SQLiteDatabase? = null
        try {
            db = databaseHelper.writableDatabase

            val values = ContentValues().apply {
                put(DatabaseContract.UserEntry.COLUMN_EMAIL, newEmail)
            }

            val rowsAffected = db.update(
                DatabaseContract.UserEntry.TABLE_NAME,
                values,
                "${DatabaseContract.UserEntry.COLUMN_USERNAME} = ?",
                arrayOf(username)
            )

            val success = rowsAffected > 0
            if (success && _currentUser?.username == username) {
                // Actualizar usuario en memoria
                _currentUser = _currentUser?.copy(email = newEmail)
            }

            return success
        } catch (e: Exception) {
            Log.e("UserManager", "Error en updateProfile", e)
            return false
        } finally {
            db?.close()
        }
    }

    // MÉTODOS EXISTENTES (sin cambios)
    fun saveUser(user: User): Boolean {
        var db: SQLiteDatabase? = null
        try {
            db = databaseHelper.writableDatabase
            return saveUserInternal(user, db)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db?.close()
        }
    }

    private fun saveUserInternal(user: User, db: SQLiteDatabase): Boolean {
        return try {
            // 1. Verificar si usuario o email ya existen
            if (userExistsInternal(user.username, db)) {
                Log.e("DB", "Error: username ya existe -> ${user.username}")
                return false
            }

            if (emailExistsInternal(user.email, db)) {
                Log.e("DB", "Error: email ya existe -> ${user.email}")
                return false
            }

            // 2. Insertar usuario
            val values = ContentValues().apply {
                put(DatabaseContract.UserEntry.COLUMN_USERNAME, user.username)
                put(DatabaseContract.UserEntry.COLUMN_EMAIL, user.email)
                put(DatabaseContract.UserEntry.COLUMN_PASSWORD, user.password)
                put(DatabaseContract.UserEntry.COLUMN_IS_ADMIN, if (user.isAdmin) 1 else 0)
            }

            val result = db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, values)

            Log.d("DB", "Insert result = $result")

            if (result == -1L) {
                Log.e("DB", "ERROR: insert devolvió -1")
                return false
            }

            _currentUser = user
            true

        } catch (e: Exception) {
            Log.e("DB", "EXCEPCIÓN EN saveUserInternal", e)
            false
        }
    }

    fun deleteUser(username: String): Boolean {
        var db: SQLiteDatabase? = null
        try {
            // No permitir eliminar admin principal
            if (username == "p.lopez") {
                return false
            }

            db = databaseHelper.writableDatabase
            val result = db.delete(
                DatabaseContract.UserEntry.TABLE_NAME,
                "${DatabaseContract.UserEntry.COLUMN_USERNAME} = ?",
                arrayOf(username)
            )

            if (result > 0 && _currentUser?.username == username) {
                _currentUser = null
            }
            return result > 0
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db?.close()
        }
    }

    fun getUsers(): List<User> {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = databaseHelper.readableDatabase
            val users = mutableListOf<User>()

            cursor = db.query(
                DatabaseContract.UserEntry.TABLE_NAME,
                null, null, null, null, null,
                "${DatabaseContract.UserEntry.COLUMN_USERNAME} ASC"
            )

            while (cursor.moveToNext()) {
                val user = cursorToUser(cursor)
                users.add(user)
            }

            return users
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    fun validateLogin(username: String, password: String): Boolean {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = databaseHelper.readableDatabase
            cursor = db.query(
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

            _currentUser = user
            return user != null
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    fun loginWithEmail(email: String, password: String): Boolean {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = databaseHelper.readableDatabase
            cursor = db.query(
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

            _currentUser = user
            return user != null
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    fun userExists(username: String): Boolean {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = databaseHelper.readableDatabase
            return userExistsInternal(username, db)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    private fun userExistsInternal(username: String, db: SQLiteDatabase): Boolean {
        val cursor = db.query(
            DatabaseContract.UserEntry.TABLE_NAME,
            null,
            "${DatabaseContract.UserEntry.COLUMN_USERNAME} = ?",
            arrayOf(username),
            null, null, null
        )

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun emailExists(email: String): Boolean {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = databaseHelper.readableDatabase
            return emailExistsInternal(email, db)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            cursor?.close()
            db?.close()
        }
    }

    private fun emailExistsInternal(email: String, db: SQLiteDatabase): Boolean {
        val cursor = db.query(
            DatabaseContract.UserEntry.TABLE_NAME,
            null,
            "${DatabaseContract.UserEntry.COLUMN_EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun isAdminEmail(email: String): Boolean {
        return email.endsWith("@duocuc.cl", ignoreCase = true)
    }

    fun getUser(): User? {
        return _currentUser
    }

    fun isUserRegistered(): Boolean {
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = databaseHelper.readableDatabase
            cursor = db.rawQuery(
                "SELECT COUNT(*) FROM ${DatabaseContract.UserEntry.TABLE_NAME}",
                null
            )

            val count = if (cursor.moveToFirst()) {
                cursor.getInt(0)
            } else {
                0
            }
            return count > 0
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            cursor?.close()
            db?.close()
        }
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

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    return email.matches(emailRegex)
}