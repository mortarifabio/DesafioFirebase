package com.mortarifabio.desafiofirebase.games.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.mortarifabio.desafiofirebase.databinding.ActivityGamesListBinding
import com.mortarifabio.desafiofirebase.games.view.adapter.GamesListAdapter
import com.mortarifabio.desafiofirebase.games.viewModel.GamesViewModel
import com.mortarifabio.desafiofirebase.utils.Constants.Intent.INTENT_GAME_KEY

class GamesListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGamesListBinding
    private val viewModel: GamesViewModel by lazy {
        ViewModelProvider(this).get(GamesViewModel::class.java)
    }
    private val gamesAdapter : GamesListAdapter by lazy{
        GamesListAdapter{
            it?.let {
                val intent = Intent(this, GamesDetailsActivity::class.java)
                intent.putExtra(INTENT_GAME_KEY, it)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        loadContent()
        setupObservables()
    }

    private fun setupObservables() = with(binding) {
        fabGamesListAdd.setOnClickListener {
            startActivity(Intent(this@GamesListActivity, GamesRegisterActivity::class.java))
        }
        tietGamesListSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.getGames(text.toString())
            loadContent()
        }
    }

    private fun setupRecyclerView() {
        binding.rvGamesList.apply {
            layoutManager = GridLayoutManager(this@GamesListActivity, 2)
            adapter = gamesAdapter
        }
    }

    private fun loadContent() {
        viewModel.gamesPagedList?.observe(this@GamesListActivity){ pagedList ->
            gamesAdapter.submitList(pagedList)
        }
    }
}