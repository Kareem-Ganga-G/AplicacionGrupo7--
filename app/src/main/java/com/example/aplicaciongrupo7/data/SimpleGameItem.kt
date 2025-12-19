package com.example.aplicaciongrupo7.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check // Nuevo icono para feedback
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.data.Product
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SimpleGameItem(
    item: Product,
    onAddToCart: () -> Unit,
    cartQuantity: Int = 0
) {
    // 🔹 NUEVO ESTADO PARA ANIMACIÓN (Reemplaza al anterior 'pressed')
    // Usamos 'isAdded' para saber si debemos mostrar el check verde
    var isAdded by remember { mutableStateOf(false) }

    // Necesitamos un Scope para poder usar 'delay' (la pausa de la animación)
    val scope = rememberCoroutineScope()

    // 1. Animación de Escala (Efecto Rebote con Spring)
    val scale by animateFloatAsState(
        targetValue = if (isAdded) 1.2f else 1f, // Crece al 120%
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // Rebote medio
            stiffness = Spring.StiffnessLow
        ),
        label = "cartScale"
    )

    // 2. Animación de Color (De tu color primario a Verde y viceversa)
    val iconColor by animateColorAsState(
        targetValue = if (isAdded) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
        label = "cartColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- SECCIÓN DE IMAGEN (INTACTA) ---
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        // --- SECCIÓN DE TEXTOS (INTACTA) ---
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.genre,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = item.price,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // ✅ STOCK (CONSERVADO TAL CUAL)
            Text(
                text = "Stock disponible: ${item.stock}",
                style = MaterialTheme.typography.bodySmall,
                color = if (item.stock > 0)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.error
            )

            if (cartQuantity > 0) {
                Text(
                    text = "En carrito: $cartQuantity",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // --- BOTÓN (MODIFICADO SOLO PARA ANIMACIÓN) ---
        IconButton(
            enabled = item.stock > cartQuantity,
            onClick = {
                // 1. Ejecutamos la lógica de compra original
                onAddToCart()

                // 2. Iniciamos la animación
                scope.launch {
                    isAdded = true       // Activa animación (crece y se pone verde)
                    delay(600)           // Espera 600ms para que el usuario lo vea
                    isAdded = false      // Vuelve al estado normal
                }
            },
            // Aplicamos la escala al botón entero para mejor sensación táctil
            modifier = Modifier.scale(scale)
        ) {
            Icon(
                // Cambia el icono: Si se agregó muestra ✅, si no 🛒
                imageVector = if (isAdded) Icons.Default.Check else Icons.Default.ShoppingCart,
                contentDescription = "Agregar al carrito",
                tint = iconColor // Aplica el color animado
            )
        }
    }
}