package com.eaglecleaners.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class ManageRequestsViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val requests: MutableList<DeliveryRequest> = mutableListOf()

    val requestsAreLoading = MutableLiveData<Boolean>()
    var requestsData: MutableLiveData<List<DeliveryRequest>> = MutableLiveData(requests)

    fun subscribeToMessaging(onSuccess: () -> Unit, onFailure: (task: Task<Void>) -> Unit) {
        Firebase.messaging.subscribeToTopic("admin")
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onFailure(task)
                }
                else {
                    onSuccess()
                }
            }
    }

    fun getRequests(onFailure: (Exception) -> Unit) {
        requestsAreLoading.value = true
        db.collection("delivery-requests")
            .orderBy("time")
            .get()
            .addOnSuccessListener { documents ->
                requests.clear()
                requests.addAll(documents.map { document ->
                    DeliveryRequest(
                        document.getString("name") ?: throw Exception("No name specified"),
                        DeliveryLocation(
                            document.getString("addressName")
                                ?: throw Exception("No address specified"),
                            document.getGeoPoint("addressCoordinates")
                                ?: throw Exception("No coordinates specified")
                        ),
                        document.getLong("time") ?: throw Exception("No time specified"),
                        document.id
                    )
                })
                requestsData.value = requests
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
            .addOnCompleteListener {
                requestsAreLoading.value = false
            }
    }

    fun removeRequest(position: Int, onFailure: (Exception) -> Unit) {
        db.collection("delivery-requests").document(requests[position].id)
            .delete()
            .addOnSuccessListener {
                requests.removeAt(position)
                requestsData.value = requests
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    companion object {
        private const val TAG = "ManageRequests"
    }
}