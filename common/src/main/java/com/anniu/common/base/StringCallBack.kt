package com.anniu.common.base

interface StringCallBack : BaseCallBack {
    fun onGetString(value:String)
    fun onGetStringFail(msg:String)
}