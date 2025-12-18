package com.example.aplicaciongrupo7.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.aplicaciongrupo7.R
import com.example.aplicaciongrupo7.components.GameItem
import com.example.aplicaciongrupo7.components.SafeGameImage
import com.example.aplicaciongrupo7.data.GameManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import com.example.aplicaciongrupo7.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBack: () -> Unit,
    onGoToProfile: () -> Unit
) {
    val context = LocalContext.current
    val gameManager = remember { GameManager(context) }
    var games by remember { mutableStateOf(gameManager.getGames()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingGame by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Panel de Administración") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    // Icono de perfil - usando icono válido
                    IconButton(onClick = onGoToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }

                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar juego")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = games.size.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Productos",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val avgRating = if (games.isNotEmpty()) {
                            games.map { it.rating }.average()
                        } else {
                            0.0
                        }
                        Text(
                            text = "%.1f".format(avgRating),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Rating Prom.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            if (games.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SafeGameImage(
                            imageRes = R.drawable.logoinicio,
                            title = "Logo de inicio",
                            modifier = Modifier.size(120.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay productos en el catálogo",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Presiona el botón + para agregar el primero",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(games) { game ->
                        GameItem(
                            game = game,
                            onEdit = { editingGame = game },
                            onDelete = {
                                gameManager.deleteGame(game.id)
                                games = gameManager.getGames()
                            },
                            showAdminActions = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (showAddDialog || editingGame != null) {
        GameEditDialog(
            game = editingGame,
            onSave = { product ->
                if (editingGame != null) {
                    gameManager.updateGame(product)
                } else {
                    gameManager.addGame(product)
                }
                games = gameManager.getGames()
                showAddDialog = false
                editingGame = null
            },
            onDismiss = {
                showAddDialog = false
                editingGame = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameEditDialog(
    game: Product?,
    onSave: (Product) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(game?.title ?: "") }
    var genre by remember { mutableStateOf(game?.genre ?: "") }
    var price by remember { mutableStateOf(game?.price ?: "") }
    var rating by remember { mutableStateOf(game?.rating?.toString() ?: "4.0") }
    var stock by remember { mutableStateOf(game?.stock?.toString() ?: "0") }
    var selectedImage by remember { mutableStateOf(game?.imageRes ?: R.drawable.procesador_amd_ryzen9) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempImageFile by remember { mutableStateOf<File?>(null) }

    // 1. Función para preparar archivo de cámara
    fun prepareCameraFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = context.getExternalFilesDir("Pictures")
            File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            ).apply {
                tempImageFile = this
            }
        } catch (e: Exception) {
            errorMessage = "Error al preparar archivo: ${e.message}"
            null
        }
    }

    // 2. Lanzador para la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempImageFile?.let { file ->
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                    imageUri = uri
                }
            } else {
                errorMessage = "Error al tomar la foto"
            }
        }
    )

    // 3. Lanzador para la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                imageUri = it
            } ?: run {
                errorMessage = "No se seleccionó ninguna imagen"
            }
        }
    )

    // 4. Permisos para la cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            prepareCameraFile()?.let { file ->
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                cameraLauncher.launch(uri)
            }
        } else {
            errorMessage = "Se necesita permiso de cámara para tomar fotos"
        }
    }

    // 5. Permisos para la galería
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            errorMessage = "Se necesita permiso de almacenamiento para seleccionar imágenes"
        }
    }

    LaunchedEffect(game) {
        title = game?.title ?: ""
        genre = game?.genre ?: ""
        price = game?.price ?: ""
        rating = game?.rating?.toString() ?: "4.0"
        stock = game?.stock?.toString() ?: "0"
        selectedImage = game?.imageRes ?: R.drawable.procesador_amd_ryzen9
        errorMessage = ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (game == null) "Agregar Nuevo Producto"
                else "Editar: ${game.title}"
            )
        },
        text = {
            Column {
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Text(
                    text = "Imagen del Producto:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Mostrar imagen seleccionada o imagen predeterminada
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUri),
                            contentDescription = "Imagen del producto",
                            modifier = Modifier
                                .size(120.dp)
                                .border(2.dp, MaterialTheme.colorScheme.primary)
                        )
                    } else {
                        SafeGameImage(
                            imageRes = selectedImage,
                            title = "Imagen seleccionada",
                            modifier = Modifier.size(120.dp)
                        )
                    }
                }

                // Botones para cámara y galería
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.CameraAlt, // Cambiado a CameraAlt
                            contentDescription = "Tomar foto",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cámara")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                galleryLauncher.launch("image/*")
                            } else {
                                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary, // Cambiado a PhotoLibrary
                            contentDescription = "Seleccionar de galería",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Galería")
                    }
                }

                // Botón para usar imagen predeterminada
                if (imageUri != null) {
                    TextButton(
                        onClick = {
                            imageUri = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Cambiar")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Usar imagen predeterminada")
                    }
                }

                // Selección de imágenes predeterminadas
                if (imageUri == null) {
                    Text(
                        text = "Seleccionar Imagen Predeterminada:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    val availableImages = listOf(
                        R.drawable.procesador_amd_ryzen9,
                        R.drawable.procesador_amd_ryzen7,
                        R.drawable.procesador_intel_i9,
                        R.drawable.gpu_rtx4090,
                        R.drawable.gpu_rtx4070,
                        R.drawable.gpu_amd_radeon,
                        R.drawable.ram_corsair_dominator,
                        R.drawable.ram_gskill_trident,
                        R.drawable.monitor_samsung_odyssey,
                        R.drawable.monitor_asus_rog,
                        R.drawable.monitor_alienware
                    )

                    val imageNames = mapOf(
                        R.drawable.procesador_amd_ryzen9 to "AMD Ryzen 9",
                        R.drawable.procesador_amd_ryzen7 to "AMD Ryzen 7",
                        R.drawable.procesador_intel_i9 to "Intel Core i9",
                        R.drawable.gpu_rtx4090 to "RTX 4090",
                        R.drawable.gpu_rtx4070 to "RTX 4070",
                        R.drawable.gpu_amd_radeon to "AMD Radeon",
                        R.drawable.ram_corsair_dominator to "RAM Corsair",
                        R.drawable.ram_gskill_trident to "RAM G.Skill",
                        R.drawable.monitor_samsung_odyssey to "Samsung Odyssey",
                        R.drawable.monitor_asus_rog to "ASUS ROG",
                        R.drawable.monitor_alienware to "Alienware"
                    )

                    LazyColumn(
                        modifier = Modifier.height(120.dp)
                    ) {
                        items(availableImages.chunked(3)) { rowImages ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                rowImages.forEach { imageRes ->
                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .border(
                                                width = 2.dp,
                                                color = if (selectedImage == imageRes) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.outline,
                                                shape = MaterialTheme.shapes.small
                                            )
                                            .clickable { selectedImage = imageRes }
                                    ) {
                                        SafeGameImage(
                                            imageRes = imageRes,
                                            title = imageNames[imageRes] ?: "Imagen",
                                            modifier = Modifier.size(60.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campos del formulario
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = ""
                    },
                    label = { Text("Título del producto") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = genre,
                    onValueChange = {
                        genre = it
                        errorMessage = ""
                    },
                    label = { Text("Categoría *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        errorMessage = ""
                    },
                    label = { Text("Precio *") },
                    placeholder = { Text("Ej: \$59.990") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = rating,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,1}$"))) {
                            rating = it
                            errorMessage = ""
                        }
                    },
                    label = { Text("Rating (0.0 - 5.0)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = stock,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) {
                            stock = it
                            errorMessage = ""
                        }
                    },
                    label = { Text("Stock *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val stockInt = stock.toIntOrNull()
                    when {
                        title.isBlank() || genre.isBlank() || price.isBlank() || stock.isBlank() -> {
                            errorMessage = "Por favor completa todos los campos obligatorios"
                        }
                        rating.toFloatOrNull() == null -> {
                            errorMessage = "El rating debe ser un número válido"
                        }
                        rating.toFloat() !in 0.0f..5.0f -> {
                            errorMessage = "El rating debe estar entre 0.0 y 5.0"
                        }
                        !price.startsWith("$") -> {
                            errorMessage = "El precio debe empezar con $ (Ej: \$59.990)"
                        }
                        stockInt == null || stockInt < 0 -> {
                            errorMessage = "El stock debe ser un número entero válido y no negativo"
                        }
                        else -> {
                            val newGame = Product(
                                id = game?.id ?: 0,
                                title = title.trim(),
                                genre = genre.trim(),
                                price = price.trim(),
                                rating = rating.toFloat(),
                                imageRes = selectedImage,
                                stock = stockInt
                            )
                            onSave(newGame)
                        }
                    }
                },
                enabled = title.isNotBlank() && genre.isNotBlank() && price.isNotBlank() && stock.isNotBlank()
            ) {
                Text(if (game == null) "Agregar" else "Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}