package com.example.familysafety

import android.content.Context.BATTERY_SERVICE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MemberAdapter(private val listMembers: List<MemberModel>) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = inflater.inflate(R.layout.item_member, parent, false)
        return ViewHolder(item)

        // Get a reference to the BatteryManager instance

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val item = listMembers[position]
//        holder.imageUser.view
        holder.userName.text = item.name
        holder.userAddress.text = item.address

    }

    override fun getItemCount(): Int {
        return listMembers.size
    }

    class ViewHolder(private val item:View): RecyclerView.ViewHolder(item) {
//        var imageUser = item.findViewById<ImageView>(R.id.user_image)
        val userName: TextView = item.findViewById<TextView>(R.id.user_name)
        val userAddress: TextView = item.findViewById<TextView>(R.id.user_address)
    }
}
