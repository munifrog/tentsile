package com.munifrog.design.tetheredtenttriangulator;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LevelingView extends Drawable {

    private final int COUNT_CIRCLES = 5;
    private final int COUNT_CIRCLE_SEGMENTS = 100;
    private final DisplayMetrics mDisplayMetrics;

    private float mPixelsCrossHairMargin;
    private float mPixelsLineWidth;
    private float mPixelsRimMargin;
    private float mPixelsTargetMargin;
    private float mPixelsTargetPadding;

    private float[] mScreenCenter;
    private float[] mBubblePosition;
    private Path mLinesPattern;

    private float mRadiusBubble = 25;
    private float mRadiusCrossHairs = 125;
    private float mRadiusInner = 85;
    private float mRadiusOuter = 100;
    private float mRadiusSphere = 125;
    private float mTicSpacing = 30;

    private Paint mPaintBackground;
    private Paint mPaintBubble;
    private Paint mPaintLines;
    private Paint mPaintRim;
    private Paint mPaintMain;

    LevelingView(DisplayMetrics displayMetrics) {
        mDisplayMetrics = displayMetrics;
        initPixelMeasurements();
        initPaint();
    }

    private void initPixelMeasurements() {
        float DP_CROSS_HAIR_MARGIN = 24;
        float DP_LINE_WIDTH = 3;
        float DP_RIM_MARGIN = 12;
        float DP_TARGET_MARGIN = 24;
        float DP_TARGET_PADDING = 8;
        mPixelsCrossHairMargin = getPixelEquivalent(DP_CROSS_HAIR_MARGIN);
        mPixelsLineWidth = getPixelEquivalent(DP_LINE_WIDTH);
        mPixelsRimMargin = getPixelEquivalent(DP_RIM_MARGIN);
        mPixelsTargetMargin = getPixelEquivalent(DP_TARGET_MARGIN);
        mPixelsTargetPadding = getPixelEquivalent(DP_TARGET_PADDING);
    }

    private float getPixelEquivalent(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, mDisplayMetrics);
    }

    void initPaint() {
        mPaintBackground = new Paint();
        mPaintBackground.setARGB(255, 0, 0, 0);

        mPaintBubble = new Paint();
        mPaintBubble.setARGB(127, 255, 255, 255);

        mPaintLines = new Paint();
        mPaintLines.setARGB(255, 142, 142, 147);
        mPaintLines.setStyle(Paint.Style.STROKE);
        mPaintLines.setStrokeWidth(mPixelsLineWidth);
        mPaintLines.setStrokeCap(Paint.Cap.ROUND);

        mPaintRim = new Paint();
        mPaintRim.setARGB(255, 250, 204, 2);

        mPaintMain = new Paint();
        mPaintMain.setARGB(255, 61, 199, 89);
    }

    public void setDimensions(double width, double height) {
        mScreenCenter = new float[]{ (float)(width / 2.0), (float)(height / 2.0) };
        double smaller = Math.min(width, height);
        initDimensions(smaller);
        initLines();
    }

    private void initDimensions(double width) {
        double diamCrossHairs = width - mPixelsTargetPadding - mPixelsTargetMargin;
        mRadiusCrossHairs = (float)(diamCrossHairs / 2.0);
        double diamOuter = diamCrossHairs - mPixelsCrossHairMargin;
        mRadiusOuter = (float)(diamOuter / 2.0);
        double diamInner = diamOuter - mPixelsRimMargin;
        mRadiusInner = (float)(diamInner / 2.0);
        mTicSpacing = mRadiusInner / COUNT_CIRCLES;
        // The bubble should fit inside the innermost circle
        mRadiusBubble = (float)(mTicSpacing - (mPixelsLineWidth / 2.0));
        mRadiusSphere = mRadiusOuter - mRadiusBubble;
    }

    private void initLines() {
        mLinesPattern = getLinesPatternPath();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        drawTarget(canvas);
        drawBubble(canvas);
    }

    private void drawTarget(@NonNull Canvas canvas) {
        canvas.drawCircle(
                mScreenCenter[0],
                mScreenCenter[1],
                mRadiusOuter,
                mPaintRim
        );
        canvas.drawCircle(
                mScreenCenter[0],
                mScreenCenter[1],
                mRadiusInner,
                mPaintMain
        );
        canvas.drawPath(mLinesPattern, mPaintLines);
    }

    private Path getLinesPatternPath() {
        Path path = new Path();
        float[][] unitCircle = getUnitCircle();
        float radius;
        // Concentric Circles
        for (int i = 1; i <= COUNT_CIRCLES; i++) {
            radius = mTicSpacing * i;
            path.moveTo(
                    mScreenCenter[0] + radius * unitCircle[0][0],
                    mScreenCenter[1] + radius * unitCircle[0][1]
            );
            for (int j = 1; j < COUNT_CIRCLE_SEGMENTS; j++) {
                path.lineTo(
                        mScreenCenter[0] + radius * unitCircle[j][0],
                        mScreenCenter[1] + radius * unitCircle[j][1]
                );
            }
            path.lineTo(
                    mScreenCenter[0] + radius * unitCircle[0][0],
                    mScreenCenter[1] + radius * unitCircle[0][1]
            );
        }
        // Cross-hairs or Axes
        path.moveTo(
                mScreenCenter[0] + mRadiusCrossHairs,
                mScreenCenter[1]
        );
        path.lineTo(
                mScreenCenter[0] - mRadiusCrossHairs,
                mScreenCenter[1]
        );
        path.moveTo(
                mScreenCenter[0],
                mScreenCenter[1] + mRadiusCrossHairs
        );
        path.lineTo(
                mScreenCenter[0],
                mScreenCenter[1] - mRadiusCrossHairs
        );

        return path;
    }

    private float[][] getUnitCircle() {
        float[][] unitCircle = new float[COUNT_CIRCLE_SEGMENTS][2];
        double angle;
        double angleDelta = (2 * Math.PI) / COUNT_CIRCLE_SEGMENTS;
        for (int i = 0; i < COUNT_CIRCLE_SEGMENTS; i++) {
            angle = angleDelta * i;
            unitCircle[i][0] = (float)Math.cos(angle);
            unitCircle[i][1] = (float)Math.sin(angle);
        }
        return unitCircle;
    }

    public void setBubblePosition(float[] direction) {
        this.mBubblePosition = direction;
        invalidateSelf();
    }

    private void drawBubble(@NonNull Canvas canvas) {
        canvas.drawCircle(
                mScreenCenter[0] + mRadiusSphere * mBubblePosition[0],
                mScreenCenter[1] + mRadiusSphere * mBubblePosition[1],
                mRadiusBubble,
                mPaintBubble
        );
    }

    @Override
    public void setAlpha(int alpha) {
        mPaintBackground.setAlpha(alpha);
        mPaintBubble.setAlpha((int)Math.round(alpha / 2.0));
        mPaintLines.setAlpha(alpha);
        mPaintLines.setAlpha(alpha);
        mPaintRim.setAlpha(alpha);
        mPaintMain.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaintBackground.setColorFilter(colorFilter);
        mPaintBubble.setColorFilter(colorFilter);
        mPaintLines.setColorFilter(colorFilter);
        mPaintLines.setColorFilter(colorFilter);
        mPaintRim.setColorFilter(colorFilter);
        mPaintMain.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}