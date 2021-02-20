package me.yuxiaoyao.virtuallocation.sheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import me.yuxiaoyao.virtuallocation.R

class MockingClickBottomSheet(
    context: Context,
    address: String,
    actions: MutableList<String>?,
    onItemClickListener: OnItemClickListener? = null
) :
    BottomSheetDialog(context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_mocking_click, null)

        val titleAddress: TextView = view.findViewById(R.id.tvMockingLocation)
        titleAddress.text = "正在模拟: $address"

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvMockingActions)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = RecyclerViewAdapter(actions, onItemClickListener)
        setContentView(view)
    }


    private inner class RecyclerViewAdapter(
        val actions: MutableList<String>?,
        val onItemClickListener: OnItemClickListener?
    ) : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bottom_sheet_mocking, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.title.text = actions!![position]
            onItemClickListener?.apply {
                holder.itemView.setOnClickListener {
                    onItemClickListener.onItemClickListener(
                        this@MockingClickBottomSheet,
                        it,
                        position
                    )
                }
            }
        }

        override fun getItemCount(): Int = actions?.size ?: 0
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_mocking_title)
    }


    interface OnItemClickListener {
        fun onItemClickListener(dialog: BottomSheetDialog, view: View, index: Int)
    }

}