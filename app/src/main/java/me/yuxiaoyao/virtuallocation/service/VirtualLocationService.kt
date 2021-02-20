package me.yuxiaoyao.virtuallocation.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import me.yuxiaoyao.virtuallocation.manager.VirtualLocationManager
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

private const val CHECK_LOCATION_INTERVAL = 1000L
private const val TAG = "VirtualLocationService"

class VirtualLocationService : Service() {


    /**
     * 经度
     */
    private var longitude = 0.0

    /**
     * 纬度
     */
    private var latitude = 0.0
    private var address: String? = null

    private val lock = Object()

    private var taskStatus = AtomicBoolean(false)

    //    private var timerTask: TimerTask? = null
    private var timer: Timer? = null
    private lateinit var virtualLocationManager: VirtualLocationManager
    var callback: Callback? = null


    override fun onBind(intent: Intent): IBinder {
        return VirtualLocationBinder()
    }

    private fun createTask(): TimerTask {
        return object : TimerTask() {
            override fun run() {
                Log.i(TAG, "模拟位置. $longitude,$latitude .地址: $address")
                try {
                    virtualLocationManager.startVirtualLocation(longitude, latitude)
                } catch (e: Exception) {
                    e.printStackTrace()
                    stopVirtualLocationService(true)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVirtualLocationService()
    }

    private fun startVirtualLocationService(lng: Double, lat: Double): Boolean {
        synchronized(lock) {
            this.longitude = lng
            this.latitude = lat
        }
        if (!taskStatus.get()) {
            virtualLocationManager = VirtualLocationManager.get(this)
            val checkEnableVirtualLocation = virtualLocationManager.checkEnableVirtualLocation()
            return if (checkEnableVirtualLocation) {
                taskStatus.set(true)
                timer = null
                timer = Timer()
                timer!!.schedule(createTask(), 0, CHECK_LOCATION_INTERVAL)
                true
            } else {
                false
            }
        }
        return true
    }

    private fun stopVirtualLocationService(execCallback: Boolean = false) {
        timer?.cancel()
        timer = null
        taskStatus.set(false)
        virtualLocationManager.stopVirtualLocation()
        if (execCallback) {
            callback?.apply {
                callback!!.virtualLocation(false)
            }
        }
    }


    inner class VirtualLocationBinder : Binder("Virtual location service binder") {

        fun getService(): VirtualLocationService {
            return this@VirtualLocationService
        }

        /**
         * GPS 坐标(非高德坐标)
         */
        fun startVirtualLocation(lng: Double, lat: Double, showAddress: String? = null): Boolean {
            address = showAddress
            return startVirtualLocationService(lng, lat)
        }

        fun stopVirtualLocation() {
            stopVirtualLocationService()
        }

        fun isStartVirtualLocation(): Boolean {
            return taskStatus.get()
        }

        fun getCurrentLocation(): Boolean {
            return taskStatus.get()
        }

        fun getAddress(): String? {
            return address
        }

        /**
         * 经度,纬度
         */
        fun getLocation(): Array<Double> {
            return arrayOf(longitude, latitude)
        }
    }

    interface Callback {
        fun virtualLocation(status: Boolean, t: Throwable? = null)
    }
}