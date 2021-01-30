package com.mortarifabio.desafiofirebase.games.paging

import android.content.Context
import android.util.Log
import androidx.paging.PageKeyedDataSource
import com.mortarifabio.desafiofirebase.extensions.filterBySearchTerm
import com.mortarifabio.desafiofirebase.extensions.toGamesList
import com.mortarifabio.desafiofirebase.games.GamesRepository
import com.mortarifabio.desafiofirebase.games.GamesResponse
import com.mortarifabio.desafiofirebase.model.Game
import com.mortarifabio.desafiofirebase.utils.Constants.Firestore.FIRESTORE_FIRST_PAGE
import com.mortarifabio.desafiofirebase.utils.Constants.Firestore.FIRESTORE_LOG_KEY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GamesPageKeyedDataSource (
    private val context: Context,
    private val searchTerm: String
) : PageKeyedDataSource<Int, Game>() {

    private val repository: GamesRepository by lazy {
        GamesRepository(context)
    }

    private var lastGame: Game? = null

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Game>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response = repository.getGames(FIRESTORE_FIRST_PAGE)) {
                is GamesResponse.Success -> {
                    val games = response.result.documents.toGamesList()
                    if(games.isNotEmpty()) lastGame = games.last()
                    val gamesFiltered = games.filterBySearchTerm(searchTerm)
                    callback.onResult(gamesFiltered, null, FIRESTORE_FIRST_PAGE + 1)
                }
                is GamesResponse.Error -> {
                    Log.e(FIRESTORE_LOG_KEY, response.message)
                    callback.onResult(mutableListOf(), null, FIRESTORE_FIRST_PAGE + 1)
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Game>) {
        loadData(params.key, params.key - 1, callback)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Game>) {
        loadData(params.key, params.key + 1, callback)
    }

    private fun loadData(page: Int, nextPage: Int, callback: LoadCallback<Int, Game>) {
        CoroutineScope(Dispatchers.IO).launch {
            when (val response = repository.getGames(page, lastGame)) {
                is GamesResponse.Success -> {
                    val games = response.result.documents.toGamesList()
                    if(games.isNotEmpty()) lastGame = games.last()
                    val gamesFiltered = games.filterBySearchTerm(searchTerm)
                    callback.onResult(gamesFiltered, nextPage)
                }
                is GamesResponse.Error -> {
                    Log.e(FIRESTORE_LOG_KEY, response.message)
                    callback.onResult(mutableListOf(), nextPage)
                }
            }
        }
    }

}