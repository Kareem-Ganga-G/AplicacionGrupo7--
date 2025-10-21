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
            label = { Text("Usuario") },
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
                    if (userManager.validateLogin(username, password)) {
                        val user = userManager.getCurrentUser()
                        if (user?.isAdmin == true) {
                            onAdminLogin()
                        } else {
                            onLoginSuccess()
                        }
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
                text = "Demo: Usuario Guardado: ${savedUser.username} - Contraseña: ${savedUser.password}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}