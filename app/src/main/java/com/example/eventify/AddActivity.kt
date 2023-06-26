package com.example.eventify

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventify.db.EventifyDatabase
import com.example.eventify.db.dao.CategoryDao
import com.example.eventify.db.model.Activity
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.imaginativeworld.whynotimagecarousel.ImageCarousel


class AddActivity : AppCompatActivity() {
    private lateinit var categoryDao: CategoryDao
    private lateinit var carousel: ImageCarousel
    private lateinit var activitySpinner: Spinner
    private val MAPS_CODE = 1

    companion object {
        private const val LEISURE = "Leisure" // TODO: Move somewhere else
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_activity)

        /* Adding on click listeners for date, time and location */
        val dateTextView = findViewById<MaterialTextView>(R.id.pickDateTextView)
        dateTextView.setOnClickListener { view -> showDatePickerDialog(view) }

        val timeTextView = findViewById<MaterialTextView>(R.id.pickTimeTextView)
        timeTextView.setOnClickListener { view -> showTimePickerDialog(view) }

        val locationTextView = findViewById<MaterialTextView>(R.id.pickLocationTextView)
        locationTextView.setOnClickListener { view -> showLocationPickerDialog(view) }

        /* Setting up image carousel */
        carousel = findViewById(R.id.carousel)
        carousel.registerLifecycle(lifecycle)
        carousel.setData(ArrayList())

        /* Setting up category dropdown/spinner */
        activitySpinner = findViewById(R.id.activityType)
        categoryDao = EventifyDatabase.getInstance(baseContext).getCategoryDao()
        val categories = categoryDao.getAllCategories()
        val categoryNames = categories.map { it.name }
        val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activitySpinner.adapter = adapter
        activitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                activitySpinner.setSelection(position)
                // Activities of type Leisure can have images as well
                val selectedValue = parent.getItemAtPosition(position) as String
                if (LEISURE == selectedValue) {
                    findViewById<FloatingActionButton>(R.id.fabAddPhoto).visibility = View.VISIBLE
                    findViewById<ImageCarousel>(R.id.carousel).visibility = View.VISIBLE
                } else {
                    findViewById<FloatingActionButton>(R.id.fabAddPhoto).visibility = View.GONE
                    findViewById<ImageCarousel>(R.id.carousel).visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        // Setting title of activity - localization is not registered from AndroidManifest android:label property
        supportActionBar?.title = getString(R.string.add_event)
    }

    fun showDatePickerDialog(view: View) {
        val newFragment = DatePickerFragment()
        newFragment.setTextView(view as MaterialTextView)
        newFragment.show(supportFragmentManager, "datePicker")
    }

    fun showTimePickerDialog(view: View) {
        val newFragment = TimePickerFragment()
        newFragment.setTextView(view as MaterialTextView)
        newFragment.show(supportFragmentManager, "timePicker")
    }

    fun showLocationPickerDialog(view: View) {
        val intent = Intent(this, MapsActivity::class.java)
        startActivityForResult(intent, MAPS_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MAPS_CODE && resultCode == android.app.Activity.RESULT_OK) {
            val selectedLocationName = data?.getStringExtra("selectedLocationName")
            if (!selectedLocationName.isNullOrEmpty()) {
                findViewById<MaterialTextView>(R.id.pickLocationTextView).text = selectedLocationName
            }
        }
    }

    fun showAddPhotoBottomDialog(view: View) {
        val addPhotoBottomDialogFragment: AddPhotoBottomDialogFragment =
            AddPhotoBottomDialogFragment.getInstance(carousel)
        addPhotoBottomDialogFragment.show(
            supportFragmentManager, "add_photo_dialog_fragment"
        )
    }

    fun addActivity(view: View) {
        val title = findViewById<TextInputEditText>(R.id.editTextTitle).text.toString().trim()
        val description =
            findViewById<TextInputEditText>(R.id.editTextDescription).text.toString().trim()
        val location =
            findViewById<MaterialTextView>(R.id.pickLocationTextView).text.toString().trim()
        val time = findViewById<MaterialTextView>(R.id.pickTimeTextView).text.toString().trim()
        val date = findViewById<MaterialTextView>(R.id.pickDateTextView).text.toString().trim()
        val category = findViewById<Spinner>(R.id.activityType).selectedItem
        val categoryId = categoryDao.getCategoryIdByName(category as String)
        if (title.isEmpty()) {
            findViewById<TextInputEditText>(R.id.editTextTitle).error =
                getString(R.string.field_is_required)
            return
        }
        if (description.isEmpty()) {
            findViewById<TextInputEditText>(R.id.editTextDescription).error =
                getString(R.string.field_is_required)
            return
        }
        if (date.isEmpty()) {
            findViewById<MaterialTextView>(R.id.pickDateTextView).error =
                getString(R.string.field_is_required)
            return
        } else {
            findViewById<MaterialTextView>(R.id.pickDateTextView).error = null
        }
        if (time.isEmpty()) {
            findViewById<MaterialTextView>(R.id.pickTimeTextView).error =
                getString(R.string.field_is_required)
            return
        } else {
            findViewById<MaterialTextView>(R.id.pickTimeTextView).error = null
        }
        if (category == null) {
            Toast.makeText(this, getString(R.string.select_category), Toast.LENGTH_SHORT).show()
            return
        }
        if (location.isEmpty()) {
            findViewById<MaterialTextView>(R.id.pickLocationTextView).error =
                getString(R.string.field_is_required)
            return
        } else {
            findViewById<MaterialTextView>(R.id.pickLocationTextView).error = null
        }

        // Create a new activity object
        val activity = Activity.createActivity(
            title, description, location, "$date $time", categoryId
        ) // TODO: date format, add images for leisure
        val activityDao = EventifyDatabase.getInstance(baseContext).getActivityDao()

        // Use a coroutine to insert the activity in a non-blocking way
        CoroutineScope(Dispatchers.IO).launch {
            activityDao.insert(activity)
        }
        // Show a toast or perform any other action to indicate the activity is added
        Toast.makeText(this, getString(R.string.activity_added_successfully), Toast.LENGTH_SHORT)
            .show()
        // End activity
        finish()
    }

}