package me.yuxiaoyao.virtuallocation.amap.client

import me.yuxiaoyao.virtuallocation.amap.model.PoiModel
import me.yuxiaoyao.virtuallocation.amap.model.ReGeoModel
import retrofit2.http.GET
import retrofit2.http.Query

interface AMapWebClient {
    @GET("/v3/place/detail?output=json")
    suspend fun getByPoiId(
        @Query("id") poiId: String
    ): PoiModel

    @GET("/v3/geocode/regeo")
    suspend fun getLocationInfo(@Query("location") location: String): ReGeoModel
}