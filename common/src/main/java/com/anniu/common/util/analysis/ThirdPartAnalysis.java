package com.anniu.common.util.analysis;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by CN-11 on 2017/8/11.
 */

public class ThirdPartAnalysis {
    public static void onResume(Context context) {
        MobclickAgent.onResume(context);
    }

    public static void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    public static void onPageStart(String name) {
        MobclickAgent.onPageStart(name);
    }

    public static void onPageEnd(String name) {
        MobclickAgent.onPageEnd(name);
    }

    public static void onEvent(Context context, String eventId){
        onEvent(context, eventId, null);
    }

    public static void onEvent(Context context, String eventId, JSONObject params){
        SensorsDataAPI.sharedInstance(context.getApplicationContext()).track(eventId, params);

    }

    private static ArrayMap<String, String> jsonToMap(JSONObject jsonObject){
        ArrayMap<String, String> map= new ArrayMap();
        Iterator it = jsonObject.keys();
        while (it.hasNext()){
            String key = String.valueOf(it.next());
            String value = jsonObject.optString(key);
            map.put(key, value);
        }
        return map;
    }
}
