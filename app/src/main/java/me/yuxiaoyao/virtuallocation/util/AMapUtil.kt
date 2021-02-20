package me.yuxiaoyao.virtuallocation.util

import com.amap.api.maps.model.LatLng


/**
 * @param lng 经度
 * @param lat 纬度
 */
fun formatMapLocation(lng: Double, lat: Double): String {
    return "${String.format("%.6f", lng)},${
        String.format("%.6f", lat)
    }"
}

fun formatAMapLocation(position: LatLng): String {
    return "${String.format("%.6f", position.longitude)},${
        String.format(
            "%.6f",
            position.latitude
        )
    }"
}

/**
 * 经度,纬度
 */
fun locationToLatLnt(loa: String): Array<Double> {
    val split = loa.split(",")
    return arrayOf(split[0].toDouble(), split[1].toDouble())
}