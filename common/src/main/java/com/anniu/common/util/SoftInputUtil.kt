package com.anniu.common.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Created by alphabet on 2018/9/20.
 */
object SoftInputUtil {
    fun showSoftInput(editText: EditText?) {
        if (editText != null) {
            val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            do {
                editText.requestFocus()
            } while (!editText.isFocused)

            if (imm != null && imm.isActive) {
                imm.showSoftInput(editText, 2)
                imm.toggleSoftInput(0, 2)
            }
        }
    }

    fun hideSoftInput(view: View?) {
        if (view != null && view.context != null) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            view.clearFocus()
            if (imm != null && imm.isActive) {
                imm.hideSoftInputFromWindow(view.applicationWindowToken, 0)
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

        }
    }
}