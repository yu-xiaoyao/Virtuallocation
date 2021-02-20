package me.yuxiaoyao.virtuallocation.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class AMapKeyInterceptor(private val key: String? = null) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (key == null) {
            return chain.proceed(chain.request())
        }

        val request = chain.request()


        if (request.method == "GET") {
            val newBuilder = request.url.newBuilder()
            newBuilder.addQueryParameter("key", key)
            val newUrl = newBuilder.build()
            return chain.proceed(request.newBuilder().url(newUrl).build())
        }
        //TODO
        return chain.proceed(chain.request())
    }
}