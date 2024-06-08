package com.example.taskkttelematic.map

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.taskkttelematic.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(private val inflater: LayoutInflater) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val infoWindow = inflater.inflate(R.layout.cutom_info_window, null)

        val tvTitle = infoWindow.findViewById<TextView>(R.id.titleTextView)
        val tvSnippet = infoWindow.findViewById<TextView>(R.id.snippetTextView)

        tvTitle.text = marker.title
        tvSnippet.text = "Latitude: ${marker.position?.latitude}\nLongitude: ${marker.position?.longitude}\nTimestamp: ${marker.snippet}"

        return infoWindow
    }
}
