package com.anniu.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.anniu.common.util.DensityUtil;

/**
 * Created by CN-11 on 2017/9/4.
 */

public class ObservableScrollView extends ScrollView {
    private ScrollViewListener scrollViewListener = null;
    private ScrollViewStateListener scrollViewStateListener = null;
    private StretchListener stretchListener = null;
    private long delayMillis = 200;
    private long lastScrollUpdate = -1;
    private float savedY = 0f;
    private boolean toHeader = false;
    private int stretchDistance = 0;

    private Runnable scrollerTask = new Runnable() {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastScrollUpdate) > 100 && scrollViewStateListener != null && ((motionEvent != null && motionEvent.getAction() == MotionEvent.ACTION_UP) || motionEvent == null)) {
                lastScrollUpdate = -1;
                scrollViewStateListener.onScrollEnd();
            } else {
                postDelayed(this, delayMillis);
            }
        }
    };
    private MotionEvent motionEvent;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    public void setScrollViewStateListener(ScrollViewStateListener scrollViewStateListener) {
        this.scrollViewStateListener = scrollViewStateListener;
    }

    public StretchListener getStretchListener() {
        return stretchListener;
    }

    public void setStretchListener(StretchListener stretchListener) {
        this.stretchListener = stretchListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
            if(getScrollY() + getHeight() ==  computeVerticalScrollRange()){
                scrollViewListener.onScrollToBottom(getScrollY() -1);
            }
        }

        if (lastScrollUpdate == -1 && scrollViewStateListener != null) {
            scrollViewStateListener.onScrollStart();
            postDelayed(scrollerTask, delayMillis);
        }
        lastScrollUpdate = System.currentTimeMillis();
        if (scrollViewStateListener != null) {
            scrollViewStateListener.onScrollChange(y);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        motionEvent = ev;
        if(getStretchListener() != null){
            if(ev.getAction() == MotionEvent.ACTION_DOWN){
                if(getScrollY() == 0 && !toHeader){
                    toHeader = true;
                    savedY = ev.getRawY();
                }
            }
            else if(ev.getAction() == MotionEvent.ACTION_MOVE){
                if(getScrollY() == 0 && !toHeader){
                    toHeader = true;
                    savedY = ev.getRawY();
                }
                if(toHeader){
                    if(stretchDistance == 0)
                        stretchDistance = DensityUtil.INSTANCE.dip2px(getContext(), 96f);
                    float distance = ev.getRawY() - savedY;
                    if(distance < 0) {
                        distance = 0;
                    }
                    else if(distance > stretchDistance) {
                        distance = stretchDistance;
                    }
                    else{
                        distance = (float) (distance * (1 - Math.pow((stretchDistance - distance)/stretchDistance, 3)));
                    }
                    getStretchListener().onStretch(distance);
                    if(distance > 0)
                        return false;
                }
            }
            else if(ev.getAction() == MotionEvent.ACTION_CANCEL
                    || ev.getAction() == MotionEvent.ACTION_UP
                    || ev.getAction() == MotionEvent.ACTION_POINTER_UP){
                toHeader = false;
                savedY = 0;
                getStretchListener().onStretchEnd();
            }
        }
        return super.onTouchEvent(ev);
    }

    public interface ScrollViewListener {
        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);

        void onScrollToBottom(int bottom);
    }

    public interface ScrollViewStateListener {
        void onScrollStart();
        void onScrollEnd();
        void onScrollChange(int y);
    }

    public interface StretchListener {
        void onStretch(float diatance);
        void onStretchEnd();
    }
}