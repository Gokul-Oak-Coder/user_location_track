package com.example.taskkttelematic.location


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskkttelematic.R
import com.example.taskkttelematic.model.LocationData

class LocationAdapter(
    val locations: MutableList<LocationData>, private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(location: LocationData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.bind(location)
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val latitudeTextView: TextView = itemView.findViewById(R.id.latitudeTextView)
        private val longitudeTextView: TextView = itemView.findViewById(R.id.longitudeTextView)
        private val timestampTextView: TextView = itemView.findViewById(R.id.timestampTextView)

        fun bind(location: LocationData) {
            latitudeTextView.text = location.latitude.toString()
            longitudeTextView.text = location.longitude.toString()
            timestampTextView.text = location.timestamp.toString()

            itemView.setOnClickListener {
                itemClickListener.onItemClick(location)
            }
        }
    }

    // Add this method to update the adapter dynamically
    fun addLocation(location: LocationData) {
        locations.add(location)
        notifyItemInserted(locations.size - 1)
    }
}
