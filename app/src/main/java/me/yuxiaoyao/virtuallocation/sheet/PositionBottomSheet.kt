package me.yuxiaoyao.virtuallocation.sheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import me.yuxiaoyao.virtuallocation.R
import me.yuxiaoyao.virtuallocation.model.AddressPair
import me.yuxiaoyao.virtuallocation.util.formatMapLocation

class PositionBottomSheet(
    context: Context,
    val currentLocation: AddressPair,
    clickListener: ClickListener? = null
) :
    BottomSheetDialog(context) {

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_position, null)

        val addressTv: TextView = view.findViewById(R.id.bottom_sheet_address)
        val latLngTv: TextView = view.findViewById(R.id.bottom_sheet_latLng)

        addressTv.text = currentLocation.address
        latLngTv.text =
            "经纬度:" + formatMapLocation(currentLocation.longitude, currentLocation.latitude)

        addressTv.setOnClickListener {
            clickListener?.apply {
                clickListener.onAddressClickListener(this@PositionBottomSheet, it)
            }
        }

        latLngTv.setOnLongClickListener {
            if (clickListener != null) {
                return@setOnLongClickListener clickListener.onLatLngLongClickListener(
                    this@PositionBottomSheet,
                    it
                )
            }
            return@setOnLongClickListener false
        }
        view.findViewById<TextView>(R.id.mockLocation).setOnClickListener {
            clickListener?.apply {
                clickListener.onMockLocationClickListener(this@PositionBottomSheet, it)
            }
        }

        view.findViewById<TextView>(R.id.myLocationSearch).setOnClickListener {
            clickListener?.apply {
                clickListener.onMyLocationSearchListener(this@PositionBottomSheet, it)
            }
        }
        setContentView(view)
    }


    interface ClickListener {
        fun onAddressClickListener(dialog: PositionBottomSheet, view: View) {
            dialog.dismiss()
        }

        fun onLatLngLongClickListener(dialog: PositionBottomSheet, view: View): Boolean {
            dialog.dismiss()
            return false
        }

        fun onMockLocationClickListener(dialog: PositionBottomSheet, view: View) {
            dialog.dismiss()
        }

        fun onMyLocationSearchListener(dialog: PositionBottomSheet, view: View) {
            dialog.dismiss()
        }
    }
}