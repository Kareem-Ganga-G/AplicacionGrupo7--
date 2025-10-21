package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.R
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
    val userManager = remember { UserManager(context) }

    // Detectar orientación
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Fondo negro completo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
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
                    IconButton(
                        onClick = onBackToLogin,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            }

            // Tu imagen como logo
            Image(
                painter = painterResource(id = R.drawable.logoinicio),
                contentDescription = "Logo Level-Up Gamer",
                modifier = Modifier
                    .size(if (isLandscape) 100.dp else 150.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Registro de Usuario",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 48.dp))

            if (errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFB00020) // Rojo oscuro para errores
                    )
                ) {
                    Text(
                        text = errorMessage,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
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
                        "Usuario *",
                        color = Color.White
                    )
                },
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

            Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = {
                    Text(
                        "Email *",
                        color = Color.White
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.isNotEmpty(),
                placeholder = {
                    Text(
                        "ejemplo@gmail.com",
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

            Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = {
                    Text(
                        "Contraseña *",
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

            Spacer(modifier = Modifier.height(if (isLandscape) 12.dp else 16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                label = {
                    Text(
                        "Confirmar Contraseña *",
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

            Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 32.dp))

            Button(
                onClick = {
                    when {
                        username.isEmpty() || password.isEmpty() || email.isEmpty() || confirmPassword.isEmpty() -> {
                            errorMessage = "Por favor completa todos los campos"
                        }
                        !isValidEmail(email) -> {
                            errorMessage = "Por favor ingresa un email válido (ej: usuario@gmail.com)"
                        }
                        password != confirmPassword -> {
                            errorMessage = "Las contraseñas no coinciden"
                        }
                        password.length < 4 -> {
                            errorMessage = "La contraseña debe tener al menos 4 caracteres"
                        }
                        userManager.userExists(username) -> {
                            errorMessage = "El usuario ya existe"
                        }
                        userManager.emailExists(email) -> {
                            errorMessage = "El email ya está registrado"
                        }
                        else -> {
                            isLoading = true
                            val success = userManager.saveUser(User(username, password, email, isAdmin = false))
                            if (success) {
                                onRegisterSuccess()
                            } else {
                                errorMessage = "Error al registrar usuario"
                            }
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
                        color = Color.White
                    )
                } else {
                    Text("Registrarse")
                }
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 24.dp))

            // Botón para volver al login - en landscape lo mostramos aquí
            if (isLandscape) {
                TextButton(
                    onClick = onBackToLogin,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿Ya tienes cuenta? Inicia sesión")
                }
            } else {
                // En portrait, mostramos el botón abajo también
                TextButton(
                    onClick = onBackToLogin,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("¿Ya tienes cuenta? Inicia sesión")
                }
            }
        }
    }
}

// Función para validar formato de email
private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    return email.matches(emailRegex)
}