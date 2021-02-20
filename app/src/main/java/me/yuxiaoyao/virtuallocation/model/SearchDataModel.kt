package me.yuxiaoyao.virtuallocation.model


data class SearchDataModel(
    val name: String,
    val address: String,
    val cityCode: String,
    val cityName: String,
    val latitude: Double,
    val longitude: Double
)