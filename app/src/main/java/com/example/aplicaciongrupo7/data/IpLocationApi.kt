package com.example.aplicaciongrupo7.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 1. Modelo de datos (Lo que responde ip-api.com)
data class LocationResponse(
    val status: String,
    val country: String,
    val city: String,
    val query: String, // Esta es la IP
    val isp: String // Proveedor de internet
)

// 2. Interfaz
interface IpApiService {
    @GET("json") // El endpoint base devuelve la info de la IP actual
    suspend fun getLocation(): LocationResponse
}

// 3. Cliente Singleton (Este es el que te faltaba)
object LocationRetrofitClient {
    // Nota: Es HTTP, por eso necesitamos el permiso usesCleartextTraffic en el Manifest
    private const val BASE_URL = "http://ip-api.com/"

    val service: IpApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IpApiService::class.java)
    }
}