package com.zwb.commonlibs.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import java.util.*

/**
 * 带有View回收机制的PagerAdapter
 *
 * @author zwb
 */
abstract class EfficientPagerAdapter : PagerAdapter() {
  private val convertViews: LinkedList<View> = LinkedList()

  override fun isViewFromObject(view: View, `object`: Any): Boolean {
    return view === `object`
  }

  override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    val view: View = `object` as View
    convertViews.push(view)
    container.removeView(view)
  }

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    val view: View = try {
      convertViews.pop()
    } catch (e: NoSuchElementException) {
      onCreateNewView(container)
    }
    updateView(view, position)
    container.addView(view)
    return view
  }

  /**
   * 不能使用回收View时，新生成一个View的方法
   *
   * @param container 生成View的容器
   * @return 新生成的View
   */
  protected abstract fun onCreateNewView(container: ViewGroup): View

  /**
   * 对View上面的元素进行更新，可以用ViewHolder模式实现
   *
   * @param view     需要更新的View
   * @param position ViewPager中的位置
   */
  protected abstract fun updateView(view: View, position: Int)

  override fun getItemPosition(`object`: Any): Int {
    return POSITION_NONE
  }
}
