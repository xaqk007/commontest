package com.anniu.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anniu.common.R;

/**
 * Created by CN-11 on 2017/8/17.
 */

public class CustomEditTextView extends RelativeLayout {
    private TextView errorText;
    private EditText etContent;
    private View errorLine;
    private Context context;
    private int errorColor, correctColor;
    private String hint;
    public CustomEditTextView(Context context) {
        this(context, null);
    }

    public CustomEditTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if(!isInEditMode()) {
            initView();
            initAttrs(attrs);
        }
    }

    private void initView(){
        View.inflate(context, R.layout.c_custom_edittextview, this);
        errorText = (TextView) findViewById(R.id.errorText);
        etContent = (EditText) findViewById(R.id.etContent);
        errorLine = findViewById(R.id.errorLine);
        errorColor = getResources().getColor(R.color.read_ff5252);
        correctColor = getResources().getColor(R.color.black_087);
    }

    private void initAttrs(AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomEditTextView);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++){
            int attr = a.getIndex(i);
            if(attr == R.styleable.CustomEditTextView_correctColor)
                correctColor = a.getInt(attr, errorColor);
            else if(attr == R.styleable.CustomEditTextView_errorColor)
                errorColor = a.getInt(attr, errorColor);
            else if(attr == R.styleable.CustomEditTextView_cet_hint)
                hint = a.getString(attr);
        }
        a.recycle();
        if(!TextUtils.isEmpty(hint))
            etContent.setHint(hint);
    }

    public void addTextWatcher(TextWatcher textWatcher){
        etContent.addTextChangedListener(textWatcher);
    }

    public void setTransformationMethod(TransformationMethod transformationMethod){
        etContent.setTransformationMethod(transformationMethod);
    }

    public void addOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener){
        etContent.setOnFocusChangeListener(onFocusChangeListener);
    }

    public void updateError(boolean error, String msg){
        if(error){
            errorText.setText(msg);
            etContent.setTextColor(errorColor);
            errorLine.setBackgroundColor(errorColor);
        }
        else {
            errorText.setText("");
            etContent.setTextColor(correctColor);
            errorLine.setBackgroundResource(R.color.black_012);
        }
    }

    public void setFilter(InputFilter[] filters){
        etContent.setFilters(filters);
    }

    public EditText getEtContent() {
        return etContent;
    }

    public void setMaxLength(int len){
        InputFilter.LengthFilter filter = new InputFilter.LengthFilter(len);
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = filter;
        setFilter(inputFilters);
    }
}
