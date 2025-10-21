package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.R
import com.example.aplicaciongrupo7.data.UserManager
import com.example.aplicaciongrupo7.data.isValidEmail

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
    var useEmailLogin by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    // Fondo negro completo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Contenido principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tu imagen como logo
            Image(
                painter = painterResource(id = R.drawable.logoinicio),
                contentDescription = "Logo Level-Up Gamer",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            // Mensaje de BIENVENIDO!
            Text(
                text = "BIENVENIDO!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Tienda Level-UP GAMER",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Toggle entre usuario y email
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Usuario",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (!useEmailLogin) MaterialTheme.colorScheme.primary
                        else Color.White.copy(alpha = 0.7f)
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
                        else Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFB00020)
                    )
                ) {
                    Text(
                        text = errorMessage,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Campos de texto con estilo oscuro
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    errorMessage = ""
                },
                label = {
                    Text(
                        if (useEmailLogin) "Email" else "Usuario",
                        color = Color.White
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.isNotEmpty(),
                placeholder = {
                    Text(
                        if (useEmailLogin) "ejemplo@gmail.com" else "Nombre de usuario",
                        color = Color.White.copy(alpha = 0.5f)
                    )
                },
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color(0x801A1A1A),
                    focusedContainerColor = Color(0x801A1A1A),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = Color.White,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = {
                    Text(
                        "Contraseña",
                        color = Color.White
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.isNotEmpty(),
                colors = TextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedContainerColor = Color(0x801A1A1A),
                    focusedContainerColor = Color(0x801A1A1A),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = Color.White,
                    unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
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
                                // Redirigir a pantalla de admin
                                onAdminLogin()
                            } else {
                                // Redirigir a pantalla normal
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
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White
                    )
                } else {
                    Text("Iniciar Sesión")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ir al registro
            TextButton(
                onClick = onRegisterClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }

            // Solo mensaje de bienvenida - sin información de usuarios
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Ingresa tus credenciales para continuar",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}