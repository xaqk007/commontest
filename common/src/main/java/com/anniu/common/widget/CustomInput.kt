package com.anniu.common.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.TransformationMethod
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import com.anniu.common.R
import kotlinx.android.synthetic.main.view_custom_input.view.*

/**
 * Created by duliang on 2018/5/18.
 */
class CustomInput : LinearLayout {
    private var input_icon:Drawable? = null
    private var input_icon_error:Drawable? = null
    private var textColor = ContextCompat.getColor(context, R.color.black_087)
    private var errorColor = ContextCompat.getColor(context, R.color.read_ff5252)
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(attrs)
    }

    private fun init(attrs:  AttributeSet?){
        View.inflate(context, R.layout.view_custom_input, this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomInput)
        val n = a.indexCount
        (0 until n).map {
            val attr = a.getIndex(it)
            when(attr){
                R.styleable.CustomInput_input_layout_height -> {
                    inputLayout.layoutParams.height = a.getDimension(attr, 48f).toInt()
                }
                R.styleable.CustomInput_input_padding -> {
                    val padding = a.getDimension(attr, 8f).toInt()
                    inputLayout.setPadding(padding, 0, padding, 0)
                    errorText.setPadding(padding, 0, padding, 0)
                }
                R.styleable.CustomInput_input_icon_size -> {
                    val size = a.getDimension(attr, 24f).toInt()
                    inputIcon.layoutParams.width = size
                    inputIcon.layoutParams.height = size
                }
                R.styleable.CustomInput_input_icon -> {
                    input_icon = a.getDrawable(attr)
                    inputIcon.setImageDrawable(input_icon)
                }
                R.styleable.CustomInput_input_icon_error -> {
                    input_icon_error = a.getDrawable(attr)
                }
                R.styleable.CustomInput_input_text_size -> {
                    inputView.textSize  = a.getDimension(attr, 16f)
                }
                R.styleable.CustomInput_input_text_color -> {
                    textColor = a.getColor(attr, textColor)
                    inputView.setTextColor(textColor)
                }
                R.styleable.CustomInput_input_text_color_hint -> {
                    inputView.setHintTextColor(a.getColor(attr, ContextCompat.getColor(context, R.color.black_026)))
                }
                R.styleable.CustomInput_error_text_size -> {
                    errorText.textSize = a.getDimension(attr, 12f)
                }
                R.styleable.CustomInput_error_color -> {
                    errorColor = a.getColor(attr, errorColor)
                    errorText.setTextColor(errorColor)
                }
                R.styleable.CustomInput_input_hint-> {
                    inputView.hint = (a.getString(attr))
                }
                R.styleable.CustomInput_ci_right_text-> {
                    val text = a.getString(attr)
                    if(!TextUtils.isEmpty(text)) {
                        rightText.text = text
                        rightText.visibility = View.VISIBLE
                    }
                    else
                        rightText.visibility = View.GONE
                }
                R.styleable.CustomInput_ci_right_text_size-> {
                    rightText.textSize = a.getDimension(attr, 16f)
                }
                R.styleable.CustomInput_ci_right_text_color-> {
                    rightText.setTextColor(a.getColor(attr, ContextCompat.getColor(context, R.color.black_087)))
                }
                R.styleable.CustomInput_ci_right_drawable-> {
                    val drawable = a.getDrawable(attr)
                    if(drawable != null) {
                        rightIcon.setImageDrawable(a.getDrawable(attr))
                        rightIcon.visibility = View.VISIBLE
                        inputView.isCursorVisible = false
                        inputView.isFocusable = false
                        inputView.isFocusableInTouchMode = false
                    }
                    else
                        rightIcon.visibility = View.GONE
                }
                R.styleable.CustomInput_ci_right_text_color-> {
                    rightIcon.layoutParams.width = a.getDimension(attr, 24f).toInt()
                    rightIcon.layoutParams.height = a.getDimension(attr, 24f).toInt()
                }
            }
        }
        a.recycle()
    }

    fun addTextWatcher(textWatcher: TextWatcher) {
        inputView.addTextChangedListener(textWatcher)
    }

    fun setTransformationMethod(transformationMethod: TransformationMethod) {
        inputView.transformationMethod = transformationMethod
    }

    fun addOnFocusChangeListener(onFocusChangeListener: View.OnFocusChangeListener) {
        inputView.onFocusChangeListener = onFocusChangeListener
    }

    fun setFilter(filters: Array<InputFilter>) {
        inputView.filters = filters
    }

    fun getInputContent(): EditText {
        return inputView
    }

    fun setMaxLength(len: Int) {
        val filter = InputFilter.LengthFilter(len)
        val inputFilters = arrayOf<InputFilter>(filter)
        setFilter(inputFilters)
    }

    fun updateError(error: Boolean, msg: String?) {
        if (error) {
            errorText.text = msg
            errorText.visibility = View.VISIBLE
            inputView.setTextColor(errorColor)
            bottomLine.setBackgroundColor(errorColor)
            inputIcon.setImageDrawable(input_icon_error)
        } else {
            errorText.text = ""
            errorText.visibility = View.GONE
            inputView.setTextColor(textColor)
            bottomLine.setBackgroundResource(com.anniu.common.R.color.black_006)
            inputIcon.setImageDrawable(input_icon)
        }
    }
}