package com.example.eventify

import com.example.eventify.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem


class AddPhotoBottomDialogFragment : BottomSheetDialogFragment() {

    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var carousel: ImageCarousel

    @SuppressLint("MissingInflatedId")
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

        fun getInstance(carousel: ImageCarousel): AddPhotoBottomDialogFragment {
            return addPhotoBottomDialogFragment ?: synchronized(this) {
                var instance = AddPhotoBottomDialogFragment()
                instance.carousel = carousel
                addPhotoBottomDialogFragment = instance
                instance
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(cameraIntent)
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
            }
        }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startGallery.launch(intent)
    }

    private val startGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (intent != null) {
                    val selectedImages =
                        intent.clipData // Use clipData to get multiple selected images
                    val selectedImage = intent.data // Use data to get a single selected image

                    if (selectedImages != null) {
                        for (i in 0 until selectedImages.itemCount) {
                            val imageUri = selectedImages.getItemAt(i).uri
                            // Add the selected image to the carousel
                            carousel.addData(CarouselItem(imageUrl = imageUri.toString()))
                        }
                    } else if (selectedImage != null) {
                        // Add the selected image to the carousel
                        carousel.addData(CarouselItem(imageUrl = selectedImage.toString()))
                    }
                }
            }
        }


    private fun showUrlInputDialog() {
        val context: Context =
            requireContext()  // Replace 'this' with the appropriate context reference

        val inputEditText = EditText(context)
        inputEditText.hint = "Enter Image URL"

        val dialog = AlertDialog.Builder(context)
            .setTitle("Enter Image URL")
            .setView(inputEditText)
            .setPositiveButton("Load") { _, _ ->
                val imageUrl = inputEditText.text.toString()
                loadImageFromUrl(imageUrl)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun loadImageFromUrl(imageUrl: String) {
        carousel.addData(CarouselItem(imageUrl = imageUrl))
    }

}