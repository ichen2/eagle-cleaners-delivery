package com.eaglecleaners.app

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class ManageRequestsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: DeliveryRequestAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeContainer: SwipeRefreshLayout

    private var requests: MutableList<DeliveryRequest> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_requests)
        recyclerView = findViewById(R.id.delivery_request_list)
        swipeContainer = findViewById(R.id.swipe_container)
        adapter = DeliveryRequestAdapter(requests, this)
        recyclerView.adapter = adapter
        db = Firebase.firestore
        swipeContainer.setOnRefreshListener { getRequests() }
        getRequests()
        subscribeToMessaging()
    }

    private fun subscribeToMessaging() {
        // TODO: Move strings to string resources
        Firebase.messaging.subscribeToTopic("admin")
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val msg = "Failed to subscribe to messaging service"
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
                else {
                    Log.d(TAG, "Successfully subscribed to messaging service")
                }
            }
    }

    private fun getRequests() {
        swipeContainer.isRefreshing = true
        db.collection("delivery-requests")
            .orderBy("time")
            .get()
            .addOnSuccessListener { documents ->
                // TODO: Instead of completely recreating list, this should just update it, adding the new requests
                requests.clear()
                requests.addAll(documents.map { document ->
                    DeliveryRequest(
                        document.getString("name") ?: "Error",
                        DeliveryLocation(document.getString("addressName") ?: "", document.getGeoPoint("addressCoordinates") ?: GeoPoint(0.0, 0.0)),
                        document.getLong("time") ?: Long.MAX_VALUE,
                        document.id
                    )
                })
                adapter.notifyDataSetChanged()
                swipeContainer.isRefreshing = false
            }
            .addOnFailureListener { exception ->
                // TODO: Add a toast for this error
                Log.w(TAG, "Error getting documents: ", exception)
                swipeContainer.isRefreshing = false // TODO: Check if there's a 'finally' equivalent for this to prevent repeat of this line
            }
    }

    fun removeRequest(position: Int) {
        db.collection("delivery-requests").document(requests[position].id)
            .delete()
            .addOnSuccessListener {
                requests.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
            .addOnFailureListener {
                    e -> Log.w(TAG, "Error deleting document", e)
            }
    }

    companion object {
        private const val TAG = "ManageRequests"
    }
}