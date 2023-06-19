package com.example.eventify

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class AddPhotoBottomDialogFragment : BottomSheetDialogFragment() {

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_photo_bottom_dialog, container, false)

        val cameraTextView = view.findViewById<TextView>(R.id.tv_btn_add_photo_camera)
        val galleryTextView = view.findViewById<TextView>(R.id.tv_btn_add_photo_gallery)
        val urlTextView = view.findViewById<TextView>(R.id.tv_btn_add_photo_url)

        /* Click Listeners for bottom fragment elements */
        cameraTextView.setOnClickListener {
            openCamera()
        }

        galleryTextView.setOnClickListener {
            openGallery()
        }

        urlTextView.setOnClickListener {
            showUrlInputDialog()
        }

        return view
    }

    companion object {

        @Volatile
        private var addPhotoBottomDialogFragment: AddPhotoBottomDialogFragment? = null

        fun getInstance(): AddPhotoBottomDialogFragment {
            return addPhotoBottomDialogFragment ?: synchronized(this) {
                val instance = AddPhotoBottomDialogFragment()
                addPhotoBottomDialogFragment = instance
                instance
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(cameraIntent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
        }
    }
    private fun openGallery() {

    }

    private fun showUrlInputDialog() {}

}