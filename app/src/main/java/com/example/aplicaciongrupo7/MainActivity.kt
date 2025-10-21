package com.example.aplicaciongrupo7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme
import com.example.aplicaciongrupo7.data.UserManager
import com.example.aplicaciongrupo7.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("login") }
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    LaunchedEffect(Unit) {
        if (!userManager.isUserRegistered()) {
            currentScreen = "register"
        }
    }

    when (currentScreen) {
        "login" -> LoginScreen(
            onLoginSuccess = { currentScreen = "catalog" },
            onRegisterClick = { currentScreen = "register" },
            onAdminLogin = { currentScreen = "admin" }
        )
        "register" -> RegisterScreen(
            onRegisterSuccess = { currentScreen = "login" },
            onBackToLogin = { currentScreen = "login" },
            userManager = userManager
        )
        "catalog" -> CatalogScreen(
            onLogout = { currentScreen = "login" },
            onGoToCart = { currentScreen = "cart" }
        )
        "cart" -> CartScreen(
            onBack = { currentScreen = "catalog" }
        )
        "admin" -> AdminScreen(
            onBack = { currentScreen = "login" }
        )
    }
}