package com.munifrog.design.tetheredtenttriangulator;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class Clearing
        extends Drawable
        implements PlatformCenterRun.PlatformCenterListener
{
    interface ClearingListener {
        void computePlatformCenter(PlatformCenterRun run);
    }

    private static final int TETHER_SELECTION_NONE = -1;
    private static final int TETHER_SELECTION_DECIDING = -2;

    private static final long PLATFORM_COMPUTATION_FREQUENCY_MILLIS = 200;

    private static final int DRAW_PLATFORM_TOO_CLOSE = 0;
    private static final int DRAW_PLATFORM_ENABLED = 1;

    private static final double MATH_ANGLE_FULL_CIRCLE = Math.PI * 2;
    private static final double MATH_METERS_TO_FEET_CONVERSION = 3.2808399;
    private static final double MATH_METERS_ACROSS_SMALLEST_DIMEN = 5.0;

    private final Paint mTetherPaint;
    private final Paint mPerimeterPaint;
    private final Paint mTreePaint;
    private final Paint mLabelPaint;

    private boolean mIsImperial;

    private float mRadiusTetherSize;
    private float mRadiusSelectionSize;
    // When calculating the distance to the nearest tree, we can skip the square root computation
    // knowing that (x1-x0)^2 + (y1-y0)^2 <= selectionRadius^2
    private float mRadiusSelectionRangeSquared; // Avoiding computation by leaving squared
    private float mSmallestDimen;

    private String mStringMeters;
    private String mStringImperial;

    private float [] mTetherCenter = new float[2];
    private float [] mPlatformCoordinates = new float[2];
    private float [][] mTethers = new float[3][2];
    private double mDist01; // c
    private double mDist12; // a
    private double mDist20; // b
    private double mScaleBase; // Units per pixel
    private double mScaleSlider; // Scaled multiplier
    private double mThreshold0P1;
    private double mThreshold1P2;
    private double mThreshold2P0;

    private int mStateTether = TETHER_SELECTION_NONE;
    private int mStatePlatform = DRAW_PLATFORM_ENABLED;
    private long mPreviousComputation;

    private ClearingListener mViewOwner;

    public Clearing(ClearingListener listener) {
        mViewOwner = listener;

        // Set up color and text size
        mTetherPaint = new Paint();
        mTetherPaint.setARGB(255, 127, 127, 127);
        mTetherPaint.setStrokeWidth(10);

        mLabelPaint = new Paint();
        mLabelPaint.setARGB(255, 0, 0, 0);
        mLabelPaint.setTextSize(56f);
        mLabelPaint.setStrokeWidth(4);

        mPerimeterPaint = new Paint();
        mPerimeterPaint.setARGB(255, 92, 113, 72);
        mPerimeterPaint.setStyle(Paint.Style.STROKE);
        mPerimeterPaint.setPathEffect(new DashPathEffect(new float[] {15f, 20f}, 0f));
        mPerimeterPaint.setStrokeWidth(5);
        mPerimeterPaint.setStrokeCap(Paint.Cap.ROUND);

        mTreePaint = new Paint();
        mTreePaint.setARGB(255, 193, 154, 107);

        mIsImperial = false;
        mScaleSlider = 1.0;

        setPlatformSymmetricAngle(2 * Math.PI / 3);
        getPlatformCenterOccasionally();
    }

    public void setUnitStrings(String meters, String imperial) {
        mStringMeters = meters;
        mStringImperial = imperial;
    }

    public void selectTether(int x, int y) {
        if (mStateTether == TETHER_SELECTION_NONE) {
            mStateTether = TETHER_SELECTION_DECIDING;

            float smallestDeltaSquared = mRadiusSelectionRangeSquared;
            int closestIndex = TETHER_SELECTION_NONE;
            // Only allow trees within a specified radius to be selected
            float deltaX;
            float deltaY;
            float deltaSquared;
            for (int i = 0; i < 3; i++) {
                // Determine which trees are within the range to be selected
                deltaX = x - mTethers[i][0];
                deltaY = y - mTethers[i][1];
                // Square everything to avoid unnecessary (and expensive) square root computation
                deltaSquared = deltaX * deltaX + deltaY * deltaY;
                if (deltaSquared < smallestDeltaSquared) {
                    // Record the closest tree at this stage
                    closestIndex = i;
                    smallestDeltaSquared = deltaSquared;
                }
            }

            // Officially select the closest tree, now that it is known
            if (closestIndex != TETHER_SELECTION_NONE) {
                mStateTether = closestIndex;
                invalidateSelf();
            }
        }
    }

    public void releaseTether() {
        mStateTether = TETHER_SELECTION_NONE;
        computePlatformCenter(); // In case the last computation was prevented by timing
    }

    public void updateTether(int x, int y) {
        if (mStateTether >= 0) {
            mTethers[mStateTether][0] = x;
            mTethers[mStateTether][1] = y;
            invalidateSelf();
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        int width = getBounds().width();
        int height = getBounds().height();
        mSmallestDimen = Math.min(width, height);

        int centerX = width/2;
        int centerY = height/2;
        mRadiusTetherSize = mSmallestDimen / 50;
        mRadiusSelectionSize = mRadiusTetherSize * 2;
        mRadiusSelectionRangeSquared = mRadiusTetherSize * mRadiusTetherSize * 9;

        mScaleBase = MATH_METERS_ACROSS_SMALLEST_DIMEN / mSmallestDimen;

        mTetherCenter[0] = centerX;
        mTetherCenter[1] = centerY;

        configDefault();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        getPlatformCenterOccasionally();
        drawTethers(canvas);
        drawConnections(canvas);
        drawStakes(canvas);
    }

    private void drawConnections(Canvas canvas) {
        Path path = new Path();
        path.moveTo(mTethers[0][0], mTethers[0][1]);
        path.lineTo(mTethers[1][0], mTethers[1][1]);
        path.lineTo(mTethers[2][0], mTethers[2][1]);
        path.lineTo(mTethers[0][0], mTethers[0][1]);
        canvas.drawPath(path, mPerimeterPaint);

        float [] mMid12 = { (mTethers[1][0] + mTethers[2][0]) / 2, (mTethers[1][1] + mTethers[2][1]) / 2 }; // a
        float [] mMid20 = { (mTethers[2][0] + mTethers[0][0]) / 2, (mTethers[2][1] + mTethers[0][1]) / 2 }; // b
        float [] mMid01 = { (mTethers[0][0] + mTethers[1][0]) / 2, (mTethers[0][1] + mTethers[1][1]) / 2 }; // c

        String units = " " + (mIsImperial ? mStringImperial : mStringMeters);
        canvas.drawText(String.format(units, scaledDimension(mDist12)), mMid12[0], mMid12[1], mLabelPaint);
        canvas.drawText(String.format(units, scaledDimension(mDist20)), mMid20[0], mMid20[1], mLabelPaint);
        canvas.drawText(String.format(units, scaledDimension(mDist01)), mMid01[0], mMid01[1], mLabelPaint);
    }

    private void drawStakes(Canvas canvas) {
        float mCurrentRadius;
        for (int i = 0; i < 3; i++) {
            mCurrentRadius = (mStateTether == i) ? mRadiusSelectionSize : mRadiusTetherSize;
            canvas.drawCircle(
                    mTethers[i][0],
                    mTethers[i][1],
                    mCurrentRadius,
                    mTreePaint
            );
        }
    }

    private void drawTethers(Canvas canvas) {
        if (mStatePlatform == DRAW_PLATFORM_ENABLED) {
            canvas.drawLine(mTethers[0][0], mTethers[0][1], mPlatformCoordinates[0], mPlatformCoordinates[1], mTetherPaint);
            canvas.drawLine(mTethers[1][0], mTethers[1][1], mPlatformCoordinates[0], mPlatformCoordinates[1], mTetherPaint);
            canvas.drawLine(mTethers[2][0], mTethers[2][1], mPlatformCoordinates[0], mPlatformCoordinates[1], mTetherPaint);

            canvas.drawCircle(
                    mPlatformCoordinates[0],
                    mPlatformCoordinates[1],
                    mRadiusTetherSize / 2,
                    mTetherPaint
            );
        }
    }

    @Override
    public void setAlpha(int alpha) {
        // This method is required
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // This method is required
    }

    @Override
    public int getOpacity() {
        // Must be PixelFormat.UNKNOWN, TRANSLUCENT, TRANSPARENT, or OPAQUE
        return PixelFormat.OPAQUE;
    }

    private void getPlatformCenterOccasionally() {
        // Computations can be a bit delayed/slow
        long current = Calendar.getInstance().getTimeInMillis();
        if (current > mPreviousComputation + PLATFORM_COMPUTATION_FREQUENCY_MILLIS) {
            mPreviousComputation = current;
            getPlatformCenter();
        } else {
            getPerimeter();
            invalidateSelf();
        }
    }

    private void getPerimeter() {
        double [] perimeter = Util.getPerimeter(mTethers);
        mDist01 = perimeter[0];
        mDist12 = perimeter[1];
        mDist20 = perimeter[2];
    }

    private void getPlatformCenter() {
        double [] perimeter = Util.getPerimeter(mTethers);
        mDist01 = perimeter[0];
        mDist12 = perimeter[1];
        mDist20 = perimeter[2];

        double angle102 = Math.acos((perimeter[5] + perimeter[3] - perimeter[4]) / 2.0 / mDist20 / mDist01);  // A = 0
        double angle210  = Math.acos((perimeter[4] + perimeter[3] - perimeter[5]) / 2.0 / mDist12 / mDist01); // B = 1
        double angle021 = Math.acos((perimeter[4] + perimeter[5] - perimeter[3]) / 2.0 / mDist12 / mDist20);  // C = 2

        if (angle102 < mThreshold1P2 && angle210 < mThreshold2P0 && angle021 < mThreshold0P1) {
            mStatePlatform = DRAW_PLATFORM_ENABLED;
            computePlatformCenter();
        } else {
            mStatePlatform = DRAW_PLATFORM_TOO_CLOSE;
        }
    }

    private void computePlatformCenter() {
        float[] thresholds = { (float) mThreshold2P0, (float) mThreshold1P2, (float) mThreshold0P1 };
        mViewOwner.computePlatformCenter(new PlatformCenterRun(this, mTethers, thresholds));
    }

    @Override
    public void onPlatformComputed(float[] newPlatform) {
        mPlatformCoordinates = newPlatform;
        invalidateSelf();
    }

    public void configDefault() {
        double lengthReference = 5 * mSmallestDimen / 12;
        double offset = -15 * Math.PI / 180;
        double currentAngle;
        for (int i = 0; i < 3; i++) {
            currentAngle = offset + MATH_ANGLE_FULL_CIRCLE / 3 * i;
            mTethers[i][0] = (float)(mTetherCenter[0] + lengthReference * Math.cos(currentAngle));
            mTethers[i][1] = (float)(mTetherCenter[1] + lengthReference * Math.sin(currentAngle));
        }
        invalidateSelf();
    }

    private void configRotate() {
        float tempX = mTethers[0][0];
        float tempY = mTethers[0][1];
        mTethers[0][0] = mTethers[1][0];
        mTethers[0][1] = mTethers[1][1];
        mTethers[1][0] = mTethers[2][0];
        mTethers[1][1] = mTethers[2][1];
        mTethers[2][0] = tempX;
        mTethers[2][1] = tempY;
    }

    public void setPlatformSymmetricAngle(double angle) {
        mThreshold0P1 = angle;
        mThreshold1P2 = angle;
        mThreshold2P0 = MATH_ANGLE_FULL_CIRCLE - mThreshold0P1 - mThreshold1P2;
        invalidateSelf();
    }

    public void rotatePlatform() {
        configRotate();
        invalidateSelf();
    }

    public boolean getIsImperial() {
        return mIsImperial;
    }

    public void setIsImperial(boolean imperial) {
        mIsImperial = imperial;
        invalidateSelf();
    }

    public void setSliderScale(double slider) {
        mScaleSlider = slider;
        invalidateSelf();
    }

    private double scaledDimension(double input) {
        return Util.forcePrecision(input * mScaleBase * mScaleSlider *
                (mIsImperial ? MATH_METERS_TO_FEET_CONVERSION : 1)
        );
    }
}