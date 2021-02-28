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

public class Clearing extends Drawable {
    private static final int TETHER_SELECTION_NONE = -1;
    private static final int TETHER_SELECTION_DECIDING = -2;

    private static final long PLATFORM_COMPUTATION_FREQUENCY_MILLIS = 200;

    private static final int DRAW_PLATFORM_TOO_CLOSE = 0;
    private static final int DRAW_PLATFORM_ENABLED = 1;

    private static final double MATH_ANGLE_PRECISION_ALLOWANCE = 0.001;
    private static final double MATH_ANGLE_FULL_CIRCLE = Math.PI * 2;
    private static final double MATH_METERS_TO_FEET_CONVERSION = 3.2808399;
    private static final double MATH_METERS_ACROSS_SMALLEST_DIMEN = 5.0;
    private static final int MATH_DEGREES_OF_PRECISION = 1;

    private boolean mIsImperial;

    private final Paint mTetherPaint;
    private final Paint mPerimeterPaint;
    private final Paint mTreePaint;
    private final Paint mLabelPaint;

    private float mRadiusTetherSize;
    private float mRadiusSelectionSize;
    // When calculating the distance to the nearest tree, we can skip the square root computation
    // knowing that (x1-x0)^2 + (y1-y0)^2 <= selectionRadius^2
    private float mRadiusSelectionRangeSquared; // Avoiding computation by leaving squared
    float mSmallestDimen;

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

