package com.anniu.common.util

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.ValueCallback

import java.io.File

/**
 * Created by CN-11 on 2017/9/15.
 */

object FileChooserUtil {
    val REQUEST_FOR_FILE_CHOOSER = 30001
    val REQUEST_FOR_FILE_CHOOSER_FOR_ANDROID_5 = 30002
    var mUploadMessage: ValueCallback<Uri>? = null
    var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    var mCameraFilePath: String? = null

    fun openFileChooser(context: Context, uploadMsg: ValueCallback<Uri>) {
        mUploadMessage = uploadMsg
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        val chooser = createChooserIntent(createCameraIntent())
        chooser.putExtra(Intent.EXTRA_INTENT, i)
        (context as Activity).startActivityForResult(chooser, REQUEST_FOR_FILE_CHOOSER)
    }

    private fun createChooserIntent(vararg intents: Intent): Intent {
        val chooser = Intent(Intent.ACTION_CHOOSER)
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents)
        chooser.putExtra(Intent.EXTRA_TITLE, "选择图片")
        return chooser
    }

    private fun createCameraIntent(): Intent {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val externalDataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val cameraDataDir = File(externalDataDir.absolutePath + File.separator + "didi")
        cameraDataDir.mkdirs()
        mCameraFilePath = cameraDataDir.absolutePath + File.separator + System.currentTimeMillis() + ".jpg"
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(mCameraFilePath!!)))
        return cameraIntent
    }

    fun showFileChooser(context: Context, filePathCallback: ValueCallback<Array<Uri>>) {
        mFilePathCallback = filePathCallback
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        val chooser = createChooserIntent(createCameraIntent())
        chooser.putExtra(Intent.EXTRA_INTENT, i)
        (context as Activity).startActivityForResult(chooser, REQUEST_FOR_FILE_CHOOSER_FOR_ANDROID_5)
    }

    fun onOpenFileChooserResult(context: Context, data: Intent?, resultCode: Int) {
        if (mUploadMessage != null) {
            var result: Uri? = null
            if (mCameraFilePath != null) {
                try {
                    result = getImageContentUri(context, File(mCameraFilePath!!))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mCameraFilePath = null
            }
            if (result == null) {
                result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            }
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        }
    }

    fun getImageContentUri(context: Context, imageFile: java.io.File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + "=? ",
                arrayOf(filePath), null)
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")
            return Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                return context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                return null
            }
        }
    }

    fun onShowFileChooserResult(context: Context, data: Intent?, resultCode: Int) {
        if (mFilePathCallback != null) {
            var result: Uri? = null
            if (mCameraFilePath != null) {
                try {
                    result = getImageContentUri(context, File(mCameraFilePath!!))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mCameraFilePath = null
            }
            if (result == null) {
                result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            }
            mFilePathCallback!!.onReceiveValue(if (result != null) arrayOf(result) else arrayOf())
            mFilePathCallback = null
        }
    }
}