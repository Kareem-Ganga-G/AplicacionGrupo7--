package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (userType: String) -> Unit,
    onRegisterClick: () -> Unit,
    onAdminLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    onBack: () -> Unit // NUEVO: parámetro para volver
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var useEmailLogin by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val coroutineScope = rememberCoroutineScope()

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
            // NUEVO: Botón volver en la esquina superior izquierda
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver al inicio")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tu imagen como logo
            Image(
                painter = painterResource(id = R.drawable.logoinicio),
                contentDescription = "Logo Level-Up Gamer",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            // Mensaje de BIENVENIDO!
            Text(
                text = "INICIAR SESIÓN",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Ingresa tus credenciales",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Validación básica
                    if (username.isEmpty() || password.isEmpty()) {
                        errorMessage = "Por favor completa todos los campos"
                        return@Button
                    }

                    if (useEmailLogin && !isValidEmail(username)) {
                        errorMessage = "Por favor ingresa un email válido"
                        return@Button
                    }

                    isLoading = true
                    errorMessage = ""

                    // Mover la lógica de login a una corrutina
                    coroutineScope.launch {
                        try {
                            val loginSuccess = if (useEmailLogin) {
                                userManager.loginWithEmail(username, password)
                            } else {
                                userManager.validateLogin(username, password)
                            }

                            if (loginSuccess) {
                                val user = userManager.currentUser
                                if (user?.isAdmin == true) {
                                    onLoginSuccess("admin")
                                } else {
                                    onLoginSuccess("user")
                                }
                            } else {
                                errorMessage = if (useEmailLogin) {
                                    "Email o contraseña incorrectos"
                                } else {
                                    "Usuario o contraseña incorrectos"
                                }
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error al iniciar sesión: ${e.message}"
                        } finally {
                            isLoading = false
                        }
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
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Iniciar Sesión")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Enlace para recuperar contraseña
            TextButton(
                onClick = onForgotPassword,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("¿Olvidaste tu contraseña?")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Enlace para registrarse
            TextButton(
                onClick = onRegisterClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("¿No tienes cuenta? Regístrate aquí")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Separador
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.2f)
                )
                Text(
                    text = "O",
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.labelSmall
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color.White.copy(alpha = 0.2f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para acceso administrador
            OutlinedButton(
                onClick = onAdminLogin,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Acceso Administrador")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para volver al inicio
            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White.copy(alpha = 0.7f)
                )
            ) {
                Text("Volver al inicio")
            }
        }
    }
}