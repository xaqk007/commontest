package com.anniu.common.util.payment

import android.app.Activity
import android.content.Context

import com.alipay.sdk.app.PayTask
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import org.json.JSONObject

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by CN-11 on 2017/8/23.
 */

class PaymentUtil(private val mContext: Context, private val listener: PayResultListener?) {

    fun aliPay(orderInfo: String) {
        Observable.create(ObservableOnSubscribe<Map<String, String>> { e ->
            val payTask = PayTask(mContext as Activity)
            val result = payTask.payV2(orderInfo, true)
            e.onNext(result)
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ map ->
            try {
                val tradeStatus = map["resultStatus"]
                if (tradeStatus == "9000") {
                    listener?.onPaySuccess()
                } else {
                    if (tradeStatus == "8000") {
                        listener?.onPayFail("支付结果确认中")
                    } else {
                        listener?.onPayFail(when(tradeStatus){
                            "4000" -> "订单支付失败"
                            "5000" -> "重复请求"
                            "6001" -> "用户中途取消"
                            "6002" -> "网络连接出错"
                            "6004" -> "支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态"
                            else -> "未知支付错误"
                        })
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }) {
            listener?.onPayFail("网络异常")
        }
    }

    fun wechatPay(value: String) {
        val iwxapi = getIwxapi(mContext)
        val request = PayReq()
        try {
            val jsonObject = JSONObject(value)
            request.appId = jsonObject.optString("appid")
            request.partnerId = jsonObject.optString("partnerid")
            request.prepayId = jsonObject.optString("prepayid")
            request.packageValue = jsonObject.optString("package")
            request.nonceStr = jsonObject.optString("noncestr")
            request.timeStamp = jsonObject.optString("timestamp")
            request.sign = jsonObject.optString("sign")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        iwxapi.sendReq(request)
    }

    companion object {
        private val WX_APPID = "wxda4b867278e12a4f"
        val ALIPAY = "ALIPAY"
        val WECHAT = "WECHAT"
//        val UPMP = "UPMP"
        var wxPayResultListener: WXPayResultListener? = null
        private var iwxapi: IWXAPI? = null

        fun getIwxapi(context: Context): IWXAPI {
            if (iwxapi == null)
                regToWX(context)
            return iwxapi!!
        }

        private fun regToWX(context: Context) {
            iwxapi = WXAPIFactory.createWXAPI(context, WX_APPID, true)
            iwxapi?.registerApp(WX_APPID)
        }
    }
}
