package me.yuxiaoyao.virtuallocation

import android.app.Application
import android.content.pm.PackageManager
import me.yuxiaoyao.virtuallocation.retrofit.AMAP_WEB_KEY


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val appInfo =
            this.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)

        AMAP_WEB_KEY = appInfo.metaData["me.yuxiaoyao.amap.webapikey"].toString()

    }
}