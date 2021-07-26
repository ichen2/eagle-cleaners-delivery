package com.eaglecleaners.app

import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.model.LatLng
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

    private lateinit var map : GoogleMap
    private val service : RequestDeliveryService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RequestDeliveryService::class.java)

    fun initializeMap(mapFragment: MapFragment, latLng: LatLng = LatLng(0.0, 0.0)) {
        if(!this::map.isInitialized) {
            mapFragment.getMapAsync {
                map = it
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    // TODO: Add remaining fields
    interface RequestDeliveryService {
        @GET(REQUEST_DELIVERY_ENDPOINT)
        fun requestDelivery(@Query("name") name : String, @Query("time") time : Long): Call<Boolean>
    }

    fun requestDelivery(deliveryRequest: DeliveryRequest) {
        service.requestDelivery(deliveryRequest.name, deliveryRequest.time).enqueue(object :
            Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                println("Delivery requested")
            }
            // TODO: Improve error handling, maybe make response more robust
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                throw t
            }

        })
    }
}