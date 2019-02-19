package com.anniu.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

import com.anniu.common.R;

/**
 * Created by CN-11 on 2017/9/14.
 */

public class CustomRadioButton extends AppCompatRadioButton {
    private int mDrawableSize;

    public CustomRadioButton(Context context) {
        super(context, null);
        init(context, null);
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);
    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        Drawable drawableLeft = null, drawableTop = null, drawableRight = null, drawableBottom = null;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomRadioButton);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if(attr == R.styleable.CustomRadioButton_crb_drawableSize)
                mDrawableSize = a.getDimensionPixelSize(attr, 50);
            else if(attr == R.styleable.CustomRadioButton_crb_drawableTop)
                drawableTop = a.getDrawable(attr);
            else if(attr == R.styleable.CustomRadioButton_crb_drawableBottom)
                drawableBottom = a.getDrawable(attr);
            else if(attr == R.styleable.CustomRadioButton_crb_drawableRight)
                drawableRight = a.getDrawable(attr);
            else if(attr == R.styleable.CustomRadioButton_crb_drawableLeft)
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
}
