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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicaciongrupo7.R
import com.example.aplicaciongrupo7.data.User
import com.example.aplicaciongrupo7.data.UserManager
import kotlinx.coroutines.launch

// --- COLORES TEMA DARK GAMER ---
private val DarkBg = Color(0xFF0F111A)
private val InputBg = Color(0xFF1E293B)
private val AccentColor = Color(0xFF5E35B1) // Violeta
private val SuccessColor = Color(0xFF4CAF50) // Verde Neon
private val ErrorColor = Color(0xFFEF5350)   // Rojo Suave

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit,
    onBack: () -> Unit
) {
    // --- ESTADOS DE LÓGICA ---
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Estados visuales extra (Ver contraseña)
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userManager = remember { UserManager(context) }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

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
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // 1. LOGO Y TÍTULO
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(AccentColor.copy(alpha = 0.4f), Color.Transparent)
                                )
                            )
                    )
                    Image(
                        painter = painterResource(id = R.drawable.logoinicio),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Text(
                    text = "CREAR CUENTA",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Únete a la comunidad gamer",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                )

                // 2. TARJETA DE ERROR
                if (errorMessage.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = ErrorColor.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, ErrorColor.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = ErrorColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = errorMessage, color = ErrorColor, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // 3. FORMULARIO (Inputs Modernos)

                // Username
                GamerTextField(
                    value = username,
                    onValueChange = { username = it; errorMessage = "" },
                    label = "Usuario",
                    icon = Icons.Default.Person,
                    placeholder = "Tu alias gamer"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                GamerTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = "" },
                    label = "Email",
                    icon = Icons.Default.Email,
                    placeholder = "ejemplo@correo.com"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                GamerPasswordField(
                    value = password,
                    onValueChange = { password = it; errorMessage = "" },
                    label = "Contraseña",
                    isVisible = passwordVisible,
                    onToggleVisibility = { passwordVisible = !passwordVisible }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password
                GamerPasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMessage = "" },
                    label = "Confirmar Contraseña",
                    isVisible = confirmPasswordVisible,
                    onToggleVisibility = { confirmPasswordVisible = !confirmPasswordVisible }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 4. CHECKLIST DE REQUISITOS (Estilo "Misiones")
                Card(
                    colors = CardDefaults.cardColors(containerColor = InputBg.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "REQUISITOS DEL SISTEMA",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        RequirementRow(isValid = username.isNotEmpty(), text = "Usuario único")
                        RequirementRow(isValid = isValidEmail(email), text = "Email válido")
                        RequirementRow(isValid = password.length >= 4, text = "Mínimo 4 caracteres")
                        RequirementRow(isValid = password == confirmPassword && password.isNotEmpty(), text = "Contraseñas coinciden")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. BOTÓN DE REGISTRO
                Button(
                    onClick = {
                        // --- TU LÓGICA ORIGINAL DE VALIDACIÓN ---
                        when {
                            username.isEmpty() || password.isEmpty() || email.isEmpty() || confirmPassword.isEmpty() -> {
                                errorMessage = "Por favor completa todos los campos"
                            }
                            !isValidEmail(email) -> {
                                errorMessage = "Por favor ingresa un email válido"
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
                                errorMessage = ""

                                coroutineScope.launch {
                                    try {
                                        val success = userManager.saveUser(User(username, password, email, isAdmin = false))
                                        if (success) {
                                            onRegisterSuccess()
                                        } else {
                                            errorMessage = "Error al registrar usuario"
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(8.dp, RoundedCornerShape(12.dp), ambientColor = AccentColor, spotColor = AccentColor),
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
                        Text("CREAR CUENTA", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 6. NAVEGACIÓN
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿Ya tienes cuenta?", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = onBackToLogin) {
                        Text("Inicia sesión", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// --- COMPONENTES REUTILIZABLES PARA REGISTRO ---

@Composable
fun GamerTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(icon, contentDescription = null) },
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
}

@Composable
fun GamerPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle"
                )
            }
        },
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
            unfocusedLeadingIconColor = Color.Gray,
            focusedTrailingIconColor = AccentColor,
            unfocusedTrailingIconColor = Color.Gray
        )
    )
}

@Composable
fun RequirementRow(isValid: Boolean, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isValid) SuccessColor else Color.Gray,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isValid) Color.White else Color.Gray,
            textDecoration = if (isValid) androidx.compose.ui.text.style.TextDecoration.None else androidx.compose.ui.text.style.TextDecoration.None
        )
    }
}

// Helper para validar email (incluido aquí para que no de error si no lo tienes en utils)
private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
    return email.matches(emailRegex)
}