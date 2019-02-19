package com.anniu.common.util

import android.text.TextUtils

/**
 * Created by CN-11 on 2017/8/14.
 */

object SIMCardInfo{
    fun isMobileNO(mobiles: String): Boolean {
        /*
		130  联通 131  联通 132  联通 133  电信 134  移动 135  移动 136  移动 137  移动 138  移动 139  移动 141  物联网/电信 145  联通 146  联通
        147  移动 149  电信 150  移动 151  移动 152  移动 153  电信 155  联通 156  联通 157  移动 158  移动 159  移动 166  联通 170  虚拟/电信
        170  虚拟/移动 170  虚拟/联通 171  虚拟/联通 172  移动 173  电信 174  卫星/电信 175  联通 176  联通 177  电信 178  移动 180  电信 181  电信
        182  移动 183  移动 184  移动 185  联通 186  联通  187  移动 188  移动 189  电信 198  移动  199  电信
		*/
        try {
            val telRegex = "^(13[0-9]|14[15679]|15[0-3,5-9]|16[6]|17[0-8]|18[0-9]|19[89])\\d{8}$"
            //			String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
            //            String telRegex = "[1]\\d{10}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
            return if (TextUtils.isEmpty(mobiles))
                false
            else
                mobiles.matches(telRegex.toRegex())
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }
}
