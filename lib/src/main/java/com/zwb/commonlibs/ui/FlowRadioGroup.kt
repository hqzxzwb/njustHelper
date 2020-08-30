package com.zwb.commonlibs.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup

/**
 * 流式布局的RadioGroup
 */
class FlowRadioGroup : RadioGroup {
  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val maxWidth = View.MeasureSpec.getSize(widthMeasureSpec)
    val childCount = childCount
    var x = 0
    var y = 0

    for (index in 0 until childCount) {
      val child = getChildAt(index)
      if (child.visibility != View.GONE) {
        child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        // 此处增加onLayout中的换行判断，用于计算所需的高度
        val width = child.measuredWidth
        val height = child.measuredHeight
        val lp = child.layoutParams as LinearLayout.LayoutParams
        x += lp.leftMargin
        x += width
        if (x > maxWidth) {
          x = width + lp.leftMargin
          y += height
        }
        x += lp.rightMargin
      }
    }
    // 设置容器所需的宽度和高度
    setMeasuredDimension(maxWidth, y + getChildAt(childCount - 1).measuredHeight)
  }

  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    val childCount = childCount
    val maxWidth = r - l
    var x = 0
    var y = 0
    for (i in 0 until childCount) {
      val child = this.getChildAt(i)
      if (child.visibility != View.GONE) {
        val width = child.measuredWidth
        val height = child.measuredHeight
        val lp = child.layoutParams as LinearLayout.LayoutParams
        x += lp.leftMargin
        x += width
        if (x > maxWidth) {
          x = width + lp.leftMargin
          y += height
        }
        child.layout(x - width, y, x, y + height)
        x += lp.rightMargin
      }
    }
  }
}
