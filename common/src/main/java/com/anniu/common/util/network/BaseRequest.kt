package com.anniu.common.util.network

import android.content.Context
import io.reactivex.Observable

/**
 * Created by duliang on 2018/9/11.
 */
open abstract class BaseRequest<T> {
    abstract var serviceClass: Class<T>
    abstract fun get(type: String, context: Context, t: T, url: String, params: Any?): Observable<out Response<*>>
    abstract fun post(type: String, context: Context, t: T, url: String, params: Any?): Observable<out Response<*>>
}