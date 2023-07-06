package org.unibl.etf.mr.eventify.ui

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.unibl.etf.mr.eventify.R
import org.unibl.etf.mr.eventify.db.EventifyDatabase
import org.unibl.etf.mr.eventify.db.model.Activity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        dateTextView.text = activity.time.toString()

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

        val fabDeleteActivity: FloatingActionButton = findViewById(R.id.fabDeleteActivity)
        fabDeleteActivity.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.answer_positive)) { dialog, _ ->
                deleteActivity()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.answer_negative)) { dialog, _ ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun deleteActivity() {
        val activityDao = EventifyDatabase.getInstance(baseContext).getActivityDao()
        // Use a coroutine to insert the activity in a non-blocking way
        CoroutineScope(Dispatchers.IO).launch {
            activityDao.delete(activity)
        }
        finish()
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
