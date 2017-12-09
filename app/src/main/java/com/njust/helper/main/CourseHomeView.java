package com.njust.helper.main;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.njust.helper.R;

import java.util.List;

/**
 * 主页显示课表的View
 */
public class CourseHomeView extends AppCompatTextView {
    private List<String> data;
    private TextPaint mTextPaint;
    private boolean expanded;
    private int maxHeight, realHeight;
    private String mEmptyString;
    private int fadeHeight;
    private Paint mFadePaint = new Paint();

    public CourseHomeView(Context context) {
        super(context);
        init(null, 0);
    }

    public CourseHomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CourseHomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public boolean isExpanded() {
        return expanded;
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(getTextSize());
        mTextPaint.setAntiAlias(true);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CourseHomeView, defStyleAttr, 0);
        maxHeight = typedArray.getDimensionPixelSize(R.styleable.CourseHomeView_maxHeight, Integer.MAX_VALUE);
        mEmptyString = typedArray.getString(R.styleable.CourseHomeView_emptyText);
        typedArray.recycle();
        fadeHeight = getResources().getDimensionPixelOffset(R.dimen.course_home_fade_height);
        int[] colors = {0x00FFFFFF, 0xFFFFFFFF};
        float[] position = {0f, 1f};
        LinearGradient linearGradient = new LinearGradient(0, 0, 0, fadeHeight, colors, position, Shader.TileMode.REPEAT);
        mFadePaint.setShader(linearGradient);
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize(size);
        if (mTextPaint != null) {
            mTextPaint.setTextSize(size);
        }
    }

    public void setData(@Nullable List<String> data) {
        this.data = data;
        //修改数据之后，回到折叠状态
        expanded = false;
        getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            //展开时使用这一段代码
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(width, height);
            expanded = true;
            return;
        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height;
        if (data == null || data.size() == 0) {
            height = getCompoundPaddingTop() + getCompoundPaddingBottom() + getLineHeight();
        } else {
            height = getCompoundPaddingTop() + getCompoundPaddingBottom() + getLineHeight() * data.size();
        }
        if (height > maxHeight) {
            realHeight = height;
            setMeasuredDimension(width, maxHeight);
        } else {
            expanded = true;
            setMeasuredDimension(width, height);
        }
    }

    public void expand() {
        if (!expanded) {
            final ViewGroup.LayoutParams params = getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofInt(getHeight(), realHeight)
                    .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));

            //扩展高度到实际需要的高度
            animator.addUpdateListener(animation -> {
                params.height = (Integer) animation.getAnimatedValue();
                requestLayout();
            });
            animator.start();
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        //宽度强制MATCH_PARENT
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        super.setLayoutParams(params);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = getCompoundPaddingLeft();
        int y = getCompoundPaddingTop() + getLayout().getLineBaseline(0);
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        if (data == null || data.size() == 0) {
            canvas.drawText(mEmptyString, x, y, mTextPaint);
        } else {
            for (String s : data) {
                CharSequence cs = TextUtils.ellipsize(s, mTextPaint, width, TextUtils.TruncateAt.END);
                canvas.drawText(cs.toString(), x, y, mTextPaint);
                if (y > getHeight()) break;
                y += getLineHeight();
            }
        }
        if (!expanded) {
            canvas.translate(0, getHeight() - fadeHeight);
            canvas.drawRect(0, 0, getWidth(), fadeHeight, mFadePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            if (getHeight() - (int) event.getY() < fadeHeight && !expanded) {
                expand();
                //把触摸cancel掉。否则控件将一直处于按下状态。
                event.setAction((event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        | MotionEvent.ACTION_CANCEL);
                super.onTouchEvent(event);
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
