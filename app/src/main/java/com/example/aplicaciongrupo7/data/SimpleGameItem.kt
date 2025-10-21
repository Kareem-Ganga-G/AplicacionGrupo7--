package com.example.aplicaciongrupo7.data

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.data.Product

@Composable
fun SimpleGameItem(
    game: Product,
    onAddToCart: (() -> Unit)? = null,
    cartQuantity: Int = 0
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = game.genre,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating y Stock
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐ ${game.rating}",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stock: ${game.stock}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (game.stock > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = game.price,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                if (cartQuantity > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$cartQuantity en carrito",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (onAddToCart != null && game.stock > 0) {
                IconButton(
                    onClick = onAddToCart,
                    enabled = game.stock > 0
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Agregar al carrito",
                        tint = if (game.stock > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}