package com.anniu.common.util.image

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.View
import com.anniu.common.R
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.PermissionListener

/**
 * Created by duliang on 2018/6/1.
 */
object ImagePickerUtil {
    fun show(context: Context, fragment: Fragment?) {
        AndPermission.with(context)
                .requestCode(100)
                .permission(Permission.CAMERA, Permission.STORAGE)
                .rationale{ _, rationale ->
                    AndPermission.rationaleDialog(context, rationale).show()
                }
                .callback(object : PermissionListener {
                    override fun onSucceed(requestCode: Int, grantPermissions: List<String>) {
                        val dialog = BottomSheetDialog(context)
                        val dialogView = View.inflate(context, R.layout.c_dialog_image_picker, null)
                        dialog.setContentView(dialogView)
                        dialogView.findViewById<View>(R.id.tvTakePhoto).setOnClickListener {
                            dialog.dismiss()
                            if (fragment == null) {
                                PictureSelector.create(context as Activity)
                                        .openCamera(PictureMimeType.ofImage())
//                                        .enableCrop(true)
//                                        .withAspectRatio(1,1)
                                        .compress(true)
                                        .minimumCompressSize(500)
                                        .rotateEnabled(false)
                                        .scaleEnabled(true)
                                        .forResult(PictureConfig.CHOOSE_REQUEST)
                            } else {
                                PictureSelector.create(fragment)
                                        .openCamera(PictureMimeType.ofImage())
//                                        .enableCrop(true)
//                                        .withAspectRatio(1,1)
                                        .compress(true)
                                        .minimumCompressSize(500)
                                        .rotateEnabled(false)
                                        .scaleEnabled(true)
                                        .forResult(PictureConfig.CHOOSE_REQUEST)
                            }
                        }
                        dialogView.findViewById<View>(R.id.tvImg).setOnClickListener {
                            dialog.dismiss()
                            if (fragment == null) {
                                PictureSelector.create(context as Activity)
                                        .openGallery(PictureMimeType.ofImage())
                                        .selectionMode(PictureConfig.SINGLE)
//                                        .enableCrop(true)
//                                        .withAspectRatio(1,1)
                                        .compress(true)
                                        .minimumCompressSize(500)
                                        .rotateEnabled(false)
                                        .scaleEnabled(true)
                                        .forResult(PictureConfig.CHOOSE_REQUEST)
                            } else {
                                PictureSelector.create(fragment)
                                        .openGallery(PictureMimeType.ofImage())
                                        .selectionMode(PictureConfig.SINGLE)
//                                        .enableCrop(true)
//                                        .withAspectRatio(1,1)
                                        .compress(true)
                                        .minimumCompressSize(500)
                                        .rotateEnabled(false)
                                        .scaleEnabled(true)
                                        .forResult(PictureConfig.CHOOSE_REQUEST)
                            }
                        }
                        dialogView.findViewById<View>(R.id.tvCancel).setOnClickListener { dialog.dismiss() }
                        dialog.show()
                    }

                    override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {
                    }
                }).start()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, listener: ImageUploadListener?) {
        when(requestCode){
            PictureConfig.CHOOSE_REQUEST -> {
                if(resultCode == Activity.RESULT_OK){
                    val  list = PictureSelector.obtainMultipleResult(data)
                    list?.get(0)?.apply {
                        if(!TextUtils.isEmpty(compressPath))
                            listener?.uploadImageFile(compressPath)
                        else if(!TextUtils.isEmpty(path))
                            listener?.uploadImageFile(path)
                    }
                }
            }
        }
    }
}