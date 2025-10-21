package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.data.UserManager

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onAdminLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var useEmailLogin by remember { mutableStateOf(false) } // Nuevo: toggle entre usuario y email

    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icono temporal en lugar de imagen
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Logo Level-Up Gamer",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Tienda Level-UP GAMER",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Toggle entre usuario y email
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Usuario",
                style = MaterialTheme.typography.bodyMedium,
                color = if (!useEmailLogin) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            Switch(
                checked = useEmailLogin,
                onCheckedChange = { useEmailLogin = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Text(
                text = "Email",
                style = MaterialTheme.typography.bodyMedium,
                color = if (useEmailLogin) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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
            label = {
                Text(
                    if (useEmailLogin) "Email" else "Usuario"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage.isNotEmpty(),
            placeholder = {
                Text(
                    if (useEmailLogin) "ejemplo@gmail.com" else "Nombre de usuario"
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
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
                } else if (useEmailLogin && !isValidEmail(username)) {
                    errorMessage = "Por favor ingresa un email válido"
                } else {
                    isLoading = true

                    val loginSuccess = if (useEmailLogin) {
                        userManager.loginWithEmail(username, password)
                    } else {
                        userManager.validateLogin(username, password)
                    }

                    if (loginSuccess) {
                        val user = userManager.currentUser
                        if (user?.isAdmin == true) {
                            onAdminLogin()
                        } else {
                            onLoginSuccess()
                        }
                    } else {
                        errorMessage = if (useEmailLogin) {
                            "Email o contraseña incorrectos"
                        } else {
                            "Usuario o contraseña incorrectos"
                        }
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

        // Botón de acceso admin
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = onAdminLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Person, contentDescription = "Admin")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Acceso de Administrador")
        }

        // Información de demo
        val savedUser = userManager.getUser()
        if (savedUser != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Demo: Usuario: ${savedUser.username} - Email: ${savedUser.email}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Función para validar formato de email
private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    return email.matches(emailRegex)
}