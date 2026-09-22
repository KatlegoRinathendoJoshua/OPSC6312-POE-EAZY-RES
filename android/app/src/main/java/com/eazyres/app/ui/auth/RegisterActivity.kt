package com.eazyres.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eazyres.app.data.local.SessionManager
import com.eazyres.app.data.repository.AuthRepository
import com.eazyres.app.databinding.ActivityRegisterBinding
import com.eazyres.app.ui.home.HomeActivity
import com.eazyres.app.utils.Validators
import kotlinx.coroutines.launch

/**
 * Handles new-user registration. The password typed here is sent to the
 * backend over HTTPS/TLS and hashed with bcrypt server-side — it is
 * never stored in plain text on the device or in the database
 * (see backend/routes/auth.js -> bcrypt.hash).
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authRepository = AuthRepository()
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        binding.btnRegister.setOnClickListener { attemptRegister() }
        binding.tvGoLogin.setOnClickListener { finish() }
    }

    private fun attemptRegister() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (!Validators.isNotBlank(fullName)) {
            binding.etFullName.error = "Enter your full name"; return
        }
        if (!Validators.isValidEmail(email)) {
            binding.etEmail.error = "Enter a valid email address"; return
        }
        if (!Validators.isValidPhone(phone)) {
            binding.etPhone.error = "Enter a valid phone number"; return
        }
        if (!Validators.isValidPassword(password)) {
            binding.etPassword.error = "Password must be at least 8 characters"; return
        }

        setLoading(true)
        lifecycleScope.launch {
            when (val result = authRepository.register(email, password, fullName, phone)) {
                is AuthRepository.Result.Success -> {
                    session.saveSession(result.data.token, result.data.user)
                    setLoading(false)
                    startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
                    finish()
                }
                is AuthRepository.Result.Error -> {
                    setLoading(false)
                    Toast.makeText(this@RegisterActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) android.view.View.VISIBLE else android.view.View.GONE
        binding.btnRegister.isEnabled = !loading
    }
}
