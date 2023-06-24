package com.example.eventify

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class AddPhotoBottomDialogFragment : BottomSheetDialogFragment() {

    private val REQUEST_IMAGE_CAPTURE = 123

    private lateinit var carousel: ImageCarousel
    private lateinit var startGallery: ActivityResultLauncher<Intent>
    private lateinit var currentPhotoPath: String
    private var photoFile: File? = null

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            // Handle permission denied
        }
    }


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

        startGallery =
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

    /* Uplaoding image from Camera */

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            dispatchTakePictureIntent()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            photoFile = createImageFile()
            val photoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.eventify.fileprovider",
                photoFile!!
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (ex: IOException) {
            // Handle file creation error
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFileName = "JPEG_${timeStamp}_"
        val imageFile = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
        }

        val resolver = requireActivity().contentResolver
        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        currentPhotoPath = imageFile.absolutePath

        return imageFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            carousel.addData(CarouselItem(imageUrl = file.absolutePath))
        }
    }


    /* Uplaoding image from Gallery */
    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startGallery.launch(intent)
    }

    /* Uplaoding image from URL */
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