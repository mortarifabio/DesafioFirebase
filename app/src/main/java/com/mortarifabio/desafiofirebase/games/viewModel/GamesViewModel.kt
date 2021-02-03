package com.mortarifabio.desafiofirebase.games.viewModel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.mortarifabio.desafiofirebase.databinding.ActivityGamesRegisterBinding
import com.mortarifabio.desafiofirebase.games.GamesBusiness
import com.mortarifabio.desafiofirebase.games.paging.GamesDataSourceFactory
import com.mortarifabio.desafiofirebase.model.Game
import com.mortarifabio.desafiofirebase.utils.Constants.Firestore.FIRESTORE_PAGE_SIZE
import kotlinx.coroutines.launch

class GamesViewModel(application: Application) : AndroidViewModel(application) {
    private val business by lazy {
        GamesBusiness(getApplication())
    }
    var saveLiveData = MutableLiveData<Boolean>()
    var gamesPagedList: LiveData<PagedList<Game>>? = null
    private var gamesLiveDataSource: LiveData<PageKeyedDataSource<Int, Game>>? = null
    private var pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(FIRESTORE_PAGE_SIZE).build()

    init {
        getGames()
    }

    fun saveGame(binding: ActivityGamesRegisterBinding, game: Game?, bitmap: Bitmap?) {
        viewModelScope.launch {
           saveLiveData.postValue(business.saveGame(binding, game, bitmap))
        }
    }

    fun getGames(searchTerm: String = "") {
        val gamesDataSourceFactory = GamesDataSourceFactory(getApplication(), searchTerm)
        gamesLiveDataSource = gamesDataSourceFactory.getLiveDataSource()
        gamesPagedList = LivePagedListBuilder(gamesDataSourceFactory, pagedListConfig).build()
    }
}