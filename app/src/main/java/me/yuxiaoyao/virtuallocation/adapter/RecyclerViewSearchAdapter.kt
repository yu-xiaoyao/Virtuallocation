package me.yuxiaoyao.virtuallocation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.yuxiaoyao.virtuallocation.R

class RecyclerViewSearchAdapter(private val data: MutableList<String>) :
    RecyclerView.Adapter<RecyclerViewSearchAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rv_search, null)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.address.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val address: TextView = itemView.findViewById(R.id.itemAddress)
    }
}