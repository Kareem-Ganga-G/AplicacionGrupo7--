package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.data.User
import com.example.aplicaciongrupo7.data.UserManager

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userManager = remember { UserManager(context) } // CREAMOS UserManager AQUÍ

    // Detectar orientación
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isLandscape) 24.dp else 32.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón volver - solo en portrait
        if (!isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackToLogin) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        }

        // Icono
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Logo Level-Up Gamer",
            modifier = Modifier.size(if (isLandscape) 80.dp else 120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Registro de Usuario",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 48.dp))

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

        Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
            },
            label = { Text("Email *") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 16.dp))

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

        Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 16.dp))

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

        Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 32.dp))
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
                        // INTENTAR REGISTRAR USUARIO
                        try {
                            val newUser = User(username, password, email, isAdmin = false)
                            userManager.saveUser(newUser) // Esto probablemente no devuelve Boolean
                            onRegisterSuccess() // Si no hay error, registro exitoso
                        } catch (e: Exception) {
                            // Si hay excepción, mostrar error
                            errorMessage = "El usuario o email ya existen"
                        } finally {
                            isLoading = false
                        }
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

        Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 24.dp))

        // Botón para volver al login - en landscape lo mostramos aquí
        if (isLandscape) {
            TextButton(
                onClick = onBackToLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
