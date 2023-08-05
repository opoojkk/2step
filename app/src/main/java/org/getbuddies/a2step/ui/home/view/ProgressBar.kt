package org.getbuddies.a2step.ui.home.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import org.getbuddies.a2step.R
import kotlin.math.min

class ProgressBar : View {
    private var mProgress = 0f
    private var mStrokeWidth = 20f
    private var mStartAngle = 270f
    private var mSweepAngle = 0f
    private var mStrokeColor = Color.BLACK
    private val rectF = RectF()
    private var mOnProgressListener: OnProgressListener? = null
    private val msgAdditionalInfo = this.hashCode()

    private val mOutlinePaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                style = Paint.Style.STROKE
                color = mStrokeColor
                strokeCap = Paint.Cap.ROUND
                strokeWidth = mStrokeWidth
            }
    }
    private val mHandlerCallback: Handler.Callback = Handler.Callback {
        if (it.what == MSG_REFRESH && it.obj == msgAdditionalInfo) {
            Log.d(TAG, "mSweepAngle: $mSweepAngle")
            if (secondsUtilsRefresh() / REFRESH_INTERVAL == 0f) {
                mOnProgressListener?.generateTotp()
            }
            updateProgress()
            mHandler.sendMessageDelayed(obtainMsg(), REFRESH_INTERVAL_MILLIS)
            return@Callback true
        }
        return@Callback false
    }
    private val mHandler = Handler(Looper.getMainLooper(), mHandlerCallback)

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        attrs ?: return
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProgressBar)
        try {
            mProgress = a.getFloat(R.styleable.ProgressBar_progress, 0f)
            mStrokeWidth = a.getDimension(R.styleable.ProgressBar_strokeWidth, 20f)
            mStartAngle = a.getFloat(R.styleable.ProgressBar_startAngle, 270f)
            mStrokeColor = a.getColor(R.styleable.ProgressBar_strokeColor, Color.BLACK)
        } finally {
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //设置宽度
        val width =
            if (widthMode == MeasureSpec.EXACTLY) {
                widthSize
            } else {
                // measure width according to your needs
                DEFAULT_WIDTH
            }
        //设置高度
        val height =
            if (heightMode == MeasureSpec.EXACTLY) {
                heightSize
            } else {
                // measure height according to your needs
                DEFAULT_HEIGHT
            }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        val drawRadius = min(width / 2f, height / 2f) - mStrokeWidth / 2f
        val centerX = width / 2f
        val centerY = height / 2f
        val left = centerX - drawRadius
        val top = centerY - drawRadius
        val right = centerX + drawRadius
        val bottom = centerY + drawRadius
        rectF.set(left, top, right, bottom)
        canvas.drawArc(rectF, mStartAngle, mSweepAngle, false, mOutlinePaint)
    }

    fun start() {
        mOnProgressListener?.generateTotp()
        mHandler.sendMessage(obtainMsg())
    }

    private fun secondsUtilsRefresh(): Float {
        val currentTimeSeconds = System.currentTimeMillis()
        return REFRESH_INTERVAL - currentTimeSeconds % REFRESH_INTERVAL.toInt()
    }

    fun stop() {
        mHandler.removeMessages(MSG_REFRESH, msgAdditionalInfo)
    }

    private fun updateProgress() {
        mProgress = secondsUtilsRefresh() / REFRESH_INTERVAL
        mSweepAngle = 360 * mProgress
        invalidate()
    }

    fun setOnProgressListener(listener: OnProgressListener) {
        mOnProgressListener = listener
    }

    private fun obtainMsg(): Message {
        return mHandler.obtainMessage(MSG_REFRESH, msgAdditionalInfo)
    }

    companion object {
        private const val TAG = "ProgressBar"
        private const val DEFAULT_HEIGHT = 50
        private const val DEFAULT_WIDTH = 50
        private const val MSG_REFRESH = 0x1101
        private const val REFRESH_INTERVAL = 30f * 1000f
        private const val REFRESH_INTERVAL_MILLIS = 100L
    }

    interface OnProgressListener {
        fun generateTotp()
    }
}