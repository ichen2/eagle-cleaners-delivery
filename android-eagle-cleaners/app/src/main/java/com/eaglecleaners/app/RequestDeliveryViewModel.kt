package com.eaglecleaners.app

import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.seatgeek.placesautocomplete.DetailsCallback
import com.seatgeek.placesautocomplete.model.Place
import com.seatgeek.placesautocomplete.model.PlaceDetails
import kotlinx.coroutines.selects.select
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


private const val BASE_URL = "https://us-central1-eagle-cleaners-b141c.cloudfunctions.net/"
private const val REQUEST_DELIVERY_ENDPOINT = "requestDelivery/"

class RequestDeliveryViewModel : ViewModel() {

    private lateinit var map: GoogleMap
    var selectedPlace: Place? = null
    var selectedPlaceDetails: PlaceDetails? = null
    var requestIsLoading = MutableLiveData<Boolean>()
    private val service: RequestDeliveryService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RequestDeliveryService::class.java)

    fun initializeMap(mapFragment: MapFragment, latLng: LatLng = LatLng(0.0, 0.0)) {
        if (!this::map.isInitialized) {
            mapFragment.getMapAsync {
                map = it
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    val detailsCallback = object : DetailsCallback {
        override fun onSuccess(p0: PlaceDetails?) {
            selectedPlaceDetails = p0
            if (p0 != null) {
                val coords = LatLng(p0.geometry.location.lat, p0.geometry.location.lng)
                map.moveCamera(CameraUpdateFactory.newLatLng(coords)
                )
                map.addMarker(MarkerOptions().position(coords))
            }
        }

        override fun onFailure(p0: Throwable?) {
            if (p0 != null) throw p0
        }

    }

    // TODO: Add remaining fields
    interface RequestDeliveryService {
        @GET(REQUEST_DELIVERY_ENDPOINT)
        fun requestDelivery(
            @Query("name") name: String,
            @Query("addressName") addressName: String,
            @Query("addressLat") addressLat: Double,
            @Query("addressLng") addressLng: Double,
            @Query("time") time: Long
        ): Call<Boolean>
    }

    fun requestDelivery(name: String) {
        // TODO: Maybe improve input validation here
        if (selectedPlaceDetails != null) {
            requestIsLoading.value = true
            val deliveryRequest = DeliveryRequest(
                name, DeliveryLocation(
                    selectedPlaceDetails!!.name,
                    GeoPoint(
                        selectedPlaceDetails!!.geometry.location.lat,
                        selectedPlaceDetails!!.geometry.location.lng
                    )
                ), System.currentTimeMillis()
            )
            service.requestDelivery(
                deliveryRequest.name,
                deliveryRequest.address.name,
                deliveryRequest.address.coordinates.latitude,
                deliveryRequest.address.coordinates.longitude,
                deliveryRequest.time
            )
                .enqueue(object : Callback<Boolean> {
                    override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                        println("Delivery requested")
                        println(response.raw().toString())
                        requestIsLoading.value = false
                    }

                    // TODO: Improve error handling, maybe make response more robust
                    override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        throw t
                        requestIsLoading.value = false
                    }
                })
        }
    }
}