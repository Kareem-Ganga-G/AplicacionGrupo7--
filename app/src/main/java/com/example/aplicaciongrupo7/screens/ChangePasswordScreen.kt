package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.R
import com.example.aplicaciongrupo7.data.UserManager
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val currentUser = userManager.currentUser
    val coroutineScope = rememberCoroutineScope()
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
            // Botón volver
            if (!isLandscape) {
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            }

            // Imagen de logo
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.logoinicio),
                contentDescription = "Logo Level-Up Gamer",
                modifier = Modifier
                    .size(if (isLandscape) 100.dp else 150.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "Cambiar Contraseña",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ingresa tu contraseña actual y la nueva contraseña",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 24.dp else 48.dp))

            // Mostrar mensajes
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
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (successMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1B5E20)
                    )
                ) {
                    Text(
                        text = successMessage,
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 1. Contraseña actual con TextButton
            OutlinedTextField(
                value = currentPassword,
                onValueChange = {
                    currentPassword = it
                    errorMessage = ""
                },
                label = {
                    Text(
                        "Contraseña Actual *",
                        color = Color.White
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.isNotEmpty(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Contraseña actual",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                },
                trailingIcon = {
                    TextButton(
                        onClick = { showCurrentPassword = !showCurrentPassword },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            if (showCurrentPassword) "OCULTAR" else "MOSTRAR",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                visualTransformation = if (showCurrentPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
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

            // 2. Nueva contraseña con TextButton
            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorMessage = ""
                },
                label = {
                    Text(
                        "Nueva Contraseña *",
                        color = Color.White
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.isNotEmpty(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Nueva contraseña",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                },
                trailingIcon = {
                    TextButton(
                        onClick = { showNewPassword = !showNewPassword },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            if (showNewPassword) "OCULTAR" else "MOSTRAR",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                visualTransformation = if (showNewPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
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

            // 3. Confirmar nueva contraseña con TextButton
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorMessage = ""
                },
                label = {
                    Text(
                        "Confirmar Nueva Contraseña *",
                        color = Color.White
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.isNotEmpty(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Confirmar contraseña",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                },
                trailingIcon = {
                    TextButton(
                        onClick = { showConfirmPassword = !showConfirmPassword },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            if (showConfirmPassword) "OCULTAR" else "MOSTRAR",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                visualTransformation = if (showConfirmPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
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

            // Requisitos de contraseña
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "La nueva contraseña debe tener:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    PasswordRequirement(
                        isValid = newPassword.length >= 4,
                        text = "Al menos 4 caracteres"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    when {
                        currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                            errorMessage = "Por favor completa todos los campos"
                        }
                        newPassword.length < 4 -> {
                            errorMessage = "La nueva contraseña debe tener al menos 4 caracteres"
                        }
                        newPassword != confirmPassword -> {
                            errorMessage = "Las nuevas contraseñas no coinciden"
                        }
                        newPassword == currentPassword -> {
                            errorMessage = "La nueva contraseña debe ser diferente a la actual"
                        }
                        currentUser?.email == null -> {
                            errorMessage = "No se pudo obtener la información del usuario"
                        }
                        else -> {
                            isLoading = true
                            errorMessage = ""
                            successMessage = ""

                            coroutineScope.launch {
                                try {
                                    val changeSuccess = userManager.changePassword(
                                        currentUser.email,
                                        currentPassword,
                                        newPassword
                                    )

                                    if (changeSuccess) {
                                        successMessage = "¡Contraseña cambiada exitosamente!"

                                        // Auto-redirección después de 2 segundos
                                        kotlinx.coroutines.delay(2000)
                                        onSuccess()
                                    } else {
                                        errorMessage = "Contraseña actual incorrecta"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Error: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
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
                    Text("Cambiar Contraseña")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}

@Composable
fun PasswordRequirement(
    isValid: Boolean,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isValid) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = if (isValid) Color(0xFF4CAF50) else Color(0xFFF44336)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (isValid) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.7f)
        )
    }
}