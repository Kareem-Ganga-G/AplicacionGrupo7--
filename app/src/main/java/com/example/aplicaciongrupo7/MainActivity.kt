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
import java.util.UUID

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
    var userType by remember { mutableStateOf("user") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            // Inicializar UserManager
            val userManager = UserManager(context)

            // Pequeña pausa para asegurar que la BD esté lista
            delay(100)

            // Verificar si hay usuarios registrados
            if (userManager.isUserRegistered()) {
                currentScreen = "welcome" // Cambiado: ahora va a welcome screen
            } else {
                currentScreen = "register" // Si no hay usuarios, ir directo a registro
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
        else -> AppNavigationContent(currentScreen, userType) { newScreen, type ->
            currentScreen = newScreen
            userType = type
        }
    }
}

@Composable
fun AppNavigationContent(
    currentScreen: String,
    currentUserType: String,
    onScreenChange: (String, String) -> Unit
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    when (currentScreen) {
        // NUEVA PANTALLA: Pantalla de bienvenida/selección
        "welcome" -> WelcomeScreen(
            onLoginClick = { onScreenChange("login", "user") },
            onRegisterClick = { onScreenChange("register", "user") },
            onAdminClick = { onScreenChange("admin_login", "admin") }
        )

        // Pantalla de login para administradores (nueva)
        "admin_login" -> AdminLoginScreen(
            onLoginSuccess = { onScreenChange("admin", "admin") },
            onBack = { onScreenChange("welcome", "user") }
        )

        "login" -> LoginScreen(
            onLoginSuccess = { userType ->
                onScreenChange(if (userType == "admin") "admin" else "catalog", userType)
            },
            onRegisterClick = { onScreenChange("register", "user") },
            onAdminLogin = { onScreenChange("admin_login", "admin") },
            onForgotPassword = { onScreenChange("forgot_password", "user") },
            onBack = { onScreenChange("welcome", "user") } // NUEVO: para volver
        )
        "register" -> RegisterScreen(
            onRegisterSuccess = {
                // Después de registrarse, ir a login
                onScreenChange("login", "user")
            },
            onBackToLogin = {
                // Si ya tiene cuenta, ir a welcome
                onScreenChange("welcome", "user")
            },
            onBack = { onScreenChange("welcome", "user") } // NUEVO: para volver
        )
        "catalog" -> CatalogScreen(
            onLogout = {
                userManager.logout()
                onScreenChange("welcome", "user")
            },
            onGoToCart = { onScreenChange("cart", "user") },
            onGoToProfile = { onScreenChange("profile", "user") }
        )
        "cart" -> CartScreen(
            onBack = { onScreenChange("catalog", "user") }
        )
        "admin" -> AdminScreen(
            onBack = {
                userManager.logout()
                onScreenChange("welcome", "user")
            },
            onGoToProfile = { onScreenChange("admin_profile", "admin") }
        )
        "profile" -> ProfileScreen(
            userType = currentUserType,
            onBack = { onScreenChange("catalog", "user") },
            onLogout = {
                userManager.logout()
                onScreenChange("welcome", "user")
            },
            onChangePassword = { onScreenChange("change_password", currentUserType) }
        )
        "admin_profile" -> ProfileScreen(
            userType = "admin",
            onBack = { onScreenChange("admin", "admin") },
            onLogout = {
                userManager.logout()
                onScreenChange("welcome", "user")
            },
            onChangePassword = { onScreenChange("change_password", "admin") }
        )
        "forgot_password" -> ForgotPasswordScreen(
            onBack = { onScreenChange("login", "user") },
            onSuccess = { onScreenChange("login", "user") }
        )
        "change_password" -> ChangePasswordScreen(
            onBack = {
                if (currentUserType == "admin") {
                    onScreenChange("admin_profile", "admin")
                } else {
                    onScreenChange("profile", "user")
                }
            },
            onSuccess = {
                if (currentUserType == "admin") {
                    onScreenChange("admin_profile", "admin")
                } else {
                    onScreenChange("profile", "user")
                }
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cargando aplicación...")
        }
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