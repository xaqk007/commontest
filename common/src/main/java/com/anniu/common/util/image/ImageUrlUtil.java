package com.anniu.common.util.image;

import android.net.Uri;

import com.anniu.common.constants.UrlConstants;

/**
 * Created by CN-11 on 2017/8/25.
 */

public class ImageUrlUtil {
    public static Uri getUriFromUrl(String url){
        if(url != null){
            if(!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("HTTP://") && !url.startsWith("HTTPS://")){
                if(!url.startsWith("/")){
                    url = UrlConstants.SERVER_URL + url;
                }
                else {
                    try{
                        url = UrlConstants.SERVER_URL + url.substring(1);
                    }catch (Exception e){}
                }
            }
        }
        Uri uri = null;
        try{
            uri = Uri.parse(url);
        }catch (Exception e){}
        return uri;
    }
}