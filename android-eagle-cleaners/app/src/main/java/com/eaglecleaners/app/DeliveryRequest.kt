package com.eaglecleaners.app

import com.google.firebase.firestore.GeoPoint

// TODO: change address field from a String to an actual address object
data class DeliveryRequest(val name : String, val address : DeliveryLocation, val time : Long, val id : String = "no_id")

data class DeliveryLocation(val name : String, val coordinates : GeoPoint)