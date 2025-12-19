// Archivo: UserManagerTest.kt (en carpeta 'androidTest')
package com.example.aplicaciongrupo7

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.aplicaciongrupo7.data.UserManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserManagerTest {

    @Test
    fun defaultAdmin_isCreatedAutomatically() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val userManager = UserManager(context)

        val dbHelper = com.example.aplicaciongrupo7.data.AppDatabaseHelper(context)
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            "users", // Nombre de tabla en DatabaseContract
            null,
            "username = ?",
            arrayOf("p.lopez"),
            null, null, null
        )

        val exists = cursor.count > 0
        cursor.close()
        db.close()

        assertEquals("El usuario administrador p.lopez debería existir", true, exists)
    }
}