package com.example.aplicaciongrupo7.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicaciongrupo7.R
import kotlinx.coroutines.delay
import androidx.compose.foundation.border


private val DarkBg = Color(0xFF0F111A)
private val AccentColor = Color(0xFF5E35B1)
private val SurfaceColor = Color(0xFF1E293B)

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    // Estado para la animación de entrada
    var isVisible by remember { mutableStateOf(false) }

    // Activamos la animación al iniciar
    LaunchedEffect(Unit) {
        delay(100) // Pequeña pausa para suavidad
        isVisible = true
    }

    // Animación de "latido" (Pulse) para el logo
    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // --- FONDO AMBIENTAL (Glow Violeta) ---
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-50).dp)
                .size(400.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentColor.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 1. LOGO ANIMADO
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .scale(scale) // Aquí aplicamos el latido
            ) {
                // Círculo decorativo detrás
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .border(BorderStroke(2.dp, Brush.verticalGradient(listOf(AccentColor, Color.Transparent))), CircleShape)
                )

                Image(
                    painter = painterResource(id = R.drawable.logoinicio),
                    contentDescription = "Logo Level-Up Gamer",
                    modifier = Modifier.size(140.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // 2. CONTENIDO ANIMADO (Entrada deslizante)
            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "LEVEL-UP GAMER",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "Tu destino gaming definitivo",
                        style = MaterialTheme.typography.titleMedium,
                        color = AccentColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 40.dp)
                    )

                    // 3. TARJETA DE ACCIONES
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = AccentColor),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceColor.copy(alpha = 0.9f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Comienza tu aventura",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            // BOTÓN INICIAR SESIÓN
                            Button(
                                onClick = onLoginClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AccentColor,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                            ) {
                                Icon(Icons.Default.Login, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("INICIAR SESIÓN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // BOTÓN REGISTRARSE
                            OutlinedButton(
                                onClick = onRegisterClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("CREAR CUENTA", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // DIVIDER CON "O"
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Divider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                                Text(" ó ", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                                Divider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.1f))
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // ACCESO ADMIN DISCRETO
                            TextButton(onClick = onAdminClick) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Acceso Administrativo", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 4. FOOTER CON CHIPS (Scroll horizontal)
                    Text(
                        text = "Explora nuestro catálogo:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val features = listOf("Procesadores", "Gráficas", "Monitores", "RAM", "Periféricos", "Sillas")

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(features) { feature ->
                            FeatureChip(text = feature)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureChip(text: String) {
    Surface(
        color = AccentColor.copy(alpha = 0.1f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, AccentColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontWeight = FontWeight.Medium
        )
    }
}