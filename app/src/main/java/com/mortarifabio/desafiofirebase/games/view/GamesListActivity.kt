package com.mortarifabio.desafiofirebase.games.view

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent.*
import android.speech.SpeechRecognizer
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.mortarifabio.desafiofirebase.R
import com.mortarifabio.desafiofirebase.databinding.ActivityGamesListBinding
import com.mortarifabio.desafiofirebase.extensions.showInSnackBar
import com.mortarifabio.desafiofirebase.games.view.adapter.GamesListAdapter
import com.mortarifabio.desafiofirebase.games.viewModel.GamesViewModel
import com.mortarifabio.desafiofirebase.utils.Constants.Analytics.ANALYTICS_ADD_GAME_EVENT
import com.mortarifabio.desafiofirebase.utils.Constants.Intent.INTENT_GAME_KEY
import java.util.*


class GamesListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGamesListBinding
    private val viewModel: GamesViewModel by lazy {
        ViewModelProvider(this).get(GamesViewModel::class.java)
    }
    private val gamesAdapter: GamesListAdapter by lazy {
        GamesListAdapter {
            it?.let {
                val intent = Intent(this, GamesDetailsActivity::class.java)
                intent.putExtra(INTENT_GAME_KEY, it)
                startActivity(intent)
            }
        }
    }
    private val speechRecognizer by lazy {
        SpeechRecognizer.createSpeechRecognizer(this)
    }
    private val analytics by lazy {
        Firebase.analytics
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupRecyclerView()
        setupObservables()
        checkAudioPermission()
    }

    override fun onResume() {
        super.onResume()
        loadContent()
    }

    private fun setupObservables() = with(binding) {
        fabGamesListAdd.setOnClickListener {
            analytics.logEvent(ANALYTICS_ADD_GAME_EVENT, null)
            startActivity(Intent(this@GamesListActivity, GamesRegisterActivity::class.java))
        }
        tietGamesListSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.getGames(text.toString())
            loadContent()
        }
        binding.ibGamesListListen.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                ACTION_UP -> {
                    view.performClick()
                    speechRecognizer.stopListening()
                    binding.tilGamesListSearch.hint = getString(R.string.search_games)
                }
                ACTION_DOWN -> {
                    val intent = Intent(ACTION_RECOGNIZE_SPEECH)
                    intent.putExtra(EXTRA_LANGUAGE_MODEL, LANGUAGE_MODEL_FREE_FORM)
                    intent.putExtra(EXTRA_LANGUAGE, Locale.getDefault())
                    speechRecognizer.startListening(intent)
                }
            }
            false
        }
    }

    private fun setupRecyclerView() {
        binding.rvGamesList.apply {
            layoutManager = GridLayoutManager(this@GamesListActivity, 2)
            adapter = gamesAdapter
        }
    }

    private fun loadContent() {
        viewModel.gamesPagedList?.observe(this@GamesListActivity) { pagedList ->
            gamesAdapter.submitList(pagedList)
        }
    }

    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PERMISSION_GRANTED) {
            setupVoiceRecognition()
        } else {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    setupVoiceRecognition()
                } else {
                    getString(R.string.voice_recognition_disabled).showInSnackBar(binding.clGamesList)
                }
            }
            requestPermissionLauncher.launch(RECORD_AUDIO)
        }
    }

    private fun setupVoiceRecognition() {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {}

            override fun onBeginningOfSpeech() {
                binding.tietGamesListSearch.setText("")
                binding.tilGamesListSearch.hint = getString(R.string.listening)
            }

            override fun onRmsChanged(p0: Float) {}
            override fun onBufferReceived(p0: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(p0: Int) {}

            override fun onResults(bundle: Bundle?) {
                val data = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                binding.tietGamesListSearch.setText(data?.get(0) ?: "")
                binding.tilGamesListSearch.hint = getString(R.string.search_games)
            }

            override fun onPartialResults(p0: Bundle?) {}
            override fun onEvent(p0: Int, p1: Bundle?) {}
        })
    }
}
