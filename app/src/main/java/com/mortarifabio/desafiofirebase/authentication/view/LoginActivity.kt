package com.mortarifabio.desafiofirebase.authentication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mortarifabio.desafiofirebase.games.view.GamesListActivity
import com.mortarifabio.desafiofirebase.authentication.AuthenticationResponse
import com.mortarifabio.desafiofirebase.authentication.viewModel.AuthenticationViewModel
import com.mortarifabio.desafiofirebase.databinding.ActivityLoginBinding
import com.mortarifabio.desafiofirebase.extensions.showInSnackBar
import com.mortarifabio.desafiofirebase.utils.Constants.Analytics.ANALYTICS_CREATE_ACCOUNT_EVENT
import com.mortarifabio.desafiofirebase.utils.Constants.Analytics.ANALYTICS_MANUAL_LOGIN_EVENT

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthenticationViewModel by lazy {
        ViewModelProvider(this).get(AuthenticationViewModel::class.java)
    }
    private val auth by lazy {
        Firebase.auth
    }
    private val analytics by lazy {
        Firebase.analytics
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservables()
        loadPreferences()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        currentUser?.let {
            val intent = Intent(this, GamesListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservables() = with(binding) {
        btLoginCreate.setOnClickListener {
            analytics.logEvent(ANALYTICS_CREATE_ACCOUNT_EVENT, null)
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        btLoginLogin.setOnClickListener {
            analytics.logEvent(ANALYTICS_MANUAL_LOGIN_EVENT, null)
            viewModel.login(binding)
        }
        viewModel.userLiveData.observe(this@LoginActivity){ response ->
            when (response) {
                is AuthenticationResponse.Success -> {
                    response.user?.uid?.let {
                        val intent = Intent(this@LoginActivity, GamesListActivity::class.java)
                        startActivity(intent)
                    }
                }
                is AuthenticationResponse.Error -> {
                    response.message.showInSnackBar(mcvLoginCard)
                }
            }
        }
    }

    private fun loadPreferences() {
        binding.tietLoginEmail.setText(viewModel.getUserEmail())
    }
}