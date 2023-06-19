package com.example.eventify

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_activity)

        // Add your additional logic here
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
        val mapsFragment = MapsFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, mapsFragment)
            .addToBackStack("fragment_maps")
            .commit()
    }

    fun showAddPhotoBottomDialog(view: View) {
        val addPhotoBottomDialogFragment: AddPhotoBottomDialogFragment =
            AddPhotoBottomDialogFragment.getInstance()
        addPhotoBottomDialogFragment.show(
            supportFragmentManager,
            "add_photo_dialog_fragment"
        )
    }

    fun addActivity(view: View) {}


}