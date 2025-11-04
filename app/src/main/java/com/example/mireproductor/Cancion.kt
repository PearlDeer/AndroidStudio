package com.example.mireproductor

data class Cancion(
    val id: String,
    val nombre: String,
    val autor: String,
    val imagenUrl: String?,
    val duracion: String,
    val cancionUrl: String?
)
