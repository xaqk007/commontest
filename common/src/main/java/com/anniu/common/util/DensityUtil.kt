package com.anniu.common.util

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import java.lang.reflect.Field

/**
 * Created by CN-11 on 2017/8/11.
 */

object DensityUtil {

    private var screenWidth: Int = 0

    private var screenHeight: Int = 0

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context?, dpValue: Float): Int {
        if (context == null)
            return 0
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun getStatusBarHeight(context: Context): Int {
        var c: Class<*>? = null
        var obj: Any? = null
        var field: Field? = null
        var x = 0
        var statusBarHeight = 0
        try {
            c = Class.forName("com.android.internal.R\$dimen")
            obj = c!!.newInstance()
            field = c.getField("status_bar_height")
            x = Integer.parseInt(field!!.get(obj).toString())
            statusBarHeight = context.resources.getDimensionPixelSize(x)
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        return statusBarHeight
    }

    fun getScreenWidth(context: Context?): Int {
        if (context == null)
            return 0
        if (screenWidth > 0)
            return screenWidth
        else
            screenWidth = context.resources.displayMetrics.widthPixels
        return screenWidth
    }

    fun getScreenHeight(context: Context?): Int {
        if (context == null)
            return 0
        if (screenHeight > 0)
            return screenHeight
        else
            screenHeight = context.resources.displayMetrics.heightPixels
        return screenHeight
    }

    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    fun setStatusBar(context: Context, statusBar: View) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            statusBar.visibility = View.VISIBLE
            val layoutParams = statusBar.layoutParams
            layoutParams.height = getStatusBarHeight(context)
            statusBar.layoutParams = layoutParams
        } else
            statusBar.visibility = View.GONE
    }

    //    设置控件最大高度
    fun setMaxHeight(view: View?, maxHeight: Int) {
        if (view == null)
            return
        if (view.height > maxHeight) {
            val params = view.layoutParams as ViewGroup.LayoutParams
            params.height = maxHeight
            view.layoutParams = params
        }
    }

}