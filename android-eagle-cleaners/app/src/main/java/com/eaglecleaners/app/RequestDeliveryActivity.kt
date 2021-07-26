package com.eaglecleaners.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng

// TODO: Add a form for the delivery request
class RequestDeliveryActivity : AppCompatActivity() {

    private val viewModel: RequestDeliveryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_delivery)
        val adminButton: Button = findViewById(R.id.btn_admin)
        adminButton.setOnClickListener {
            if (getSharedPreferences("loginStatus", Context.MODE_PRIVATE).getBoolean(
                    "isLoggedIn",
                    false
                )
            ) {
                startActivity(Intent(this, ManageRequestsActivity::class.java))
            } else {
                startActivity(Intent(this, AdminLoginActivity::class.java))
            }
        }
        val requestButton: Button = findViewById(R.id.btn_request)
        requestButton.setOnClickListener {
            viewModel.requestDelivery(DeliveryRequest("John Cena", "Ur moms house", 100))
        }
        checkLocationPermission()
        // TODO: This logic could probably be simplified
        LocationServices.getFusedLocationProviderClient(this).lastLocation
            .addOnSuccessListener { location: Location? ->
                viewModel.initializeMap(
                    (fragmentManager.findFragmentById(R.id.map) as MapFragment),
                    LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                )
            }.addOnFailureListener {
                viewModel.initializeMap((fragmentManager.findFragmentById(R.id.map) as MapFragment))
            }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        }
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    companion object {
        private const val TAG = "RequestDelivery"
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}