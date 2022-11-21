package com.xiaoxiaoying.spider.widget.loading


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import com.xiaoxiaoying.spider.R
import kotlin.math.max
import kotlin.math.min


class LoadingIndicatorView(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null) : this(context, attrs, 0)
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : this(context, attrs, defStyleAttr, 0)

    companion object {
        private const val MIN_SHOW_TIME = 500 // ms
        private const val MIN_DELAY = 500 // ms
    }

    private var mShouldStartAnimationDrawable: Boolean = false
    private var mStartTime = -1L

    private var mPostedHide = false

    private var mPostedShow = false

    private var mDismissed = false
    private var minWidth = 24
    private var minHeight = 24
    private var maxWidth = 24
    private var maxHeight = 24
    private var indicatorColor = Color.WHITE
    private var mIndicator: Indicator = BallSpinFadeLoaderIndicator()

    init {

        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingIndicatorView,
            defStyleAttr,
            defStyleRes
        )
        minWidth =
            typedArray.getDimensionPixelSize(R.styleable.LoadingIndicatorView_minWidth, minWidth)
        minHeight =
            typedArray.getDimensionPixelSize(R.styleable.LoadingIndicatorView_minHeight, minHeight)
        maxWidth =
            typedArray.getDimensionPixelSize(R.styleable.LoadingIndicatorView_maxWidth, maxWidth)
        maxHeight =
            typedArray.getDimensionPixelSize(R.styleable.LoadingIndicatorView_maxHeight, maxHeight)

        indicatorColor =
            typedArray.getColor(R.styleable.LoadingIndicatorView_indicatorColor, Color.WHITE)
        setIndicator(mIndicator, true)
        typedArray.recycle()
    }

    fun setIndicator(d: Indicator, init: Boolean = false) {
        if (mIndicator !== d || init) {
            mIndicator.callback = null
            unscheduleDrawable(mIndicator)

            mIndicator = d
            //need to set indicator color again if you didn't specified when you update the indicator .
            setIndicatorColor(indicatorColor)
            d.callback = this
            postInvalidate()
        }
    }


    fun setIndicatorColor(color: Int) {
        this.indicatorColor = color
        mIndicator.setColor(color)
        postInvalidate()
    }


    fun smoothToShow() {
        startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in))
        visibility = VISIBLE
    }

    fun smoothToHide() {
        startAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out))
        visibility = GONE
    }


    fun hide() {
        mDismissed = true
        removeCallbacks(this::delayedShow)
        val diff = System.currentTimeMillis() - mStartTime
        if (diff >= MIN_SHOW_TIME || mStartTime == -1L) {
            // The progress spinner has been shown long enough
            // OR was not shown yet. If it wasn't shown yet,
            // it will just never be shown.
            visibility = GONE
        } else {
            // The progress spinner is shown, but not long enough,
            // so put a delayed message in to hide it when its been
            // shown long enough.
            if (!mPostedHide) {
                postDelayed(this::delayedHide, MIN_SHOW_TIME - diff)
                mPostedHide = true
            }
        }
    }

    fun show() {
        // Reset the start time.
        mStartTime = -1
        mDismissed = false
        removeCallbacks(this::delayedHide)
        if (!mPostedShow) {
            postDelayed(this::delayedShow, MIN_DELAY.toLong())
            mPostedShow = true
        }
    }

    private fun delayedHide() {
        mPostedHide = false
        mStartTime = -1L
        visibility = GONE
    }

    private fun delayedShow() {
        mPostedShow = false
        if (!mDismissed) {
            mStartTime = System.currentTimeMillis()
            visibility = VISIBLE
        }
    }


    override fun verifyDrawable(who: Drawable): Boolean {
        return who === mIndicator || super.verifyDrawable(who)
    }

    fun startAnimation() {
        if (visibility != VISIBLE) {
            return
        }

        mShouldStartAnimationDrawable = true

        postInvalidate()
    }

    fun stopAnimation() {
        mIndicator.stop()
        mShouldStartAnimationDrawable = false
        postInvalidate()
    }

    override fun setVisibility(v: Int) {
        if (visibility != v) {
            super.setVisibility(v)
            if (v == GONE || v == INVISIBLE) {
                stopAnimation()
            } else {
                startAnimation()
            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == GONE || visibility == INVISIBLE) {
            stopAnimation()
        } else {
            startAnimation()
        }
    }

    override fun invalidateDrawable(dr: Drawable) {
        if (verifyDrawable(dr)) {
            val dirty = dr.bounds
            val scrollX = scrollX + paddingLeft
            val scrollY = scrollY + paddingTop
            postInvalidate(
                dirty.left + scrollX, dirty.top + scrollY,
                dirty.right + scrollX, dirty.bottom + scrollY
            )
        } else {
            super.invalidateDrawable(dr)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        updateDrawableBounds(w, h)
    }

    private fun updateDrawableBounds(width: Int, height: Int) {
        var w = width
        var h = height
        // onDraw will translate the canvas so we draw starting at 0,0.
        // Subtract out padding for the purposes of the calculations below.
        w -= paddingRight + paddingLeft
        h -= paddingTop + paddingBottom

        var right = w
        var bottom = h
        var top = 0
        var left = 0

        // Maintain aspect ratio. Certain kinds of animated drawables
        // get very confused otherwise.
        val intrinsicWidth = mIndicator.intrinsicWidth
        val intrinsicHeight = mIndicator.intrinsicHeight
        val intrinsicAspect = intrinsicWidth.toFloat() / intrinsicHeight
        val boundAspect = w.toFloat() / h
        if (intrinsicAspect != boundAspect) {
            if (boundAspect > intrinsicAspect) {
                // New width is larger. Make it smaller to match height.
                val width1 = (h * intrinsicAspect).toInt()
                left = (w - width1) / 2
                right = left + width1
            } else {
                // New height is larger. Make it smaller to match width.
                val height1 = (w * (1 / intrinsicAspect)).toInt()
                top = (h - height1) / 2
                bottom = top + height1
            }
        }
        mIndicator.setBounds(left, top, right, bottom)
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTrack(canvas)
    }

    fun drawTrack(canvas: Canvas) {
        val d = mIndicator
        // Translate canvas so a indeterminate circular progress bar with padding
        // rotates properly in its animation
        val saveCount = canvas.save()

        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())

        d.draw(canvas)
        canvas.restoreToCount(saveCount)

        if (mShouldStartAnimationDrawable) {
            (d as Animatable).start()
            mShouldStartAnimationDrawable = false
        }
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var dw = 0
        var dh = 0

        val d = mIndicator
        dw = max(minWidth, min(maxWidth, d.intrinsicWidth))
        dh = max(minHeight, min(maxHeight, d.intrinsicHeight))

        updateDrawableState()

        dw += paddingLeft + paddingRight
        dh += paddingTop + paddingBottom

        val measuredWidth = resolveSizeAndState(dw, widthMeasureSpec, 0)
        val measuredHeight = resolveSizeAndState(dh, heightMeasureSpec, 0)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        updateDrawableState()
    }

    private fun updateDrawableState() {
        val state = drawableState
        if (mIndicator.isStateful) {
            mIndicator.state = state
        }
    }

//    override fun drawableHotspotChanged(x: Float, y: Float) {
//        super.drawableHotspotChanged(x, y)
//
//        mIndicator.setHotspot(x, y)
//    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
        removeCallbacks()
    }

    override fun onDetachedFromWindow() {
        stopAnimation()
        // This should come after stopAnimation(), otherwise an invalidate message remains in the
        // queue, which can prevent the entire view hierarchy from being GC'ed during a rotation
        super.onDetachedFromWindow()
        removeCallbacks()
    }

    private fun removeCallbacks() {
        removeCallbacks(this::delayedHide)
        removeCallbacks(this::delayedShow)
    }

}