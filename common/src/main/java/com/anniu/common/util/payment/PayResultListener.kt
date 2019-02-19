package com.anniu.common.util.payment

/**
 * Created by CN-11 on 2017/8/25.
 */

interface PayResultListener {
    fun onPaySuccess()
    fun onPayCancel()
    fun onPayFail(msg: String)
}
