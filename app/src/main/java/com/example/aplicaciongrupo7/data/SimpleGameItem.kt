package com.example.aplicaciongrupo7.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// Interfaz común para productos y juegos
interface ProductLike {
    val id: Int
    val title: String
    val genre: String
    val price: String
    val rating: Float
    val imageRes: Int
    val stock: Int
}

@Composable
fun SimpleGameItem(
    item: Any, // Acepta cualquier objeto
    onAddToCart: (() -> Unit)? = null,
    cartQuantity: Int = 0
) {
    // Convertir el item a propiedades usando when
    val title = when (item) {
        is com.example.aplicaciongrupo7.data.Product -> item.title
        else -> item.toString()
    }

    val genre = when (item) {
        is com.example.aplicaciongrupo7.data.Product -> item.genre
        else -> ""
    }

    val price = when (item) {
        is com.example.aplicaciongrupo7.data.Product -> item.price
        else -> "$0.00"
    }

    val rating = when (item) {
        is com.example.aplicaciongrupo7.data.Product -> item.rating
        else -> 0f
    }

    val stock = when (item) {
        is com.example.aplicaciongrupo7.data.Product -> item.stock
        else -> 0
    }

    val imageRes = when (item) {
        is com.example.aplicaciongrupo7.data.Product -> item.imageRes
        else -> android.R.drawable.ic_menu_report_image // Imagen por defecto
    }

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
            // IMAGEN DEL PRODUCTO

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = genre,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating y Stock
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⭐ $rating",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stock: $stock",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (stock > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = price,
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

            if (onAddToCart != null && stock > 0) {
                IconButton(
                    onClick = onAddToCart,
                    enabled = stock > 0
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "Agregar al carrito",
                        tint = if (stock > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}