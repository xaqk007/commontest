package com.anniu.common.util;

import android.content.Context;

import java.io.File;

/**
 * Created by CN-11 on 2017/8/14.
 */

public class ClearWebViewCacheUtil {
    private static final String APP_CACAHE_DIRNAME = "/webcache";

    public static void clearWebViewCache(Context context){
        //清理Webview缓存数据库
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //WebView 缓存文件
        File appCacheDir = new File(context.getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME);
        File webviewCacheDir = new File(context.getCacheDir().getAbsolutePath()+"/webviewCache");
        //删除webview 缓存目录
        if(webviewCacheDir.exists()){
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if(appCacheDir.exists()){
            deleteFile(appCacheDir);
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    private static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }
}
