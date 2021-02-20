package me.yuxiaoyao.virtuallocation.sheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import me.yuxiaoyao.virtuallocation.R
import me.yuxiaoyao.virtuallocation.model.AddressModel
import me.yuxiaoyao.virtuallocation.model.PoiSheetModel
import me.yuxiaoyao.virtuallocation.util.DimenUtil
import me.yuxiaoyao.virtuallocation.util.formatMapLocation


fun buildLocationSheetShowMessage(item: PoiSheetModel): String {
    return if (item.name != null) {
        "${item.cityName}${item.adName}${item.address}${item.name}"
    } else {
        item.address
    }
}

class LocationBottomSheet(
    context: Context,
    data: List<PoiSheetModel>? = null,
    onItemClickListener: OnItemClickListener? = null,
    onItemLongClickListener: OnItemLongClickListener? = null,
    childClientId: Int? = null,
    onChildClickListener: OnChildClickListener? = null
) :
    BottomSheetDialog(context) {

    init {
        val recyclerView = RecyclerView(context)

        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = Adapter(
            data,
            onItemClickListener,
            onItemLongClickListener,
            childClientId,
            onChildClickListener
        )
        recyclerView.adapter = adapter

        val height = if (data.isNullOrEmpty() || data.size <= 3) {
            DimenUtil.dp2px(context, 72.0f) * 3
        } else {
            val displayHeightPixels = DimenUtil.displayHeightPixels(context)
            val once = DimenUtil.dp2px(context, 72.0f)
            val h = ((displayHeightPixels / once) * 0.75).toInt()
            h * once
        }

        val layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, height)
        val frameLayout = FrameLayout(context)
        frameLayout.layoutParams = layoutParams
        frameLayout.addView(recyclerView)
        setContentView(frameLayout)
    }

    inner class Adapter(
        private val data: List<PoiSheetModel>?,
        private var onItemClickListener: OnItemClickListener? = null,
        private val onItemLongClickListener: OnItemLongClickListener? = null,
        private var clickViewId: Int? = null,
        private var onChildClickListener: OnChildClickListener? = null
    ) :
        RecyclerView.Adapter<ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_bottom_sheet, parent, false)
            // set default
            return ViewHolder(view, clickViewId ?: R.id.bottom_sheet_set_target)
//            return ViewHolder(view, clickViewId)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = data!![position]


            val address = buildLocationSheetShowMessage(item)

            holder.address.text = address
            holder.latLng.text = formatMapLocation(item.longitude, item.latitude)

            onItemClickListener?.apply {
                holder.itemView.setOnClickListener {
                    onItemClickListener!!.onItemClick(this@LocationBottomSheet, it, position)
                }
            }

            onItemLongClickListener?.apply {
                holder.itemView.setOnLongClickListener {
                    return@setOnLongClickListener onItemLongClickListener.onItemLongClick(
                        this@LocationBottomSheet,
                        it,
                        position
                    )
                }
            }

            onChildClickListener?.apply {
                holder.clickChildView?.apply {
                    this.setOnClickListener {
                        onChildClickListener!!.onChildClick(
                            this@LocationBottomSheet,
                            holder.itemView,
                            this,
                            position
                        )
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            if (data == null) return 0
            return data.size
        }

    }


    class ViewHolder(itemView: View, clickViewId: Int?) : RecyclerView.ViewHolder(itemView) {
        val address: TextView = itemView.findViewById(R.id.bottom_sheet_address)
        val latLng: TextView = itemView.findViewById(R.id.bottom_sheet_latLng)
        var clickChildView: View? = null

        init {
            if (clickViewId != null) {
                clickChildView = itemView.findViewById(clickViewId)
            }
        }
    }

    /**
     * callback interface
     */
    interface OnItemClickListener {
        fun onItemClick(dialog: BottomSheetDialog, view: View, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(dialog: BottomSheetDialog, view: View, position: Int): Boolean
    }

    interface OnChildClickListener {
        fun onChildClick(dialog: BottomSheetDialog, view: View, clientView: View, position: Int)
    }


}

