package com.example.familysafety

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InviteAdapter(private val listContacts: List<ContactModel>) :
    RecyclerView.Adapter<InviteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = inflater.inflate(R.layout.item_invite, parent, false)
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listContacts[position]
        holder.name.text = item.name
    }

    override fun getItemCount(): Int {
        return listContacts.size
    }

    class ViewHolder(private val item: View) : RecyclerView.ViewHolder(item) {
        val name: TextView = item.findViewById<TextView>(R.id.image_name)
    }

}