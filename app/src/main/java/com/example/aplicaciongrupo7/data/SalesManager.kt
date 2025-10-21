package com.example.aplicaciongrupo7.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class SalesManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("sales_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addSale(sale: Sale) {
        val sales = getSales().toMutableList()
        sales.add(sale)
        saveSales(sales)
    }

    fun getSales(): List<Sale> {
        val salesJson = sharedPreferences.getString("sales", "[]")
        val type = object : TypeToken<List<Sale>>() {}.type
        return gson.fromJson(salesJson, type) ?: emptyList()
    }

    private fun saveSales(sales: List<Sale>) {
        val salesJson = gson.toJson(sales)
        sharedPreferences.edit().putString("sales", salesJson).apply()
    }
}