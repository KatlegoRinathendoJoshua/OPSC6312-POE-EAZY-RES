package com.eazyres.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eazyres.app.data.local.SessionManager
import com.eazyres.app.data.repository.AuthRepository
import com.eazyres.app.databinding.ActivityLoginBinding
import com.eazyres.app.ui.home.HomeActivity
import com.eazyres.app.utils.Validators
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (!Validators.isValidEmail(email)) {
            binding.etEmail.error = "Enter a valid email address"
            return
        }
        if (!Validators.isValidPassword(password)) {
            binding.etPassword.error = "Password must be at least 8 characters"
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            when (val result = authRepository.login(email, password)) {
                is AuthRepository.Result.Success -> {
                    session.saveSession(result.data.token, result.data.user)
                    setLoading(false)
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()
                }
                is AuthRepository.Result.Error -> {
                    setLoading(false)
                    Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnLogin.isEnabled = !loading
    }
}
