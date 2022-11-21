package com.xiaoxiaoying.spider.widget.loading

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Paint
import java.util.ArrayList
import kotlin.math.cos
import kotlin.math.sin


class BallSpinFadeLoaderIndicator : Indicator() {
    companion object {
        const val SCALE = 1.0f
        const val ALPHA = 255
    }


    private var scaleFloats = floatArrayOf(SCALE, SCALE, SCALE, SCALE, SCALE, SCALE, SCALE, SCALE)

    private var alphas = intArrayOf(ALPHA, ALPHA, ALPHA, ALPHA, ALPHA, ALPHA, ALPHA, ALPHA)
    override fun draw(canvas: Canvas, paint: Paint) {

        val radius = getWidth() / 10f
        for (i in 0..7) {
            canvas.save()
            val point =
                circleAt(getWidth(), getHeight(), getWidth() / 2 - radius, i * (Math.PI / 4))
            canvas.translate(point.x.toFloat(), point.y.toFloat())
            canvas.scale(scaleFloats[i], scaleFloats[i])
            paint.alpha = alphas[i]
            canvas.drawCircle(0f, 0f, radius, paint)
            canvas.restore()
        }
    }

    override fun onCreateAnimators(): ArrayList<ValueAnimator> {

        val animators = ArrayList<ValueAnimator>()
        val delays = intArrayOf(0, 120, 240, 360, 480, 600, 720, 780, 840)
        for (i in 0..7) {
            val scaleAnim = ValueAnimator.ofFloat(1F, 0.4f, 1F)
            scaleAnim.duration = 1000
            scaleAnim.repeatCount = -1
            scaleAnim.startDelay = delays[i].toLong()
            addUpdateListener(scaleAnim, ValueAnimator.AnimatorUpdateListener { animation ->
                scaleFloats[i] = animation.animatedValue as Float
                postInvalidate()
            })

            val alphaAnim = ValueAnimator.ofInt(255, 77, 255)
            alphaAnim.duration = 1000
            alphaAnim.repeatCount = -1
            alphaAnim.startDelay = delays[i].toLong()
            addUpdateListener(alphaAnim) { animation ->
                alphas[i] = animation.animatedValue as Int
                postInvalidate()
            }
            animators.add(scaleAnim)
            animators.add(alphaAnim)
        }
        return animators

    }


    /**
     * 圆O的圆心为(a,b),半径为R,点A与到X轴的为角α.
     * 则点A的坐标为(a+R*cosα,b+R*sinα)
     * @param width
     * @param height
     * @param radius
     * @param angle
     * @return
     */
    private fun circleAt(width: Int, height: Int, radius: Float, angle: Double): Point {
        val x = (width / 2 + radius * cos(angle)).toFloat()
        val y = (height / 2 + radius * sin(angle)).toFloat()
        return Point(x, y)
    }

    internal inner class Point(var x: Float, var y: Float)

}