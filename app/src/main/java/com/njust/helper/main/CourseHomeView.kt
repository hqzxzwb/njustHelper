package com.njust.helper.main

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import com.njust.helper.R
import com.njust.helper.main.CourseHomeViewConstants.GRADIENT_COLOR_1
import com.njust.helper.main.CourseHomeViewConstants.GRADIENT_COLOR_2

/**
 * 主页显示课表的View
 */
class CourseHomeView : AppCompatTextView {
  private var data: List<String> = emptyList()
  private val mTextPaint: TextPaint = TextPaint()
  private var isExpanded: Boolean = false
  private var mMaxHeight: Int = 0
  private var realHeight: Int = 0
  private lateinit var mEmptyString: String
  private var fadeHeight: Int = 0
  private val mFadePaint = Paint()

  constructor(context: Context) : super(context) {
    init(null, 0)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init(attrs, 0)
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
      : super(context, attrs, defStyleAttr) {
    init(attrs, defStyleAttr)
  }

  private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
    mTextPaint.textSize = textSize
    mTextPaint.isAntiAlias = true
    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CourseHomeView, defStyleAttr, 0)
    mMaxHeight = typedArray.getDimensionPixelSize(R.styleable.CourseHomeView_maxHeight, Integer.MAX_VALUE)
    mEmptyString = typedArray.getString(R.styleable.CourseHomeView_emptyText) ?: ""
    typedArray.recycle()
    fadeHeight = resources.getDimensionPixelOffset(R.dimen.course_home_fade_height)
    val colors = intArrayOf(GRADIENT_COLOR_1, GRADIENT_COLOR_2)
    val position = floatArrayOf(0f, 1f)
    val linearGradient = LinearGradient(0f, 0f, 0f, fadeHeight.toFloat(), colors,
        position, Shader.TileMode.CLAMP)
    mFadePaint.shader = linearGradient
  }

  override fun setTextSize(size: Float) {
    super.setTextSize(size)
    mTextPaint.textSize = size
  }

  fun setData(data: List<String>?) {
    this.data = data ?: emptyList()
    //修改数据之后，回到折叠状态
    isExpanded = false
    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    requestLayout()
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
      //展开时使用这一段代码
      val width = MeasureSpec.getSize(widthMeasureSpec)
      val height = MeasureSpec.getSize(heightMeasureSpec)
      setMeasuredDimension(width, height)
      isExpanded = true
      return
    }
    val width = MeasureSpec.getSize(widthMeasureSpec)
    val height = if (data.isEmpty()) {
      compoundPaddingTop + compoundPaddingBottom + lineHeight
    } else {
      compoundPaddingTop + compoundPaddingBottom + lineHeight * data.size
    }
    if (height > mMaxHeight) {
      realHeight = height
      setMeasuredDimension(width, mMaxHeight)
    } else {
      isExpanded = true
      setMeasuredDimension(width, height)
    }
  }

  private fun expand() {
    if (!isExpanded) {
      val params = layoutParams
      val animator = ValueAnimator.ofInt(height, realHeight).setDuration(
          resources.getInteger(android.R.integer.config_shortAnimTime).toLong())

      //扩展高度到实际需要的高度
      animator.addUpdateListener { animation ->
        params.height = animation.animatedValue as Int
        requestLayout()
      }
      animator.start()
    }
  }

  override fun setLayoutParams(params: ViewGroup.LayoutParams) {
    //宽度强制MATCH_PARENT
    params.width = ViewGroup.LayoutParams.MATCH_PARENT
    super.setLayoutParams(params)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    val x = compoundPaddingLeft
    var y = compoundPaddingTop + layout.getLineBaseline(0)
    val width = width - paddingLeft - paddingRight
    if (data.isEmpty()) {
      canvas.drawText(mEmptyString, x.toFloat(), y.toFloat(), mTextPaint)
    } else {
      for (s in data) {
        val cs = TextUtils.ellipsize(s, mTextPaint, width.toFloat(), TextUtils.TruncateAt.END)
        canvas.drawText(cs.toString(), x.toFloat(), y.toFloat(), mTextPaint)
        if (y > height) break
        y += lineHeight
      }
    }
    if (!isExpanded) {
      canvas.translate(0f, (height - fadeHeight).toFloat())
      canvas.drawRect(0f, 0f, getWidth().toFloat(), fadeHeight.toFloat(), mFadePaint)
    }
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (event.actionMasked == MotionEvent.ACTION_UP) {
      if (height - event.y.toInt() < fadeHeight && !isExpanded) {
        expand()
        //把触摸cancel掉。否则控件将一直处于按下状态。
        event.action = event.action and MotionEvent.ACTION_POINTER_INDEX_MASK or MotionEvent.ACTION_CANCEL
        super.onTouchEvent(event)
        return true
      }
    }
    return super.onTouchEvent(event)
  }
}
