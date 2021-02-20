package me.yuxiaoyao.virtuallocation.util

import android.content.Context
import android.provider.Settings


fun isOpenDevMode(context: Context): Boolean {
    return (Settings.Secure.getInt(
        context.contentResolver,
        android.provider.Settings.Global.ADB_ENABLED,
        0
    ) > 0)
}