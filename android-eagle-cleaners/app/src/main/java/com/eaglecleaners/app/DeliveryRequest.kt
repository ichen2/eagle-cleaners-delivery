package com.eaglecleaners.app

// TODO: change address field from a String to an actual address object
data class DeliveryRequest(val name : String, val address : String, val time : Long)