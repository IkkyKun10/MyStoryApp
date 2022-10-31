package com.riezki.storyapp.ui.addstory

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.riezki.storyapp.databinding.ActivityAddStoryBinding
import com.riezki.storyapp.databinding.PopupAddstoryBinding
import com.riezki.storyapp.ui.home.HomeActivity
import com.riezki.storyapp.utils.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private lateinit var token: String
    private var location: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        supportActionBar?.elevation = 0f

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.btnGetLoc.setOnClickListener {
            binding.btnGetLoc.isEnabled = false
            getMyLastLoc()
        }
        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage(location) }
        binding.fabBack.setOnClickListener {
            Intent(this, HomeActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) binding.progressBar.visibility = View.VISIBLE else View.GONE
    }

    private fun checkPermission(permission: String): Boolean {
        return this.let {
            ContextCompat.checkSelfPermission(
                it, permission
            )
        } == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLoc() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    location = loc
                    uploadImage(location)
                } else {
                    binding.btnGetLoc.isEnabled = true
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLoc()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLoc()
            }

            else -> {
                binding.btnGetLoc.isEnabled = true
            }
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.riezki.storyapp.ui.addstory",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage(location: Location?) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description = (binding.inputDesc.text ?: " ").toString().toRequestBody("text/plain".toMediaType())

            val requestImageFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            viewModel.userTokenFromDataStore.observe(this) { tokenUser ->
                token = tokenUser

                viewModel.setUploadImage(
                    this,
                    "Bearer $token",
                    imageMultipart,
                    description,
                    location
                ).observe(this) { addStory ->
                    when (addStory) {
                        is Resource.Loading -> {
                            showLoading(true)
                        }

                        is Resource.Success -> {
                            showLoading(false)
                            if (addStory.data?.error == true) {
                                Toast.makeText(
                                    this,
                                    "Gambar Gagal diupload, terjadi kesalahan!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val view = PopupAddstoryBinding.inflate(layoutInflater)
                                val builder = AlertDialog.Builder(this)
                                    .setView(view.root)

                                val dialog = builder.create()
                                dialog.show()
                                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                                dialog.setCancelable(false)

                                view.btnOk.setOnClickListener {
                                    Intent(this, HomeActivity::class.java).also {
                                        startActivity(it)
                                        finish()
                                    }
                                    dialog.dismiss()
                                }
                            }
                        }

                        is Resource.Error -> {
                            showLoading(false)
                            Toast.makeText(
                                this,
                                "Gambar Gagal diupload, terjadi kesalahan!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        } else {
            showLoading(false)
            Toast.makeText(
                this,
                "Silakan masukkan berkas gambar terlebih dahulu.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            val result = BitmapFactory.decodeFile(myFile.path)

            getFile = myFile

            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri

            val myFile = uriToFile(selectedImg, this)

            getFile = myFile

            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /*

    private fun getMyLocation(mMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

 */


    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}