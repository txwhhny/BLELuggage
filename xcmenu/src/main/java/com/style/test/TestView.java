package com.style.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.TextView;

import com.style.menu.R;

/**
 * Created by Administrator on 2016/10/28.
 */
public class TestView extends TextView implements OnGestureListener
{
    @Override
    public boolean onDown(MotionEvent e) {
        if (ml != null)
        {
            ml.OnClicked();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public interface OnTestClickedListener
    {
        public void OnClicked();
    }

    private boolean mShowText;
    private int mLabelPosition;
    private OnTestClickedListener ml;
    private GestureDetector detector;


    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);

        detector = new GestureDetector(this);
        TypedArray attrSet = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TestView, 0, 0);
        try {
            mShowText = attrSet.getBoolean(R.styleable.TestView_showText, false);
            mLabelPosition = attrSet.getInteger(R.styleable.TestView_labelPosition, 0);
        }
        finally {
            attrSet.recycle();
        }
    }

    public boolean isShowText()
    {
        return mShowText;
    }

    public void setShowText(boolean showText)
    {
        mShowText = showText;
        invalidate();
        requestLayout();
    }

    public void setTestOnClickedListener(OnTestClickedListener l)
    {
        ml = l;
    }
}
