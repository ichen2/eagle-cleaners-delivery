package com.eaglecleaners.app

import com.google.firebase.firestore.GeoPoint

data class DeliveryRequest(val name : String, val address : DeliveryLocation, val time : Long, val id : String = "no_id")

data class DeliveryLocation(val name : String, val coordinates : GeoPoint)