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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

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
    // --- ESTADOS DEL FORMULARIO ---
    var title by remember { mutableStateOf(game?.title ?: "") }
    var genre by remember { mutableStateOf(game?.genre ?: "") }
    var price by remember { mutableStateOf(game?.price ?: "") }
    var rating by remember { mutableStateOf(game?.rating?.toString() ?: "4.0") }
    var stock by remember { mutableStateOf(game?.stock?.toString() ?: "0") }

    // Imagen seleccionada (puede ser un ID de recursos o un URI de archivo)
    var selectedImageRes by remember { mutableStateOf(game?.imageRes ?: R.drawable.procesador_amd_ryzen9) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var errorMessage by remember { mutableStateOf("") }

    // --- HERRAMIENTAS DE CÁMARA Y ARCHIVOS ---
    val context = LocalContext.current
    var tempImageFile by remember { mutableStateOf<File?>(null) }
    val scrollState = rememberScrollState() // Estado para el scroll vertical

    // 1. Función para crear el archivo temporal de la foto
    fun prepareCameraFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = context.getExternalFilesDir("Pictures")
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
                tempImageFile = this
            }
        } catch (e: Exception) {
            errorMessage = "Error creando archivo: ${e.message}"
            null
        }
    }

    // 2. Launcher de CÁMARA
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
                    selectedImageUri = uri
                }
            }
        }
    )

    // 3. Launcher de GALERÍA
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) selectedImageUri = uri
        }
    )

    // 4. Permisos
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
            errorMessage = "Se requiere permiso de cámara"
        }
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) galleryLauncher.launch("image/*")
        else errorMessage = "Se requiere permiso de almacenamiento"
    }

    // --- UI DEL DIÁLOGO ---
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (game == null) "Nuevo Producto" else "Editar Producto")
        },
        text = {
            // ✨ SCROLL VERTICAL HABILITADO ✨
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState) // <-- ESTO ARREGLA EL SCROLL
            ) {
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // --- PREVISUALIZACIÓN DE IMAGEN ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(vertical = 8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = selectedImageUri),
                            contentDescription = "Imagen seleccionada",
                            modifier = Modifier
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        SafeGameImage(
                            imageRes = selectedImageRes,
                            title = "Imagen predeterminada",
                            modifier = Modifier
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }

                // --- BOTONES DE CÁMARA Y GALERÍA ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Cámara", style = MaterialTheme.typography.bodySmall)
                    }

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                galleryLauncher.launch("image/*")
                            } else {
                                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Galería", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Botón para volver a imagen por defecto si se usó una foto
                if (selectedImageUri != null) {
                    TextButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Usar imagen predeterminada")
                    }
                }

                // --- SELECCIÓN DE IMÁGENES PREDETERMINADAS ---
                // Solo se muestra si no hay foto de cámara seleccionada
                if (selectedImageUri == null) {
                    Text(
                        "O selecciona una imagen:",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    val defaultImages = listOf(
                        R.drawable.procesador_amd_ryzen9, R.drawable.procesador_amd_ryzen7,
                        R.drawable.procesador_intel_i9, R.drawable.gpu_rtx4090,
                        R.drawable.gpu_rtx4070, R.drawable.gpu_amd_radeon,
                        R.drawable.ram_corsair_dominator, R.drawable.ram_gskill_trident,
                        R.drawable.monitor_samsung_odyssey, R.drawable.monitor_asus_rog,
                        R.drawable.monitor_alienware
                    )

                    // Usamos Column + Row en lugar de LazyGrid para evitar conflictos de scroll
                    Column(Modifier.fillMaxWidth()) {
                        defaultImages.chunked(4).forEach { rowImages ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                rowImages.forEach { imgRes ->
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .border(
                                                width = if (selectedImageRes == imgRes) 2.dp else 0.dp,
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .clip(RoundedCornerShape(4.dp))
                                            .clickable { selectedImageRes = imgRes }
                                    ) {
                                        SafeGameImage(imageRes = imgRes, title = "", modifier = Modifier.fillMaxSize())
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- CAMPOS DE TEXTO ---
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text("Categoría") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio (Ej: $9.990)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fila optimizada para Rating y Stock
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = rating,
                        onValueChange = { rating = it },
                        label = { Text("Rating (0-5)") },
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock") },
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                // Espacio extra al final para asegurar visibilidad
                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val stockInt = stock.toIntOrNull()
                    val ratingFloat = rating.toFloatOrNull()

                    when {
                        title.isBlank() || genre.isBlank() || price.isBlank() ->
                            errorMessage = "Faltan campos obligatorios"
                        !price.startsWith("$") ->
                            errorMessage = "El precio debe iniciar con $"
                        ratingFloat == null || ratingFloat !in 0f..5f ->
                            errorMessage = "Rating inválido (0.0 - 5.0)"
                        stockInt == null || stockInt < 0 ->
                            errorMessage = "Stock inválido"
                        else -> {
                            // Crear el objeto producto actualizado
                            val updatedGame = Product(
                                id = game?.id ?: 0,
                                title = title.trim(),
                                genre = genre.trim(),
                                price = price.trim(),
                                rating = ratingFloat,
                                imageRes = selectedImageRes,
                                stock = stockInt,
                                // Nota: Si usaras URI real en base de datos, deberías guardarlo aquí como string
                                // description = selectedImageUri?.toString() ?: ""
                            )
                            onSave(updatedGame)
                        }
                    }
                }
            ) {
                Text(if (game == null) "Crear" else "Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}