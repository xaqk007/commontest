package com.anniu.common.util

import java.math.BigDecimal

/**
 * Created by duliang on 2018/8/23.
 */
object DecimalUtil {
    private val DEF_DIV_SCALE = 10

    fun add(d1: Double, d2: Double): Double {
        val b1 = BigDecimal(java.lang.Double.toString(d1))
        val b2 = BigDecimal(java.lang.Double.toString(d2))
        return b1.add(b2).toDouble()

    }

    fun sub(d1: Double, d2: Double): Double {
        val b1 = BigDecimal(java.lang.Double.toString(d1))
        val b2 = BigDecimal(java.lang.Double.toString(d2))
        return b1.subtract(b2).toDouble()

    }

    fun mul(d1: Double, d2: Double): Double {
        val b1 = BigDecimal(java.lang.Double.toString(d1))
        val b2 = BigDecimal(java.lang.Double.toString(d2))
        return b1.multiply(b2).toDouble()

    }

    @JvmOverloads
    fun div(d1: Double, d2: Double, scale: Int = DEF_DIV_SCALE): Double {
        if (scale < 0) {
            throw IllegalArgumentException("The scale must be a positive integer or zero")
        }
        val b1 = BigDecimal(java.lang.Double.toString(d1))
        val b2 = BigDecimal(java.lang.Double.toString(d2))
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).toDouble()

    }

    fun pow(d1: Double, n: Int): Double {
        val b1 = BigDecimal(java.lang.Double.toString(d1))
        return b1.pow(n).toDouble()
    }

    fun round(d1: Double, scale: Int): Double {
        var b1 = BigDecimal(java.lang.Double.toString(d1))
        b1 = b1.setScale(scale, BigDecimal.ROUND_HALF_EVEN)
        return b1.toDouble()
    }
}