    public Clearing() {
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
        invalidateSelf();
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

        canvas.drawText(Double.toString(scaledDimension(mDist12)), mMid12[0], mMid12[1], mLabelPaint);
        canvas.drawText(Double.toString(scaledDimension(mDist20)), mMid20[0], mMid20[1], mLabelPaint);
        canvas.drawText(Double.toString(scaledDimension(mDist01)), mMid01[0], mMid01[1], mLabelPaint);
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
        }
    }

    private void getPlatformCenter() {
        double diff01x = mTethers[0][0] - mTethers[1][0]; // Ax - Bx
        double diff01y = mTethers[0][1] - mTethers[1][1]; // Ay - By
        double diff12x = mTethers[1][0] - mTethers[2][0]; // Bx - Cx
        double diff12y = mTethers[1][1] - mTethers[2][1]; // By - Cy
        double diff20x = mTethers[2][0] - mTethers[0][0]; // Cx - Ax
        double diff20y = mTethers[2][1] - mTethers[0][1]; // Cy - Ay
        double dist01sq = diff01x * diff01x + diff01y * diff01y;
        double dist12sq = diff12x * diff12x + diff12y * diff12y;
        double dist20sq = diff20x * diff20x + diff20y * diff20y;
        mDist01 = Math.sqrt(dist01sq); // c
        mDist12 = Math.sqrt(dist12sq); // a
        mDist20 = Math.sqrt(dist20sq); // b

        double angle102 = Math.acos((dist20sq + dist01sq - dist12sq) / 2.0 / mDist20 / mDist01);  // A = 0
        double angle210  = Math.acos((dist12sq + dist01sq - dist20sq) / 2.0 / mDist12 / mDist01); // B = 1
        double angle021 = Math.acos((dist12sq + dist20sq - dist01sq) / 2.0 / mDist12 / mDist20);  // C = 2

        if (angle102 < mThreshold1P2 && angle210 < mThreshold2P0 && angle021 < mThreshold0P1) {
            mStatePlatform = DRAW_PLATFORM_ENABLED;

            // Equilateral triangle (simple) case: 2P0, 1P2 and 0P1 are all 120(o) or 2 * PI / 3
            // Rather than computing the sines of these angles, could compute them ahead of time and load per tent

            double sine2P0 = Math.sin(mThreshold2P0); // rho
            double sine1P2 = Math.sin(mThreshold1P2); // lambda
            //double sine0P1 = Math.sin(mThreshold0P1); // psi

            double angleTheta =  MATH_ANGLE_FULL_CIRCLE - mThreshold2P0 - mThreshold1P2 - angle021;
            double angleP12 = Math.atan(mDist20 * Math.sin(angleTheta) * sine1P2 / (mDist12 * sine2P0 + mDist20 * sine1P2 * Math.cos(angleTheta)));
            double angleP21 = Math.PI - angleP12 - mThreshold1P2;
            double angleP20 = angle021 - angleP21;

            //double dist0P = mDist20 * Math.sin(angleP20) / sine2P0;
            //double dist1P = mDist12 * Math.sin(angleP21) / sine1P2;
            double dist2P = mDist12 * Math.sin(angleP12) / sine1P2;

            // Determine the location of the platform center (Q is for quadrant or Y=0 line)
            double angleQ21 = getDirection(mDist12, diff12x, diff12y);
            double angleQ20 = getDirection(mDist20, -diff20x, -diff20y);

            // If adding lambda1 to angle CA, then subtracting lambda2 from angle CB
            // If subtracting lambda1 from angle CA, then adding lambda2 to angle CB
            double angleP20d1 = angleQ20 + angleP20; // compare only with angle12Pd2
            //double angleP21d2 = angleQ21 - angleP21; // compare only with angleP20d1
            double angleP20d2 = angleQ20 - angleP20; // compare only with angle12Pd1
            double angleP21d1 = angleQ21 + angleP21; // compare only with angle02Pd2

            // Find the angle pair that give the same (or close) angle
            double anglePlatform = areAnglesEquivalent(angleP20d2, angleP21d1) ? angleP21d1 : angleP20d1;

            // After figuring out where the platform center is, calculate the extent of the tent
            // If dist0P, dist1P, and dist2P are large enough for the tent, show the platform center

            // x = Cx + dist2P * cos(anglePlatform)
            mPlatformCoordinates[0] = (float) (mTethers[2][0] + dist2P * Math.cos(anglePlatform));
            // y = Cy + dist2P * sin(anglePlatform)
            mPlatformCoordinates[1] = (float) (mTethers[2][1] + dist2P * Math.sin(anglePlatform));
        } else {
            mStatePlatform = DRAW_PLATFORM_TOO_CLOSE;
        }
    }

    // deltaY = (distal Y - proximal Y); if deltaY >= 0, then angle is within Q1 or Q2;
    // if deltaY < 0, then angle within Q3 or Q4; allowing us to narrow down the quadrant
    private double getDirection(double hypotenuse, double deltaX, double deltaY) {
        double angle = Math.asin(deltaY / hypotenuse);
        if ((angle >= 0) && (deltaX < 0) || ((angle < 0) && (deltaX < 0))) {
            // symmetric with respect to line (x = 0)
            angle = Math.PI - angle;
        }
        return angle;
    }

    private boolean areAnglesEquivalent(double angleA, double angleB) {
        double rawDelta = Math.abs(angleB - angleA);
        if (rawDelta < MATH_ANGLE_PRECISION_ALLOWANCE) {
            return true;
        } else if (Math.signum(angleA) != Math.signum(angleB)) {
            // In theory we would need to handle N 2*PI, but in practice it suffices to compare +/-
            return (Math.abs(MATH_ANGLE_FULL_CIRCLE - rawDelta) < MATH_ANGLE_PRECISION_ALLOWANCE);
        }
        return false;
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

    public void setSliderScale(double slider) {
        mScaleSlider = slider;
        invalidateSelf();
    }

    private double scaledDimension(double input) {
        return forcePrecision(input * mScaleBase * mScaleSlider *
                (mIsImperial ? MATH_METERS_TO_FEET_CONVERSION : 1)
        );
    }

    private double forcePrecision(double input) {
        double precision = Math.pow(10, MATH_DEGREES_OF_PRECISION);
        return Math.round(precision * input) / precision;
    }
}