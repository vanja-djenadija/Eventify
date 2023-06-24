package com.example.eventify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.eventify.db.EventifyDatabase
import com.google.android.material.button.MaterialButton
import org.imaginativeworld.whynotimagecarousel.ImageCarousel


class AddActivity : AppCompatActivity() {
    lateinit var carousel: ImageCarousel
    lateinit var activitySpinner: Spinner
    private val MAPS_CODE = 1
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
                // Use the selectedCategoryName as needed
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }

        }
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
        if (requestCode == MAPS_CODE && resultCode == Activity.RESULT_OK) {
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

    fun addActivity(view: View) {}

}