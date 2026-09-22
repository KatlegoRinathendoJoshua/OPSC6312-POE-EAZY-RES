package com.eazyres.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eazyres.app.R
import com.eazyres.app.data.repository.PropertyRepository
import com.eazyres.app.databinding.ActivityHomeBinding
import com.eazyres.app.ui.property.PropertyDetailActivity
import com.eazyres.app.ui.settings.SettingsActivity
import kotlinx.coroutines.launch

/**
 * Screen 3 — Home. Shows the property list fetched live from the
 * REST API (GET /properties). Pull-to-refresh re-fetches the list.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val propertyRepository = PropertyRepository()
    private lateinit var adapter: PropertyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        adapter = PropertyAdapter { property ->
            val intent = Intent(this, PropertyDetailActivity::class.java)
            intent.putExtra(PropertyDetailActivity.EXTRA_PROPERTY_ID, property.id)
            startActivity(intent)
        }
        binding.rvProperties.layoutManager = LinearLayoutManager(this)
        binding.rvProperties.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { loadProperties() }

        loadProperties()
    }

    private fun loadProperties() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        lifecycleScope.launch {
            when (val result = propertyRepository.getProperties()) {
                is PropertyRepository.Result.Success -> {
                    adapter.submitList(result.data)
                }
                is PropertyRepository.Result.Error -> {
                    Toast.makeText(this@HomeActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
            binding.progressBar.visibility = android.view.View.GONE
            binding.swipeRefresh.isRefreshing = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
