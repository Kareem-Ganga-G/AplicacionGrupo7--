package com.example.aplicaciongrupo7.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.data.Product

/**
 * GameItem - composable que muestra un producto en el listado.
 * Firma compatible con AdminScreen (incluye onDelete).
 */
@Composable
fun GameItem(
    game: Product,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    showAdminActions: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Usa SafeGameImage (está en el mismo package "components")
            SafeGameImage(
                imageRes = game.imageRes,
                title = game.title,
                modifier = Modifier.size(70.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(game.title, style = MaterialTheme.typography.titleMedium)
                Text(game.genre, style = MaterialTheme.typography.bodySmall)
                Text(game.price, style = MaterialTheme.typography.bodyMedium)
            }

            if (showAdminActions) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar")
                }
            }
        }
    }
}
