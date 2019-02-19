package com.anniu.common.util.network

/**
 * Created by duliang on 2018/9/11.
 */
class Response<T>(val code:String?,
                  val msg:String?,
                  val reqId:String?,
                  val data:T?)