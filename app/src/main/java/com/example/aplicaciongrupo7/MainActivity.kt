package com.example.aplicaciongrupo7

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

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

data class User(
    val username: String,
    val password: String,
    val email: String
)

class UserManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        with(sharedPreferences.edit()) {
            putString("username", user.username)
            putString("password", user.password)
            putString("email", user.email)
            apply()
        }
    }

    fun getUser(): User? {
        val username = sharedPreferences.getString("username", null)
        val password = sharedPreferences.getString("password", null)
        val email = sharedPreferences.getString("email", null)

        return if (username != null && password != null && email != null) {
            User(username, password, email)
        } else {
            null
        }
    }

    fun validateLogin(username: String, password: String): Boolean {
        val savedUser = getUser()
        return savedUser?.username == username && savedUser.password == password
    }

    fun isUserRegistered(): Boolean {
        return getUser() != null
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
            userManager = userManager
        )
        "register" -> RegisterScreen(
            onRegisterSuccess = { currentScreen = "login" },
            onBackToLogin = { currentScreen = "login" },
            userManager = userManager
        )
        "catalog" -> CatalogScreen(
            onLogout = { currentScreen = "login" }
        )
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    userManager: UserManager
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logoinicio),
            contentDescription = "Logo Level-Up Gamer",
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "Tienda Gamer Grupo 7",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorMessage = "" // Limpiar error cuando el usuario escriba
            },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = "" // Limpiar error cuando el usuario escriba
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (username.isEmpty() || password.isEmpty()) {
                    errorMessage = "Por favor completa todos los campos"
                } else {
                    isLoading = true
                    // Simular validación
                    if (userManager.validateLogin(username, password)) {
                        onLoginSuccess()
                    } else {
                        errorMessage = "Usuario o contraseña incorrectos"
                    }
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para ir al registro
        TextButton(onClick = onRegisterClick) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }

        // Información de demo
        val savedUser = userManager.getUser()
        if (savedUser != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Demo: Usuario: ${savedUser.username} - Contraseña: ${savedUser.password}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    userManager: UserManager
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón para volver al login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = onBackToLogin) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
        }

        Image(
            painter = painterResource(id = R.drawable.logoinicio),
            contentDescription = "Logo Level-Up Gamer",
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = "Registro de Usuario",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                errorMessage = ""
            },
            label = { Text("Usuario *") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
            },
            label = { Text("Email *") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            label = { Text("Contraseña *") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                errorMessage = ""
            },
            label = { Text("Confirmar Contraseña *") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                when {
                    username.isEmpty() || password.isEmpty() || email.isEmpty() || confirmPassword.isEmpty() -> {
                        errorMessage = "Por favor completa todos los campos"
                    }
                    password != confirmPassword -> {
                        errorMessage = "Las contraseñas no coinciden"
                    }
                    password.length < 4 -> {
                        errorMessage = "La contraseña debe tener al menos 4 caracteres"
                    }
                    else -> {
                        isLoading = true
                        // Guardar usuario
                        val newUser = User(username, password, email)
                        userManager.saveUser(newUser)
                        isLoading = false
                        onRegisterSuccess()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Registrarse")
            }
        }
    }
}

@Composable
fun CatalogScreen(onLogout: () -> Unit) {
    val games = listOf(
        "The Legend of Zelda - Aventura - $59.99",
        "Cyberpunk 2077 - RPG - $49.99",
        "FIFA 24 - Deportes - $69.99",
        "Call of Duty - FPS - $59.99",
        "Minecraft - Sandbox - $29.99",
        "Grand Theft Auto VI - Acción - $69.99"
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Catálogo Gamer",
                style = MaterialTheme.typography.headlineSmall
            )

            Button(onClick = onLogout) {
                Text("Cerrar Sesión")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            games.forEach { game ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = game,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}