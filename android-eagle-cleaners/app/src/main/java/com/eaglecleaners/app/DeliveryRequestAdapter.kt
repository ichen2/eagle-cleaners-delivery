package com.eaglecleaners.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeliveryRequestAdapter(private val requests: List<DeliveryRequest>) :
    RecyclerView.Adapter<DeliveryRequestAdapter.DeliveryRequestViewHolder>() {

    class DeliveryRequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val textView: TextView = view.findViewById(R.id.delivery_requester)

        init {
            // TODO: Add swipe listener to let user dismiss requests
        }

        fun setText(text: String) {
            textView.text = text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.delivery_request_item, parent, false)

        return DeliveryRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeliveryRequestViewHolder, position: Int) {
        // TODO: Add binding for other fields
        holder.setText(requests[position].name)
    }

    override fun getItemCount(): Int {
        return requests.size
    }
}