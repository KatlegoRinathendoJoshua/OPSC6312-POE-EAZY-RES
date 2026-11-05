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
import com.eazyres.app.data.local.SessionManager
import com.eazyres.app.data.repository.FavouriteRepository
import com.eazyres.app.data.repository.PropertyRepository
import com.eazyres.app.databinding.ActivityHomeBinding
import com.eazyres.app.ui.property.PropertyDetailActivity
import com.eazyres.app.ui.settings.SettingsActivity
import kotlinx.coroutines.launch
import com.eazyres.app.ui.saved.SavedPropertiesActivity
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val propertyRepository = PropertyRepository()

    private lateinit var favouriteRepository: FavouriteRepository
    private lateinit var adapter: PropertyAdapter

    private val savedPropertyIds = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val sessionManager = SessionManager(this)
        favouriteRepository = FavouriteRepository(sessionManager)

        adapter = PropertyAdapter(
            onClick = { property ->
                val intent = Intent(
                    this,
                    PropertyDetailActivity::class.java
                )
                intent.putExtra(
                    PropertyDetailActivity.EXTRA_PROPERTY_ID,
                    property.id
                )
                startActivity(intent)
            },
            onFavouriteClick = { property ->
                toggleFavourite(property.id)
            }
        )

        binding.rvProperties.layoutManager = LinearLayoutManager(this)
        binding.rvProperties.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            loadProperties()
            loadFavourites()
        }

        loadProperties()
        loadFavourites()
    }

    private fun loadProperties() {
        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            when (val result = propertyRepository.getProperties()) {
                is PropertyRepository.Result.Success -> {
                    adapter.submitList(result.data)
                }

                is PropertyRepository.Result.Error -> {
                    Toast.makeText(
                        this@HomeActivity,
                        result.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            binding.progressBar.visibility = android.view.View.GONE
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun loadFavourites() {
        lifecycleScope.launch {
            when (val result = favouriteRepository.getFavourites()) {
                is PropertyRepository.Result.Success -> {
                    savedPropertyIds.clear()
                    savedPropertyIds.addAll(result.data)

                    adapter.setSavedPropertyIds(result.data)
                }

                is PropertyRepository.Result.Error -> {
                    Toast.makeText(
                        this@HomeActivity,
                        result.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun toggleFavourite(propertyId: String) {
        lifecycleScope.launch {

            val result = if (propertyId in savedPropertyIds) {
                favouriteRepository.removeFavourite(propertyId)
            } else {
                favouriteRepository.saveFavourite(propertyId)
            }

            when (result) {
                is PropertyRepository.Result.Success -> {

                    val isSaved = propertyId !in savedPropertyIds

                    if (isSaved) {
                        savedPropertyIds.add(propertyId)
                    } else {
                        savedPropertyIds.remove(propertyId)
                    }

                    adapter.updateSavedState(propertyId, isSaved)

                    Toast.makeText(
                        this@HomeActivity,
                        result.data,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PropertyRepository.Result.Error -> {
                    Toast.makeText(
                        this@HomeActivity,
                        result.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }

            R.id.action_saved_properties -> {
                startActivity(
                    Intent(this, SavedPropertiesActivity::class.java)
                )
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}