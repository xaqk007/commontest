package com.anniu.common.util

object MapUtil {
    fun getStirng(map:Map<String, Any>?, key:String):String?{
        map?.apply {
            val value = get(key)
            if(value is String)
                return value
        }
        return null
    }

    fun getBoolean(map:Map<String, Any>?, key:String):Boolean{
        map?.apply {
            val value = get(key)
            if(value is Boolean)
                return value
        }
        return false
    }

    fun getInt(map:Map<String, Any>?, key:String):Int{
        map?.apply {
            val value = get(key)
            if(value is Double)
                return value.toInt()
        }
        return 0
    }

    fun getLong(map:Map<String, Any>?, key:String):Long{
        map?.apply {
            val value = get(key)
            if(value is Double)
                return value.toLong()
        }
        return 0L
    }

    fun getFloat(map:Map<String, Any>?, key:String):Float{
        map?.apply {
            val value = get(key)
            if(value is Double)
                return value.toFloat()
        }
        return 0f
    }

    fun getDouble(map:Map<String, Any>?, key:String):Double{
        map?.apply {
            val value = get(key)
            if(value is Double)
                return value
        }
        return 0.0
    }
}