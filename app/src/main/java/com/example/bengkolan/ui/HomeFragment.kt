package com.example.bengkolan.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.findFragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.bengkolan.MainActivity
import com.example.bengkolan.R
import com.example.bengkolan.data.DataObject
import com.example.bengkolan.databinding.FragmentHomeBinding
import com.example.bengkolan.databinding.FragmentLoginBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding?= null
    private val binding get() = _binding!!
    private lateinit var currentLoc: LatLng
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    val location = DataObject.location
    val title = DataObject.title

    private val callback = OnMapReadyCallback { googleMap ->
        for (i in location.indices) {
            googleMap.addMarker(MarkerOptions().position(location[i]).title(title[i]))
        }
        googleMap.addMarker(MarkerOptions().position(currentLoc).title("Current Location")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc,14f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return inflater.inflate(R.layout.fragment_home, container, false)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        BottomSheetBehavior.from(binding.bottomSheet).apply {
            peekHeight=450
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        firebaseAuth = FirebaseAuth.getInstance()
        getCurrentLoc()

        binding.cardDarurat.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToPerbaikanFragment()
            view.findNavController().navigate(action)
        }

        getUser()

        binding.cardView.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToDaruratFragment()
            findNavController().navigate(action)
        }

        binding.cardSukucadang.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToSukuCadangFragment()
            findNavController().navigate(action)
        }
    }

    private fun getUser() {
        val currentUser = firebaseAuth.currentUser
        db = Firebase.firestore

        if (currentUser != null) {
            db.collection("users").document(currentUser.uid)
                .get().addOnSuccessListener {
                    Log.d("get firestore","${it.data}")
                    binding.tvUsername.text = "${it.getString("username")}"
                }
                .addOnFailureListener {
                    binding.tvUsername.visibility = View.GONE
                }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLoc() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 99)

            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // getting the last known or current location
                if (location != null) {
                    currentLoc = LatLng(location.latitude,location.longitude)

                    val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                    mapFragment?.getMapAsync(callback)

                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed on getting current location",
                    Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            99 -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted

                    getCurrentLoc()
                } else {
                    // permission denied
                    Toast.makeText(requireContext(), "You need to grant permission to access location",
                        Toast.LENGTH_SHORT).show()

                    showPermissionDeniedDialog()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("permission denied")
            .setMessage("please allow the permission")
            .setPositiveButton("App Setting") { _,_ ->
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package","com.example.bengkolan",null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("cancle") { dialog,_ -> dialog.cancel()}
            .show()
    }
}