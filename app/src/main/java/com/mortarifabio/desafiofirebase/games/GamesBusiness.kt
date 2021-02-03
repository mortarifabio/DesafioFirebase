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

    suspend fun saveGame(binding: ActivityGamesRegisterBinding, game: Game?, bitmap: Bitmap?): Boolean {
        val gameUpdated = Game(
            id = game?.id,
            name = binding.tietGamesRegisterName.text.toString(),
            createdAt = binding.tietGamesRegisterCreatedAt.text.toString(),
            description = binding.tietGamesRegisterDescription.text.toString(),
            image = game?.image
        )
        return repository.saveGame(gameUpdated, bitmap)
    }
}