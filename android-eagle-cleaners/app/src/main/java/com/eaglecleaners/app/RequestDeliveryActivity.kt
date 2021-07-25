package com.eaglecleaners.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// TODO: Add google map and a form for the delivery request
class RequestDeliveryActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var service: RequestDeliveryService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_delivery)
        val adminButton: Button = findViewById(R.id.btn_admin)
        adminButton.setOnClickListener {
            if(getSharedPreferences("loginStatus", Context.MODE_PRIVATE).getBoolean("isLoggedIn", false)) {
                startActivity(Intent(this, ManageRequestsActivity::class.java))
            } else {
                startActivity(Intent(this, AdminLoginActivity::class.java))
            }
        }
        val requestButton: Button = findViewById(R.id.btn_request)
        requestButton.setOnClickListener {
            requestDelivery(DeliveryRequest("John Cena", "Ur moms house", 100))
        }
        db = Firebase.firestore
        // TODO: Create string resources for api endpoints
        service = Retrofit.Builder()
            .baseUrl("https://us-central1-eagle-cleaners-b141c.cloudfunctions.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RequestDeliveryService::class.java)
        getToken()
    }

    // TODO: Remove this whenever done debugging
    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val msg = task.result ?: "Error"
            Log.d(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
    }

    fun requestDelivery(deliveryRequest: DeliveryRequest) {
        service.requestDelivery(deliveryRequest.name, deliveryRequest.time).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                println("Delivery requested")
            }
            // TODO: Improve error handling, maybe make response more robust
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                throw t
            }

        })
    }

    companion object {
        val TAG = "RequestDelivery"
    }

    // TODO: Add remaining fields
    interface RequestDeliveryService {
        @GET("requestDelivery/")
        fun requestDelivery(@Query("name") name : String, @Query("time") time : Long): Call<Boolean>
    }
}