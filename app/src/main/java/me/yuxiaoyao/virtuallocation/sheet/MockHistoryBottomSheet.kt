package me.yuxiaoyao.virtuallocation.sheet

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import me.yuxiaoyao.virtuallocation.R
import me.yuxiaoyao.virtuallocation.cache.ListHistoryCache
import me.yuxiaoyao.virtuallocation.constants.CACHE_NAME
import me.yuxiaoyao.virtuallocation.constants.MOCK_HISTORY_LIST_KEY
import me.yuxiaoyao.virtuallocation.model.AddressPair
import me.yuxiaoyao.virtuallocation.util.DimenUtil
import me.yuxiaoyao.virtuallocation.util.formatMapLocation

class MockHistoryBottomSheet(
    context: Context,
    private val onItemClickListener: OnItemClickListener? = null
) : BottomSheetDialog(context) {

    private val dataList: MutableList<AddressPair> =
        ListHistoryCache.with<AddressPair>(context, CACHE_NAME)
            .getAll(MOCK_HISTORY_LIST_KEY, AddressPair::class.java)

    private val adapter: RvAdapter

    init {

        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_mock_history, null)

        val tvNoHistory: TextView = view.findViewById(R.id.tvNoHistory)
        val recyclerViewHistoryList: RecyclerView = view.findViewById(R.id.recyclerViewHistoryList)

        recyclerViewHistoryList.layoutManager = LinearLayoutManager(context)
        adapter = RvAdapter(onItemClickListener, object : OnItemLongClickListener {
            override fun onItemLongClickListener(position: Int, ap: AddressPair): Boolean {
                showDialog(position, ap)
                return true
            }
        })
        recyclerViewHistoryList.adapter = adapter
        if (dataList.isNullOrEmpty()) {
            tvNoHistory.visibility = View.VISIBLE
        }

        val height = DimenUtil.displayHeightPixels(context)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        val frameLayout = FrameLayout(context)
        frameLayout.layoutParams = layoutParams
        frameLayout.addView(view)

        setContentView(frameLayout)
    }


    private inner class RvAdapter(
        private val onItemClickListener: OnItemClickListener?,
        private val longClickListener: OnItemLongClickListener?,

        ) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bottom_sheet_mock_history, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = dataList[position]
            holder.title.text = item.cityName
            holder.address.text = item.address
            holder.locationInfo.text = formatMapLocation(item.longitude, item.latitude)

            onItemClickListener?.apply {
                holder.itemView.setOnClickListener {
                    onItemClickListener.onItemClick(this@MockHistoryBottomSheet, position, item)
                }
            }
            longClickListener?.apply {
                holder.itemView.setOnLongClickListener {
                    return@setOnLongClickListener longClickListener.onItemLongClickListener(
                        position,
                        item
                    )
                }
            }
        }

        override fun getItemCount() = dataList.size

    }

    private fun showDialog(position: Int, item: AddressPair) {
        val action = arrayOf("模拟定位", "删除记录")
        val builder = AlertDialog.Builder(context)
        builder.setItems(
            action
        ) { dialog, which ->
            when (which) {
                0 -> {
                    dialog.dismiss()
                    onItemClickListener?.apply {
                        onItemClickListener.onMockClick(this@MockHistoryBottomSheet, item)
                    }
                }
                1 -> {
                    dataList.removeAt(position)
                    adapter.notifyDataSetChanged()

                    dialog.dismiss()
                    ListHistoryCache.with<AddressPair>(context, CACHE_NAME)
                        .remove(MOCK_HISTORY_LIST_KEY, position)
                }
            }

        }
        builder.show()
    }

    private class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val address: TextView = itemView.findViewById(R.id.tvAddress)
        val locationInfo: TextView = itemView.findViewById(R.id.tvLocationInfo)
    }

    private interface OnItemLongClickListener {
        fun onItemLongClickListener(position: Int, ap: AddressPair): Boolean
    }

    interface OnItemClickListener {
        fun onItemClick(dialog: MockHistoryBottomSheet, position: Int, ap: AddressPair)
        fun onMockClick(dialog: MockHistoryBottomSheet, ap: AddressPair) {}
    }

}