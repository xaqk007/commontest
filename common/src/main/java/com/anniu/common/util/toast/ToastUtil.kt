package com.anniu.common.util.toast

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.Gravity
import android.widget.Toast

/**
 * Created by CN-11 on 2017/8/14.
 */

object ToastUtil {
    fun textToast(context: Context?, text: CharSequence) {
        textToast(context, text, Toast.LENGTH_SHORT, Gravity.CENTER)
    }

    fun textToast(context: Context?, text: CharSequence, duration: Int, gravity: Int) {
        try {

            if (context == null)
                return
            if (context is Activity) {
                if (context.isFinishing || (Build.VERSION.SDK_INT >= 17 && context.isDestroyed)) {
                    return
                }
            }
            // 创建一个Toast提示消息
            val toast = Toast.makeText(context, text, duration)
            toast.setGravity(gravity, 0, 0)
            toast.setText(text)
            toast.duration = duration
            val sdkInt = Build.VERSION.SDK_INT
            if (sdkInt >= Build.VERSION_CODES.N && sdkInt < Build.VERSION_CODES.O){
                reflectTNHandler(toast)
            }
            // 显示消息
            toast.show()
        } catch (e: Exception) {
        }
    }

    private fun reflectTNHandler(toast:Toast) {
        try {
            val tNField = toast::class.java.getDeclaredField("mTN")
            tNField?.apply {
                isAccessible = true
                val TN = get(toast)
                TN?.apply {
                    val handlerField = TN::class.java.getDeclaredField("mHandler")
                    handlerField?.apply {
                        isAccessible = true
                        set(TN, ProxyTNHandler(TN))
                    }
                }
            }
        } catch (e: Exception) {
        }
    }
}