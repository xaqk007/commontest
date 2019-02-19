package com.anniu.common.base

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.WindowManager
import com.anniu.common.R
import me.yokeyword.fragmentation.*
import me.yokeyword.fragmentation.anim.FragmentAnimator

/**
 * Created by duliang on 2018/9/11.
 */
abstract class BaseActivity : AppCompatActivity() , ISupportActivity, BaseCallBack{
    private val mDelegate = SupportActivityDelegate(this)
    private val presenterProvider = SimplePresenterProvider<BasePresenter>()

    fun addPresenter(presenter:BasePresenter):BasePresenter?{
        return presenterProvider.addPresenter(presenter)
    }

    fun addPresenter(name:String, presenter:BasePresenter):BasePresenter?{
        return presenterProvider.addPresenter(name, presenter)
    }

    fun <T : BasePresenter> getPresenter(clazz: Class<T>):T{
        return presenterProvider.getPresenter(clazz as Class<BasePresenter>) as T
    }

    override fun getSupportDelegate(): SupportActivityDelegate {
        return mDelegate
    }

    override fun onResume() {
        super.onResume()
        presenterProvider.takeView()
    }

    override fun onPause() {
        presenterProvider.dropView()
        super.onPause()
    }

    open fun initWindow(){}
    open fun loadFragment(){}
    open fun initViews(){}
    open fun setListeners(){}
    open fun loadData(){}
    open fun addPresenters(){}
    open fun initToolBar(){}

    abstract fun getLayoutId():Int

    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，添加SharedElement动画，操作非回退栈Fragment
     */
    override fun extraTransaction(): ExtraTransaction {
        return mDelegate.extraTransaction()
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDelegate.onCreate(savedInstanceState)
        initWindow()
        setContentView(getLayoutId())
        if(savedInstanceState == null){
            loadFragment()
        }
        initViews()
        initToolBar()
        addPresenters()
        presenterProvider.onCreate(savedInstanceState)
        loadData()
        setListeners()
    }

    override fun onPostCreate(@Nullable savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mDelegate.onPostCreate(savedInstanceState)
    }

    override fun onDestroy() {
        mDelegate.onDestroy()
        presenterProvider.onDestroy()
        super.onDestroy()
    }

    /**
     * Note： return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev)
    }

    /**
     * 不建议复写该方法,请使用 [.onBackPressedSupport] 代替
     */
    override fun onBackPressed() {
        mDelegate.onBackPressed()
    }

    /**
     * 该方法回调时机为,Activity回退栈内Fragment的数量 小于等于1 时,默认finish Activity
     * 请尽量复写该方法,避免复写onBackPress(),以保证SupportFragment内的onBackPressedSupport()回退事件正常执行
     */
    override fun onBackPressedSupport() {
        mDelegate.onBackPressedSupport()
    }

    /**
     * 获取设置的全局动画 copy
     *
     * @return FragmentAnimator
     */
    override fun getFragmentAnimator(): FragmentAnimator {
        return mDelegate.fragmentAnimator
    }

    /**
     * Set all fragments animation.
     * 设置Fragment内的全局动画
     */
    override fun setFragmentAnimator(fragmentAnimator: FragmentAnimator) {
        mDelegate.fragmentAnimator = fragmentAnimator
    }

    /**
     * Set all fragments animation.
     * 构建Fragment转场动画
     *
     *
     * 如果是在Activity内实现,则构建的是Activity内所有Fragment的转场动画,
     * 如果是在Fragment内实现,则构建的是该Fragment的转场动画,此时优先级 > Activity的onCreateFragmentAnimator()
     *
     * @return FragmentAnimator对象
     */
    override fun onCreateFragmentAnimator(): FragmentAnimator {
        return mDelegate.onCreateFragmentAnimator()
    }

    override fun post(runnable: Runnable) {
        mDelegate.post(runnable)
    }

    /****************************************以下为可选方法(Optional methods)******************************************************/

    /**
     * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
     *
     * @param containerId 容器id
     * @param toFragment  目标Fragment
     */
    fun loadRootFragment(containerId: Int, toFragment: ISupportFragment) {
        mDelegate.loadRootFragment(containerId, toFragment)
    }

