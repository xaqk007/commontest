package com.anniu.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.anniu.common.R
import com.anniu.common.util.DensityUtil

/**
 * Created by alphabet on 2017/12/1.
 */

class RvDecoration : RecyclerView.ItemDecoration {
    private var mDividerPaint: Paint? = null
    private var mDividerHeight: Int = 0
    private var mDivideMarginLeft: Int = 0
    private var mDividerMarginRight: Int = 0
    private var mFoodCount: Int = 0
    private val mBackgrountPaint: Paint

    constructor(context: Context, divideMarginLeft: Int, dividerMarginRight: Int, footCount: Int = 0,dividerColorRes: Int = R.color.black_004,bgColorRes: Int = R.color.white) {
        mFoodCount = footCount
        mDivideMarginLeft = DensityUtil.dip2px(context, divideMarginLeft.toFloat())
        mDividerMarginRight = DensityUtil.dip2px(context, dividerMarginRight.toFloat())
        mDividerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgrountPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgrountPaint.color = ContextCompat.getColor(context,bgColorRes)
        mDividerPaint!!.color = ContextCompat.getColor(context,dividerColorRes)
        mDividerHeight = DensityUtil.dip2px(context, 1f)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = mDividerHeight
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        val right = parent.width - parent.paddingRight - mDividerMarginRight
        val left = parent.paddingLeft + mDivideMarginLeft
        if (mFoodCount > 0) {
            for (i in 0 until childCount - 1 - mFoodCount) {
                val view = parent.getChildAt(i)
                val top = view.bottom.toFloat()
                val bottom = (view.bottom + mDividerHeight).toFloat()
                c.drawRect(left.toFloat(), top, right.toFloat(), bottom, mDividerPaint!!)
                if (mDivideMarginLeft != 0)
                    c.drawRect(parent.paddingLeft.toFloat(), top, left.toFloat(), bottom, mBackgrountPaint)
                if (mDividerMarginRight != 0)
                    c.drawRect(right.toFloat(), top, (parent.width - parent.paddingRight).toFloat(), bottom, mBackgrountPaint)
            }
        } else {
            for (i in 0 until childCount - 1) {
                val view = parent.getChildAt(i)
                val top = view.bottom.toFloat()
                val bottom = (view.bottom + mDividerHeight).toFloat()
                c.drawRect(left.toFloat(), top, right.toFloat(), bottom, mDividerPaint!!)
            }
        }
    }

    fun setDividerHeight(dividerHeight: Int) {
        mDividerHeight = dividerHeight
    }

    fun setDividerColor(dividerColor: Int) {
        mDividerPaint!!.color = dividerColor
    }

}
