package com.mortarifabio.desafiofirebase.authentication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mortarifabio.desafiofirebase.games.view.GamesListActivity
import com.mortarifabio.desafiofirebase.databinding.ActivitySplashBinding
import com.mortarifabio.desafiofirebase.utils.Constants.Analytics.ANALYTICS_AUTOMATIC_LOGIN_EVENT
import java.util.*
import kotlin.concurrent.schedule

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val auth by lazy {
        Firebase.auth
    }
    private val analytics by lazy {
        Firebase.analytics
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTimer()
    }

    private fun setTimer() {
        Timer("SplashScreen", false).schedule(2000){
            val intent = auth.currentUser?.let {
                analytics.logEvent(ANALYTICS_AUTOMATIC_LOGIN_EVENT, null)
                Intent(this@SplashActivity, GamesListActivity::class.java)
            } ?: run {
                Intent(this@SplashActivity, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}