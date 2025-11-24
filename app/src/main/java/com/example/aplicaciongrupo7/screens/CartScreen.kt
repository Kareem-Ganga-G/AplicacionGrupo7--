package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.data.CartManager
import com.example.aplicaciongrupo7.data.GameManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val gameManager = remember { GameManager(context) }
    val cartManager = remember { CartManager(context) }

    val allGames by remember { mutableStateOf(gameManager.getGames()) }

    // StateFlow para los items del carrito
    val cartItems by cartManager.cartItems.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Actualizar el carrito cuando cambien los juegos
    LaunchedEffect(allGames) {
        cartManager.setProducts(allGames)
    }

    // Calcular el total del carrito
    val cartTotal by remember(cartItems) {
        derivedStateOf {
            cartItems.sumOf { cartItem ->
                val priceString = cartItem.product.price
                    .replace("$", "")
                    .replace(".", "")
                    .replace(",", ".")
                val price = priceString.toDoubleOrNull() ?: 0.0
                price * cartItem.quantity
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:", style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = "$${"%,.0f".format(cartTotal).replace(",", ".")}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val outOfStockItems = cartItems.filter {
                                    it.quantity > it.product.stock
                                }

                                if (outOfStockItems.isNotEmpty()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Algunos productos no tienen stock suficiente"
                                        )
                                    }
                                } else {
                                    cartManager.clearCart()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡Compra realizada con éxito!")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Finalizar Compra")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Tu carrito está vacío",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Agrega productos desde el catálogo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onBack) {
                            Text("Explorar Catálogo")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = cartItems,
                        key = { it.product.id }
                    ) { cartItem ->
                        CartListItem(
                            cartItem = cartItem,
                            onQuantityChange = { newQuantity ->
                                if (newQuantity <= cartItem.product.stock) {
                                    cartManager.updateQuantity(cartItem.product.id, newQuantity)
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Stock máximo: ${cartItem.product.stock} unidades"
                                        )
                                    }
                                }
                            },
                            onRemove = {
                                cartManager.removeFromCart(cartItem.product.id)
                                scope.launch {
                                    snackbarHostState.showSnackbar("Producto eliminado")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartListItem(
    cartItem: com.example.aplicaciongrupo7.data.CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            SafeProductImage(
                imageRes = cartItem.product.imageRes,
                title = cartItem.product.title,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    cartItem.product.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    cartItem.product.genre,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Precio y controles de cantidad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = cartItem.product.price,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Controles de cantidad
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onQuantityChange(cartItem.quantity - 1) },
                            enabled = cartItem.quantity > 1
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Disminuir cantidad"
                            )
                        }

                        Text(
                            text = "${cartItem.quantity}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = { onQuantityChange(cartItem.quantity + 1) },
                            enabled = cartItem.quantity < cartItem.product.stock
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Aumentar cantidad"
                            )
                        }
                    }
                }

                // Información de stock
                if (cartItem.quantity >= cartItem.product.stock) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Stock máximo alcanzado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botón eliminar
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar del carrito",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// SOLUCIÓN CORREGIDA - Maneja tipos de imagen no soportados
@Composable
fun SafeProductImage(
    imageRes: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    var canLoadImage by remember(imageRes) { mutableStateOf(true) }
    var hasImageError by remember(imageRes) { mutableStateOf(false) }


    @Composable
    fun ImagePlaceholder(modifier: Modifier = Modifier, title: String) {
        Box(
            modifier = modifier
                .background(Color(0xFF2A2A2A)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Imagen no disponible: $title",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    "IMG",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}