package com.anniu.common.util;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by CN-11 on 2017/8/11.
 */

public class DeviceUtil {
    private static String deviceId;
    public static String getDeviceId(Context context){
        if(TextUtils.isEmpty(deviceId)) {
            deviceId = SensorsDataAPI.sharedInstance().getAnonymousId();
        }
        return deviceId;
    }

    private static String imsi;

    public static String getImsi(Context context){
        if(TextUtils.isEmpty(imsi) && context != null){
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    imsi = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                }catch (Exception e){
                }
            }
        }
        return imsi;
    }

    private static  String buildInfo;

    public static String getBuildInfo(){
        if(TextUtils.isEmpty(buildInfo)){
            try {
                buildInfo = "35" + Build.BOARD.length() % 10 +
                        Build.BRAND.length() % 10 +
                        Build.CPU_ABI.length() % 10 +
                        Build.DEVICE.length() % 10 +
                        Build.DISPLAY.length() % 10 +
                        Build.HOST.length() % 10 +
                        Build.ID.length() % 10 +
                        Build.MANUFACTURER.length() % 10 +
                        Build.MODEL.length() % 10 +
                        Build.PRODUCT.length() % 10 +
                        Build.TAGS.length() % 10 +
                        Build.TYPE.length() % 10 +
                        Build.USER.length() % 10;
            } catch (Exception e) {
            }
        }
        return buildInfo;
    }

    private static String serial;

    public static String getSerial(){
        if(TextUtils.isEmpty(serial)){
            try {
                serial = Build.SERIAL;
            }catch (Exception e){}
        }
        return serial;
    }

    public static String getRamTotalSize(Context context){
        try{
            ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi= new ActivityManager.MemoryInfo();
            am.getMemoryInfo(mi);
            String[] temp = fileSize(mi.totalMem);
            return temp[0] + temp[1];
        }catch (Exception e){
            return null;
        }
    }

    private static String[] fileSize(double size){
        String str="";
        if(size>=1000){
            str="KB";
            size/=1000;
            if(size>=1000){
                str="MB";
                size/=1000;
                if(size>=1000){
                    str="GB";
                    size/=1000;
                }
            }
        }
        String result[]=new String[2];
        result[0]=DecimalUtil.INSTANCE.round(size, 0) + "";
        result[1]=str;
        return result;
    }

    /*是否模拟器*/
    public static boolean isSimulator(Context context){
//        if(!hasBlueTooth())读取蓝牙信息需动态申请权限，不再作为判断依据
//            return true;
        if(!hasLightSensorManager(context))
            return true;
        if(isSimulatorFromFeatures())
            return true;
        String cpuInfo = readCpuInfo();
        if ((cpuInfo.contains("intel") || cpuInfo.contains("amd"))) {
            return true;
        }
        return false;
    }

    /*是否有蓝牙功能*/
    public static boolean hasBlueTooth() {
        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        if (ba == null) {
            return false;
        } else {
            String name = ba.getName();
            if (TextUtils.isEmpty(name)) {
                return false;
            } else {
                return true;
            }
        }
    }

    /*是否有光传感器*/
    public static Boolean hasLightSensorManager(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光
        if (null != sensor8) {
            return true;
        } else {
            return false;
        }
    }

    /*通过features判断是否模拟器*/
    public static boolean isSimulatorFromFeatures() {
        return Build.FINGERPRINT.toLowerCase().startsWith("generic")
                || Build.FINGERPRINT.toLowerCase().contains("vbox")
                || Build.FINGERPRINT.toLowerCase().contains("test-keys")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static String readCpuInfo() {
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            ProcessBuilder cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            StringBuffer sb = new StringBuffer();
            String readLine = "";
            BufferedReader responseReader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine);
            }
            responseReader.close();
            result = sb.toString().toLowerCase();
        } catch (IOException ex) {
        }
        return result;
    }

    //判断手机是否root
    public static boolean isRoot() {
        String binPath = "/system/bin/su";
        String xBinPath = "/system/xbin/su";
        if (new File(binPath).exists() && isCanExecute(binPath)) {
            return true;
        }
        if (new File(xBinPath).exists() && isCanExecute(xBinPath)) {
            return true;
        }
        return false;
    }

    private static boolean isCanExecute(String filePath) {
        java.lang.Process process = null;
        try {
            process = Runtime.getRuntime().exec("ls -l " + filePath);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String str = in.readLine();
            if (str != null && str.length() >= 4) {
                char flag = str.charAt(3);
                if (flag == 's' || flag == 'x')
                    return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    public static boolean isVpnUsed() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if(niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if(!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())){
                        return true; // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * 判断设备 是否使用代理上网
     * */
    public boolean isWifiProxy(Context context) {
        final boolean IS_ICS_OR_LATER = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
        String proxyAddress;
        int proxyPort;
        if (IS_ICS_OR_LATER) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portStr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portStr != null ? portStr : "-1"));
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);

    }
}