package me.yuxiaoyao.virtuallocation.manager

import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.RemoteException
import android.os.SystemClock
import android.util.Log
import java.util.*

private const val TAG = "VirtualLocationManager"


//        provider.add(LocationManager.NETWORK_PROVIDER);
//        provider.add(LocationManager.GPS_PROVIDER);
class VirtualLocationManager(
    private val context: Context,
    private val provider: List<String> = listOf(LocationManager.GPS_PROVIDER)
) {
    private val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var status: Boolean = false


    companion object {

        fun get(context: Context): VirtualLocationManager {
            return VirtualLocationManager(context)
        }
    }

    /**
     * 模拟位置是否启用
     * 若启用，则 addTestProvider
     */
    fun checkEnableVirtualLocation(): Boolean {
        try {
            for (providerStr in provider) {
                val provider = locationManager.getProvider(providerStr)
                if (provider != null) {
                    locationManager.addTestProvider(
                        provider.name,
                        provider.requiresNetwork(),
                        provider.requiresSatellite(),
                        provider.requiresCell(),
                        provider.hasMonetaryCost(),
                        provider.supportsAltitude(),
                        provider.supportsSpeed(),
                        provider.supportsBearing(),
                        provider.powerRequirement,
                        provider.accuracy
                    )
                } else {
                    when (providerStr) {
                        LocationManager.GPS_PROVIDER -> {
                            locationManager.addTestProvider(
                                providerStr,
                                true,
                                true,
                                false,
                                false,
                                true,
                                true,
                                true,
                                Criteria.POWER_HIGH,
                                Criteria.ACCURACY_FINE
                            )
                        }
                        LocationManager.NETWORK_PROVIDER -> {
                            locationManager.addTestProvider(
                                providerStr,
                                true,
                                false,
                                true,
                                false,
                                false,
                                false,
                                false,
                                Criteria.POWER_LOW,
                                Criteria.ACCURACY_FINE
                            )
                        }
                        else -> {
                            locationManager.addTestProvider(
                                providerStr,
                                false,
                                false,
                                false,
                                false,
                                true,
                                true,
                                true,
                                Criteria.POWER_LOW,
                                Criteria.ACCURACY_FINE
                            )
                        }
                    }
                }
                locationManager.setTestProviderEnabled(providerStr, true)
                // locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
            }
            return true
        } catch (re: IllegalArgumentException) {
            re.printStackTrace()
            Log.e(TAG, "${re.message}")
            return true
        } catch (se: SecurityException) {
            se.printStackTrace()
            Log.e(TAG, "${se.message}")
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "${e.message}")
            return false
        }
    }

    fun startVirtualLocation(lng: Double, lat: Double) {
        // 模拟位置（ addTestProvider 成功的前提下）
        for (providerStr in provider) {
            // Log.e("TAGGG", "$longitude, $latitude")
            val mockLocation = Location(providerStr)

            mockLocation.longitude = lng // 经度（度）
            mockLocation.latitude = lat // 维度（度）

            mockLocation.accuracy = 0.1f // 精度（米）
            mockLocation.time = Date().time // 本地时间
            mockLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

            try {
                // 直接调用这个方法,没有开启模拟位置,会抛出 java.lang.SecurityException XXX not allowed to perform MOCK_LOCATION
                locationManager.setTestProviderLocation(providerStr, mockLocation)
            } catch (se: SecurityException) {
                se.printStackTrace()
                throw se
            }
        }

    }

    fun stopVirtualLocation() {

        for (provider in provider) {
            try {
                locationManager.removeTestProvider(provider)
            } catch (ex: java.lang.Exception) {
                // 此处不需要输出日志，若未成功addTestProvider，则必然会出错
                // 这里是对于非正常情况的预防措施
            }
        }

    }


/*fun startVirtualLocationTask(lng: Double, lat: Double): Boolean {
    Log.e(TAG, "模拟定位: 经度:$lng 纬度:$lat")

    if (!checkEnableVirtualLocation()) {
        Toast.makeText(context, "模拟定位未开启", Toast.LENGTH_SHORT).show()
        return false
    }
    if (!status) {
        // 开始模拟
        val runnable = Runnable {
            run {
                while (true) {
                    Thread.sleep(1000L)
                    startVirtualLocation()
                }
            }
        }
        Thread(runnable).start()
        status = true
    }
    // 已经开始了
    if (lng == longitude && lat == latitude) {
        // 同一个位置
        return true
    }
    synchronized(lock) {
        this.longitude = lng
        this.latitude = lat
    }
    return true
}
*/
/*
private fun startVirtualLocation() {
    // 模拟位置（ addTestProvider 成功的前提下）
    for (providerStr in provider) {
        // Log.e("TAGGG", "$longitude, $latitude")
        val mockLocation = Location(providerStr)
        synchronized(lock) {
            mockLocation.longitude = longitude // 经度（度）
            mockLocation.latitude = latitude // 维度（度）
        }
        mockLocation.accuracy = 0.1f // 精度（米）
        mockLocation.time = Date().time // 本地时间
        mockLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()

        try {
            // 直接调用这个方法,没有开启模拟位置,会抛出 java.lang.SecurityException XXX not allowed to perform MOCK_LOCATION
            locationManager.setTestProviderLocation(providerStr, mockLocation)
        } catch (se: SecurityException) {
            se.printStackTrace()
        }
    }
}*/


}