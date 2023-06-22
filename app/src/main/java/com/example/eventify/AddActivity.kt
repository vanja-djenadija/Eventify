package com.example.eventify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton


class AddActivity : AppCompatActivity() {

    private val MAPS_CODE = 1
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
            AddPhotoBottomDialogFragment.getInstance()
        addPhotoBottomDialogFragment.show(
            supportFragmentManager,
            "add_photo_dialog_fragment"
        )
    }

    fun addActivity(view: View) {}


}