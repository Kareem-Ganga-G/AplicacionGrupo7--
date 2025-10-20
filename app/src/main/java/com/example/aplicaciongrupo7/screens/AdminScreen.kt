package com.example.aplicaciongrupo7.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.aplicaciongrupo7.components.GameItem
import com.example.aplicaciongrupo7.data.Game
import com.example.aplicaciongrupo7.data.GameManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val gameManager = remember { GameManager(context) }
    var games by remember { mutableStateOf(gameManager.getGames()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingGame by remember { mutableStateOf<Game?>(null) }

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
                Icon(Icons.Default.Add, contentDescription = "Agregar juego")
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
                            text = "Juegos",
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
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Sin juegos",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay juegos en el catálogo",
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
            onSave = { game ->
                if (editingGame != null) {
                    gameManager.updateGame(game)
                } else {
                    gameManager.addGame(game)
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

@Composable
fun GameEditDialog(
    game: Game?,
    onSave: (Game) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(game?.title ?: "") }
    var genre by remember { mutableStateOf(game?.genre ?: "") }
    var price by remember { mutableStateOf(game?.price ?: "") }
    var rating by remember { mutableStateOf(game?.rating?.toString() ?: "4.0") }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(game) {
        title = game?.title ?: ""
        genre = game?.genre ?: ""
        price = game?.price ?: ""
        rating = game?.rating?.toString() ?: "4.0"
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

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = ""
                    },
                    label = { Text("Título del juego *") },
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
                    label = { Text("Género *") },
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
                    placeholder = { Text("Ej: $59.99") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = rating,
                    onValueChange = {
                        // Validación manual para solo permitir números y punto decimal
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            rating = it
                            errorMessage = ""
                        }
                    },
                    label = { Text("Rating (0.0 - 5.0)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage.isNotEmpty()
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "* Campos obligatorios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        title.isEmpty() || genre.isEmpty() || price.isEmpty() -> {
                            errorMessage = "Por favor completa todos los campos obligatorios"
                        }
                        rating.toFloatOrNull() == null -> {
                            errorMessage = "El rating debe ser un número válido"
                        }
                        rating.toFloat() !in 0f..5f -> {
                            errorMessage = "El rating debe estar entre 0.0 y 5.0"
                        }
                        !price.startsWith("$") -> {
                            errorMessage = "El precio debe empezar con $ (Ej: \$59.990)"
                        }
                        else -> {
                            val newGame = Game(
                                id = game?.id ?: 0,
                                title = title.trim(),
                                genre = genre.trim(),
                                price = price.trim(),
                                rating = rating.toFloat()
                            )
                            onSave(newGame)
                        }
                    }
                },
                enabled = title.isNotEmpty() && genre.isNotEmpty() && price.isNotEmpty()
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