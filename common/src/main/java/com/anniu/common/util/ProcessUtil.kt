package com.anniu.common.util

import android.app.ActivityManager
import android.content.Context
import android.text.TextUtils

/**
 * Created by CN-11 on 2017/8/14.
 */

object ProcessUtil {
    fun inMainProcess(context: Context): Boolean {
        val mainProcessName = context.applicationInfo.processName
        val processName = getProcessName(context)
        return TextUtils.equals(mainProcessName, processName)
    }

    /**
     * 获取当前进程名
     */
    fun getProcessName(context: Context): String? {
        val pid = android.os.Process.myPid()
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return am.runningAppProcesses.find {
            it.pid == pid
        }?.processName
    }
}
