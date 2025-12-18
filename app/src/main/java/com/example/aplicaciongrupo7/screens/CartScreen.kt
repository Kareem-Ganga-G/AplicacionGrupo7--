package com.example.aplicaciongrupo7.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.data.CartItem
import com.example.aplicaciongrupo7.data.CartManager
import com.example.aplicaciongrupo7.data.GameManager
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
                            Text("Total", style = MaterialTheme.typography.titleLarge)
                            Text(
                                text = "$${"%,.0f".format(cartTotal).replace(",", ".")}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                // 🔴 VALIDAR STOCK REAL
                                val outOfStock = cartItems.any {
                                    it.quantity > it.product.stock
                                }

                                if (outOfStock) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Algunos productos no tienen stock suficiente"
                                        )
                                    }
                                    return@Button
                                }

                                // ✅ DESCONTAR STOCK EN BD
                                cartItems.forEach { cartItem ->
                                    gameManager.decreaseStock(
                                        productId = cartItem.product.id,
                                        quantity = cartItem.quantity
                                    )
                                }

                                // 🧹 VACIAR CARRITO
                                cartManager.clearCart()

                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "¡Compra realizada con éxito!"
                                    )
                                }
                            }
                        ) {
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
                                cartManager.updateQuantity(
                                    cartItem.product.id,
                                    newQuantity
                                )
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Stock máximo: ${cartItem.product.stock}"
                                    )
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
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text("IMG", color = Color.White)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(cartItem.product.title, fontWeight = FontWeight.Bold)
                Text(cartItem.product.genre, style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity - 1) },
                        enabled = cartItem.quantity > 1
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }

                    Text(
                        text = cartItem.quantity.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = { onQuantityChange(cartItem.quantity + 1) },
                        enabled = cartItem.quantity < cartItem.product.stock
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
