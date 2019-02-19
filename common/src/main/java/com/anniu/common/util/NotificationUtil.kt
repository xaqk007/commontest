package com.anniu.common.util

import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationManagerCompat
import android.app.AppOpsManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import java.lang.reflect.InvocationTargetException


/**
 * Created by duliang on 2018/7/18.
 */
object NotificationUtil {
    private val CHECK_OP_NO_THROW = "checkOpNoThrow"
    private val OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION"

    fun enableNotification(context: Context){
        if(!areNotificationEnabled(context)){
            val intent = Intent()
            when(Build.VERSION.SDK_INT){
                in 26..Int.MAX_VALUE ->{
                    intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName())
                }
                in 21..25 -> {
                    intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    intent.putExtra("app_package", context.packageName)
                    intent.putExtra("app_uid", context.applicationInfo.uid)
                }
                else -> {
                    intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                    intent.data = Uri.fromParts("package", context.packageName, null)
                }
            }
            context.startActivity(intent)
        }
    }

    fun areNotificationEnabled(context: Context):Boolean{
        return when(Build.VERSION.SDK_INT){
            in 25..Int.MAX_VALUE -> NotificationManagerCompat.from(context).areNotificationsEnabled()
            in 19..24 -> isNotificationEnabled19to24(context)
            else -> true
        }
    }

    fun isNotificationEnabled19to24(context: Context): Boolean {
        val mAppOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val appInfo = context.applicationInfo
        val pkg = context.applicationContext.packageName
        val uid = appInfo.uid
        var appOpsClass: Class<*>? = null
        try {
            appOpsClass = Class.forName(AppOpsManager::class.java.name)
            val checkOpNoThrowMethod = appOpsClass!!.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String::class.java)
            val opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION)
            val value = opPostNotificationValue.get(Int::class.java) as Int
            return checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) as Int == AppOpsManager.MODE_ALLOWED

        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return false
    }

    fun createNotificationChannel(context: Context, id:String, notify:Boolean=false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // 用户可以看到的通知渠道的名字.
            val name = "notification channel"
            // 用户可以看到的通知渠道的描述
            val description = "notification description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(id, name, importance)
            // 配置通知渠道的属性
            mChannel.description = description
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(notify)
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(notify)
            if(notify){
                mChannel.lightColor = Color.RED
                mChannel.vibrationPattern = (longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            }
            else{
                mChannel.setSound(null, null)
            }
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel)
        }
    }
}