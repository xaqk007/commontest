package com.anniu.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.anniu.common.R;


/**
 * Created by Administrator on 2017/12/27 0027.
 */

public class CustomTextView extends AppCompatTextView {
    private int mDrawableSize;
    private Drawable drawableLeft = null, drawableTop = null, drawableRight = null, drawableBottom = null;
    private Context mContext;

    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if(attr == R.styleable.CustomTextView_drawableSizeTv)
                mDrawableSize = a.getDimensionPixelSize(attr, 50);
            else if(attr == R.styleable.CustomTextView_drawableTopTv)
                drawableTop = a.getDrawable(attr);
            else if(attr == R.styleable.CustomTextView_drawableBottomTv)
                drawableBottom = a.getDrawable(attr);
            else if(attr == R.styleable.CustomTextView_drawableRightTv)
                drawableRight = a.getDrawable(attr);
            else if(attr == R.styleable.CustomTextView_drawableLeftTv)
                drawableLeft = a.getDrawable(attr);
        }
        a.recycle();
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        setClickable(true);
    }

    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (left != null) {
            left.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        if (right != null) {
            right.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        if (top != null) {
            top.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, mDrawableSize, mDrawableSize);
        }
        setCompoundDrawables(left, top, right, bottom);
    }
    float textWidth;
    int drawablePadding;
    int drawableWidth;
    float bodyWidth;
    int width;
    @Override
    protected void onDraw(Canvas canvas) {
        if (drawableLeft != null) {
            //取得字符串的宽度值
            if(textWidth == 0)
                textWidth = getPaint().measureText(getText().toString());
            //获取控件的内边距
            if(drawablePadding == 0)
                drawablePadding = getCompoundDrawablePadding();
            //返回图片呢的固有宽度,单位是DP
            if(drawableWidth == 0)
                drawableWidth = drawableLeft.getIntrinsicWidth();
            if(bodyWidth == 0)
                bodyWidth = textWidth + drawableWidth + drawablePadding;
            if(width == 0)
                width = getWidth();
            setPadding(0, 0, (int)(width - bodyWidth), 0);
//            canvas.translate((width - bodyWidth) / 2, 0);
        }
        super.onDraw(canvas);
    }

    public void setDrawableTop(int imgRes) {
        drawableTop = mContext.getResources().getDrawable(imgRes);
        setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
    }
}
