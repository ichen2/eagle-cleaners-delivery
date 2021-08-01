package com.eaglecleaners.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.seatgeek.placesautocomplete.DetailsCallback
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView
import com.seatgeek.placesautocomplete.model.PlaceDetails

// TODO: Add themeing, define string resources
class RequestDeliveryActivity : AppCompatActivity() {

    private val viewModel: RequestDeliveryViewModel by viewModels()
    private lateinit var openFormButton: Button
    private lateinit var openLoginButton: Button
    private lateinit var infoForm: LinearLayout
    private lateinit var addressField: PlacesAutocompleteTextView
    private lateinit var nameField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO: Organize this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_delivery)
        openLoginButton = findViewById(R.id.btn_admin)
        openLoginButton.setOnClickListener {
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
        infoForm = findViewById(R.id.info_form)
        openFormButton = findViewById(R.id.btn_open_form)
        openFormButton.setOnClickListener {
            setFormVisibility(true)
        }
        val closeFormButton: Button = findViewById(R.id.btn_close_form)
        closeFormButton.setOnClickListener {
            setFormVisibility(false)
        }
        val requestButton: Button = findViewById(R.id.btn_request)
        requestButton.setOnClickListener {
            addressField.getDetailsFor(viewModel.selectedPlace, object : DetailsCallback {
                override fun onSuccess(placeDetails: PlaceDetails?) {
                    if(placeDetails != null) {
                        viewModel.requestDelivery(
                            DeliveryRequest(
                                nameField.text.toString(),
                                DeliveryLocation(
                                    placeDetails.name,
                                    GeoPoint(
                                        placeDetails.geometry.location.lat,
                                        placeDetails.geometry.location.lng
                                    )
                                ),
                                System.currentTimeMillis()
                            )
                        )
                    }
                }

                override fun onFailure(p0: Throwable?) {
                    if(p0 != null) throw p0
                }
            })
        }
        addressField = findViewById(R.id.address_field)
        addressField.setOnPlaceSelectedListener { place ->
            viewModel.selectedPlace = place
            // TODO: Have map focus on selected place
        }
        addressField.setOnClearListener {
            viewModel.selectedPlace = null
        }
        nameField = findViewById(R.id.name_field)
        checkLocationPermission()
        // TODO: This logic could probably be simplified
        LocationServices.getFusedLocationProviderClient(this).lastLocation
            .addOnSuccessListener { location: Location? ->
                viewModel.initializeMap(
                    (fragmentManager.findFragmentById(R.id.map) as MapFragment),
                    LatLng(location?.latitude ?: 0.0, location?.longitude ?: 0.0)
                )
                if (location != null) {
                    addressField.currentLocation = location
                }
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

    private fun setFormVisibility(showForm: Boolean) {
        if (showForm) {
            openFormButton.visibility = View.GONE
            openLoginButton.visibility = View.GONE
            infoForm.visibility = View.VISIBLE
        } else {
            openFormButton.visibility = View.VISIBLE
            openLoginButton.visibility = View.VISIBLE
            infoForm.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "RequestDelivery"
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}