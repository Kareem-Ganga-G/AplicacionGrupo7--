package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.components.SimpleGameItem
import com.example.aplicaciongrupo7.data.CartManager
import com.example.aplicaciongrupo7.data.GameManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onLogout: () -> Unit,
    onGoToCart: () -> Unit
) {
    val context = LocalContext.current
    val gameManager = remember { GameManager(context) }
    val cartManager = remember { CartManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val games by remember { mutableStateOf(gameManager.getGames()) }
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var sortOption by remember { mutableStateOf("nombre") }

    // ESTA LÍNEA ES CRUCIAL - Pasar los productos al CartManager
    LaunchedEffect(games) {
        cartManager.setProducts(games)
    }

    // Estado del carrito - usando StateFlow
    val cartItemsCount by cartManager.cartItemsCount.collectAsState()
    val cartItems by cartManager.cartItems.collectAsState()

    // Estado local para la cantidad en carrito por producto
    val productQuantities = remember { mutableStateMapOf<Int, Int>() }

    // Detectar orientación
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Inicializar cantidades de productos
    LaunchedEffect(cartItems) {
        productQuantities.clear()
        cartItems.forEach { cartItem ->
            productQuantities[cartItem.product.id] = cartItem.quantity
        }
    }

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

    // Función para agregar al carrito
    fun addToCart(productId: Int) {
        coroutineScope.launch {
            try {
                cartManager.addToCart(productId, 1)
                // El StateFlow actualizará automáticamente cartItemsCount y cartItems
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Fondo negro completo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Scaffold(
            topBar = {
                SmallTopAppBar(
                    title = {
                        Text(
                            "Catálogo",
                            style = if (isLandscape) MaterialTheme.typography.bodyMedium
                            else MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xFF1A1A1A),
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    actions = {
                        BadgedBox(
                            badge = {
                                if (cartItemsCount > 0) {
                                    Badge(
                                        modifier = Modifier.size(if (isLandscape) 16.dp else 20.dp),
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            cartItemsCount.toString(),
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            IconButton(
                                onClick = onGoToCart,
                                modifier = Modifier.size(if (isLandscape) 24.dp else 48.dp)
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Carrito",
                                    modifier = Modifier.size(if (isLandscape) 16.dp else 24.dp),
                                    tint = Color.White
                                )
                            }
                        }

                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.size(if (isLandscape) 24.dp else 48.dp)
                        ) {
                            Icon(
                                Icons.Default.ExitToApp,
                                contentDescription = "Salir",
                                modifier = Modifier.size(if (isLandscape) 16.dp else 24.dp),
                                tint = Color.White
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
                    .background(Color.Black)
            ) {
                // Card de búsqueda con estilo oscuro
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(if (isLandscape) 4.dp else 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A1A)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (isLandscape) 1.dp else 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = if (isLandscape) 8.dp else 16.dp,
                            vertical = if (isLandscape) 4.dp else 16.dp
                        )
                    ) {
                        OutlinedTextField(
                            value = searchText,
                            onValueChange = { searchText = it },
                            label = {
                                Text(
                                    "Buscar",
                                    style = if (isLandscape) MaterialTheme.typography.labelSmall
                                    else MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Buscar",
                                    modifier = Modifier.size(if (isLandscape) 14.dp else 24.dp),
                                    tint = Color.White
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = if (isLandscape) 36.dp else 56.dp),
                            singleLine = true,
                            textStyle = if (isLandscape) MaterialTheme.typography.bodySmall
                            else MaterialTheme.typography.bodyMedium,
                            colors = TextFieldDefaults.colors(
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedContainerColor = Color(0x80212121),
                                focusedContainerColor = Color(0x80212121),
                                unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                                focusedLabelColor = MaterialTheme.colorScheme.primary,
                                cursorColor = Color.White,
                                unfocusedIndicatorColor = Color.White.copy(alpha = 0.5f),
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(modifier = Modifier.height(if (isLandscape) 4.dp else 12.dp))

                        // Filtros con estilo oscuro
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
                                    Text("Nombre", style = chipTextStyle, color = Color.White)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (sortOption == "nombre") MaterialTheme.colorScheme.primary
                                    else Color(0xFF2A2A2A),
                                    labelColor = if (sortOption == "nombre") Color.White
                                    else Color.White.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                            FilterChip(
                                selected = sortOption == "precio",
                                onClick = { sortOption = "precio" },
                                label = {
                                    Text("Precio", style = chipTextStyle, color = Color.White)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (sortOption == "precio") MaterialTheme.colorScheme.primary
                                    else Color(0xFF2A2A2A),
                                    labelColor = if (sortOption == "precio") Color.White
                                    else Color.White.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                            FilterChip(
                                selected = sortOption == "rating",
                                onClick = { sortOption = "rating" },
                                label = {
                                    Text("Rating", style = chipTextStyle, color = Color.White)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (sortOption == "rating") MaterialTheme.colorScheme.primary
                                    else Color(0xFF2A2A2A),
                                    labelColor = if (sortOption == "rating") Color.White
                                    else Color.White.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                            FilterChip(
                                selected = sortOption == "genero",
                                onClick = { sortOption = "genero" },
                                label = {
                                    Text("Género", style = chipTextStyle, color = Color.White)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = if (sortOption == "genero") MaterialTheme.colorScheme.primary
                                    else Color(0xFF2A2A2A),
                                    labelColor = if (sortOption == "genero") Color.White
                                    else Color.White.copy(alpha = 0.8f)
                                ),
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }

                // Contador de resultados
                Text(
                    text = if (searchText.text.isEmpty()) {
                        "${filteredGames.size} productos"
                    } else {
                        "${filteredGames.size} resultados para \"${searchText.text}\""
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(
                        horizontal = if (isLandscape) 8.dp else 16.dp,
                        vertical = if (isLandscape) 4.dp else 8.dp
                    )
                )

                Spacer(modifier = Modifier.height(if (isLandscape) 2.dp else 8.dp))

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
                                    "No hay productos en el catálogo"
                                } else {
                                    "No se encontraron productos"
                                },
                                style = if (isLandscape) MaterialTheme.typography.bodyMedium
                                else MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.7f),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .padding(horizontal = if (isLandscape) 4.dp else 16.dp)
                            .background(Color.Black),
                        verticalArrangement = Arrangement.spacedBy(if (isLandscape) 4.dp else 8.dp)
                    ) {
                        items(filteredGames) { game ->
                            val cartQuantity = productQuantities[game.id] ?: 0
                            SimpleGameItem(
                                item = game,
                                onAddToCart = {
                                    addToCart(game.id)
                                },
                                cartQuantity = cartQuantity
                            )
                        }
                    }
                }
            }
        }
    }
}