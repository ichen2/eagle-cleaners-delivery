package com.eaglecleaners.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.sql.Time
import java.util.*
import kotlin.math.round

open class DeliveryRequestAdapter(private val requests: List<DeliveryRequest>, private val context: Context) :
    RecyclerView.Adapter<DeliveryRequestAdapter.DeliveryRequestViewHolder>() {

    class DeliveryRequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val name: TextView = view.findViewById(R.id.delivery_requester_name)
        private val address: TextView = view.findViewById(R.id.delivery_requester_address)
        private val time: TextView = view.findViewById(R.id.delivery_requester_time)
        val closeButton: FrameLayout = view.findViewById(R.id.close_button)

        init {
            // TODO: Add swipe listener to let user dismiss requests
        }

        fun setName(name: String) {
            this.name.text = name
        }

        fun setAddress(address: String) {
            this.address.text = address
        }

        fun setTime(time: String) {
            this.time.text = time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.delivery_request_item, parent, false)
        return DeliveryRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeliveryRequestViewHolder, position: Int) {
        // TODO: Add binding for other fields
        val request = requests[position]
        holder.setName(request.name)
        holder.setAddress(request.address.name)
        holder.setTime("Request made ${getTimePassedMessage(Date().time, request.time)} ago")
        holder.closeButton.setOnClickListener{
            (context as ManageRequestsActivity).removeRequest(position)
        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    companion object {

        private const val secondInMillis = 1000
        private const val minuteInMillis = secondInMillis * 60
        private const val hourInMillis = minuteInMillis * 60
        private const val dayInMillis = hourInMillis * 24

        fun getTimePassedMessage(currentTime: Long, previousTime: Long): String {

            val difference: Int = (currentTime - previousTime).toInt()
            return when {
                difference < hourInMillis -> {
                    val minutes = difference / minuteInMillis
                    "$minutes ${if (minutes == 1) "minute" else "minutes"}"
                }
                difference < dayInMillis -> {
                    val hours = difference / hourInMillis
                    "$hours ${if (hours == 1) "hour" else "hours"}"
                }
                else -> {
                    val days = difference / dayInMillis
                    val hours = (difference % dayInMillis) / hourInMillis
                    "$days ${if (days == 1) "day" else "days"}${if (hours > 0) " and $hours ${if (hours == 1) "hour" else "hours"}" else ""}"
                }
            }
        }
    }
}