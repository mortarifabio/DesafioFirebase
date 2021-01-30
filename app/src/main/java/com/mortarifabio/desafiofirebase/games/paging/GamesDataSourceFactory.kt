package com.mortarifabio.desafiofirebase.games.paging

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.mortarifabio.desafiofirebase.model.Game

class GamesDataSourceFactory(
    private val context: Context,
    private val searchTerm: String
) : DataSource.Factory<Int, Game>() {
    private val gamesLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, Game>>()

    override fun create(): DataSource<Int, Game> {
        val gamesDataSource = GamesPageKeyedDataSource(context, searchTerm)
        gamesLiveDataSource.postValue(gamesDataSource)
        return gamesDataSource
    }

    fun getLiveDataSource(): MutableLiveData<PageKeyedDataSource<Int, Game>> {
        return gamesLiveDataSource
    }
}