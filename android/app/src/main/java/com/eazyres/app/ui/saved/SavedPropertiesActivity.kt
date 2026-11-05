package com.eazyres.app.ui.saved

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.eazyres.app.data.local.SessionManager
import com.eazyres.app.data.repository.FavouriteRepository
import com.eazyres.app.data.repository.PropertyRepository
import com.eazyres.app.databinding.ActivitySavedPropertiesBinding
import com.eazyres.app.ui.home.PropertyAdapter
import com.eazyres.app.ui.property.PropertyDetailActivity
import kotlinx.coroutines.launch

class SavedPropertiesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedPropertiesBinding
    private val propertyRepository = PropertyRepository()
    private lateinit var favouriteRepository: FavouriteRepository
    private lateinit var adapter: PropertyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySavedPropertiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        favouriteRepository = FavouriteRepository(SessionManager(this))

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Saved Properties"

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

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
                removeFavourite(property.id)
            }
        )

        binding.rvSavedProperties.layoutManager =
            LinearLayoutManager(this)

        binding.rvSavedProperties.adapter = adapter

        loadSavedProperties()
    }

    private fun loadSavedProperties() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        lifecycleScope.launch {
            when (val result = favouriteRepository.getFavourites()) {
                is PropertyRepository.Result.Success -> {
                    val properties = mutableListOf<com.eazyres.app.data.model.Property>()

                    for (id in result.data) {
                        when (val propertyResult =
                            propertyRepository.getPropertyById(id)) {

                            is PropertyRepository.Result.Success ->
                                properties.add(propertyResult.data)

                            is PropertyRepository.Result.Error -> {
                                Toast.makeText(
                                    this@SavedPropertiesActivity,
                                    propertyResult.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    adapter.submitList(properties)
                    adapter.setSavedPropertyIds(result.data)

                    binding.tvEmpty.visibility =
                        if (properties.isEmpty()) View.VISIBLE else View.GONE
                }

                is PropertyRepository.Result.Error -> {
                    Toast.makeText(
                        this@SavedPropertiesActivity,
                        result.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            binding.progressBar.visibility = View.GONE
        }
    }

    private fun removeFavourite(propertyId: String) {
        lifecycleScope.launch {
            when (val result =
                favouriteRepository.removeFavourite(propertyId)) {

                is PropertyRepository.Result.Success -> {
                    Toast.makeText(
                        this@SavedPropertiesActivity,
                        result.data,
                        Toast.LENGTH_SHORT
                    ).show()

                    loadSavedProperties()
                }

                is PropertyRepository.Result.Error -> {
                    Toast.makeText(
                        this@SavedPropertiesActivity,
                        result.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}