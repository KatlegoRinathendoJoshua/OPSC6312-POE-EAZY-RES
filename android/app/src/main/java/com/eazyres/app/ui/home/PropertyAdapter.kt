package com.eazyres.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eazyres.app.data.model.Property
import com.eazyres.app.databinding.ItemPropertyBinding
import java.text.NumberFormat
import java.util.Locale

class PropertyAdapter(
    private val onClick: (Property) -> Unit
) : ListAdapter<Property, PropertyAdapter.PropertyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val binding = ItemPropertyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PropertyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PropertyViewHolder(private val binding: ItemPropertyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(property: Property) {
            binding.tvName.text = property.name
            binding.tvAddress.text = "${property.address}, ${property.city}"
            val rands = NumberFormat.getCurrencyInstance(Locale("en", "ZA")).format(property.price)
            binding.tvPrice.text = "$rands / month"

            Glide.with(binding.ivPhoto.context)
                .load(property.photoUrl)
                .centerCrop()
                .into(binding.ivPhoto)

            binding.root.setOnClickListener { onClick(property) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Property>() {
            override fun areItemsTheSame(oldItem: Property, newItem: Property) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Property, newItem: Property) = oldItem == newItem
        }
    }
}
