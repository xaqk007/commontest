package com.anniu.common.util.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.text.TextUtils
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by duliang on 2018/6/1.
 */
object BitmapUtils {
    fun compressImage(path: String, name: String?): File {
        var name = name
        // 获得图片的宽和高，并不把图片加载到内存中
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inSampleSize = caculateInSampleSize(options, 480, 800)
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(path, options)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var quality = 100
        while (baos.toByteArray().size / 1024 > 500) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
            baos.reset()//重置baos即清空baos
            quality -= 10
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)//这里压缩options%，把压缩后的数据存放到baos中
        }
        if (TextUtils.isEmpty(name)) {
            val format = SimpleDateFormat("yyyyMMddHHmmss")
            val date = Date(System.currentTimeMillis())
            name = format.format(date)
        }
        val file = File(Environment.getExternalStorageDirectory(), name!! + ".png")
        try {
            val fos = FileOutputStream(file)
            try {
                fos.write(baos.toByteArray())
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        recycleBitmap(bitmap)
        return file
    }

    fun compressImage(path: String): File {
        return compressImage(path, null)
    }

    private fun recycleBitmap(vararg bitmaps: Bitmap) {
        if (bitmaps == null) {
            return
        }
        for (bm in bitmaps) {
            if (null != bm && !bm.isRecycled) {
                bm.recycle()
            }
        }
    }

    fun caculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int,
                             reqHeight: Int): Int {
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1
        if (width > reqWidth || height > reqHeight) {
            val widthRadio = Math.round(width * 1.0f / reqWidth)
            val heightRadio = Math.round(height * 1.0f / reqHeight)
            inSampleSize = Math.max(widthRadio, heightRadio)
        }
        return inSampleSize
    }
}