package com.mortarifabio.desafiofirebase.authentication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.mortarifabio.desafiofirebase.authentication.AuthenticationResponse
import com.mortarifabio.desafiofirebase.authentication.viewModel.AuthenticationViewModel
import com.mortarifabio.desafiofirebase.databinding.ActivityRegisterBinding
import com.mortarifabio.desafiofirebase.extensions.showInSnackBar
import com.mortarifabio.desafiofirebase.games.view.GamesListActivity
import com.mortarifabio.desafiofirebase.utils.Constants.Analytics.ANALYTICS_REGISTER_USER_EVENT

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthenticationViewModel by lazy {
        ViewModelProvider(this).get(AuthenticationViewModel::class.java)
    }
    private val analytics by lazy {
        Firebase.analytics
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservables()
    }

    private fun setupObservables() = with(binding) {
        ibRegisterBack.setOnClickListener {
            onBackPressed()
        }
        btRegisterSave.setOnClickListener {
            analytics.logEvent(ANALYTICS_REGISTER_USER_EVENT, null)
            viewModel.registerUser(binding)
        }
        viewModel.userLiveData.observe(this@RegisterActivity) { response ->
            when (response) {
                is AuthenticationResponse.Success -> {
                    response.user?.uid?.let {
                        val intent = Intent(this@RegisterActivity, GamesListActivity::class.java)
                        startActivity(intent)
                    }
                }
                is AuthenticationResponse.Error -> {
                    response.message.showInSnackBar(mcvRegisterCard)
                }
            }
        }
    }
}