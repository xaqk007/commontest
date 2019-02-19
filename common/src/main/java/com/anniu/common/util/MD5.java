package com.anniu.common.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by CN-11 on 2017/8/11.
 */

public class MD5 {
    public static String md5(String plainText) {
        String str;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
            return str;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F' };

    public static String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static String md5File(String filePath){
        InputStream is = null;
        byte[] buffer = new byte[1024];
        int len = 0;
        MessageDigest md5;
        try{
            is = new FileInputStream(filePath);
            md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            while((len = is.read(buffer)) > 0){
                md5.update(buffer, 0, len);
            }
            is.close();
            return toHexString(md5.digest());
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
