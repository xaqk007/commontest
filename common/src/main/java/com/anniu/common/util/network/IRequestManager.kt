package com.anniu.common.util.network

import android.content.Context
import io.reactivex.Observable

/**
 * Created by duliang on 2018/9/11.
 */
interface IRequestManager {
    fun get(context: Context, apiClass: Class<out BaseRequest<*>>, url: String, params: Any?): Observable<out Response<*>>

    fun post(context: Context, apiClass: Class<out BaseRequest<*>>, url: String, params: Any?): Observable<out Response<*>>
}