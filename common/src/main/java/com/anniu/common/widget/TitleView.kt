package com.anniu.common.widget

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.anniu.common.R
import com.anniu.common.base.BaseActivity
import com.anniu.common.util.ClickUtils
import kotlinx.android.synthetic.main.l_layout_title.view.*

/**
 * Created by alphabet on 2019/1/7.
 */
class TitleView : RelativeLayout {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.l_layout_title, this)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView)

        val strTitle = typedArray.getString(R.styleable.TitleView_title_text)
        tvTitle.text = strTitle
        val color = typedArray.getColor(R.styleable.TitleView_title_text_color, ContextCompat.getColor(context, R.color.black_087))
        tvTitle.setTextColor(color)

        val titleBg = typedArray.getColor(R.styleable.TitleView_title_bg, ContextCompat.getColor(context, R.color.colorPrimaryDark))
        titleRoot.setBackgroundColor(titleBg)

        val backDrawable = typedArray.getDrawable(R.styleable.TitleView_back_drawable)
        if (backDrawable != null)
            ivBack.setImageDrawable(backDrawable)
    }

    fun setOnBackFinishActivity(activity: BaseActivity) {
        ClickUtils.setNoFastClickListener(ivBack, {
            activity.finish()
        })
    }
}