package me.yuxiaoyao.virtuallocation.model


data class AddressPair(
    val address: String,
    val cityCode: String?,
    val cityName: String,
    val longitude: Double,
    val latitude: Double
)
