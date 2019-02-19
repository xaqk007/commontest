package com.anniu.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.common.R;
import com.anniu.common.util.DensityUtil;

import static com.anniu.common.R.drawable.c_button_background_down;
import static com.anniu.common.R.drawable.c_button_background_up;

/**
 * Created by CN-11 on 2017/8/14.
 */

public class CustomButton extends RelativeLayout {
    private TextView tvBgDown, tvBgUp;
    private int nBgUp = c_button_background_up;
    private int nBgDown = c_button_background_down;
    private int textColor = Color.parseColor("#ffffff");
    private String buttonText;
    private View mCustomContainer;

    public CustomButton(Context context) {
        this(context, null);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode())
            initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs){
        View.inflate(context, R.layout.c_custom_button, this);
        tvBgUp = (TextView) this.findViewById(R.id.tvBgUp);
        tvBgDown = (TextView) this.findViewById(R.id.tvBgDown);
        mCustomContainer = this.findViewById(R.id.customContainer);
        initAttributes(context, attrs);
        if(!TextUtils.isEmpty(buttonText))
            tvBgUp.setText(buttonText);
        tvBgUp.setTextColor(textColor);
        updateBg(true, true);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    updateBg(true, false);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    updateBg(true, true);
                }
                return false;
            }
        });
    }

    private void initAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomButton);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++){
            int attr = a.getIndex(i);
            if(attr == R.styleable.CustomButton_drawable_up)
                nBgUp = a.getInt(attr, R.drawable.c_button_background_up);
            else if(attr == R.styleable.CustomButton_drawable_down)
                nBgDown = a.getInt(attr, R.drawable.c_button_background_down);
            else if(attr == R.styleable.CustomButton_button_text)
                buttonText = a.getString(attr);
            else if(attr == R.styleable.CustomButton_button_textcolor)
                textColor = a.getInt(attr, R.color.white);
        }
        a.recycle();
    }

    private void updateBg(boolean enabled, boolean up){
        setEnabled(enabled);
        if(enabled){
            if(up){
                tvBgUp.setBackgroundResource(nBgUp);
                tvBgDown.setBackgroundResource(R.drawable.button_shadow_normal);
            }
            else {
                tvBgUp.setBackgroundResource(nBgDown);
                tvBgDown.setBackgroundResource(R.drawable.button_shadow_press);
            }
        }
        else {
            tvBgUp.setBackgroundResource(R.drawable.c_button_background_unenable);
            tvBgDown.setBackgroundResource(R.color.black_000);
        }
    }

    public void setButtonEnabled(boolean enabled){
        if(enabled)
            updateBg(true, true);
        else
            updateBg(false, true);
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
        tvBgUp.setText(buttonText);
    }

    public void setButtonTextColor(int color){
        textColor = color;
        tvBgUp.setTextColor(textColor);
    }

    public void setnBgUp(int nBgUp) {
        this.nBgUp = nBgUp;
    }

    public void setnBgDown(int nBgDown) {
        this.nBgDown = nBgDown;
    }

    public void setwidth(int margin,Context context) {
        ViewGroup.LayoutParams layoutParams = mCustomContainer.getLayoutParams();
        layoutParams.width = DensityUtil.INSTANCE.getScreenWidth(context) - DensityUtil.INSTANCE.dip2px(context,32) * 2;
        mCustomContainer.setLayoutParams(layoutParams);
    }



}
