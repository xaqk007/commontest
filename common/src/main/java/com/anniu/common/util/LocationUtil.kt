package com.anniu.common.util

import android.content.Context
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import com.anniu.common.R
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.PermissionListener
import org.greenrobot.eventbus.EventBus

/**
 * Created by duliang on 2018/9/13.
 */
object LocationUtil {
    private val REQUEST_FOR_LOCATION = 9001
    private var locationClient: LocationClient? = null
    private var count: Int = 0
    var scanning = false

    private fun getLocationClient(context: Context): LocationClient {
        if (locationClient == null) {
            synchronized(LocationClient::class.java) {
                if (locationClient == null) {
                    locationClient = LocationClient(context)
                    initLocationClient()
                }
            }
        }
        return locationClient!!
    }

    private fun initLocationClient() {
        val option = LocationClientOption()
        option.setIsNeedAddress(true)
        option.locationMode = LocationClientOption.LocationMode.Battery_Saving
        option.setCoorType("bd09ll")
        option.setScanSpan(30000)
        option.isOpenGps = false
        option.setIgnoreKillProcess(false)
        option.SetIgnoreCacheException(true)
        option.setWifiCacheTimeOut(5 * 60 * 1000)
        option.setEnableSimulateGps(false)
        locationClient!!.locOption = option
    }

    fun startLocation(context: Context){
        startLocation(context, null)
    }

    fun startLocation(context: Context, listener: BDAbstractLocationListener?) {
        saveBdCity(context, null)
        AndPermission.with(context)
                .permission(Permission.LOCATION)
                .requestCode(REQUEST_FOR_LOCATION)
                .rationale { requestCode, rationale ->
                    AndPermission.rationaleDialog(context, rationale).show()
                }
                .callback(object : PermissionListener {
                    override fun onSucceed(requestCode: Int, grantPermissions: MutableList<String>) {
                        val locationClient = getLocationClient(context)
                        if (!locationClient.isStarted) {
                            count = 0
                            locationClient.registerLocationListener(listener?:(object : BDAbstractLocationListener() {
                                override fun onReceiveLocation(bdLocation: BDLocation?) {
                                    var city = bdLocation?.city
                                    if(city?.endsWith("市") == true){
                                        val len = city.length
                                        city = city.take(len-1)
                                    }
                                    saveBdCity(context, city)
                                    if(!TextUtils.isEmpty(city)) {
                                        val citySaved = getCity(context)
                                        if (!TextUtils.isEmpty(citySaved) && citySaved != city){
                                            val view = LayoutInflater.from(context).inflate(R.layout.dialog_change_city, null)
                                            val dialog = AlertDialog.Builder(context)
                                                    .setView(view)
                                                    .create()
                                            dialog.show()
                                            ClickUtils.setNoFastClickListener(view.findViewById(R.id.tvCancel), {dialog.dismiss()})
                                            ClickUtils.setNoFastClickListener(view.findViewById(R.id.tvConfirm), {
                                                dialog.dismiss()
                                                EventBus.getDefault().post(LOCATE_SUCCESS)
                                                saveCity(context, city)
                                            })
                                        }
                                        else {
                                            EventBus.getDefault().post(LOCATE_SUCCESS)
                                            saveCity(context, city)
                                        }
                                        release()
                                    }
                                    else {
                                        if (count >= 5) {
                                            release()
                                            EventBus.getDefault().post(LOCATE_FAIL)
                                        }
                                        count++
                                    }
                                }
                            }))
                            locationClient.start()
                            scanning = true
                        }
                    }

                    override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>) {
                        EventBus.getDefault().post(LOCATE_FAIL)
                        release()
                    }
                })
                .start()
    }

    fun saveBdCity(context: Context, city:String?){
        SPUtils.saveString(context, BD_CITY, city)
    }

    fun getBdCity(context: Context):String?{
        return SPUtils.getString(context, BD_CITY, "")
    }

    fun saveCity(context: Context, city:String?){
        SPUtils.saveString(context, CITY_SAVED, city)
    }

    fun getCity(context: Context):String?{
        return SPUtils.getString(context, CITY_SAVED, "")
    }

    private fun release(){
        locationClient?.stop()
        locationClient = null
        scanning = false
    }

    const val BD_CITY = "bdCity"//百度定位城市
    const val CITY_SAVED = "citySaved"//缓存城市
    const val LOCATE_SUCCESS = "locateSuccess"//定位成功
    const val LOCATE_FAIL = "locateFail"//定位失败
}