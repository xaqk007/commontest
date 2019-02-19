package com.anniu.common.util.network

import android.content.Context
import io.reactivex.Observable
import retrofit2.Retrofit

/**
 * Created by duliang on 2018/9/11.
 */
class RetrofitRequestManager(val retrofit: Retrofit) : IRequestManager {
    override fun get(context: Context, apiClass: Class<out BaseRequest<*>>, url: String, params: Any?): Observable<out Response<*>> {
        val api = apiClass.newInstance()
        return (api as BaseRequest<Any>).get("retrofit", context, retrofit.create(api.serviceClass), url, params)
    }

    override fun post(context: Context, apiClass: Class<out BaseRequest<*>>, url: String, params: Any?): Observable<out Response<*>> {
        val api = apiClass.newInstance()
        return (api as BaseRequest<Any>).post("retrofit", context, retrofit.create(api.serviceClass), url, params)
    }
}