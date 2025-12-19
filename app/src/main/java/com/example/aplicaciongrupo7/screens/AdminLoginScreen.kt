package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicaciongrupo7.R
import com.example.aplicaciongrupo7.data.UserManager
import kotlinx.coroutines.launch

// --- COLORES TEMA DARK GAMER ---
private val DarkBg = Color(0xFF0F111A)
private val InputBg = Color(0xFF1E293B)
private val AccentColor = Color(0xFF5E35B1) // Violeta
private val AdminAlertColor = Color(0xFFFFB74D) // Naranja suave para notas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
    onLoginSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) } // Agregado para mejor UX

    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = DarkBg,
        topBar = {
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

        Box(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // 1. HEADER CON ICONO DE SEGURIDAD
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(AccentColor.copy(alpha = 0.3f), Color.Transparent)
                                )
                            )
                    )
                    // Usamos la imagen del logo, pero le superponemos un icono pequeño de Admin
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Image(
                            painter = painterResource(id = R.drawable.logoinicio),
                            contentDescription = "Logo",
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Fit
                        )
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .background(AccentColor, CircleShape)
                                .padding(4.dp)
                                .size(24.dp)
                        )
                    }
                }

                Text(
                    text = "ACCESO ADMIN",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "Área restringida. Autorización requerida.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                )

                // 2. ERROR CARD
                if (errorMessage.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFB00020).copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, Color(0xFFB00020).copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF5350))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = errorMessage, color = Color(0xFFEF5350), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // 3. INPUTS ESTILIZADOS
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        errorMessage = ""
                    },
                    label = { Text("Usuario Administrador") },
                    placeholder = { Text("root_access") },
                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedBorderColor = AccentColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = AccentColor,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = AccentColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLeadingIconColor = AccentColor,
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
                    label = { Text("Clave de Acceso") },
                    leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle",
                                tint = Color.Gray
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedBorderColor = AccentColor,
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = AccentColor,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = AccentColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLeadingIconColor = AccentColor,
                        unfocusedLeadingIconColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 4. BOTÓN DE ACCESO
                Button(
                    onClick = {
                        if (username.isEmpty() || password.isEmpty()) {
                            errorMessage = "Por favor completa todos los campos"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = ""

                        coroutineScope.launch {
                            try {
                                val loginSuccess = userManager.validateLogin(username, password)

                                if (loginSuccess) {
                                    val user = userManager.currentUser
                                    if (user?.isAdmin == true) {
                                        onLoginSuccess()
                                    } else {
                                        errorMessage = "Acceso denegado: No tienes permisos"
                                    }
                                } else {
                                    errorMessage = "Credenciales inválidas"
                                }
                            } catch (e: Exception) {
                                errorMessage = "Error del sistema: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(10.dp, RoundedCornerShape(12.dp), ambientColor = AccentColor, spotColor = AccentColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentColor,
                        disabledContainerColor = Color.Gray
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("ACCEDER AL SISTEMA", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 5. NOTA DE DESARROLLADOR (Estilo Consola/Terminal)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(4.dp), // Más cuadrado tipo terminal
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "// SYSTEM LOG: DEBUG MODE",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = AdminAlertColor,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(bottom = 8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "user: ",
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "p.lopez",
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "pass: ",
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "admin123",
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}