package com.mortarifabio.desafiofirebase.extensions

import com.google.firebase.firestore.DocumentSnapshot
import com.mortarifabio.desafiofirebase.model.Game

fun List<DocumentSnapshot>.toGamesList(): List<Game> {
    return this.map {
        Game(
            id = it.id,
            name = it.get("name")?.toString() ?: "",
            createdAt = it.get("createdAt")?.toString() ?: "",
            description = it.get("description")?.toString() ?: "",
            image = it.get("image")?.toString()
        )
    }
}

fun List<Game>.filterBySearchTerm(searchTerm: String): List<Game> {
    return this.filter {
        it.name.contains(searchTerm, true) ||
        it.description.contains(searchTerm, true) ||
        it.createdAt.contains(searchTerm, true)
    }
}