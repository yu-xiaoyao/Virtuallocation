package me.yuxiaoyao.virtuallocation.retrofit

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory


private const val AMAP_BASE_URL = "https://restapi.amap.com/"
lateinit var AMAP_WEB_KEY: String

fun <T> getAMapRetrofitClient(cls: Class<T>): T {
    val builder = OkHttpClient.Builder()

    val aMapKeyInterceptor = AMapKeyInterceptor(AMAP_WEB_KEY)
    builder.addInterceptor(aMapKeyInterceptor)

    val httpLoggingInterceptor = HttpLoggingInterceptor()
    httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    builder.addInterceptor(httpLoggingInterceptor)

    val objectMapper = ObjectMapper()
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    objectMapper.registerKotlinModule()

    val retrofit = Retrofit.Builder()
        .baseUrl(AMAP_BASE_URL)
        .addConverterFactory(JacksonConverterFactory.create(objectMapper))
        .client(builder.build())
        .build()
    return retrofit.create(cls)


}