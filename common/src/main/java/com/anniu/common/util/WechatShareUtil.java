package com.anniu.common.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.Gravity;
import android.widget.Toast;

import com.anniu.common.R;
import com.anniu.common.util.toast.ToastUtil;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by CN-11 on 2017/8/29.
 */

public class WechatShareUtil {
    private static final String WX_APPID = "wxda4b867278e12a4f";
    private static IWXAPI iwxapi;
    private static WXShareListener wxShareListener;

    public static IWXAPI getIwxapi(Context context) {
        if(iwxapi == null)
            regToWX(context);
        return iwxapi;
    }

    private static void regToWX(Context context){
        iwxapi = WXAPIFactory.createWXAPI(context, WX_APPID, true);
        iwxapi.registerApp(WX_APPID);
    }

    public static void setWxShareListener(WXShareListener wxShareListener) {
        WechatShareUtil.wxShareListener = wxShareListener;
    }

    public static WXShareListener getWxShareListener() {
        return wxShareListener;
    }

    public static void shareToWechat(final Context context, final IWXAPI iwxapi){
        if(!iwxapi.isWXAppInstalled()){
            ToastUtil.INSTANCE.textToast(context, "您还没有安装微信,暂不支持此功能!", Toast.LENGTH_LONG, Gravity.CENTER);
        }
//        else if(!iwxapi.isWXAppSupportAPI()){
//            ToastUtil.INSTANCE.textToast(context, "你安装的微信版本不支持此功能", Toast.LENGTH_LONG, Gravity.CENTER);
//        }
        else {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.colorprimary_r2);
            WXImageObject wxImageObject = new WXImageObject(bitmap);
            WXMediaMessage wxMediaMessage = new WXMediaMessage();
            wxMediaMessage.mediaObject = wxImageObject;
            Bitmap thumb = Bitmap.createScaledBitmap(bitmap, 120, 120, true);
            bitmap.recycle();
            wxMediaMessage.thumbData = Bitmap2Bytes(thumb);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message = wxMediaMessage;
//                    req.scene = SendMessageToWX.Req.WXSceneSession;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
            iwxapi.sendReq(req);
        }
    }

    public static void shareToWechat(final Context context, final IWXAPI iwxapi, String url, final String imageUrl, String title, String content, final boolean pengyouquan){
        if(!iwxapi.isWXAppInstalled()){
            ToastUtil.INSTANCE.textToast(context, "您还没有安装微信,暂不支持此功能!", Toast.LENGTH_LONG, Gravity.CENTER);
        }
//        else if(!iwxapi.isWXAppSupportAPI()){
//            ToastUtil.INSTANCE.textToast(context, "你安装的微信版本不支持此功能", Toast.LENGTH_LONG, Gravity.CENTER);
//        }
        else {
            WXWebpageObject wxWebpageObject = new WXWebpageObject();
            wxWebpageObject.webpageUrl = url;
            final WXMediaMessage wxMediaMessage = new WXMediaMessage(wxWebpageObject);
            wxMediaMessage.title = title;
            wxMediaMessage.description = content;
            Observable.just(1)
                    .flatMap(new Function<Integer, ObservableSource<Bitmap>>() {
                        @Override
                        public ObservableSource<Bitmap> apply(Integer integer) throws Exception {
                            return Observable.create(new ObservableOnSubscribe<Bitmap>() {
                                @Override
                                public void subscribe(ObservableEmitter<Bitmap> e) throws Exception {
                                    Bitmap bitmap = null;
                                    if(imageUrl != null) {
                                        InputStream inputStream = null;
                                        try {
                                            BitmapFactory.Options options = new BitmapFactory.Options();
                                            options.inJustDecodeBounds = true;
                                            inputStream = getInputStream(imageUrl);
                                            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                                            float scale = (float) Math.sqrt((options.outHeight * options.outWidth) / (32 * 1024f));
                                            if (scale > 1) {
                                                int n = 1;
                                                while ((options.outHeight * options.outWidth)/(n*n) > (32 * 1024)){
                                                    n++;
                                                }
                                                options.inSampleSize = n;
                                            }
                                            else
                                                options.inSampleSize = 1;
                                            options.inJustDecodeBounds = false;
                                            if(inputStream != null)
                                                inputStream.close();
                                            inputStream = getInputStream(imageUrl);
                                            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                                        } catch (Exception e1) {
                                            e1.printStackTrace();
                                        }
                                        if(inputStream != null)
                                            inputStream.close();
                                    }
                                    if(bitmap == null)
                                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_share);
                                    e.onNext(bitmap);
                                }
                            });
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Bitmap>() {
                        @Override
                        public void accept(Bitmap bitmap) throws Exception {
                            wxMediaMessage.thumbData = Bitmap2Bytes(bitmap);
                            SendMessageToWX.Req req = new SendMessageToWX.Req();
                            req.transaction = String.valueOf(System.currentTimeMillis());
                            req.message = wxMediaMessage;
                            req.scene = pengyouquan ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                            iwxapi.sendReq(req);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ToastUtil.INSTANCE.textToast(context, throwable.getMessage(), Toast.LENGTH_SHORT, Gravity.CENTER);
                        }
                    });
        }
    }

    public static void sharePicture(Context context,Bitmap bitmap,IWXAPI iwxapi) {
        if(!iwxapi.isWXAppInstalled()){
            ToastUtil.INSTANCE.textToast(context, "您还没有安装微信,暂不支持此功能!", Toast.LENGTH_LONG, Gravity.CENTER);
            return;
        }
//        if(!iwxapi.isWXAppSupportAPI()){
//            ToastUtil.INSTANCE.textToast(context, "你安装的微信版本不支持此功能", Toast.LENGTH_LONG, Gravity.CENTER);
//            return;
//        }
        WXImageObject imgObj = new WXImageObject(bitmap);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        bitmap.recycle();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        iwxapi.sendReq(req);
    }

    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static InputStream getInputStream(String url){
        try {
            HttpURLConnection connection = (HttpURLConnection)(new URL(url)).openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200)
                return connection.getInputStream();
        }catch (Exception e){}
        return null;
    }

    public static void openWeChat(Context context){
        if(getIwxapi(context).isWXAppInstalled()){
            getIwxapi(context).openWXApp();
        }
        else{
            StringBuilder localStringBuilder = new StringBuilder().append("market://details?id=");
            localStringBuilder.append("com.tencent.mm");
            Uri localUri = Uri.parse(localStringBuilder.toString());
            Intent intent = new Intent("android.intent.action.VIEW", localUri);
            List<ResolveInfo> localList = context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
            if ((localList != null) && (localList.size() > 0)){
                context.startActivity(intent);
            }
        }
    }

    public interface WXShareListener {
        void onShareSuccess(BaseResp response);
        void onShareCancel(BaseResp response);
        void onShareFail(BaseResp response);
    }
}
