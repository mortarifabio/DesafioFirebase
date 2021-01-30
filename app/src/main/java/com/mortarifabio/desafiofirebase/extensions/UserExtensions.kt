package com.mortarifabio.desafiofirebase.extensions

import com.mortarifabio.desafiofirebase.model.User

fun User.toHashMap(): HashMap<String, String> {
    return hashMapOf(
        "name" to name,
        "email" to email
    )
}