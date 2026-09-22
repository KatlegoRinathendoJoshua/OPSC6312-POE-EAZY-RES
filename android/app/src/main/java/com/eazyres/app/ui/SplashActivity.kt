package com.eazyres.app.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.eazyres.app.data.local.SessionManager
import com.eazyres.app.databinding.ActivitySplashBinding
import com.eazyres.app.ui.auth.LoginActivity
import com.eazyres.app.ui.home.HomeActivity

/**
 * Screen 1 — Splash. Routes the user to Home if already logged in
 * (session token found in encrypted storage), otherwise to Login.
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            val next = if (session.isLoggedIn()) HomeActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this, next))
            finish()
        }, 1200)
    }
}
