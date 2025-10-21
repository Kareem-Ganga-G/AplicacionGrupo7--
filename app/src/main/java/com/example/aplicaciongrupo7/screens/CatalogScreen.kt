package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.components.SimpleGameItem
import com.example.aplicaciongrupo7.data.CartManager
import com.example.aplicaciongrupo7.data.GameManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onLogout: () -> Unit,
    onGoToCart: () -> Unit
) {
    val context = LocalContext.current
    val gameManager = remember { GameManager(context) }
    val cartManager = remember { CartManager(context) }

    val games by remember { mutableStateOf(gameManager.getGames()) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var sortOption by remember { mutableStateOf("nombre") }

    // Estado del carrito - CORREGIDO
    var cartItems by remember { mutableStateOf(cartManager.getCartItems(games)) }
    val cartItemsCount by remember { mutableStateOf(cartManager.getCartItemsCount()) }

    val filteredGames = remember(games, searchText.text, sortOption) {
        var result = games

        // filtrado
        if (searchText.text.isNotEmpty()) {
            result = result.filter { game ->
                game.title.contains(searchText.text, ignoreCase = true) ||
                        game.genre.contains(searchText.text, ignoreCase = true)
            }
        }

        // ordenado
        result = when (sortOption) {
            "precio" -> result.sortedBy {
                it.price.replace("$", "").replace(",", "").toFloatOrNull() ?: 0f
            }
            "rating" -> result.sortedByDescending { it.rating }
            "genero" -> result.sortedBy { it.genre }
            else -> result.sortedBy { it.title } // "nombre"
        }

        result
    }

    // Función para actualizar el carrito
    fun refreshCart() {
        cartItems = cartManager.getCartItems(games)
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Catálogo Gamer") },
                actions = {
                    BadgedBox(
                        badge = {
                            if (cartItemsCount > 0) {
                                Badge {
                                    Text(
                                        cartItemsCount.toString(),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = onGoToCart) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Carrito de compras"
                            )
                        }
                    }

                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // busqueda
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Buscar productos...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Buscar")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Ordenar por:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = sortOption == "nombre",
                            onClick = { sortOption = "nombre" },
                            label = { Text("Nombre") }
                        )
                        FilterChip(
                            selected = sortOption == "precio",
                            onClick = { sortOption = "precio" },
                            label = { Text("Precio") }
                        )
                        FilterChip(
                            selected = sortOption == "rating",
                            onClick = { sortOption = "rating" },
                            label = { Text("Rating") }
                        )
                        FilterChip(
                            selected = sortOption == "genero",
                            onClick = { sortOption = "genero" },
                            label = { Text("Categoría") }
                        )
                    }
                }
            }

            // Contador de resultados
            Text(
                text = if (searchText.text.isEmpty()) {
                    "Mostrando ${filteredGames.size} productos"
                } else {
                    "${filteredGames.size} resultados para \"${searchText.text}\""
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de los productos - CORREGIDO
            if (filteredGames.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (searchText.text.isEmpty()) {
                                "No hay productos en el catálogo"
                            } else {
                                "No se encontraron productos para \"${searchText.text}\""
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchText.text.isEmpty()) {
                                "Contacta al administrador para agregar productos"
                            } else {
                                "Intenta con otros términos de búsqueda"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                ) {
                    items(filteredGames) { game ->
                        val cartItem = cartItems.find { it.product.id == game.id }
                        SimpleGameItem(
                            item = game, // ← Cambiado de 'product' a 'item'
                            onAddToCart = {
                                cartManager.addToCart(game.id, 1)
                                refreshCart()
                            },
                            cartQuantity = cartItem?.quantity ?: 0
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}