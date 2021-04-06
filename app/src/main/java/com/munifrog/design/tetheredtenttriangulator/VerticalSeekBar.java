package com.munifrog.design.tetheredtenttriangulator;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

// Credit to Paul Tsupikoff for this example of vertical SeekBar: https://stackoverflow.com/a/7341546
public class VerticalSeekBar extends AppCompatSeekBar {
    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.rotate(-90);
        canvas.translate(-getHeight(), 0);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        final int y;
        final int height;
        final int width;
        final int paddingLeft;
        final int paddingRight;
        final float scale;

        float progress;

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                // For now keep in mind that the indicated padding is actually for a different side:
                // left->bottom, bottom->right, right->top, top->left
                height = getHeight();
                width = getWidth();
                paddingLeft = getPaddingLeft();
                paddingRight = getPaddingRight();
                y = Math.round(event.getY());
                if (y < paddingRight) {
                    scale = 0.0f;
                } else if (y > height - paddingLeft) {
                    scale = 1.0f;
                } else {
                    scale = (y - paddingRight) / (float) (height - paddingLeft - paddingRight);
                }
                final int range = getMax();
                progress = (1 - scale) * range;
                setProgress(Math.round(progress));
                onSizeChanged(width, height, 0, 0);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}
