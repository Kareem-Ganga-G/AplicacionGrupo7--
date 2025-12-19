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
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.R
import com.example.aplicaciongrupo7.data.UserManager
import androidx.compose.foundation.BorderStroke

// --- COLORES TEMA DARK GAMER ---
// (Mismos colores que definimos antes)
val DarkBackgroundProfile = Color(0xFF0F111A)
val SurfaceColorProfile = Color(0xFF1E293B)
val AccentColorProfile = Color(0xFF5E35B1)
val TextPrimaryProfile = Color(0xFFFFFFFF)
val TextSecondaryProfile = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userType: String,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onChangePassword: () -> Unit
) {
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    // --- CORRECCIÓN AQUÍ ---
    // En lugar de leerlo una sola vez, lo guardamos en un estado
    var currentUser by remember { mutableStateOf(userManager.currentUser) }

    // Este bloque se ejecuta apenas entras a la pantalla para asegurar los datos
    LaunchedEffect(Unit) {
        // Forzamos la lectura del usuario actual
        currentUser = userManager.currentUser
    }
    // -----------------------

    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackgroundProfile,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 8.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextPrimaryProfile
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                if (userType == "admin") {
                    Surface(
                        color = AccentColorProfile.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, AccentColorProfile)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = AccentColorProfile,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ADMIN",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = AccentColorProfile
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- HEADER PERFIL ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(AccentColorProfile.copy(alpha = 0.5f), Color.Transparent)
                            )
                        )
                )

                Image(
                    painter = painterResource(id = R.drawable.logoinicio),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(3.dp, AccentColorProfile, CircleShape)
                        .background(Color.Black),
                    contentScale = ContentScale.Fit
                )
            }

            // AQUI USAMOS EL ESTADO currentUser
            Text(
                text = currentUser?.username ?: "Cargando usuario...", // Texto de respaldo
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryProfile
            )

            Text(
                text = currentUser?.email ?: "Cargando correo...", // Texto de respaldo
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryProfile,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- TARJETA INFO ---
            ProfileSectionCard(title = "Información Personal") {
                ProfileItem(
                    icon = Icons.Default.Person,
                    title = "Nombre de Usuario",
                    value = currentUser?.username ?: "Sin datos"
                )
                Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(start = 56.dp))

                ProfileItem(
                    icon = Icons.Default.Email,
                    title = "Correo Electrónico",
                    value = currentUser?.email ?: "Sin datos"
                )
                Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(start = 56.dp))

                ProfileItem(
                    icon = Icons.Default.VerifiedUser,
                    title = "Nivel de Acceso",
                    value = if (userType == "admin") "Administrador" else "Usuario Gamer",
                    valueColor = if (userType == "admin") AccentColorProfile else TextPrimaryProfile
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- SEGURIDAD ---
            ProfileSectionCard(title = "Seguridad y Cuenta") {
                ProfileOption(
                    icon = Icons.Default.Lock,
                    title = "Cambiar Contraseña",
                    onClick = onChangePassword
                )

                Divider(color = Color.White.copy(alpha = 0.05f), modifier = Modifier.padding(horizontal = 16.dp))

                ProfileOption(
                    icon = Icons.Default.ExitToApp,
                    title = "Cerrar Sesión",
                    onClick = { showLogoutDialog = true },
                    textColor = Color(0xFFEF5350),
                    iconColor = Color(0xFFEF5350)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- FOOTER ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Level-Up Gamer Store",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondaryProfile.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = SurfaceColorProfile,
            title = {
                Text("¿Cerrar Sesión?", color = TextPrimaryProfile, fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Tendrás que ingresar tus datos nuevamente.", color = TextSecondaryProfile)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        // Importante: Limpiar datos al salir si tu userManager lo requiere
                        // userManager.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Cerrar Sesión", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = TextSecondaryProfile)
                }
            }
        )
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun ProfileSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = AccentColorProfile,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth().shadow(8.dp, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceColorProfile),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun ProfileItem(
    icon: ImageVector,
    title: String,
    value: String,
    valueColor: Color = TextPrimaryProfile
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondaryProfile,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryProfile
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        }
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    textColor: Color = TextPrimaryProfile,
    iconColor: Color = TextPrimaryProfile
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(20.dp),
                tint = iconColor
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = TextSecondaryProfile.copy(alpha = 0.5f)
        )
    }
}