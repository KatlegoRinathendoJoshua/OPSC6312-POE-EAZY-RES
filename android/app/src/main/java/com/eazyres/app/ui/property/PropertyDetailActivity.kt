package com.eazyres.app.ui.property

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.eazyres.app.data.repository.PropertyRepository
import com.eazyres.app.databinding.ActivityPropertyDetailBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * Screen 5 — Property Detail. Loaded from GET /properties/:id.
 */
class PropertyDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPropertyDetailBinding
    private val propertyRepository = PropertyRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPropertyDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val propertyId = intent.getStringExtra(EXTRA_PROPERTY_ID)
        if (propertyId == null) {
            finish()
            return
        }
        loadProperty(propertyId)

        binding.btnContact.setOnClickListener {
            Toast.makeText(this, "Opening chat with landlord…", Toast.LENGTH_SHORT).show()
            // In the full build this opens Screen 8 — Chat.
        }
    }

    private fun loadProperty(id: String) {
        lifecycleScope.launch {
            when (val result = propertyRepository.getPropertyById(id)) {
                is PropertyRepository.Result.Success -> {
                    val property = result.data
                    binding.tvName.text = property.name
                    binding.tvAddress.text = "${property.address}, ${property.city}"
                    val rands = NumberFormat.getCurrencyInstance(Locale("en", "ZA")).format(property.price)
                    binding.tvPrice.text = "$rands / month"
                    binding.tvRating.text = "★ ${property.ratingAvg}  •  ${property.availableRooms} rooms available"
                    binding.tvDescription.text = property.description
                    binding.tvAmenities.text = property.amenities.joinToString(", ")
                    Glide.with(this@PropertyDetailActivity).load(property.photoUrl).into(binding.ivPhoto)
                }
                is PropertyRepository.Result.Error -> {
                    Toast.makeText(this@PropertyDetailActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        const val EXTRA_PROPERTY_ID = "extra_property_id"
    }
}
