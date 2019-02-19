package com.anniu.common.util

import android.content.Context

/**
 * Created by CN-11 on 2017/9/8.
 */

object SPUtils {

    private val name = "config"
    private val mode = Context.MODE_PRIVATE

    /**
     * 保存首选项
     * @param context
     * @param key
     * @param value
     */
    fun saveBoolean(context: Context?, key: String, value: Boolean?) {
        if(context == null)
            return
        val sp = context.getSharedPreferences(name, mode)
        val edit = sp.edit()
        edit.putBoolean(key, value?:false)
        edit.apply()
    }

    fun saveInt(context: Context?, key: String, value: Int?) {
        if(context == null)
            return
        val sp = context.getSharedPreferences(name, mode)
        val edit = sp.edit()
        edit.putInt(key, value?:0)
        edit.apply()
    }

    fun saveLong(context: Context?, key: String, value: Long?) {
        if(context == null)
            return
        val sp = context.getSharedPreferences(name, mode)
        val edit = sp.edit()
        edit.putLong(key, value?:0)
        edit.apply()
    }

    fun saveString(context: Context?, key: String, value: String?) {
        if(context == null)
            return
        val sp = context.getSharedPreferences(name, mode)
        val edit = sp.edit()
        edit.putString(key, value?:"")
        edit.apply()
    }


    /**
     * 获取首选项
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    fun getBoolean(context: Context?, key: String, defValue: Boolean?): Boolean {
        if(context == null)
            return false
        val sp = context.getSharedPreferences(name, mode)
        return sp.getBoolean(key, defValue?:false)
    }

    fun getInt(context: Context?, key: String, defValue: Int?): Int {
        if(context == null)
            return 0
        val sp = context.getSharedPreferences(name, mode)
        return sp.getInt(key, defValue?:0)
    }

    fun getLong(context: Context?, key: String, defValue: Long?): Long {
        if(context == null)
            return 0
        val sp = context.getSharedPreferences(name, mode)
        return sp.getLong(key, defValue?:0)
    }

    fun getString(context: Context?, key: String, defValue: String?): String? {
        if(context == null)
            return null
        val sp = context.getSharedPreferences(name, mode)
        return sp.getString(key, defValue?:"")
    }

    fun clear(context: Context) {
        val sp = context.getSharedPreferences(name, mode)
        sp.edit().clear().apply()
    }
}