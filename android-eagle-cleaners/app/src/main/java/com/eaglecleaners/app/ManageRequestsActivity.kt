package com.eaglecleaners.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


// TODO: Add notifications for new requests, make address text a link to open google maps
class ManageRequestsActivity : AppCompatActivity() {

    val viewModel: ManageRequestsViewModel by viewModels()

    private lateinit var adapter: DeliveryRequestAdapter

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_requests)
        recyclerView = findViewById(R.id.delivery_request_list)
        swipeContainer = findViewById(R.id.swipe_container)
        adapter = DeliveryRequestAdapter(viewModel.requestsData.value ?: listOf(), this)
        recyclerView.adapter = adapter
        swipeContainer.setOnRefreshListener { viewModel.getRequests(::displayErrorMessage) }
        viewModel.getRequests(::displayErrorMessage)
        viewModel.subscribeToMessaging({
            d(TAG, "Successfully subscribed to messaging service")
        }, { task ->
            val msg = "Failed to subscribe to messaging service"
            d(TAG, msg, task.exception)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })
        viewModel.requestsAreLoading.observe(this, { requestsAreLoading ->
            swipeContainer.isRefreshing = requestsAreLoading
        })
        viewModel.requestsData.observe(this, {
            adapter.notifyDataSetChanged()
        })
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                d(TAG, "Message received")
                viewModel.getRequests(::displayErrorMessage)
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
            IntentFilter(MyFirebaseMessagingService.NEW_DELIVERY_INTENT)
        )
    }

    fun displayErrorMessage(exception: Exception) {
        Log.w(TAG, getString(R.string.request_retrieval_failure), exception)
        Toast.makeText(this, getString(R.string.request_retrieval_failure), Toast.LENGTH_SHORT).show()
    }


    companion object {
        private const val TAG = "ManageRequestsActivity"
    }
}