package com.mortarifabio.desafiofirebase.games

import android.content.Context
import android.graphics.Bitmap
import com.mortarifabio.desafiofirebase.databinding.ActivityGamesRegisterBinding
import com.mortarifabio.desafiofirebase.model.Game

class GamesBusiness(
    val context: Context
) {
    private val repository by lazy {
        GamesRepository(context)
    }

    suspend fun saveGame(binding: ActivityGamesRegisterBinding, gameId: String?, bitmap: Bitmap?): Boolean {
        val game = Game(
            name = binding.tietGamesRegisterName.text.toString(),
            createdAt = binding.tietGamesRegisterCreatedAt.text.toString(),
            description = binding.tietGamesRegisterDescription.text.toString()
        )
        return repository.saveGame(game, gameId, bitmap)
    }
}