package com.example.aplicaciongrupo7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.aplicaciongrupo7.data.UserManager
import com.example.aplicaciongrupo7.screens.*
import com.example.aplicaciongrupo7.ui.theme.AplicacionGrupo7Theme
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.*
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AplicacionGrupo7Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SafeAppNavigation()
                }
            }
        }
    }
}

@Composable
fun SafeAppNavigation() {
    var currentScreen by remember { mutableStateOf("loading") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            // Test de la base de datos
            val userManager = UserManager(context)

            // Pequeña pausa para asegurar que la BD esté lista
            delay(100)

            if (!userManager.isUserRegistered()) {
                currentScreen = "register"
            } else {
                currentScreen = "login"
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
            currentScreen = "error"
            e.printStackTrace()
        }
    }

    when (currentScreen) {
        "loading" -> LoadingScreen()
        "error" -> ErrorScreen(
            message = errorMessage ?: "Error desconocido",
            onRetry = {
                currentScreen = "loading"
                errorMessage = null
            }
        )
        else -> AppNavigationContent(currentScreen) { newScreen ->
            currentScreen = newScreen
        }
    }
}

@Composable
fun AppNavigationContent(
    currentScreen: String,
    onScreenChange: (String) -> Unit
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    when (currentScreen) {
        "login" -> LoginScreen(
            onLoginSuccess = { onScreenChange("catalog") },
            onRegisterClick = { onScreenChange("register") },
            onAdminLogin = { onScreenChange("admin") }
        )
        "register" -> RegisterScreen(
            onRegisterSuccess = { onScreenChange("login") },
            onBackToLogin = { onScreenChange("login") }
        )
        "catalog" -> CatalogScreen(
            onLogout = {
                userManager.logout()
                onScreenChange("login")
            },
            onGoToCart = { onScreenChange("cart") }
        )
        "cart" -> CartScreen(
            onBack = { onScreenChange("catalog") }
        )
        "admin" -> AdminScreen(
            onBack = {
                userManager.logout()
                onScreenChange("login")
            }
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Cargando...")
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Error al iniciar la aplicación",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text(text = "Reintentar")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Si el error persiste, desinstala y reinstala la app",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

