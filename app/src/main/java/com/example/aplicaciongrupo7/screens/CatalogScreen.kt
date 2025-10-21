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
import androidx.compose.ui.platform.LocalConfiguration
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

    // Estado del carrito
    var cartItems by remember { mutableStateOf(cartManager.getCartItems(games)) }
    val cartItemsCount by remember { mutableStateOf(cartManager.getCartItemsCount()) }

    // Detectar orientación
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

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
                title = {
                    Text(
                        "Catálogo", // Texto más corto
                        style = if (isLandscape) MaterialTheme.typography.bodyMedium
                        else MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (cartItemsCount > 0) {
                                Badge(
                                    modifier = Modifier.size(if (isLandscape) 16.dp else 20.dp)
                                ) {
                                    Text(
                                        cartItemsCount.toString(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(
                            onClick = onGoToCart,
                            modifier = Modifier.size(if (isLandscape) 24.dp else 48.dp) // Reducido 24dp
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Carrito",
                                modifier = Modifier.size(if (isLandscape) 16.dp else 24.dp) // Reducido 16dp
                            )
                        }
                    }

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.size(if (isLandscape) 24.dp else 48.dp) // Reducido 24dp
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Salir",
                            modifier = Modifier.size(if (isLandscape) 16.dp else 24.dp) // Reducido 16dp
                        )
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
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isLandscape) 4.dp else 16.dp), // Reducido a 4dp
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isLandscape) 1.dp else 4.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = if (isLandscape) 8.dp else 16.dp,
                        vertical = if (isLandscape) 4.dp else 16.dp // Reducido a 4dp
                    )
                ) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = {
                            Text(
                                "Buscar", // Texto más corto
                                style = if (isLandscape) MaterialTheme.typography.labelSmall
                                else MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar",
                                modifier = Modifier.size(if (isLandscape) 14.dp else 24.dp) // Reducido 14dp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = if (isLandscape) 36.dp else 56.dp), // Altura máxima reducida 36dp
                        singleLine = true,
                        textStyle = if (isLandscape) MaterialTheme.typography.bodySmall
                        else MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 12.dp)) // Reducido 4dp


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val chipTextStyle = if (isLandscape) MaterialTheme.typography.labelSmall
                        else MaterialTheme.typography.bodySmall

                        FilterChip(
                            selected = sortOption == "nombre",
                            onClick = { sortOption = "nombre" },
                            label = {
                                Text("N", style = chipTextStyle) // Solo inicial
                            },
                            modifier = Modifier.padding(horizontal = 0.dp)
                        )
                        FilterChip(
                            selected = sortOption == "precio",
                            onClick = { sortOption = "precio" },
                            label = {
                                Text("P", style = chipTextStyle) // Solo inicial
                            },
                            modifier = Modifier.padding(horizontal = 0.dp)
                        )
                        FilterChip(
                            selected = sortOption == "rating",
                            onClick = { sortOption = "rating" },
                            label = {
                                Text("R", style = chipTextStyle) // Solo inicial
                            },
                            modifier = Modifier.padding(horizontal = 0.dp)
                        )
                        FilterChip(
                            selected = sortOption == "genero",
                            onClick = { sortOption = "genero" },
                            label = {
                                Text("C", style = chipTextStyle) // Solo inicial
                            },
                            modifier = Modifier.padding(horizontal = 0.dp)
                        )
                    }
                }
            }

            Text(
                text = if (searchText.text.isEmpty()) {
                    "${filteredGames.size} prod"
                } else {
                    "${filteredGames.size} \"${searchText.text}\""
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = if (isLandscape) 8.dp else 16.dp,
                    vertical = if (isLandscape) 1.dp else 8.dp
                )
            )

            Spacer(modifier = Modifier.height(if (isLandscape) 1.dp else 8.dp))

            // Lista de productos
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
                                "Sin productos"
                            } else {
                                "No hay"
                            },
                            style = if (isLandscape) MaterialTheme.typography.labelSmall
                            else MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = if (isLandscape) 4.dp else 16.dp), // Reducido 4dp
                    verticalArrangement = Arrangement.spacedBy(if (isLandscape) 2.dp else 8.dp) // Reducido 2dp
                ) {
                    items(filteredGames) { game ->
                        val cartItem = cartItems.find { it.product.id == game.id }
                        SimpleGameItem(
                            item = game,
                            onAddToCart = {
                                cartManager.addToCart(game.id, 1)
                                refreshCart()
                            },
                            cartQuantity = cartItem?.quantity ?: 0
                        )
                    }
                }
            }
        }
    }
}