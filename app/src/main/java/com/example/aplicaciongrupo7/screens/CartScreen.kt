package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.aplicaciongrupo7.data.CartItem
import com.example.aplicaciongrupo7.data.CartManager
import com.example.aplicaciongrupo7.data.GameManager
import com.example.aplicaciongrupo7.data.LocationRetrofitClient
import com.example.aplicaciongrupo7.data.LocationResponse
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartManager: CartManager,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val gameManager = remember { GameManager(context) }
    val cartItems by cartManager.cartItems.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // --- 1. ESTADO PARA LA API (UBICACIÓN) ---
    var locationInfo by remember { mutableStateOf<LocationResponse?>(null) }
    var isLoadingLocation by remember { mutableStateOf(true) }

    // Llamada a la API al entrar al carrito
    LaunchedEffect(Unit) {
        try {
            val response = LocationRetrofitClient.service.getLocation()
            locationInfo = response
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoadingLocation = false
        }
    }
    // -----------------------------------------

    // Total del carrito
    val cartTotal by remember(cartItems) {
        derivedStateOf {
            cartItems.sumOf { item ->
                val price = item.product.price
                    .replace("$", "")
                    .replace(".", "")
                    .replace(",", ".")
                    .toDoubleOrNull() ?: 0.0
                price * item.quantity
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
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // --- 2. MOSTRAR INFORMACIÓN DE LA API (ENVÍO/SEGURIDAD) ---
                        Text(
                            text = "Detalles de Envío (Detectado por IP)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isLoadingLocation) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Calculando ubicación de envío...", style = MaterialTheme.typography.bodySmall)
                                } else if (locationInfo != null) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Enviar a: ${locationInfo!!.city}, ${locationInfo!!.country}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "ISP Segura: ${locationInfo!!.isp}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                } else {
                                    Icon(Icons.Default.SignalWifiOff, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("No se pudo detectar ubicación", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp))
                        // ------------------------------------------------------------

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total a Pagar", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "$${"%,.0f".format(cartTotal).replace(",", ".")}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                // VALIDAR STOCK REAL
                                val outOfStock = cartItems.any { it.quantity > it.product.stock }

                                if (outOfStock) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Algunos productos no tienen stock suficiente")
                                    }
                                    return@Button
                                }

                                // DESCONTAR STOCK EN BD
                                cartItems.forEach { cartItem ->
                                    gameManager.decreaseStock(
                                        productId = cartItem.product.id,
                                        quantity = cartItem.quantity
                                    )
                                }

                                // VACIAR CARRITO
                                cartManager.clearCart()

                                scope.launch {
                                    val destination = locationInfo?.city ?: "tu domicilio"
                                    snackbarHostState.showSnackbar("¡Compra exitosa! Enviando a $destination")
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Finalizar Compra")
                        }
                    }
                }
            }
        }
    ) { padding ->

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        "Tu carrito está vacío",
                        style = MaterialTheme.typography.headlineSmall,
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
                    .padding(padding),
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
                            if (newQuantity in 1..cartItem.product.stock) {
                                cartManager.updateQuantity(cartItem.product.id, newQuantity)
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Stock máximo: ${cartItem.product.stock}")
                                }
                            }
                        },
                        onRemove = {
                            cartManager.removeFromCart(cartItem.product.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CartListItem(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = cartItem.product.imageRes),
                contentDescription = cartItem.product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cartItem.product.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = cartItem.product.genre,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = cartItem.product.price,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity - 1) },
                        enabled = cartItem.quantity > 1,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Menos")
                    }

                    Text(
                        text = cartItem.quantity.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity + 1) },
                        enabled = cartItem.quantity < cartItem.product.stock,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Más")
                    }
                }
            }
        }
    }
}