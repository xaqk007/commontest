package com.anniu.common.util.payment

import com.tencent.mm.opensdk.modelbase.BaseResp

/**
 * Created by Administrator on 2018/2/26 0026.
 */

interface WXPayResultListener {
    fun onPaySuccess(response: BaseResp?)
    fun onPayCancel(response: BaseResp?)
    fun onPayFail(response: BaseResp?)
}
