package com.anniu.common.util

import android.text.TextUtils

/**
 * Created by duliang on 2018/5/20.
 */
object StringUtil {
    /**
     * 检查对象是否为数字型字符串。
     */
    fun isNumeric(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        val str = obj.toString()
        val sz = str.length
        if (sz == 0) {
            return false
        }
        if (!Character.isDigit(str[0]) && str[0] != '-') {
            return false
        }
        (1 until sz).map {
            if (!Character.isDigit(str[it]) && str[it] != '.') {
                return false
            }
        }
        return true
    }

    fun isLetter(value: String): Boolean {
        if (TextUtils.isEmpty(value))
            return false
        val len = value.length
        (0 until len).map {
            val c = value[it]
            if (c < 'A' || (c > 'Z' && c < 'a') || c > 'z')
                return false
        }
        return true
    }
}