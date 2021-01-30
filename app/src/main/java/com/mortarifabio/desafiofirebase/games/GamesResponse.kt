package com.mortarifabio.desafiofirebase.games

import com.google.firebase.firestore.QuerySnapshot

sealed class GamesResponse {
    class Success(val result: QuerySnapshot) : GamesResponse()
    class Error(val message: String) : GamesResponse()
}
