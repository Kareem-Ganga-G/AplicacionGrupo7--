package com.example.aplicaciongrupo7.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.data.Product
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue


@Composable
fun SimpleGameItem(
    item: Product,
    onAddToCart: () -> Unit,
    cartQuantity: Int = 0
) {
    // 🔹 ESTADO PARA ANIMACIÓN
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.2f else 1f,
        label = "cartScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = item.title,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

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

            // ✅ STOCK (NO SE BORRÓ)
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

        IconButton(
            enabled = item.stock > cartQuantity,
            onClick = {
                pressed = true
                onAddToCart()
                // 🔁 volver a estado normal
                pressed = false
            }
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Agregar al carrito",
                modifier = Modifier.scale(scale)
            )
        }
    }
}
