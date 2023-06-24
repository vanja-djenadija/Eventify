package com.example.eventify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventify.db.EventifyDatabase
import com.example.eventify.db.model.Activity
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class AddActivity : AppCompatActivity() {
    lateinit var carousel: ImageCarousel
    lateinit var activitySpinner: Spinner
    private val MAPS_CODE = 1

    companion object {
        private const val LEISURE = "Leisure" // TODO: Move somewhere else
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_activity)

        /* Setting up image carousel */
        carousel = findViewById(R.id.carousel)
        carousel.registerLifecycle(lifecycle)
        carousel.setData(ArrayList())

        /* Setting up category dropdown/spinner */
        activitySpinner = findViewById(R.id.activityType)
        val categoryDao = EventifyDatabase.getInstance(baseContext).getCategoryDao()
        val categories = categoryDao.getAllCategories()
        val categoryNames = categories.map { it.name }
        val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        activitySpinner.adapter = adapter
        activitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
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
        newFragment.setButton(view as MaterialButton)
        newFragment.show(supportFragmentManager, "datePicker")
    }

    fun showTimePickerDialog(view: View) {
        val newFragment = TimePickerFragment()
        newFragment.setButton(view as MaterialButton)
        newFragment.show(supportFragmentManager, "datePicker")
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
                findViewById<MaterialButton>(R.id.locationButton).text = selectedLocationName
            }
        }
    }

    fun showAddPhotoBottomDialog(view: View) {
        val addPhotoBottomDialogFragment: AddPhotoBottomDialogFragment =
            AddPhotoBottomDialogFragment.getInstance(carousel)
        addPhotoBottomDialogFragment.show(
            supportFragmentManager,
            "add_photo_dialog_fragment"
        )
    }

    fun addActivity(view: View) {
        val title = findViewById<TextInputEditText>(R.id.editTextTitle).text.toString()
        val description = findViewById<TextInputEditText>(R.id.editTextDescription).text.toString()
        val location = findViewById<MaterialButton>(R.id.locationButton).text.toString()
        val time = findViewById<MaterialButton>(R.id.pickTimeButton).text.toString()
        val date = findViewById<MaterialButton>(R.id.pickDateButton).text.toString()
        val category = findViewById<Spinner>(R.id.activityType).selectedItem

        // Create a new activity object
        val activity = Activity.createActivity(title, description, location, time, 1)
        val activityDao = EventifyDatabase.getInstance(baseContext).getActivityDao()

        // Use a coroutine to insert the activity in a non-blocking way
        CoroutineScope(Dispatchers.IO).launch {
            activityDao.insert(activity)
        }
        // Show a toast or perform any other action to indicate the activity is added
        Toast.makeText(this, "Activity added successfully", Toast.LENGTH_SHORT).show()
    }

}