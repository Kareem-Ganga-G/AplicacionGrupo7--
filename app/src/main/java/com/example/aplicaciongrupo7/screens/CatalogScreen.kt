package com.example.aplicaciongrupo7.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.data.CartManager
import com.example.aplicaciongrupo7.data.GameManager
import com.example.aplicaciongrupo7.data.Product
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- COLORES PERSONALIZADOS PARA ESTA PANTALLA ---
val BackgroundColor = Color(0xFFF4F6F9) // Gris azulado muy claro (Moderno)
val CardColor = Color.White
val PriceColor = Color(0xFF00897B)      // Verde Teal elegante
val PrimaryButtonColor = Color(0xFF5E35B1) // Violeta Gamer (Deep Purple)
val TopBarColor = Color(0xFF1A237E)     // Azul oscuro profundo
val TextStockLow = Color(0xFFD32F2F)    // Rojo alerta
val BadgeColor = Color(0xFF039BE5)      // Azul claro para badges

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
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    val cartItems by cartManager.cartItems.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val loaded = gameManager.getGames()
        products = loaded
        cartManager.setProducts(loaded)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Catálogo Gamer", fontWeight = FontWeight.Bold, color = Color.White)
                        Text(
                            "${products.size} títulos disponibles",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBarColor, // Barra oscura
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onGoToCart) {
                        BadgedBox(
                            badge = {
                                if (cartItems.isNotEmpty()) {
                                    Badge(containerColor = Color.Red, contentColor = Color.White) {
                                        Text(cartItems.sumOf { it.quantity }.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                        }
                    }
                    IconButton(onClick = onGoToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                }
            )
        },
        content = { padding ->
            // Fondo general de la pantalla
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundColor) // Color de fondo gris suave
                    .padding(padding)
                    .padding(horizontal = 8.dp)
            ) {
                if (products.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryButtonColor)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (isLandscape) 3 else 2),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(products, key = { it.id }) { product ->

                            val qtyInCart = cartItems.find { it.product.id == product.id }?.quantity ?: 0

                            GameGridCard(
                                product = product,
                                cartQuantity = qtyInCart,
                                onAddToCart = {
                                    if (qtyInCart < product.stock) {
                                        cartManager.addToCart(product.id)
                                        scope.launch {
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                        }
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("¡Has alcanzado el límite de stock!")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun GameGridCard(
    product: Product,
    cartQuantity: Int,
    onAddToCart: () -> Unit
) {
    val isMaxedOut = cartQuantity >= product.stock
    val isOutOfStock = product.stock <= 0

    // Animación
    var isAnimated by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isAnimated) 0.95f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "scale"
    )

    LaunchedEffect(isAnimated) {
        if (isAnimated) {
            delay(100)
            isAnimated = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(290.dp) // Un poco más alta para mejor espacio
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(6.dp, RoundedCornerShape(12.dp)) // Sombra más elegante
            .clickable(enabled = !isMaxedOut && !isOutOfStock) {
                isAnimated = true
                onAddToCart()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor), // Tarjeta blanca limpia
        elevation = CardDefaults.cardElevation(0.dp) // Quitamos elevación default para usar shadow custom
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. Imagen (50%)
            Box(
                modifier = Modifier
                    .weight(0.50f)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = product.imageRes),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Badge de AGOTADO
                if (isOutOfStock) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AGOTADO", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // 2. Información (50%)
            Column(
                modifier = Modifier
                    .weight(0.50f)
                    .padding(12.dp), // Más padding interno
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Título
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black, // Texto negro nítido
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Género
                    Text(
                        text = product.genre,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Stock y Estado del carrito
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Stock
                        Text(
                            text = "Stock: ${product.stock}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (product.stock < 5) TextStockLow else Color.Gray
                        )

                        // "Llevas X" (Badge visual)
                        if (cartQuantity > 0) {
                            Surface(
                                color = if (isMaxedOut) TextStockLow.copy(alpha=0.1f) else BadgeColor.copy(alpha=0.1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (isMaxedOut) "MAX" else "x$cartQuantity",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMaxedOut) TextStockLow else BadgeColor,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))

                // Precio y Botón
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.price,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PriceColor // Usamos el color verde teal definido arriba
                    )

                    // Botón personalizado
                    FilledIconButton(
                        onClick = {
                            isAnimated = true
                            onAddToCart()
                        },
                        modifier = Modifier.size(36.dp), // Un poco más grande
                        enabled = !isMaxedOut && !isOutOfStock,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = PrimaryButtonColor, // Violeta
                            disabledContainerColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp) // Botón un poco más cuadrado
                    ) {
                        Icon(
                            imageVector = if (isMaxedOut) Icons.Default.Block else Icons.Default.AddShoppingCart,
                            contentDescription = "Agregar",
                            modifier = Modifier.size(18.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}