package com.munifrog.design.tetheredtenttriangulator;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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

    private static final int STATE_CONFIG_EQUILATERAL = 0;
    private static final int STATE_CONFIG_ISOSCELES = 1;
    private static final int STATE_CONFIG_SCALENE = 2;
    private static final int STATE_CONFIG_MAX = STATE_CONFIG_SCALENE;
    private static final int STATE_ROTATION_ABC = 0;
    private static final int STATE_ROTATION_BCA = 1;
    private static final int STATE_ROTATION_CAB = 2;
    private static final int STATE_ROTATION_ACB = 3;
    private static final int STATE_ROTATION_CBA = 4;
    private static final int STATE_ROTATION_BAC = 5;
    private static final int STATE_ROTATION_MAX = STATE_ROTATION_BAC;

    private static final double MATH_SQUARE_ROOT_OF_THREE = Math.sqrt(3);
    private static final double MATH_BASE_LENGTH_N = 200;

    private static final double MATH_ANGLE_PRECISION_ALLOWANCE = 0.001;
    private static final double MATH_ANGLE_FULL_CIRCLE = Math.PI * 2;
    private static final double MATH_RADIAN_2_DEGREE_MULTIPLIER = 180.0 / Math.PI;

    private final Paint tetherPaint;
    private final Paint perimeterPaint;
    private final Paint treePaint;
    private final Paint labelPaint;

    private float mRadiusTetherSize;
    private float mRadiusSelectionSize;
    // When calculating the distance to the nearest tree, we can skip the square root computation
    // knowing that (x1-x0)^2 + (y1-y0)^2 <= selectionRadius^2
    private float mRadiusSelectionRangeSquared; // Avoiding computation by leaving squared

    private float [] mTetherCenter = new float[2];
    private float [] mPlatformCoordinates = new float[2];
    private float [][] mTethers = new float[3][2];

    private int mStateConfiguration = STATE_CONFIG_EQUILATERAL;
    private int mStateRotation = STATE_ROTATION_ABC;
    private int mStateTether = TETHER_SELECTION_NONE;
    private int mStatePlatform = DRAW_PLATFORM_ENABLED;
    private long mPreviousComputation;

    public Clearing() {
        // Set up color and text size
        tetherPaint = new Paint();
        tetherPaint.setARGB(255, 127, 127, 127);
        tetherPaint.setStrokeWidth(10);

        labelPaint = new Paint();
        labelPaint.setARGB(255, 255, 0, 0);
        labelPaint.setTextSize(64f);
        labelPaint.setStrokeWidth(4);

        perimeterPaint = new Paint();
        perimeterPaint.setARGB(255, 255, 0, 0);
        perimeterPaint.setStyle(Paint.Style.STROKE);
        perimeterPaint.setPathEffect(new DashPathEffect(new float[] {20f, 40f}, 10f));
        perimeterPaint.setStrokeWidth(5);

        treePaint = new Paint();
        treePaint.setARGB(255, 193, 154, 107);

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
        float mSmallestDimen = Math.min(width, height) / 2.0f;

        int centerX = width/2;
        int centerY = height/2;
        mRadiusTetherSize = mSmallestDimen / 25;
        mRadiusSelectionSize = mRadiusTetherSize * 2;
        mRadiusSelectionRangeSquared = mRadiusTetherSize * mRadiusTetherSize * 9;

        mTetherCenter[0] = centerX;
        mTetherCenter[1] = centerY;

        configEquilateral();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        getPlatformCenterOccasionally();
        drawTethers(canvas);
        drawConnections(canvas);
        drawStakes(canvas);
    }

    private void drawConnections(Canvas canvas) {
        canvas.drawLine(mTethers[0][0], mTethers[0][1], mTethers[1][0], mTethers[1][1], perimeterPaint);
        canvas.drawLine(mTethers[1][0], mTethers[1][1], mTethers[2][0], mTethers[2][1], perimeterPaint);
        canvas.drawLine(mTethers[2][0], mTethers[2][1], mTethers[0][0], mTethers[0][1], perimeterPaint);

        float [] mMid12 = { (mTethers[1][0] + mTethers[2][0]) / 2, (mTethers[1][1] + mTethers[2][1]) / 2 }; // a
        float [] mMid20 = { (mTethers[2][0] + mTethers[0][0]) / 2, (mTethers[2][1] + mTethers[0][1]) / 2 }; // b
        float [] mMid01 = { (mTethers[0][0] + mTethers[1][0]) / 2, (mTethers[0][1] + mTethers[1][1]) / 2 }; // c
    }

    private void drawStakes(Canvas canvas) {
        float mCurrentRadius;
        for (int i = 0; i < 3; i++) {
            mCurrentRadius = (mStateTether == i) ? mRadiusSelectionSize : mRadiusTetherSize;
            canvas.drawCircle(
                    mTethers[i][0],
                    mTethers[i][1],
                    mCurrentRadius,
                    treePaint
            );
        }
    }

    private void drawTethers(Canvas canvas) {
        if (mStatePlatform == DRAW_PLATFORM_ENABLED) {
            canvas.drawLine(mTethers[0][0], mTethers[0][1], mPlatformCoordinates[0], mPlatformCoordinates[1], tetherPaint);
            canvas.drawLine(mTethers[1][0], mTethers[1][1], mPlatformCoordinates[0], mPlatformCoordinates[1], tetherPaint);
            canvas.drawLine(mTethers[2][0], mTethers[2][1], mPlatformCoordinates[0], mPlatformCoordinates[1], tetherPaint);

            canvas.drawCircle(
                    mPlatformCoordinates[0],
                    mPlatformCoordinates[1],
                    mRadiusTetherSize / 2,
                    tetherPaint
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
        double threshold = MATH_ANGLE_FULL_CIRCLE / 3.0;

        double diff01x = mTethers[0][0] - mTethers[1][0]; // Ax - Bx
        double diff01y = mTethers[0][1] - mTethers[1][1]; // Ay - By
        double diff12x = mTethers[1][0] - mTethers[2][0]; // Bx - Cx
        double diff12y = mTethers[1][1] - mTethers[2][1]; // By - Cy
        double diff20x = mTethers[2][0] - mTethers[0][0]; // Cx - Ax
        double diff20y = mTethers[2][1] - mTethers[0][1]; // Cy - Ay
        double dist01sq = diff01x * diff01x + diff01y * diff01y;
        double dist12sq = diff12x * diff12x + diff12y * diff12y;
        double dist20sq = diff20x * diff20x + diff20y * diff20y;
        double dist01 = Math.sqrt(dist01sq); // c
        double dist12 = Math.sqrt(dist12sq); // a
        double dist20 = Math.sqrt(dist20sq); // b

        double angle102 = Math.acos((dist20sq + dist01sq - dist12sq) / 2.0 / dist20 / dist01);  // A = 0
        double angle210  = Math.acos((dist12sq + dist01sq - dist20sq) / 2.0 / dist12 / dist01); // B = 1
        double angle021 = Math.acos((dist12sq + dist20sq - dist01sq) / 2.0 / dist12 / dist20);  // C = 2
        if (angle102 < threshold && angle210 < threshold && angle021 < threshold) {
            mStatePlatform = DRAW_PLATFORM_ENABLED;

            // Equilateral triangle (simple) case: 2P0, 1P2 and 0P1 are all 120(o) or 2 * PI / 3
            // Rather than computing the sines of these angles, could compute them ahead of time and load per tent
            double angle2P0 = MATH_ANGLE_FULL_CIRCLE / 3.0; // rho
            double sine2P0 = Math.sin(angle2P0);
            double angle1P2 = MATH_ANGLE_FULL_CIRCLE / 3.0; // lambda
            double sine1P2 = Math.sin(angle1P2);
            //double angle0P1 = MATH_ANGLE_FULL_CIRCLE / 3.0; // psi
            //double sine0P1 = Math.sin(angle0P1);

            double angleTheta =  MATH_ANGLE_FULL_CIRCLE - angle2P0 - angle1P2 - angle021;
            double angleP12 = Math.atan(dist20 * Math.sin(angleTheta) * sine1P2 / (dist12 * sine2P0 + dist20 * sine1P2 * Math.cos(angleTheta)));
            double angleP21 = Math.PI - angleP12 - angle1P2;
            double angleP20 = angle021 - angleP21;

            //double dist0P = dist20 * Math.sin(angleP20) / sine2P0;
            //double dist1P = dist12 * Math.sin(angleP21) / sine1P2;
            double dist2P = dist12 * Math.sin(angleP12) / sine1P2;

            // Determine the location of the platform center (Q is for quadrant or Y=0 line)
            double angleQ21 = getDirection(dist12, diff12x, diff12y);
            double angleQ20 = getDirection(dist20, -diff20x, -diff20y);

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

    private void configEquilateral() {
        mTethers[0][0] = (float)(mTetherCenter[0]);
        mTethers[0][1] = (float)(mTetherCenter[1] + MATH_BASE_LENGTH_N);
        mTethers[1][0] = (float)(mTetherCenter[0] - MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2);
        mTethers[1][1] = (float)(mTetherCenter[1] - MATH_BASE_LENGTH_N / 2);
        mTethers[2][0] = (float)(mTetherCenter[0] + MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2);
        mTethers[2][1] = (float)(mTetherCenter[1] - MATH_BASE_LENGTH_N / 2);
    }

    private void configScalene() {
        mTethers[0][0] = (float)(mTetherCenter[0] - MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2);
        mTethers[0][1] = (float)(mTetherCenter[1] + MATH_BASE_LENGTH_N + MATH_BASE_LENGTH_N / 2);
        mTethers[1][0] = (float)(mTetherCenter[0] + MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N);
        mTethers[1][1] = (float)(mTetherCenter[1] + MATH_BASE_LENGTH_N + MATH_BASE_LENGTH_N);
        mTethers[2][0] = (float)(mTetherCenter[0]);
        mTethers[2][1] = (float)(mTetherCenter[1] + MATH_BASE_LENGTH_N - 3 * MATH_BASE_LENGTH_N);
    }

    private void configIsosceles() {
        mTethers[0][0] = (float)(mTetherCenter[0] - MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N);
        mTethers[0][1] = (float)(mTetherCenter[1] + MATH_BASE_LENGTH_N + MATH_BASE_LENGTH_N);
        mTethers[1][0] = (float)(mTetherCenter[0] + MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N);
        mTethers[1][1] = (float)(mTetherCenter[1] + MATH_BASE_LENGTH_N + MATH_BASE_LENGTH_N);
        mTethers[2][0] = (float)(mTetherCenter[0]);
        mTethers[2][1] = (float)(mTetherCenter[1] + MATH_BASE_LENGTH_N - 3 * MATH_BASE_LENGTH_N);
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

    private void configFlip() {
        // Switching any two corners is sufficient to flip the orientation; rotate afterwards
        float tempX = mTethers[0][0];
        float tempY = mTethers[0][1];
        mTethers[0][0] = mTethers[2][0];
        mTethers[0][1] = mTethers[2][1];
        mTethers[2][0] = tempX;
        mTethers[2][1] = tempY;
    }

    public void nextConfiguration() {
        mStateRotation++;
        if (mStateRotation > STATE_ROTATION_MAX) {
            mStateRotation = STATE_ROTATION_ABC;
            mStateConfiguration++;
            if (mStateConfiguration > STATE_CONFIG_MAX) {
                mStateConfiguration = STATE_CONFIG_EQUILATERAL;
                configEquilateral();
            } else {
                switch(mStateConfiguration) {
                    default:
                    case STATE_CONFIG_EQUILATERAL:
                        break;
                    case STATE_CONFIG_ISOSCELES:
                        configIsosceles();
                        break;
                    case STATE_CONFIG_SCALENE:
                        configScalene();
                        break;
                }
            }
        } else {
            switch (mStateRotation) {
                default:
                case STATE_ROTATION_ABC:
                    break;
                case STATE_ROTATION_BCA:
                case STATE_ROTATION_CAB:
                case STATE_ROTATION_CBA:
                case STATE_ROTATION_BAC:
                    configRotate();
                    break;
                case STATE_ROTATION_ACB:
                    configFlip(); // only one flip is necessary
                    break;
            }
        }
        invalidateSelf();
    }

    private double radians2Degrees(double radians) {
        return forcePrecision(radians * MATH_RADIAN_2_DEGREE_MULTIPLIER);
    }

    private double forcePrecision(double input) {
        double precision = Math.pow(10, 2);
        return Math.round(precision * input) / precision;
    }
}