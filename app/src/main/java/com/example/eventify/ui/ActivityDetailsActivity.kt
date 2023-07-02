package com.example.eventify.ui

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.eventify.R
import com.example.eventify.db.EventifyDatabase
import com.example.eventify.db.model.Activity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.io.IOException


class ActivityDetailsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var activity: Activity
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details) // Replace with your XML layout file

        // Retrieve the activity object passed from the previous activity
        activity = intent.getSerializableExtra("activity") as Activity

        val titleTextView: TextView = findViewById(R.id.titleTextView)
        titleTextView.text = activity.name

        val descriptionTextView: TextView = findViewById(R.id.descriptionTextView)
        descriptionTextView.text = activity.description

        val locationTextView: TextView = findViewById(R.id.locationTextView)
        locationTextView.text = activity.location

        val dateTextView: TextView = findViewById(R.id.dateTextView)
        dateTextView.text = activity.time

        val categoryChip: Chip = findViewById(R.id.categoryChip)
        categoryChip.text = EventifyDatabase.getInstance(this).getCategoryDao()
            .getCategoryNameById(activity.categoryId)

        val images =
            EventifyDatabase.getInstance(this).getImageDao().getImagesForActivity(activity.id)
        val carousel = findViewById<ImageCarousel>(R.id.activityDetailsCarousel)
        if (images.isEmpty()) {
            carousel.visibility = View.GONE
        } else {
            for (image in images) {
                carousel.addData(CarouselItem(imageUrl = image.url))
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Retrieve the latitude and longitude coordinates for the location name
        val locationName = activity.location

        val geocoder = Geocoder(this)
        val addresses: List<Address>?

        try {
            addresses = geocoder.getFromLocationName(locationName, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val latitude = address.latitude
                val longitude = address.longitude

                // Create a LatLng object with the retrieved coordinates
                val locationLatLng = LatLng(latitude, longitude)

                // Add a marker to the map
                mMap.addMarker(
                    MarkerOptions()
                        .position(locationLatLng)
                        .title(locationName)
                )

                // Move the camera to the marker location
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 15f))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
