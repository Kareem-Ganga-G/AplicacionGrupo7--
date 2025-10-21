package com.example.aplicaciongrupo7.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import com.example.aplicaciongrupo7.R
class GameManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
    private val gamesKey = "games_list"

    // Listapor defecto
    private val defaultGames = listOf(
        Product(1, "AMD Ryzen 9 7950X", "Procesador", "$699.990", 4.8f, "16 núcleos, 4.5GHz", R.drawable.procesador_amd_ryzen9, stock = 10),
        Product(2, "Intel Core i9-14900K", "Procesador", "$589.990", 4.6f, "24 núcleos, 6.0GHz", R.drawable.procesador_intel_i9, stock = 10),
        Product(3, "AMD Ryzen 7 7800X3D", "Procesador", "$449.990", 4.9f, "8 núcleos, 5.0GHz", R.drawable.procesador_amd_ryzen7, stock = 15),
        Product(4, "NVIDIA GeForce RTX 4090", "Tarjeta Gráfica", "$1599.990", 4.7f, "24GB GDDR6X", R.drawable.gpu_rtx4090, stock = 5),
        Product(5, "AMD Radeon RX 7900 XTX", "Tarjeta Gráfica", "$999.990", 4.5f, "24GB GDDR6", R.drawable.gpu_amd_radeon, stock = 8),
        Product(6, "NVIDIA GeForce RTX 4070 Ti", "Tarjeta Gráfica", "$799.990", 4.4f, "12GB GDDR6X", R.drawable.gpu_rtx4070, stock = 12),
        Product(7, "Samsung Odyssey G9", "Monitor", "$1299.990", 4.8f, "49 Curvo 240Hz", R.drawable.monitor_samsung_odyssey, stock = 7),
        Product(8, "ASUS ROG Swift PG279QM", "Monitor", "$899.990", 4.6f, "27 1440p 240Hz", R.drawable.monitor_asus_rog, stock = 10),
        Product(9, "Alienware AW3423DW", "Monitor", "$1199.990", 4.9f, "34 OLED 175Hz", R.drawable.monitor_alienware, stock = 6),
        Product(10, "Corsair Dominator Platinum", "Memoria RAM", "$249.990", 4.7f, "32GB DDR5 6000MHz", R.drawable.ram_corsair_dominator, stock = 20),
        Product(11, "G.Skill Trident Z5 RGB", "Memoria RAM", "$199.990", 4.5f, "32GB DDR5 5600MHz", R.drawable.ram_gskill_trident, stock = 25)
    )

    fun getGames(): List<Product> {
        val gamesJson = sharedPreferences.getString(gamesKey, null)
        return if (gamesJson != null) {
            parseGamesFromJson(gamesJson)
        } else {
            saveGames(defaultGames)
            defaultGames
        }
    }

    fun addGame(game: Product) {
        val currentGames = getGames().toMutableList()
        val newId = (currentGames.maxOfOrNull { it.id } ?: 0) + 1
        val newGame = game.copy(id = newId)
        currentGames.add(newGame)
        saveGames(currentGames)
    }

    fun updateGame(updatedGame: Product) {
        val currentGames = getGames().toMutableList()
        val index = currentGames.indexOfFirst { it.id == updatedGame.id }
        if (index != -1) {
            currentGames[index] = updatedGame
            saveGames(currentGames)
        }
    }

    fun deleteGame(gameId: Int) {
        val currentGames = getGames().toMutableList()
        currentGames.removeAll { it.id == gameId }
        saveGames(currentGames)
    }

    private fun saveGames(games: List<Product>) {
        val gamesJson = convertGamesToJson(games)
        sharedPreferences.edit().putString(gamesKey, gamesJson).apply()
    }

    private fun convertGamesToJson(games: List<Product>): String {
        val jsonArray = JSONArray()
        games.forEach { product ->
            val jsonObject = JSONObject().apply {
                put("id", product.id)
                put("title", product.title)
                put("genre", product.genre)
                put("price", product.price)
                put("rating", product.rating.toDouble())
            }
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    private fun parseGamesFromJson(jsonString: String): List<Product> {
        val games = mutableListOf<Product>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val game = Product(
                    id = jsonObject.getInt("id"),
                    title = jsonObject.getString("title"),
                    genre = jsonObject.getString("genre"),
                    price = jsonObject.getString("price"),
                    rating = jsonObject.getDouble("rating").toFloat(),
                    imageRes = jsonObject.optInt("imageRes"),
                    stock = jsonObject.optInt("stock")
                )
                games.add(game)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
        return games
    }
}