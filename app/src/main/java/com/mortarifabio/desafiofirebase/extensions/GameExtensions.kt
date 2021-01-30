package com.mortarifabio.desafiofirebase.extensions

import com.mortarifabio.desafiofirebase.model.Game

fun Game.toHashMap(): HashMap<String, String?> {
    return hashMapOf(
        "name" to name,
        "createdAt" to createdAt,
        "description" to description,
        "image" to image
    )
}