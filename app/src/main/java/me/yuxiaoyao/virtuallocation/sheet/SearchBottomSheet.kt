package me.yuxiaoyao.virtuallocation.sheet

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import me.yuxiaoyao.virtuallocation.R
import me.yuxiaoyao.virtuallocation.model.SearchDataModel
import me.yuxiaoyao.virtuallocation.util.DimenUtil
import me.yuxiaoyao.virtuallocation.util.formatMapLocation


@SuppressLint("SetTextI18n")
class SearchBottomSheet(
    context: Context,
    title: String? = null,
    var searchTextContent: String?,
    var dataList: MutableList<SearchDataModel> = arrayListOf(),
    private val onItemClickListener: ItemClickListener? = null,
    var searchActionAsync: (text: String) -> Unit
) :
    BottomSheetDialog(context, R.style.BottomSheetEdit) {

    private val searchText: EditText
    private val noDateTv: TextView
    private val recyclerView: RecyclerView

    private val adapter: Adapter


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_search, null)

        // view config and listener

        val titleType: TextView = view.findViewById(R.id.titleType)
        searchText = view.findViewById(R.id.searchText)
        noDateTv = view.findViewById(R.id.tvNoData)
        recyclerView = view.findViewById(R.id.recyclerViewList)
        val doSearchAction: ImageView = view.findViewById(R.id.doSearchAction)

        if (title != null) {
            titleType.text = "在 $title 搜索"
        } else {
            titleType.text = "搜索"
        }
        searchTextContent?.apply {
            searchText.setText(searchTextContent!!)
        }

        doSearchAction.setOnClickListener {
            doSearch()
        }
        searchText.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch()
            }
            false
        }
        adapter = Adapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        val height = DimenUtil.displayHeightPixels(context)
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        val frameLayout = FrameLayout(context)
        frameLayout.layoutParams = layoutParams
        frameLayout.addView(view)

        setContentView(frameLayout)
    }

    /**
     * 替换 show()
     */
    fun search(longitude: Double, latitude: Double) {


    }

    private fun doSearch() {
        val text = searchText.text
        if (text.isNotEmpty()) {
            searchActionAsync(text.toString())
        }
    }

    fun setData(data: MutableList<SearchDataModel>?) {
        if (data == null) {
            dataList = arrayListOf()
            adapter.notifyDataSetChanged()
            noDateTv.visibility = View.VISIBLE
            return
        }
        noDateTv.visibility = View.GONE
        dataList = data
        adapter.notifyDataSetChanged()
    }


    private inner class Adapter : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_bottom_sheet_search, parent, false)
            return ViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = dataList[position]
            holder.title.text = "${position + 1}. ${item.name}"
            holder.address.text = item.address
            holder.locationInfo.text = formatMapLocation(item.longitude, item.latitude)

            onItemClickListener?.apply {
                holder.itemView.setOnClickListener {
                    onItemClickListener.onSearchItemClick(position)
                }
            }
        }

        override fun getItemCount() = dataList.size

    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val address: TextView = itemView.findViewById(R.id.tvAddress)
        val locationInfo: TextView = itemView.findViewById(R.id.tvLocationInfo)
    }


    interface ItemClickListener {
        fun onSearchItemClick(position: Int)
    }
}