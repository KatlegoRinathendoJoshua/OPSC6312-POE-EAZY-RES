package com.eazyres.app.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eazyres.app.data.local.SessionManager
import com.eazyres.app.data.repository.AuthRepository
import com.eazyres.app.databinding.ActivitySettingsBinding
import com.eazyres.app.ui.auth.LoginActivity
import kotlinx.coroutines.launch

/**
 * "The user must be able to change their settings" requirement.
 * Lets the user update their profile (full name, phone) via
 * PUT /users/settings, plus local-only preferences (notifications,
 * dark mode) stored in encrypted SharedPreferences.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var session: SessionManager
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        val user = session.getUser()
        binding.etFullName.setText(user?.fullName ?: "")
        binding.etPhone.setText(user?.phone ?: "")
        binding.switchNotifications.isChecked = session.areNotificationsEnabled()
        binding.switchDarkMode.isChecked = session.isDarkModeEnabled()

        binding.btnSave.setOnClickListener { saveSettings() }
        binding.btnLogout.setOnClickListener { logout() }
    }

    private fun saveSettings() {
        val fullName = binding.etFullName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val token = session.getToken() ?: return

        session.setNotificationsEnabled(binding.switchNotifications.isChecked)
        session.setDarkModeEnabled(binding.switchDarkMode.isChecked)

        lifecycleScope.launch {
            when (val result = authRepository.updateSettings(token, fullName, phone)) {
                is AuthRepository.Result.Success -> {
                    session.saveSession(token, result.data.user)
                    Toast.makeText(this@SettingsActivity, "Settings saved", Toast.LENGTH_SHORT).show()
                }
                is AuthRepository.Result.Error -> {
                    Toast.makeText(this@SettingsActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun logout() {
        session.clear()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
