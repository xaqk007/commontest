package com.anniu.common.base

/**
 * Created by duliang on 2018/9/11.
 */
interface PresenterProvider<T:BasePresenter> {
    fun getPresenter(name:String):T
    fun getPresenter(clazz: Class<T>):T
    fun addPresenter(presenter:T):T?
    fun addPresenter(name:String, presenter:T):T?
}