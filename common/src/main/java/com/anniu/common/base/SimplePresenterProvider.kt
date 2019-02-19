package com.anniu.common.base

import android.os.Bundle
import android.support.v4.util.ArrayMap
import android.text.TextUtils

/**
 * Created by duliang on 2018/9/11.
 */
class SimplePresenterProvider<T:BasePresenter> : PresenterProvider<T> {
    private val classNames = ArrayMap<Class<*>, String>()
    private val  presenters = ArrayMap<String, T>()
    override fun getPresenter(name: String): T {
        if(TextUtils.isEmpty(name))
            throw Throwable("Presenter name can not be null")
        val presenter = presenters[name]
        return presenter?:throw Throwable("Presenter with name $name not found")
    }

    override fun getPresenter(clazz: Class<T>): T {
        val name = getNameFromClass(clazz)
        return getPresenter(name)
    }

    override fun addPresenter(presenter: T): T? {
        val name = getNameFromClass(presenter::class.java)
        return addPresenter(name, presenter)
    }

    override fun addPresenter(name: String, presenter: T): T? {
        if(TextUtils.isEmpty(name))
            throw Throwable("Presenter name can not be null")
        if(name != getNameFromClass(presenter::class.java))
            throw Throwable("Presenter name not validate")
        return presenters.put(name, presenter)
    }

    private fun getNameFromClass(clazz: Class<*>):String{
        var name = classNames[clazz]
        if(TextUtils.isEmpty(name)){
            name = clazz.simpleName
            classNames.put(clazz, name)
        }
        return name!!
    }

    fun onCreate(savedInstanceState: Bundle?){
        presenters.forEach{
            it.value.create(savedInstanceState)
        }
    }

    fun save(savedInstanceState: Bundle?){
        presenters.forEach{
            it.value.save(savedInstanceState)
        }
    }

    fun onDestroy(){
        presenters.forEach{
            it.value.destroy()
        }
    }

    fun takeView(){
        presenters.forEach{
            it.value.takeView()
        }
    }

    fun dropView(){
        presenters.forEach{
            it.value.dropView()
        }
    }
}