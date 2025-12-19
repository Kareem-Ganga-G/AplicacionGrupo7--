package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.components.SimpleGameItem
import com.example.aplicaciongrupo7.data.CartManager
import com.example.aplicaciongrupo7.data.GameManager
import com.example.aplicaciongrupo7.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    cartManager: CartManager,
    onBack: () -> Unit,
    onGoToCart: () -> Unit,
    onGoToProfile: () -> Unit
) {
    val context = LocalContext.current
    val gameManager = remember { GameManager(context) }

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    // Lista de productos y items del carrito (observando el flow del CartManager)
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    val cartItems by cartManager.cartItems.collectAsState()

    LaunchedEffect(Unit) {
        val loaded = gameManager.getGames()
        products = loaded
        cartManager.setProducts(loaded)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Catálogo", style = MaterialTheme.typography.titleMedium)
                        // Línea secundaria con número de productos (similar a información extra en Profile)
                        Text("${products.size} artículos", style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Botón carrito con badge
                    IconButton(onClick = onGoToCart) {
                        BadgedBox(
                            badge = {
                                if (cartItems.isNotEmpty()) {
                                    Badge { Text(cartItems.sumOf { it.quantity }.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
                    }

                    // Botón perfil (agregado)
                    IconButton(onClick = onGoToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                }
            )
        },
        content = { padding ->
            // Contenedor principal que respeta el padding del scaffold
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(if (isLandscape) 12.dp else 16.dp)
            ) {
                // Espacio superior
                Spacer(modifier = Modifier.height(8.dp))

                // Si quieres, aquí puedes añadir filtros / buscador (similar a cards de Profile)
                // Por simplicidad mostramos la lista de productos como antes.

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        val qtyInCart =
                            cartItems.find { it.product.id == product.id }?.quantity ?: 0

                        SimpleGameItem(
                            item = product,
                            cartQuantity = qtyInCart,
                            onAddToCart = {
                                cartManager.addToCart(product.id)
                            }
                        )
                    }
                }
            }
        }
    )
}
