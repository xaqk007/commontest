package com.anniu.common.util.network

import android.content.Context
import android.net.ConnectivityManager


/**
 * Created by duliang on 2018/9/11.
 */
object ConnectionUtil {
    var isWifi: Boolean? = null
    fun isWifi(mContext: Context): Boolean {
        if (isWifi == null) {
            val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetInfo = connectivityManager.activeNetworkInfo
            isWifi = (activeNetInfo != null && activeNetInfo.type == ConnectivityManager.TYPE_WIFI)
        }
        return isWifi ?: false
    }

    fun isConnected(mContext: Context): Boolean {
        val connectivityManager = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetInfo = connectivityManager.activeNetworkInfo
        return activeNetInfo?.isConnected ?: false
    }
}