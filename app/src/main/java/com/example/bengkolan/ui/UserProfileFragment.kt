package com.example.bengkolan.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.bengkolan.AuthActivity
import com.example.bengkolan.MainActivity
import com.example.bengkolan.R
import com.example.bengkolan.databinding.FragmentRegisterBinding
import com.example.bengkolan.databinding.FragmentUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream

class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding?= null
    private val binding get() = _binding!!
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var imageProfile: String = "null"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_user_profile, container, false)
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        getUser()

        binding.btnSave.setOnClickListener {
            updateData()
        }

        binding.logout.setOnClickListener {
            logout()
        }

//        binding.btnEdit.setOnClickListener {
//            val dialogFragment = EditProfileDialog()
//            dialogFragment.show(childFragmentManager,null)
//        }

        binding.selectImage.setOnClickListener {
            checkingPermission()
        }

    }

    private fun getUser() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            db.collection("users").document(currentUser.uid)
                .get().addOnSuccessListener {
                    Log.d("get firestore","${it.data}")
                    binding.tvProfileUsername.text = "${it.getString("username")}"
                    binding.tvProfileEmail.text = "${it.getString("email")}"

                    if (it.getString("fullname") != null) {
                        binding.wrapFullname.editText?.setText("${it.getString("fullname")}")
                    } else {
                        binding.wrapFullname.editText?.setText("fullname")
                    }

                    if (it.getString("address") != null) {
                        binding.wrapAddress.editText?.setText("${it.getString("address")}")
                    } else {
                        binding.wrapAddress.editText?.setText("address")
                    }

                    if (it.getString("phone") != null) {
                        binding.wrapPhone.editText?.setText("${it.getString("phone")}")
                    } else {
                        binding.wrapPhone.editText?.setText("phone")
                    }

                    if (it.getString("instagram") != null) {
                        binding.wrapInstagram.editText?.setText("${it.getString("instagram")}")
                    } else {
                        binding.wrapInstagram.editText?.setText("instagram")
                    }

                    if (it.getString("image") != null) {
                        val imageBytes = Base64.decode(it.getString("image"),0)
                        val image = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)

                        Glide.with(requireContext())
                            .load(image)
                            .centerCrop()
                            .into(binding.ivProfilePicture)
                    }

            }
            .addOnFailureListener {
//                    binding.tvUsername.visibility = View.GONE
            }
        }

    }

    private fun updateData() {
        val fullname = binding.wrapFullname.editText?.text.toString()
        val address = binding.wrapAddress.editText?.text.toString()
        val phone = binding.wrapPhone.editText?.text.toString()
        val instagram = binding.wrapInstagram.editText?.text.toString()
        val currentUser = firebaseAuth.currentUser

        if (inputCheck(fullname, address, phone, instagram,imageProfile)) {
            val data = mapOf(
                "fullname" to fullname,
                "address" to address,
                "phone" to phone,
                "instagram" to instagram,
                "image" to imageProfile
            )

            if (currentUser!=null) {
                db.collection("users").document(currentUser.uid)
                    .update(data)
            }
            Toast.makeText(requireContext(), "Update data berhasil...", Toast.LENGTH_SHORT).show()

            getUser()
        } else {
            Toast.makeText(context, "Fields kosong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Logout")
        dialog.setMessage("Apakah anda yakin ingin logout dari aplikasi?")

        dialog.setCancelable(true)
        dialog.setPositiveButton("YES"){dialogInterface, p1 ->
            firebaseAuth.signOut()

            startActivity(Intent(activity, AuthActivity::class.java))
            activity?.finish()

            Toast.makeText(requireContext(),"Berhasil logout...", Toast.LENGTH_LONG).show()
        }

        dialog.setNegativeButton("NO"){dialogInterface, p1->
            dialogInterface.dismiss()
        }

        dialog.show()
    }

    private fun checkingPermission() {
        if (isGranted(
                requireActivity(),
                android.Manifest.permission.CAMERA,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_PERMISSION )
        ) {
            chooseImageDialog()
        }
    }

    private fun isGranted(
        activity: Activity,
        permission: String,
        permissions: Array<String>,
        request: Int
    ): Boolean {
        val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(activity, permissions, request)
            }
            false
        } else {
            true
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("permission denied")
            .setMessage("please allow the permission")
            .setPositiveButton("App Setting") { _,_ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", "com.example.bengkolan", null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("cancle") { dialog,_ -> dialog.cancel()}
            .show()
    }

    private fun chooseImageDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Pilih Gambar")
//            .setPositiveButton("Gallery") {_,_ -> openGallery() }
            .setNegativeButton("Camera") {_,_ -> openCamera() }
            .show()
    }

//    private val galleryResult =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
//            val toBitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, Uri.parse(result.toString()))
//            val bitmapToString = bitmapToString(toBitmap)
//            imageProfile = bitmapToString
//
//            binding.tvProfileUsername.text = imageProfile
//            Glide.with(this)
//                .load(result)
//                .centerCrop()
//                .into(binding.ivProfilePicture)
//        }
//
//    private fun openGallery() {
//        Intent().type = "image/*"
//        galleryResult.launch("image/*")
//    }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleCameraImage(result.data)
            }
        }

    private fun handleCameraImage(intent: Intent?) {
        val bitmap = intent?.extras?.get("data") as Bitmap
        imageProfile = bitmapToString(bitmap)
        Glide.with(this)
            .load(bitmap)
            .centerCrop()
            .into(binding.ivProfilePicture)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResult.launch(cameraIntent)
    }

    private fun bitmapToString(bitmap: Bitmap): String {
        val byteArrayStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayStream)
        Bitmap.createScaledBitmap(bitmap, 150,150,false)
        val toByteArray = byteArrayStream.toByteArray()

        return Base64.encodeToString(toByteArray, Base64.DEFAULT)
    }

    private fun inputCheck(fullname: String, address:String,phone:String,instagram:String,image:String): Boolean {
        return !(TextUtils.isEmpty(fullname) || TextUtils.isEmpty(address) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(instagram) || image == "null")
    }

    companion object {
        val REQUEST_CODE_PERMISSION = 99
    }
}