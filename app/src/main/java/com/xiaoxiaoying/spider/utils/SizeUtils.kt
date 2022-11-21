package com.xiaoxiaoying.spider.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager

import java.text.DecimalFormat
import kotlin.math.roundToInt

object SizeUtils {


    private fun screen(context: Context, isWidth: Boolean): Int {
        val wm = context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()

        wm.defaultDisplay.getMetrics(outMetrics)
        return if (isWidth) outMetrics.widthPixels else outMetrics.heightPixels
    }

    const val B: Long = 1024
    private const val KB = 1024 * B
    private const val MB = 1024 * KB
    private const val GB = 1024 * MB
    fun fileSize(size: Long): String {
        val var2 = DecimalFormat("###.00")
        return when {
            size < B -> "$size bytes"
            size < KB -> var2.format((size / B.toFloat()).toDouble()) + "KB"
            size < MB -> var2.format((size / KB.toFloat()).toDouble()) + "MB"
            size < GB -> var2.format((size / MB.toFloat()).toDouble()) + "GB"
            else -> var2.format((size / GB.toFloat()).toDouble()) + "TB"
        }
    }




    /**
     * 获得屏幕宽度
     *
     * @param
     * @return
     */
    @JvmStatic
    fun Context.getScreenWidth(): Int {
        return screen(this, true)
    }
    /**
     * 获取屏幕高度
     */
    @JvmStatic
    fun Context.getScreenHeight(): Int {
        return screen(this, false)
    }

    @JvmStatic
    fun Context.dipToPix(dip: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip.toFloat(),
            this.resources.displayMetrics
        )
            .toInt()
    }


    @JvmStatic
    fun Context.dipToPix(dip: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            this.resources.displayMetrics
        ).roundToInt()
    }

    @JvmStatic
    fun Context.dipToPixFloat(dip: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            this.resources.displayMetrics
        )
    }


    @JvmStatic
    fun Context.pixToDip(pix: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PX,
            pix.toFloat(),
            resources.displayMetrics
        ).toInt()
    }
}