    fun loadRootFragment(containerId: Int, toFragment: ISupportFragment, addToBackStack: Boolean, allowAnimation: Boolean) {
        mDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnimation)
    }

    /**
     * 加载多个同级根Fragment,类似Wechat, QQ主页的场景
     */
    fun loadMultipleRootFragment(containerId: Int, showPosition: Int, vararg toFragments: ISupportFragment) {
        mDelegate.loadMultipleRootFragment(containerId, showPosition, *toFragments)
    }

    /**
     * show一个Fragment,hide其他同栈所有Fragment
     * 使用该方法时，要确保同级栈内无多余的Fragment,(只有通过loadMultipleRootFragment()载入的Fragment)
     *
     *
     * 建议使用更明确的[.showHideFragment]
     *
     * @param showFragment 需要show的Fragment
     */
    fun showHideFragment(showFragment: ISupportFragment) {
        mDelegate.showHideFragment(showFragment)
    }

    /**
     * show一个Fragment,hide一个Fragment ; 主要用于类似微信主页那种 切换tab的情况
     */
    fun showHideFragment(showFragment: ISupportFragment, hideFragment: ISupportFragment) {
        mDelegate.showHideFragment(showFragment, hideFragment)
    }

    /**
     * It is recommended to use [SupportFragment.start].
     */
    fun start(toFragment: ISupportFragment) {
        mDelegate.start(toFragment)
    }

    /**
     * It is recommended to use [SupportFragment.start].
     *
     * @param launchMode Similar to Activity's LaunchMode.
     */
    fun start(toFragment: ISupportFragment, @ISupportFragment.LaunchMode launchMode: Int) {
        mDelegate.start(toFragment, launchMode)
    }

    /**
     * It is recommended to use [SupportFragment.startForResult].
     * Launch an fragment for which you would like a result when it poped.
     */
    fun startForResult(toFragment: ISupportFragment, requestCode: Int) {
        mDelegate.startForResult(toFragment, requestCode)
    }

    /**
     * It is recommended to use [SupportFragment.startWithPop].
     * Start the target Fragment and pop itself
     */
    fun startWithPop(toFragment: ISupportFragment) {
        mDelegate.startWithPop(toFragment)
    }

    /**
     * It is recommended to use [SupportFragment.startWithPopTo].
     *
     * @see .popTo
     * @see .start
     */
    fun startWithPopTo(toFragment: ISupportFragment, targetFragmentClass: Class<*>, includeTargetFragment: Boolean) {
        mDelegate.startWithPopTo(toFragment, targetFragmentClass, includeTargetFragment)
    }

    /**
     * It is recommended to use [SupportFragment.replaceFragment].
     */
    fun replaceFragment(toFragment: ISupportFragment, addToBackStack: Boolean) {
        mDelegate.replaceFragment(toFragment, addToBackStack)
    }

    /**
     * Pop the fragment.
     */
    fun pop() {
        mDelegate.pop()
    }

    /**
     * Pop the last fragment transition from the manager's fragment
     * back stack.
     *
     *
     * 出栈到目标fragment
     *
     * @param targetFragmentClass   目标fragment
     * @param includeTargetFragment 是否包含该fragment
     */
    fun popTo(targetFragmentClass: Class<*>, includeTargetFragment: Boolean) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment)
    }

    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 如果你想在出栈后, 立刻进行FragmentTransaction操作，请使用该方法
     */
    fun popTo(targetFragmentClass: Class<*>, includeTargetFragment: Boolean, afterPopTransactionRunnable: Runnable) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable)
    }

    fun popTo(targetFragmentClass: Class<*>, includeTargetFragment: Boolean, afterPopTransactionRunnable: Runnable, popAnim: Int) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim)
    }

    /**
     * 当Fragment根布局 没有 设定background属性时,
     * Fragmentation默认使用Theme的android:windowbackground作为Fragment的背景,
     * 可以通过该方法改变其内所有Fragment的默认背景。
     */
    fun setDefaultFragmentBackground(@DrawableRes backgroundRes: Int) {
        mDelegate.defaultFragmentBackground = backgroundRes
    }

    /**
     * 得到位于栈顶Fragment
     */
    fun getTopFragment(): ISupportFragment {
        return SupportHelper.getTopFragment(supportFragmentManager)
    }

    /**
     * 获取栈内的fragment对象
     */
    fun <T : ISupportFragment> findFragment(fragmentClass: Class<T>): T {
        return SupportHelper.findFragment(supportFragmentManager, fragmentClass)
    }

    private var progressDialog: ProgressDialog? = null

    fun showProgressDialog(show:Boolean, message:String, cancelable:Boolean, finishOnCancel:Boolean){
        progressDialog = progressDialog?: ProgressDialog(this)
        progressDialog?.apply{
            setTheme(R.style.AlertDialog)
            setMessage(message)
            setCancelable(cancelable)
            setCanceledOnTouchOutside(cancelable)
            setOnCancelListener {
                if(finishOnCancel)
                    finish()
            }
        }
        if(show)
            progressDialog?.show()
        else
            progressDialog?.dismiss()
    }

    open fun getCurrentPageName():String?{
        return null
    }

    open fun getPrePageName():String?{
        return null
    }

    fun setTranslucentStatus() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            val window = window
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    fun setWindowAlpha(alpha: Float) {
        val lp = window.attributes
        lp.alpha = alpha
        window.attributes = lp
    }
}