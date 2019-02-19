package com.anniu.common.util

import android.app.AlertDialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import com.anniu.common.R

/**
 * Created by alphabet on 2018/11/2.
 */
object DialogUtil {

    fun getBottomDialogWindow(dialog: AlertDialog, layoutId: Int): Window {
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        val window = dialog.window
        window!!.decorView.setPadding(0, 0, 0, 0)
        window.setGravity(Gravity.BOTTOM)
        window.setWindowAnimations(R.style.pop_from_bottom)
        window.setContentView(layoutId)
        val lp = window.attributes
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = lp
        return window
    }

    fun getNormalDialog(context: Context, layoutId: Int,cancelAble: Boolean = true): AlertDialog {
        val viewDialog = View.inflate(context, layoutId, null)
        val alertDialog = AlertDialog.Builder(context,R.style.AlertDialog).setView(viewDialog).create()
        alertDialog.setCanceledOnTouchOutside(cancelAble)
        alertDialog.show()
        return alertDialog
    }
}