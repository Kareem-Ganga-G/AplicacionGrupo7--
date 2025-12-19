package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicaciongrupo7.R
import com.example.aplicaciongrupo7.data.UserManager
import com.example.aplicaciongrupo7.data.isValidEmail
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke // <--- ESTO FALTABA

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (userType: String) -> Unit,
    onRegisterClick: () -> Unit,
    onAdminLogin: () -> Unit,
    onForgotPassword: () -> Unit,
    onBack: () -> Unit
) {
    // --- ESTADOS DE LÓGICA (INTACTOS) ---
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var useEmailLogin by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) } // Extra: Para ver contraseña

    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // --- COLORES DEL TEMA ---
    val darkBg = Color(0xFF0F111A)
    val inputBg = Color(0xFF1E293B)
    val accentColor = Color(0xFF5E35B1) // Violeta Gamer
    val errorColor = Color(0xFFEF5350)

    // Estructura principal
    Scaffold(
        containerColor = darkBg,
        topBar = {
            // Botón volver discreto
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onBack,
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // 1. LOGO CON EFECTO NEÓN
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    // Círculo de brillo detrás
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(accentColor.copy(alpha = 0.4f), Color.Transparent)
                                )
                            )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.logoinicio),
                        contentDescription = "Logo",
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // 2. TEXTOS DE BIENVENIDA
                Text(
                    text = "BIENVENIDO",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Ingresa a tu cuenta gamer",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )

                // 3. SELECTOR TIPO USUARIO (Estilizado)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .background(inputBg, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (useEmailLogin) "Usar Email" else "Usar Usuario",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Switch(
                        checked = useEmailLogin,
                        onCheckedChange = { useEmailLogin = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accentColor,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.Black.copy(alpha = 0.3f)
                        )
                    )
                }

                // 4. MENSAJE DE ERROR (Si existe)
                if (errorMessage.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = errorColor.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, errorColor.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = errorColor,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // 5. CAMPOS DE TEXTO (Inputs modernos)
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = ""
                    },
                    label = { Text(if (useEmailLogin) "Email" else "Usuario") },
                    placeholder = { Text(if (useEmailLogin) "ejemplo@correo.com" else "Tu alias gamer") },
                    leadingIcon = {
                        Icon(
                            if (useEmailLogin) Icons.Default.Email else Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = accentColor,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = accentColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLeadingIconColor = accentColor,
                        unfocusedLeadingIconColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = ""
                    },
                    label = { Text("Contraseña") },
                    placeholder = { Text("••••••••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Ver contraseña"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = inputBg,
                        unfocusedContainerColor = inputBg,
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = accentColor,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = accentColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLeadingIconColor = accentColor,
                        unfocusedLeadingIconColor = Color.Gray,
                        focusedTrailingIconColor = accentColor,
                        unfocusedTrailingIconColor = Color.Gray
                    )
                )

                // Olvidé contraseña alineado a la derecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onForgotPassword) {
                        Text(
                            "¿Olvidaste tu contraseña?",
                            color = accentColor.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 6. BOTÓN PRINCIPAL (Con sombra y gradiente visual)
                Button(
                    onClick = {
                        // --- TU LÓGICA ORIGINAL ---
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
                                errorMessage = "Error: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                        // -------------------------
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(10.dp, RoundedCornerShape(12.dp), ambientColor = accentColor, spotColor = accentColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 7. REGISTRO
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("¿No tienes cuenta?", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = onRegisterClick) {
                        Text("Regístrate aquí", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Divisor sutil
                Divider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(24.dp))

                // 8. ACCESO ADMIN
                OutlinedButton(
                    onClick = onAdminLogin,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Text("Acceso Administrador")
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}