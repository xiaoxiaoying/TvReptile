package com.xiaoxiaoying.spider.widget.loading

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import java.util.ArrayList
import java.util.HashMap

abstract class Indicator : Drawable(), Animatable {
    private val mPaint = Paint()
    var mDrawBounds = Rect()
    private var mAnimators: ArrayList<ValueAnimator>? = null
    private var mHasAnimators = false
    private val mUpdateListeners = HashMap<ValueAnimator, ValueAnimator.AnimatorUpdateListener>()
    private var mAlpha = 255
    init {
        mPaint.color = Color.WHITE
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }

    fun getColor() = mPaint.color

    override fun getOpacity(): Int = PixelFormat.OPAQUE

    override fun draw(canvas: Canvas) {
        draw(canvas, mPaint)
    }

    abstract fun draw(canvas: Canvas, paint: Paint)

    abstract fun onCreateAnimators(): ArrayList<ValueAnimator>

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        setDrawBounds(bounds)
    }

    fun setDrawBounds(drawBounds: Rect) {
        setDrawBounds(drawBounds.left, drawBounds.top, drawBounds.right, drawBounds.bottom)
    }

    fun setDrawBounds(left: Int, top: Int, right: Int, bottom: Int) {
        this.mDrawBounds = Rect(left, top, right, bottom)
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getAlpha(): Int = this.mAlpha

    override fun setAlpha(alpha: Int) {
        this.mAlpha = alpha
    }
    fun postInvalidate() {
        invalidateSelf()
    }

    fun getDrawBounds(): Rect {
        return mDrawBounds
    }

    fun getWidth(): Int {
        return mDrawBounds.width()
    }

    fun getHeight(): Int {
        return mDrawBounds.height()
    }

    fun centerX(): Int {
        return mDrawBounds.centerX()
    }

    fun centerY(): Int {
        return mDrawBounds.centerY()
    }

    fun exactCenterX(): Float {
        return mDrawBounds.exactCenterX()
    }

    fun exactCenterY(): Float {
        return mDrawBounds.exactCenterY()
    }

    override fun start() {
        ensureAnimators()

        if (mAnimators == null) {
            return
        }

        // If the animators has not ended, do nothing.
        if (isStarted()) {
            return
        }
        startAnimators()
        invalidateSelf()
    }

    override fun stop() {
        stopAnimators()
    }

    private fun stopAnimators() {
        mAnimators?.forEach {
            if (it.isStarted) {
                it.removeAllUpdateListeners()
                it.end()
            }
        }
    }

    override fun isRunning(): Boolean {
        mAnimators?.forEach {
            return it.isRunning
        }

        return false
    }

    private fun startAnimators() {

        mAnimators?.forEach {
            val updateListener = mUpdateListeners[it]
            it.addUpdateListener(updateListener)
            it.start()
        }

    }

    private fun ensureAnimators() {
        if (!mHasAnimators) {
            mAnimators = onCreateAnimators()
            mHasAnimators = true
        }
    }

    private fun isStarted(): Boolean {
        mAnimators?.forEach {
            return it.isStarted
        }
        return false
    }

    /**
     * Your should use this to add AnimatorUpdateListener when
     * create animator , otherwise , animator doesn't work when
     * the animation restart .
     * @param updateListener
     */
    fun addUpdateListener(
        animator: ValueAnimator,
        updateListener: ValueAnimator.AnimatorUpdateListener
    ) {
        mUpdateListeners[animator] = updateListener
    }
}