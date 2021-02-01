package com.mortarifabio.desafiofirebase.games.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mortarifabio.desafiofirebase.R
import com.mortarifabio.desafiofirebase.databinding.ActivityGamesDetailsBinding
import com.mortarifabio.desafiofirebase.model.Game
import com.mortarifabio.desafiofirebase.utils.Constants.Analytics.ANALYTICS_UPDATE_GAME_EVENT
import com.mortarifabio.desafiofirebase.utils.Constants.Intent.INTENT_GAME_KEY

class GamesDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGamesDetailsBinding
    private var game: Game? = null
    private val storageRef by lazy {
        Firebase.storage.reference
    }

    private val analytics by lazy {
        Firebase.analytics
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamesDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        game = intent.getParcelableExtra(INTENT_GAME_KEY)
        loadContent()
        setupObservables()
    }

    private fun loadContent() = with(binding) {
        game?.let{
            val imgRef = it.image?.let { image -> storageRef.child(image) }
            Glide.with(this@GamesDetailsActivity)
                .load(imgRef)
                .placeholder(R.drawable.bg_placeholder)
                .fitCenter()
                .into(ivGameDetailsImage)
            tvGameDetailsImageName.text = it.name
            tvGameDetailsName.text = it.name
            tvGameDetailsYear.text = it.createdAt
            tvGameDetailsDescription.text = it.description
        }
    }

    private fun setupObservables() = with(binding) {
        btGamesDetailsBack.setOnClickListener {
            finish()
        }
        btGamesDetailsEdit.setOnClickListener {
            analytics.logEvent(ANALYTICS_UPDATE_GAME_EVENT, null)
            val intent = Intent(this@GamesDetailsActivity, GamesRegisterActivity::class.java)
            intent.putExtra(INTENT_GAME_KEY, game)
            startActivity(intent)
        }
    }

}