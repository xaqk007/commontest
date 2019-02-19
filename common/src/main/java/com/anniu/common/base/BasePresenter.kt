package com.anniu.common.base

import android.os.Bundle
import android.support.v4.util.ArrayMap
import io.reactivex.disposables.Disposable
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by duliang on 2018/9/11.
 */
open class BasePresenter(val callBack:BaseCallBack){
    var destroied = false
    private val disposableMap: ArrayMap<Int, Disposable> = ArrayMap()
    private val onDestroyListeners = CopyOnWriteArrayList<OnDestroyListener>()

    fun addDisposable(id:Int, disposable: Disposable){
        var disposable = disposableMap.put(id, disposable)
        if(disposable != null && !disposable.isDisposed){
            disposable.dispose()
        }
    }

    open fun onCreate(savedState: Bundle?) {
        destroied = false
    }

    open fun onDestroy() {
        destroied = true
        disposableMap.entries.forEach {
            if(!it.value.isDisposed)
                it.value.dispose()
        }
        disposableMap.clear()
    }

    open fun onSave(state: Bundle?) {}

    open fun onTakeView() {}

    open fun onDropView() {}

    interface OnDestroyListener {
        fun onDestroy()
    }


    fun addOnDestroyListener(listener: OnDestroyListener) {
        onDestroyListeners.add(listener)
    }


    fun removeOnDestroyListener(listener: OnDestroyListener) {
        onDestroyListeners.remove(listener)
    }


    fun getView(): BaseCallBack? {
        return callBack
    }


    fun create(bundle: Bundle?) {
        onCreate(bundle)
    }


    fun destroy() {
        for (listener in onDestroyListeners)
            listener.onDestroy()
        onDestroy()
    }

    fun save(state: Bundle?) {
        onSave(state)
    }

    fun takeView() {
        onTakeView()
    }

    fun dropView() {
        onDropView()
    }
}