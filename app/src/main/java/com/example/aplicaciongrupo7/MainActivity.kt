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
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.data.CartManager
import com.example.aplicaciongrupo7.data.UserManager
import com.example.aplicaciongrupo7.screens.*
import com.example.aplicaciongrupo7.ui.theme.AplicacionGrupo7Theme
import kotlinx.coroutines.delay

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

    val cartManager = remember {
        CartManager(context.applicationContext)
    }

    LaunchedEffect(Unit) {
        try {
            val userManager = UserManager(context)
            delay(100)
            currentScreen =
                if (userManager.isUserRegistered()) "welcome" else "register"
        } catch (e: Exception) {
            errorMessage = e.message
            currentScreen = "error"
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

        else -> AppNavigationContent(
            currentScreen = currentScreen,
            currentUserType = userType,
            cartManager = cartManager
        ) { screen, type ->
            currentScreen = screen
            userType = type
        }
    }
}

@Composable
fun AppNavigationContent(
    currentScreen: String,
    currentUserType: String,
    cartManager: CartManager,
    onScreenChange: (String, String) -> Unit
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    when (currentScreen) {

        "welcome" -> WelcomeScreen(
            onLoginClick = { onScreenChange("login", "user") },
            onRegisterClick = { onScreenChange("register", "user") },
            onAdminClick = { onScreenChange("admin_login", "admin") }
        )

        "admin_login" -> AdminLoginScreen(
            onLoginSuccess = { onScreenChange("admin", "admin") },
            onBack = { onScreenChange("welcome", "user") }
        )

        "login" -> LoginScreen(
            onLoginSuccess = { type ->
                onScreenChange(if (type == "admin") "admin" else "catalog", type)
            },
            onRegisterClick = { onScreenChange("register", "user") },
            onAdminLogin = { onScreenChange("admin_login", "admin") },
            onForgotPassword = { onScreenChange("forgot_password", "user") },
            onBack = { onScreenChange("welcome", "user") }
        )

        "register" -> RegisterScreen(
            onRegisterSuccess = { onScreenChange("login", "user") },
            onBackToLogin = { onScreenChange("welcome", "user") },
            onBack = { onScreenChange("welcome", "user") }
        )

        // ✅ CATÁLOGO (CORREGIDO: ahora recibe onGoToProfile)
        "catalog" -> CatalogScreen(
            cartManager = cartManager,
            onBack = {
                userManager.logout()
                cartManager.clearCart()
                onScreenChange("welcome", "user")
            },
            onGoToCart = { onScreenChange("cart", "user") },
            onGoToProfile = { onScreenChange("profile", "user") } // <-- agregado
        )

        "cart" -> CartScreen(
            cartManager = cartManager,
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
                cartManager.clearCart()
                onScreenChange("welcome", "user")
            },
            onChangePassword = {
                onScreenChange("change_password", currentUserType)
            }
        )

        "admin_profile" -> ProfileScreen(
            userType = "admin",
            onBack = { onScreenChange("admin", "admin") },
            onLogout = {
                userManager.logout()
                onScreenChange("welcome", "user")
            },
            onChangePassword = {
                onScreenChange("change_password", "admin")
            }
        )

        "forgot_password" -> ForgotPasswordScreen(
            onBack = { onScreenChange("login", "user") },
            onSuccess = { onScreenChange("login", "user") }
        )

        "change_password" -> ChangePasswordScreen(
            onBack = {
                onScreenChange(
                    if (currentUserType == "admin") "admin_profile" else "profile",
                    currentUserType
                )
            },
            onSuccess = {
                onScreenChange(
                    if (currentUserType == "admin") "admin_profile" else "profile",
                    currentUserType
                )
            }
        )
    }
}

/* ───────── UI AUX ───────── */

@Composable
fun LoadingScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("Cargando aplicación...")
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Error al iniciar", color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Text(message)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text("Reintentar")
        }
    }
}
