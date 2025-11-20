package com.example.aplicaciongrupo7.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.data.Product


@Composable
fun GameItem(
    game: Product,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onAddToCart: (() -> Unit)? = null,
    cartQuantity: Int = 0,
    showAdminActions: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = game.imageRes),
                contentDescription = "Imagen de ${game.title}",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Fit
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = game.genre,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Rating y Stock
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${game.rating}/5.0",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stock: ${game.stock}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (game.stock > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }

                // Mostrar cantidad en carrito si existe
                if (cartQuantity > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$cartQuantity en carrito",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Columna de precio y acciones
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = game.price,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Botones de acciones
                Row {
                    if (onAddToCart != null && game.stock > 0) {
                        IconButton(
                            onClick = onAddToCart,
                            modifier = Modifier.size(32.dp),
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

                    // Botones de administrador
                    if (showAdminActions) {
                        if (onEdit != null) {
                            IconButton(
                                onClick = onEdit,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editar producto",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (onDelete != null) {
                            IconButton(
                                onClick = onDelete,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar producto",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleGameItem(
    game: Product,
    onAddToCart: (() -> Unit)? = null,
    cartQuantity: Int = 0
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = game.imageRes),
                contentDescription = "Imagen de ${game.title}",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Fit
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = game.genre,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Rating y Stock
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${game.rating}/5.0",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stock: ${game.stock}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (game.stock > 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )
                }

                if (cartQuantity > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$cartQuantity en carrito",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = game.price,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (onAddToCart != null && game.stock > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    IconButton(
                        onClick = onAddToCart,
                        modifier = Modifier.size(32.dp),
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
}