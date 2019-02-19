package com.anniu.common.base

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.TextView
import com.anniu.common.BuildConfig
import com.anniu.common.R
import com.anniu.common.util.*
import com.anniu.common.util.toast.ToastUtil
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import kotlinx.android.synthetic.main.c_activity_webview.*
import kotlinx.android.synthetic.main.l_layout_title.*

/**
 * Created by duliang on 2018/10/10.
 */
open class BaseWebViewActivity : BaseActivity() {
    var mUrl: String? = null
    var needClearHistory = false
    var finishOnBack = false
    override fun getLayoutId(): Int {
        return R.layout.c_activity_webview
    }

    override fun initViews() {
        super.initViews()
        initWebview()
    }

    override fun loadData() {
        super.loadData()
        mUrl = intent.getStringExtra("url")
        onPreLoadUrl()
        webview.loadUrl(mUrl)
    }

    open fun onPreLoadUrl(){}

    override fun setListeners() {
        super.setListeners()
        ClickUtils.setNoFastClickListener(ivBack) {
            if(finishOnBack){
                finish()
                return@setNoFastClickListener
            }
            if(webview.canGoBack()){
                webview.goBack()
                onWebViewBack()
            }
            else{
                onWebViewQuit()
            }
        }
    }

    open fun onWebViewBack(){

    }

    open fun onWebViewQuit(){
        finish()
    }

    private fun initWebview() {
        webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        val webSettings = webview.settings
        try {
            webSettings.javaScriptEnabled = true
        } catch (e: Exception) {}
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.builtInZoomControls = true
        webSettings.setSupportZoom(true)
        webSettings.displayZoomControls = false
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.domStorageEnabled = true
        webSettings.setAppCacheEnabled(true)
        val appCachePath = applicationContext.cacheDir.absolutePath
        if (!TextUtils.isEmpty(appCachePath))
            webSettings.setAppCachePath(appCachePath)
        webSettings.allowFileAccess = true
        webSettings.domStorageEnabled = true
        webSettings.setGeolocationEnabled(true)
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            webSettings.allowFileAccessFromFileURLs = true
            webSettings.allowUniversalAccessFromFileURLs = true
        }
        if (Build.VERSION.SDK_INT >= 19 && BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        if (Build.VERSION.SDK_INT >= 21) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        webview.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
                callback?.invoke(origin, true, false)
            }

            fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                this.openFileChooser(uploadMsg, "*/*")
            }

            fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String) {
                this.openFileChooser(uploadMsg, acceptType, null)
            }

            fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String?) {
                AndPermission.with(this@BaseWebViewActivity)
                        .permission(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE))
                        .callback(object : PermissionListener {
                            override fun onSucceed(requestCode: Int, grantPermissions: List<String>) {
                                FileChooserUtil.openFileChooser(this@BaseWebViewActivity, uploadMsg)
                            }

                            override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {

                            }
                        }).start()
            }

            override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
                AndPermission.with(this@BaseWebViewActivity)
                        .permission(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE))
                        .callback(object : PermissionListener {
                            override fun onSucceed(requestCode: Int, grantPermissions: List<String>) {
                                FileChooserUtil.showFileChooser(this@BaseWebViewActivity, filePathCallback)
                            }

                            override fun onFailed(requestCode: Int, deniedPermissions: List<String>) {

                            }
                        }).start()
                return true
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                pbWebview.progress = newProgress
                super.onProgressChanged(view, newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                var title = title
                if (!TextUtils.isEmpty(title)) {
                    if ((title?.length?:0) > 8)
                        title = title!!.substring(0, 8) + "..."
                    tvTitle.text = title?:""
                }
                super.onReceivedTitle(view, title)
            }
        }
        webview.webViewClient = object : WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                this@BaseWebViewActivity.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                this@BaseWebViewActivity.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if(this@BaseWebViewActivity.shouldOverrideUrlLoading(view, url))
                    return true
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                this@BaseWebViewActivity.onLoadResource(webview, url)
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
                if (needClearHistory)
                    webview.clearHistory()
            }
        }
        webview.setDownloadListener{ url, userAgent, contentDisposition, mimetype, contentLength ->
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: Exception) {}
        }
    }

    open fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?){}

    open fun onPageFinished(view: WebView?, url: String?){}

    open fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean{
        val scheme = Uri.parse(url).scheme
        if(scheme == "tmast"){
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (exception: ActivityNotFoundException) {
                exception.printStackTrace()
                ToastUtil.textToast(this@BaseWebViewActivity, "请先下载安装应用宝")
            } catch (e: Exception) {
                e.printStackTrace()
                ToastUtil.textToast(this@BaseWebViewActivity, "请先下载安装应用宝")
            }
            return true
        }
        if(scheme == "alipays" || scheme == "weixin"){//支付宝支付或微信支付
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (notFoundEx: ActivityNotFoundException) {//尝试h5网页支付
            }
            return true
        }
        return false
    }

    open fun onLoadResource(view: WebView?, url: String?){}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            FileChooserUtil.REQUEST_FOR_FILE_CHOOSER -> {
                FileChooserUtil.onOpenFileChooserResult(this@BaseWebViewActivity, data, resultCode)
            }
            FileChooserUtil.REQUEST_FOR_FILE_CHOOSER_FOR_ANDROID_5 -> {
                FileChooserUtil.onShowFileChooserResult(this@BaseWebViewActivity, data, resultCode)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webview.canGoBack()) {
                webview.goBack()
                onWebViewBack()
            } else
                onWebViewQuit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun getWebView():WebView{
        return webview
    }

    fun getTitleView(): TextView {
        return tvTitle
    }
}