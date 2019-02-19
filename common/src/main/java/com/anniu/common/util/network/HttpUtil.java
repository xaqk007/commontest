package com.anniu.common.util.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.anniu.common.BuildConfig;
import com.anniu.common.constants.UrlConstants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by duliang on 2018/9/11.
 */

public class HttpUtil {
    private static IRequestManager requestManager = null;
    private static IRequestManager requestManagerLongTime = null;

    public static IRequestManager getRequestManager(Context context) {
        if(requestManager == null){
            synchronized (HttpUtil.class){
                if(requestManager == null){
                    requestManager = new RetrofitRequestManager(getRetrofit(context.getApplicationContext(), 0, 0));
                }
            }
        }
        return requestManager;
    }

    public static IRequestManager getRequestManagerLongTime(Context context) {
        if(requestManagerLongTime == null){
            synchronized (HttpUtil.class){
                if(requestManagerLongTime == null){
                    requestManagerLongTime = new RetrofitRequestManager(getRetrofit(context.getApplicationContext(), 60, 60));
                }
            }
        }
        return requestManagerLongTime;
    }

    private static Retrofit getRetrofit(Context context, long connectTime, long readTime){
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(UrlConstants.SERVER_URL);
        builder.addConverterFactory(ScalarsConverterFactory.create());
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        builder.client(getOKHttpClient(context, connectTime, readTime));
        return builder.build();
    }

    private static OkHttpClient getOKHttpClient(final Context context, long connectTime, long readTime){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if(connectTime > 0)
            builder.connectTimeout(connectTime, TimeUnit.SECONDS);
        else
            builder.connectTimeout(20, TimeUnit.SECONDS);
        if(readTime > 0)
            builder.readTimeout(readTime, TimeUnit.SECONDS);
        else
            builder.readTimeout(20, TimeUnit.SECONDS);
        builder.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder requestBuilder = request.newBuilder();
                request = requestBuilder.build();
                okhttp3.Response response = chain.proceed(request);
                if(BuildConfig.DEBUG){
                    MediaType mediaType = response.body().contentType();
                    String content = response.body().string();
                    String requestBody = request.toString();
                    if(!TextUtils.isEmpty(requestBody))
                        Log.d("retrofit",requestBody);
                    if(!TextUtils.isEmpty(content))
                        Log.d("retrofit",content);
                    response = response.newBuilder().body(ResponseBody.create(mediaType, content)).build();
                }
                return response;
            }
        });
        return builder.build();
    }
}
