package com.example.eventify.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.eventify.R
import com.example.eventify.db.EventifyDatabase
import com.example.eventify.db.model.Activity
import com.google.android.material.chip.Chip
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class ActivityDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details) // Replace with your XML layout file

        // Retrieve the activity object passed from the previous activity
        val activity = intent.getSerializableExtra("activity") as Activity

        // Use the activity object to populate the UI with the details
        // You can find the required views in the layout and set their text accordingly
        // For example:
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
    }
}
