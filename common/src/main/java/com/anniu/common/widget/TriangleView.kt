package com.anniu.common.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.anniu.common.R
import java.lang.Exception

class TriangleView: View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init(context, attrs)
    }

    private var triangleWidth = 0
    private var triangleHeight = 0
    private var triangleColor = Color.parseColor("#000000")
    private var triangleDirection = "right"
    private lateinit var paint: Paint
    private var startPoint = PointF(0f, 0f)
    private var midPoint = PointF(0f, 0f)
    private var endPoint = PointF(0f, 0f)
    private var path:Path? = null

    private fun init(context: Context?, attrs: AttributeSet?){
        val a = context?.obtainStyledAttributes(attrs, R.styleable.TriangleView)
        val n = a?.indexCount?:0
        for (i in 0 until n) {
            val attr = a?.getIndex(i)
            when(attr){
                R.styleable.TriangleView_triangle_width -> {
                    triangleWidth = a.getDimension(attr, 0f).toInt()
                }
                R.styleable.TriangleView_triangle_height -> {
                    triangleHeight = a.getDimension(attr, 0f).toInt()
                }
                R.styleable.TriangleView_triangle_color -> {
                    triangleColor = a.getColor(attr, triangleColor)
                }
                R.styleable.TriangleView_triangle_direction -> {
                    triangleDirection = a.getString(attr)
                }
            }
        }
        a?.recycle()
        paint = Paint()
        paint.color = triangleColor
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 1f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(triangleWidth > 0 && triangleHeight > 0){
            path = Path()
            when(triangleDirection){
                "right" -> {
                    startPoint.set(0f, 0f)
                    midPoint.set(triangleWidth.toFloat(), triangleHeight/2f)
                    endPoint.set(0f, triangleHeight.toFloat())
                }
                "left" -> {
                    startPoint.set(triangleWidth.toFloat(), 0f)
                    midPoint.set(0f, triangleHeight/2f)
                    endPoint.set(triangleWidth.toFloat(), triangleHeight.toFloat())
                }
                "top" -> {
                    startPoint.set(0f, triangleHeight.toFloat())
                    midPoint.set(triangleWidth/2f, 0f)
                    endPoint.set(triangleWidth.toFloat(), triangleHeight.toFloat())
                }
                else -> {
                    startPoint.set(0f, 0f)
                    midPoint.set(triangleWidth/2f, triangleHeight.toFloat())
                    endPoint.set(triangleWidth.toFloat(), 0f)
                }
            }
            path?.moveTo(startPoint.x, startPoint.y)
            path?.lineTo(midPoint.x, midPoint.y)
            path?.lineTo(endPoint.x, endPoint.y)
            path?.lineTo(startPoint.x, startPoint.y)
            canvas?.drawPath(path, paint)
        }
        else{
            throw Exception("triangleWidth or triangleHeight can not be zero")
        }
    }

    fun setColor(color:Int){
        triangleColor = color
        paint.color = triangleColor
        postInvalidate()
    }
}