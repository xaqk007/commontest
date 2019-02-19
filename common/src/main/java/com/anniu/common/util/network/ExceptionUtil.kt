package com.anniu.common.util.network

import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import retrofit2.HttpException
import java.net.*

/**
 * Created by duliang on 2018/9/11.
 */
object ExceptionUtil {
    fun getMsg(throwable: Throwable): String? {
        var msg: String? = null
        if (throwable is HttpException
                || throwable is UnknownHostException
                || throwable is ConnectException
                || throwable is SocketException
                || throwable is UnknownServiceException) {
            msg = "网络异常"
        } else if (throwable is SocketTimeoutException) {
            msg = "请求超时"
        } else if (throwable is JsonSyntaxException || throwable is MalformedJsonException) {
            msg = "数据解析失败"
        } else if (throwable is URISyntaxException || throwable is MalformedURLException) {
            msg = "地址解析失败"
        }
        return msg
    }
}