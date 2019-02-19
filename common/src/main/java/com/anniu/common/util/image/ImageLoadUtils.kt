package com.anniu.common.util.image

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions

import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.common.internal.Supplier
import com.facebook.common.logging.FLog
import com.facebook.common.memory.MemoryTrimType
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry
import com.facebook.common.util.ByteConstants
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.facebook.imagepipeline.cache.MemoryCacheParams
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig
import com.facebook.imagepipeline.image.ImmutableQualityInfo
import com.facebook.imagepipeline.image.QualityInfo
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.listener.RequestLoggingListener

import java.util.HashSet

import okhttp3.OkHttpClient
import java.security.MessageDigest

/**
 * Created by CN-11 on 2017/8/21.
 */

object ImageLoadUtils {
    private val IMAGE_PIPELINE_CACHE_DIR = "image_cache"
    private val IMAGE_PIPELINE_SMALL_CACHE_DIR = "image_small_cache"
    private val MAX_DISK_SMALL_CACHE_SIZE = 10 * ByteConstants.MB
    private val MAX_DISK_SMALL_ONLOWDISKSPACE_CACHE_SIZE = 5 * ByteConstants.MB
    private var sImagePipelineConfig: ImagePipelineConfig? = null
    fun getImagePipelineConfig(context: Context): ImagePipelineConfig {
        if (sImagePipelineConfig == null) {
            val fileCacheDir = context.applicationContext.cacheDir
            val mainDiskCacheConfig = DiskCacheConfig.newBuilder(context)
                    .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                    .setBaseDirectoryPath(fileCacheDir)
                    .build()
            val smallDiskCacheConfig = DiskCacheConfig.newBuilder(context)
                    .setBaseDirectoryPath(fileCacheDir)
                    .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)
                    .setMaxCacheSize(MAX_DISK_SMALL_CACHE_SIZE.toLong())
                    .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_SMALL_ONLOWDISKSPACE_CACHE_SIZE.toLong())
                    .build()
            FLog.setMinimumLoggingLevel(FLog.ERROR)
            val requestListeners = HashSet<RequestListener>()
            requestListeners.add(RequestLoggingListener())
            // 当内存紧张时采取的措施
            val memoryTrimmableRegistry = NoOpMemoryTrimmableRegistry.getInstance()
            memoryTrimmableRegistry.registerMemoryTrimmable { trimType ->
                val suggestedTrimRatio = trimType.suggestedTrimRatio
                //                    MLog.i(String.format("Fresco onCreate suggestedTrimRatio : %d", suggestedTrimRatio));
                if (MemoryTrimType.OnCloseToDalvikHeapLimit.suggestedTrimRatio == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.suggestedTrimRatio == suggestedTrimRatio
                        || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.suggestedTrimRatio == suggestedTrimRatio) {
                    // 清除内存缓存
                    Fresco.getImagePipeline().clearMemoryCaches()
                }
            }
            val okHttpClient = OkHttpClient.Builder()
            sImagePipelineConfig = OkHttpImagePipelineConfigFactory.newBuilder(context, okHttpClient.build())
                    .setBitmapsConfig(Bitmap.Config.RGB_565) // 若不是要求忒高清显示应用，就用使用RGB_565吧（默认是ARGB_8888)
                    .setDownsampleEnabled(true) // 在解码时改变图片的大小，支持PNG、JPG以及WEBP格式的图片，与ResizeOptions配合使用
                    // 设置Jpeg格式的图片支持渐进式显示
                    .setProgressiveJpegConfig(object : ProgressiveJpegConfig {
                        override fun getNextScanNumberToDecode(scanNumber: Int): Int {
                            return scanNumber + 2
                        }

                        override fun getQualityInfo(scanNumber: Int): QualityInfo {
                            val isGoodEnough = scanNumber >= 5
                            return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false)
                        }
                    })
                    .setRequestListeners(requestListeners)
                    .setMemoryTrimmableRegistry(memoryTrimmableRegistry) // 报内存警告时的监听
                    // 设置内存配置
                    .setBitmapMemoryCacheParamsSupplier(BitmapMemoryCacheParamsSupplier(
                            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager))
                    .setMainDiskCacheConfig(mainDiskCacheConfig) // 设置主磁盘配置
                    .setSmallImageDiskCacheConfig(smallDiskCacheConfig) // 设置小图的磁盘配置
                    .build()
        }
        return sImagePipelineConfig!!
    }

    class BitmapMemoryCacheParamsSupplier(private val mActivityManager: ActivityManager) : Supplier<MemoryCacheParams> {

        private// We don't want to use more ashmem on Gingerbread for now, since it doesn't respond well to
                // native memory pressure (doesn't throw exceptions, crashes app, crashes phone)
        val maxCacheSize: Int
            get() {
                val maxMemory = Math.min(mActivityManager.memoryClass * ByteConstants.MB, Integer.MAX_VALUE)
                return if (maxMemory < 32 * ByteConstants.MB) {
                    4 * ByteConstants.MB
                } else if (maxMemory < 64 * ByteConstants.MB) {
                    6 * ByteConstants.MB
                } else {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                        8 * ByteConstants.MB
                    } else {
                        maxMemory / 4
                    }
                }
            }

        override fun get(): MemoryCacheParams {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                MemoryCacheParams(maxCacheSize, // 内存缓存中总图片的最大大小,以字节为单位。
                        56, // 内存缓存中图片的最大数量。
                        Integer.MAX_VALUE, // 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                        Integer.MAX_VALUE, // 内存缓存中准备清除的总图片的最大数量。
                        Integer.MAX_VALUE)                     // 内存缓存中单个图片的最大大小。
            } else {
                MemoryCacheParams(
                        maxCacheSize,
                        256,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE,
                        Integer.MAX_VALUE)
            }
        }

    }

    fun setImage(context: Context, imageView: ImageView, imageId:Int){
        setImage(context, imageView, imageId, null)
    }

    fun setImage(context: Context, imageView: ImageView, imageId:Int, defaultId:Int?){
        var requestOptions = RequestOptions()
        if(defaultId != null)
            requestOptions.placeholder(defaultId!!)
        Glide.with(context).load(imageId).apply(requestOptions).into(imageView)
    }

    fun setGif(context: Context, imageView: ImageView, imageId:Int, defaultId:Int?){
        var requestOptions = RequestOptions()
        if(defaultId != null)
            requestOptions.placeholder(defaultId!!)
        Glide.with(context).asGif().load(imageId).apply(requestOptions).into(imageView)
    }

    fun setImage(activity: Activity, imageView: ImageView, imageId:Int){
        setImage(activity, imageView, imageId, null)
    }

    fun setImage(activity: Activity, imageView: ImageView, imageId:Int, defaultId:Int?){
        var requestOptions = RequestOptions()
        if(defaultId != null)
            requestOptions.placeholder(defaultId!!)
        Glide.with(activity).load(imageId).apply(requestOptions).into(imageView)
    }

    fun setImage(frgment: Fragment, imageView: ImageView, imageId:Int){
        setImage(frgment, imageView, imageId, null)
    }

    fun setImage(frgment: Fragment, imageView: ImageView, imageId:Int, defaultId:Int?){
        var requestOptions = RequestOptions()
        if(defaultId != null)
            requestOptions.placeholder(defaultId!!)
        Glide.with(frgment).load(imageId).apply(requestOptions).into(imageView)
    }



    fun setImage(context: Context, imageView: ImageView, url:String?){
        setImage(context, imageView, url, null)
    }

    fun setImage(context: Context, imageView: ImageView, url:String?, defaultId:Int?){
        setImage(context, imageView, url, defaultId, null)
    }

    fun setImage(context: Context, imageView: ImageView, url:String?, defaultId:Int?, radius:Float?){
        var requestOptions = RequestOptions()
        if(defaultId != null)
            requestOptions.placeholder(defaultId!!)
        if(radius != null){
            requestOptions.transform(RoundTransformatio(context, radius))
        }
        Glide.with(context).load(url).apply(requestOptions).into(imageView)
    }

    fun setImage(activity: Activity, imageView: ImageView, url:String?){
        setImage(activity, imageView, url, null)
    }

    fun setImage(activity: Activity, imageView: ImageView, url:String?, defaultId:Int?){
        var requestOptions = RequestOptions()
        if(defaultId != null)
            requestOptions.placeholder(defaultId!!)
        Glide.with(activity).load(url).apply(requestOptions).into(imageView)
    }

    fun setImage(frgment: Fragment, imageView: ImageView, url:String?){
        setImage(frgment, imageView, url, null)
    }

    fun setImage(frgment: Fragment, imageView: ImageView, url:String?, defaultId:Int?){
        var requestOptions = RequestOptions()
        if(defaultId != null)
            requestOptions.placeholder(defaultId!!)
        Glide.with(frgment).load(url).apply(requestOptions).into(imageView)
    }

    class RoundTransformatio(val context: Context, val radius: Float) : BitmapTransformation() {
        override fun updateDiskCacheKey(messageDigest: MessageDigest) {

        }

        override fun transform(@Nullable pool: BitmapPool, @Nullable toTransform:Bitmap, outWidth:Int, outHeight:Int):Bitmap? {
            return roundCrop(pool, toTransform)
        }

        private fun roundCrop(pool: BitmapPool?, source:Bitmap?):Bitmap? {
            if (source == null)
                return null
            var result = pool?.get(source.width, source.height, Bitmap.Config.ARGB_8888)
            if (result == null) {
                result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888);
            }

            val canvas = Canvas(result)
            val paint = Paint()
            paint.shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.isAntiAlias = true
            val rectF = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
            canvas.drawRoundRect(rectF, radius, radius, paint)
            return result
        }
    }
}