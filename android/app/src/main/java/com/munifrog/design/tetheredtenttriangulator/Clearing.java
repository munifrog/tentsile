package com.munifrog.design.tetheredtenttriangulator;

import static com.munifrog.design.tetheredtenttriangulator.Util.MATH_PRECISION_UNITS;
import static com.munifrog.design.tetheredtenttriangulator.Util.MATH_PRECISION_TENTHS;
import static com.munifrog.design.tetheredtenttriangulator.Util.MATH_PRECISION_HUNDREDTHS;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

public class Clearing
        extends Drawable
        implements PlatformCenterRun.PlatformCenterListener
{
    interface ClearingListener {
        void computePlatformCenter(PlatformCenterRun run);
        void triangleHasFlipped(boolean isFlipped);
        void fineTuneLabelStart();
    }

    private static final int TETHER_SELECTION_NONE = -1;
    private static final int TETHER_SELECTION_DECIDING = -2;
    private static final int TETHER_SELECTION_A = 0;
    private static final int TETHER_SELECTION_B = 1;
    private static final int TETHER_SELECTION_C = 2;
    private static final int TETHER_SELECTION_SHIFT = 3;
    private static final int TETHER_SELECTION_AB_WAIT = 4;
    private static final int TETHER_SELECTION_BC_WAIT = 5;
    private static final int TETHER_SELECTION_CA_WAIT = 6;
    private static final int TETHER_SELECTION_AB = 7;
    private static final int TETHER_SELECTION_BC = 8;
    private static final int TETHER_SELECTION_CA = 9;
    private static final int TETHER_SELECTION_ROTATE = 10;

    private static final int DRAW_PLATFORM_TOO_CLOSE = 0;
    private static final int DRAW_PLATFORM_ENABLED = 1;

    private static final int DRAW_TETHERS_TOO_CLOSE = 0;
    private static final int DRAW_TETHERS_ENABLED = 1;

    private static final double MATH_ANGLE_FULL_CIRCLE = Math.PI * 2;
    private static final double MATH_ANGLE_ONE_THIRD_CIRCLE = 2 * Math.PI / 3;
    private static final double MATH_ANGLE_RADIANS_TO_DEGREES = 180 / Math.PI;
    private static final double MATH_FEET_TO_METERS_CONVERSION = 0.3048;
    private static final double MATH_METERS_TO_FEET_CONVERSION = 3.2808399;
    private static final double MATH_METERS_ACROSS_SMALLEST_DIMEN = 5.0;
    private static final double MATH_METERS_TOO_CLOSE = 0.7;
    private static final double MATH_METERS_TOO_CLOSE_SQUARED =
            MATH_METERS_TOO_CLOSE * MATH_METERS_TOO_CLOSE;
    // The tether holds an 8 kilogram tent and is under 300 kilograms of force:
    private static final double MATH_INCLINATION_RADIANS = Math.asin(8.0 / 300.0);
    private static final double MATH_INCLINATION_MULTIPLIER = 1.0 / Math.cos(MATH_INCLINATION_RADIANS);
    private static final double MATH_THREE_EIGHTHS_INCH_TO_FEET_CONVERSION = 0.03125;

    private static final double MATH_SEEKBAR_POINT_00 = 0.0;
    private static final double MATH_SEEKBAR_POINT_01 = 50.0;
    private static final double MATH_SEEKBAR_POINT_02 = 75.0;
    private static final double MATH_SEEKBAR_POINT_03 = 100.0;
    private static final double MATH_SCALE_POINT_00 = 1.0;
    private static final double MATH_SCALE_POINT_01 = 3.0;
    private static final double MATH_SCALE_POINT_02 = 8.0;
    private static final double MATH_SCALE_POINT_03 = 10.0;
    private static final double MATH_SCALE_SLOPE_00_01 = (MATH_SCALE_POINT_01 - MATH_SCALE_POINT_00) /
            (MATH_SEEKBAR_POINT_01 - MATH_SEEKBAR_POINT_00);
    private static final double MATH_SCALE_SLOPE_01_02 = (MATH_SCALE_POINT_02 - MATH_SCALE_POINT_01) /
            (MATH_SEEKBAR_POINT_02 - MATH_SEEKBAR_POINT_01);
    private static final double MATH_SCALE_SLOPE_02_03 = (MATH_SCALE_POINT_03 - MATH_SCALE_POINT_02) /
            (MATH_SEEKBAR_POINT_03 - MATH_SEEKBAR_POINT_02);

    private final Paint mTetherPaintPlatform;
    private final Paint mTetherPaintStraps;
    private final Paint mTetherPaintExtensions;
    private final Paint mPerimeterPaint;
    private final Paint mPlatformPaint;
    private final Paint mTreePaint;
    private final Paint mLabelConnectionPaint;
    private final Paint mLabelPlatformPaint;

    private boolean mIsImperial;
    private boolean mSetupFreshConfiguration;

    private float mRadiusTetherSize;
    private float mRadiusSelectionSize;
    // When calculating the distance to the nearest tree, we can skip the square root computation
    // knowing that (x1-x0)^2 + (y1-y0)^2 <= selectionRadius^2
    private float mRadiusSelectionRangeSquared; // Avoiding computation by leaving squared
    private float mSmallestDimen;

    private String mStringMeters;
    private String mStringImperial;
    private int mStringPrecision;

    private float [] mPlatformCoordinates = new float[2];
    private final float [] mSnapshotPlatform = new float[2];
    private final float [][] mSnapshotTethers = new float[3][2];
    private final float [][] mSnapshotAngles = new float[4][2];
    private final float [][] mTethers = new float[3][2];
    private double [][] mPlatformExtremities = new double[3][2];
    private double [][] mTransExtremities = new double[3][2];
    private double mDist01; // c
    private double mDist12; // a
    private double mDist20; // b
    private double mMetersPerPixel;
    private double mPixelsPerMeter;
    private double mScaledMetersPerPixel;
    private double mScaledPixelsPerMeter;
    private boolean mLatestFlippedState = false;
    private boolean mTetherOrientationFlips = false;
    private boolean mComputeTetherCenterAgain = false;
    private boolean mComputingTetherCenter = false;
    private double mStrapLength;
    private double mTreeCircumference;
    private double mPlatformTetherAngleSmall = MATH_ANGLE_ONE_THIRD_CIRCLE;
    private double mPlatformTetherAngleLarge = MATH_ANGLE_ONE_THIRD_CIRCLE;
    private double mAlteredValue;

    private int mStateTether = TETHER_SELECTION_NONE;
    private int mDrawTethers = DRAW_TETHERS_ENABLED;
    private int mDrawPlatform = DRAW_PLATFORM_ENABLED;

    private final int [] mCenter = new int[2];

    private final ClearingListener mViewOwner;
    private Path mPlatformPath = new Path();
    private final Path mTransformedPath = new Path();

    private Symbol mSymbolVerbosity = Symbol.safe;
    private Drawable mIconCannot = null;
    private Drawable mIconTricky = null;
    private Drawable mIconWarn = null;

    public Clearing(ClearingListener listener) {
        mViewOwner = listener;

        // Set up color and text size
        mTetherPaintPlatform = new Paint();
        mTetherPaintPlatform.setARGB(255, 127, 127, 127); // grey
        mTetherPaintPlatform.setStrokeWidth(10);
        mTetherPaintPlatform.setStrokeCap(Paint.Cap.ROUND);

        mTetherPaintStraps = new Paint();
        mTetherPaintStraps.setARGB(255, 33, 150, 243); // blue
        mTetherPaintStraps.setStrokeWidth(10);
        mTetherPaintStraps.setStrokeCap(Paint.Cap.ROUND);

        mTetherPaintExtensions = new Paint();
        mTetherPaintExtensions.setARGB(255, 255, 235, 59); // yellow
        mTetherPaintExtensions.setStrokeWidth(10);
        mTetherPaintExtensions.setStrokeCap(Paint.Cap.ROUND);

        mLabelConnectionPaint = new Paint();
        mLabelConnectionPaint.setARGB(255, 254, 255, 57);
        mLabelConnectionPaint.setTextSize(72f);
        mLabelConnectionPaint.setTextAlign(Paint.Align.CENTER);
        mLabelConnectionPaint.setStrokeWidth(4);
        mLabelConnectionPaint.setTypeface(Typeface.DEFAULT);

        mLabelPlatformPaint = new Paint();
        mLabelPlatformPaint.setARGB(255, 255, 255, 255);
        mLabelPlatformPaint.setTextSize(72f);
        mLabelPlatformPaint.setTextAlign(Paint.Align.CENTER);
        mLabelPlatformPaint.setStrokeWidth(4);
        mLabelPlatformPaint.setTypeface(Typeface.DEFAULT);

        mPerimeterPaint = new Paint();
        mPerimeterPaint.setARGB(255, 100, 100, 100);
        mPerimeterPaint.setStyle(Paint.Style.STROKE);
        mPerimeterPaint.setPathEffect(new DashPathEffect(new float[] {40f, 20f, 4f, 20f}, 0f));
        mPerimeterPaint.setStrokeWidth(8);
        mPerimeterPaint.setStrokeCap(Paint.Cap.SQUARE);

        mPlatformPaint = new Paint();
        mPlatformPaint.setARGB(127, 255, 255, 255);
        mPlatformPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPlatformPaint.setStrokeWidth(0);
        mPlatformPaint.setStrokeCap(Paint.Cap.ROUND);

        mTreePaint = new Paint();
        mTreePaint.setARGB(255, 193, 154, 107);

        mSetupFreshConfiguration = true;
        mIsImperial = false;

        setPlatformSymmetricAngle();
    }

    public void setSymbolVerbosity(Symbol level) {
        mSymbolVerbosity = level;
        invalidateSelf();
    }

    public void setUnitStrings(String meters, String imperial, int precision) {
        mStringMeters = meters;
        mStringImperial = imperial;
        mStringPrecision = precision;
    }

    public void pointSelection(int x, int y) {
        switch (mStateTether) {
            case TETHER_SELECTION_NONE:
                mStateTether = TETHER_SELECTION_DECIDING;
                mStateTether = getSelection(x, y);
                if (mStateTether == TETHER_SELECTION_SHIFT) {
                    saveInitialPositions();
                    invalidateSelf();
                } else if (mStateTether == TETHER_SELECTION_ROTATE) {
                    saveInitialAngles(x, y);
                    invalidateSelf();
                }
                break;
            case TETHER_SELECTION_AB:
            case TETHER_SELECTION_BC:
            case TETHER_SELECTION_CA:
                resetSelection();
                break;
            default:
                // This should not happen
                break;
        }
    }

    public int getSelection(int x, int y) {
        int selectState = TETHER_SELECTION_DECIDING;
        float smallestDeltaSquared = mRadiusSelectionRangeSquared;
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
                selectState = TETHER_SELECTION_A + i;
                smallestDeltaSquared = deltaSquared;
            }
        }
        if (mDrawPlatform == DRAW_PLATFORM_ENABLED) {
            // Allow the perimeter labels to be selected
            float centerX;
            float centerY;
            int secondIndex;
            for (int i = 0; i < 3; i++) {
                secondIndex = i < 2 ? i + 1 : 0;
                // Determine which labels are within the range to be selected
                centerX = (mTethers[i][0] + mTethers[secondIndex][0]) / 2.0f;
                centerY = (mTethers[i][1] + mTethers[secondIndex][1]) / 2.0f;
                deltaX = x - centerX;
                deltaY = y - centerY;
                // Square everything to avoid unnecessary (and expensive) square root computation
                deltaSquared = deltaX * deltaX + deltaY * deltaY;
                if (deltaSquared < smallestDeltaSquared) {
                    // Record the closest tree at this stage
                    selectState = TETHER_SELECTION_AB_WAIT + i;
                    smallestDeltaSquared = deltaSquared;
                }
            }
            // ... also allowing the tether-center to be selected
            deltaX = x - mPlatformCoordinates[0];
            deltaY = y - mPlatformCoordinates[1];
            deltaSquared = deltaX * deltaX + deltaY * deltaY;
            if (deltaSquared < smallestDeltaSquared) {
                selectState = TETHER_SELECTION_SHIFT;
            }
            if (selectState == TETHER_SELECTION_DECIDING) {
                selectState = TETHER_SELECTION_ROTATE;
            }
        }
        return selectState;
    }

    private void saveInitialPositions() {
        mSnapshotPlatform[0] = mPlatformCoordinates[0];
        mSnapshotPlatform[1] = mPlatformCoordinates[1];
        mSnapshotTethers[0][0] = mTethers[0][0];
        mSnapshotTethers[0][1] = mTethers[0][1];
        mSnapshotTethers[1][0] = mTethers[1][0];
        mSnapshotTethers[1][1] = mTethers[1][1];
        mSnapshotTethers[2][0] = mTethers[2][0];
        mSnapshotTethers[2][1] = mTethers[2][1];
    }

    private void updateConfigurationPositions(int x, int y) {
        float shiftX = x - mSnapshotPlatform[0];
        float shiftY = y - mSnapshotPlatform[1];
        mTethers[0][0] = mSnapshotTethers[0][0] + shiftX;
        mTethers[0][1] = mSnapshotTethers[0][1] + shiftY;
        mTethers[1][0] = mSnapshotTethers[1][0] + shiftX;
        mTethers[1][1] = mSnapshotTethers[1][1] + shiftY;
        mTethers[2][0] = mSnapshotTethers[2][0] + shiftX;
        mTethers[2][1] = mSnapshotTethers[2][1] + shiftY;
        mPlatformCoordinates[0] = x; // mSnapshotPlatform[0] + shiftX
        mPlatformCoordinates[1] = y; // mSnapshotPlatform[1] + shiftY
    }

    private void saveInitialAngles(int x, int y) {
        double deltaX, deltaY, hypotenuse;
        for (int i = 0; i < 3; i++) {
            deltaX = mTethers[i][0] - mPlatformCoordinates[0];
            deltaY = mTethers[i][1] - mPlatformCoordinates[1];
            hypotenuse = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            mSnapshotAngles[i][1] = (float) hypotenuse;
            mSnapshotAngles[i][0] = (float) Util.getDirection(hypotenuse, deltaX, deltaY);
        }
        deltaX = x - mPlatformCoordinates[0];
        deltaY = y - mPlatformCoordinates[1];
        hypotenuse = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        mSnapshotAngles[3][1] = (float) hypotenuse;
        mSnapshotAngles[3][0] = (float) Util.getDirection(hypotenuse, deltaX, deltaY);
    }

    private void updateConfigurationAngles(int x, int y) {
        double deltaX, deltaY, hypotenuse, angleAltered, angleNow, angleDelta;
        deltaX = x - mPlatformCoordinates[0];
        deltaY = y - mPlatformCoordinates[1];
        hypotenuse = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        angleNow = Util.getDirection(hypotenuse, deltaX, deltaY);
        angleDelta = angleNow - mSnapshotAngles[3][0];
        for (int i = 0; i < 3; i++) {
            angleAltered = mSnapshotAngles[i][0] + angleDelta;
            mTethers[i][0] = (float)(mPlatformCoordinates[0] + mSnapshotAngles[i][1] * Math.cos(angleAltered));
            mTethers[i][1] = (float)(mPlatformCoordinates[1] + mSnapshotAngles[i][1] * Math.sin(angleAltered));
        }
    }

    public String getOffsetString(int offset) {
        double pixels;
        if (mStateTether == TETHER_SELECTION_AB) {
            pixels = mDist01;
        } else if (mStateTether == TETHER_SELECTION_BC) {
            pixels = mDist12;
        } else if (mStateTether == TETHER_SELECTION_CA) {
            pixels = mDist20;
        } else {
            pixels = 0.0;
        }
        double offsetValue = offset * (mIsImperial ? MATH_THREE_EIGHTHS_INCH_TO_FEET_CONVERSION : 0.01);
        mAlteredValue = getPixelsToMeasure(pixels) + offsetValue;
        return getMeasurementString(mAlteredValue);
    }

    public void resetSelection() {
        mStateTether = TETHER_SELECTION_NONE;
    }

    public void releaseSelection(int x, int y) {
        switch (mStateTether) {
            case TETHER_SELECTION_A:
            case TETHER_SELECTION_B:
            case TETHER_SELECTION_C:
            case TETHER_SELECTION_ROTATE:
            case TETHER_SELECTION_SHIFT:
            case TETHER_SELECTION_DECIDING:
                resetSelection();
                computePlatformCenter(); // In case the last computation was prevented by timing
                break;
            case TETHER_SELECTION_AB_WAIT:
            case TETHER_SELECTION_BC_WAIT:
            case TETHER_SELECTION_CA_WAIT:
                int currentSelection = getSelection(x, y);
                if (currentSelection == mStateTether) {
                    mStateTether += 3; // Shifts from _WAIT into _READY
                    mViewOwner.fineTuneLabelStart();
                } else {
                    resetSelection();
                }
                break;
            default:
                break;
        }
    }

    public void updateSelection(int x, int y) {
        switch (mStateTether) {
            case TETHER_SELECTION_A:
            case TETHER_SELECTION_B:
            case TETHER_SELECTION_C:
                // TODO: Is this where I can enforce bounds on the touch point?
                mTethers[mStateTether][0] = x;
                mTethers[mStateTether][1] = y;
                getPlatformCenterOccasionally();
                break;
            case TETHER_SELECTION_AB_WAIT:
            case TETHER_SELECTION_BC_WAIT:
            case TETHER_SELECTION_CA_WAIT:
                // Wait for the touch event to end before doing anything
                break;
            case TETHER_SELECTION_AB:
            case TETHER_SELECTION_BC:
            case TETHER_SELECTION_CA:
                makeAnchorsMatchMeasurements();
                resetSelection();
                getPlatformCenterOccasionally();
                break;
            case TETHER_SELECTION_SHIFT:
                updateConfigurationPositions(x, y);
                invalidateSelf();
                break;
            case TETHER_SELECTION_ROTATE:
                updateConfigurationAngles(x, y);
                invalidateSelf();
                break;
            default:
                break;
        }
    }

    void setFullWindowBounds(Rect bounds) {
        int width = bounds.width();
        int height = bounds.height();
        mSmallestDimen = Math.min(width, height);

        int centerX = width/2;
        int centerY = height/2;
        mRadiusTetherSize = mSmallestDimen / 50;
        mRadiusSelectionSize = mRadiusTetherSize * 2;
        mRadiusSelectionRangeSquared = mRadiusTetherSize * mRadiusTetherSize * 9;
        mMetersPerPixel = MATH_METERS_ACROSS_SMALLEST_DIMEN / mSmallestDimen;
        mPixelsPerMeter = mSmallestDimen / MATH_METERS_ACROSS_SMALLEST_DIMEN;

        mCenter[0] = centerX;
        mCenter[1] = centerY;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        int[] previousCenter = mCenter.clone();
        mCenter[0] = bounds.width() / 2;
        mCenter[1] = bounds.height() / 2;
        int[] shiftToNewCenter = {
                mCenter[0] - previousCenter[0],
                mCenter[1] - previousCenter[1]
        };

        mTethers[0][0] += shiftToNewCenter[0];
        mTethers[0][1] += shiftToNewCenter[1];
        mTethers[1][0] += shiftToNewCenter[0];
        mTethers[1][1] += shiftToNewCenter[1];
        mTethers[2][0] += shiftToNewCenter[0];
        mTethers[2][1] += shiftToNewCenter[1];

        mPlatformCoordinates[0] += shiftToNewCenter[0];
        mPlatformCoordinates[1] += shiftToNewCenter[1];

        if (mSetupFreshConfiguration) {
            configDefault();
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        // Draw order:
        //  Lines between stakes
        drawConnectionLines(canvas);
        //  Tether-center computed
        computePlatform();
        //  Platform-tethers (if showing platform)
        Symbol[] symbolsTethers = drawPlatformTethers(canvas);
        //  Platform (if showing platform)
        drawPlatform(canvas);
        //  Tethers from platform-corners and stakes (if showing platform)
        //  Tethers from tether-center and stakes (if not showing platform)
        //  Selection points (anchors and tether-center)
        drawSelectionPoints(canvas);
        //  Distance between stakes
        drawConnectionLabels(canvas);
        //  Distance from platform-corners to stakes
        AnchorDetails anchorDetails = computeDistancesAndSymbols();
        //  Draw any symbols on the anchor points
        Symbol[] symbols = mergeSymbols(anchorDetails.symbols, symbolsTethers);
        drawAnchorSymbols(canvas, symbols);
        // Draw the distances last
        drawPlatformLabels(canvas, anchorDetails.distances);
    }

    private void drawConnectionLines(Canvas canvas) {
        Path path = new Path();
        path.moveTo(mTethers[0][0], mTethers[0][1]);
        path.lineTo(mTethers[1][0], mTethers[1][1]);
        path.lineTo(mTethers[2][0], mTethers[2][1]);
        path.lineTo(mTethers[0][0], mTethers[0][1]);
        canvas.drawPath(path, mPerimeterPaint);
    }

    private void drawConnectionLabels(Canvas canvas) {
        canvas.drawText(
                getMeasurementString(getPixelsToMeasure(mDist01)),
                (mTethers[0][0] + mTethers[1][0]) / 2,
                (mTethers[0][1] + mTethers[1][1]) / 2,
                mLabelConnectionPaint
        );
        canvas.drawText(
                getMeasurementString(getPixelsToMeasure(mDist12)),
                (mTethers[1][0] + mTethers[2][0]) / 2,
                (mTethers[1][1] + mTethers[2][1]) / 2,
                mLabelConnectionPaint
        );
        canvas.drawText(
                getMeasurementString(getPixelsToMeasure(mDist20)),
                (mTethers[2][0] + mTethers[0][0]) / 2,
                (mTethers[2][1] + mTethers[0][1]) / 2,
                mLabelConnectionPaint
        );
    }

    private void drawSelectionPoints(Canvas canvas) {
        float [] radii = {
                mRadiusTetherSize,
                mRadiusTetherSize,
                mRadiusTetherSize,
                mRadiusTetherSize
        };
        if ((mStateTether >= TETHER_SELECTION_A) && (mStateTether <= TETHER_SELECTION_SHIFT)) {
            radii[mStateTether] = mRadiusSelectionSize;
        }
        for (int i = 0; i < 3; i++) {
            // Draw anchor/tether points
            canvas.drawCircle(
                    mTethers[i][0],
                    mTethers[i][1],
                    radii[i],
                    mTreePaint
            );
        }
        // Add what will appear as a knot at the tether-center
        if (mDrawPlatform == DRAW_PLATFORM_ENABLED) {
            canvas.drawCircle(
                    mPlatformCoordinates[0],
                    mPlatformCoordinates[1],
                    radii[TETHER_SELECTION_SHIFT],
                    mTetherPaintStraps
            );
        }
    }

    private void drawTethers(Canvas canvas) {
        if (mDrawTethers == DRAW_TETHERS_ENABLED) {
            canvas.drawLine(mTethers[0][0], mTethers[0][1], mPlatformCoordinates[0], mPlatformCoordinates[1], mTetherPaintPlatform);
            canvas.drawLine(mTethers[1][0], mTethers[1][1], mPlatformCoordinates[0], mPlatformCoordinates[1], mTetherPaintPlatform);
            canvas.drawLine(mTethers[2][0], mTethers[2][1], mPlatformCoordinates[0], mPlatformCoordinates[1], mTetherPaintPlatform);

            canvas.drawCircle(
                    mPlatformCoordinates[0],
                    mPlatformCoordinates[1],
                    mRadiusTetherSize / 2,
                    mTetherPaintStraps
            );
        }
    }

    private void computePlatform() {
        if (mDrawTethers == DRAW_TETHERS_ENABLED) {
            double deltaX = mTethers[0][0] - mPlatformCoordinates[0];
            double deltaY = mTethers[0][1] - mPlatformCoordinates[1];
            double hypotenuse = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            double angle0 = Util.getDirection(hypotenuse, deltaX, deltaY);

            Matrix matrix = new Matrix();
            double scale = metersToPixels();
            matrix.setScale((float) scale, (float) scale);
            matrix.postRotate((float) (angle0 * MATH_ANGLE_RADIANS_TO_DEGREES));
            matrix.postTranslate(mPlatformCoordinates[0], mPlatformCoordinates[1]);

            mPlatformPath.transform(matrix, mTransformedPath);
            double[] translation = {mPlatformCoordinates[0], mPlatformCoordinates[1]};
            mTransExtremities = Util.shiftedCoordinates(mPlatformExtremities, angle0, metersToPixels(), translation);

            float diffAx = (float) getScaledPixelsToMeters(mPlatformCoordinates[0] - mTethers[0][0]);
            float diffAy = (float) getScaledPixelsToMeters(mPlatformCoordinates[1] - mTethers[0][1]);
            float diffBx = (float) getScaledPixelsToMeters(mPlatformCoordinates[0] - mTethers[1][0]);
            float diffBy = (float) getScaledPixelsToMeters(mPlatformCoordinates[1] - mTethers[1][1]);
            float diffCx = (float) getScaledPixelsToMeters(mPlatformCoordinates[0] - mTethers[2][0]);
            float diffCy = (float) getScaledPixelsToMeters(mPlatformCoordinates[1] - mTethers[2][1]);
            float squaredA = (diffAx * diffAx + diffAy * diffAy);
            float squaredB = (diffBx * diffBx + diffBy * diffBy);
            float squaredC = (diffCx * diffCx + diffCy * diffCy);
            mDrawPlatform = (squaredA >= MATH_METERS_TOO_CLOSE_SQUARED) &&
                    (squaredB >= MATH_METERS_TOO_CLOSE_SQUARED) &&
                    (squaredC >= MATH_METERS_TOO_CLOSE_SQUARED) ?
                    DRAW_PLATFORM_ENABLED : DRAW_PLATFORM_TOO_CLOSE;
        }
    }

    private int[] getIndexOrder() {
        int[] indices = { 0, 1, 2 };
        if (mTetherOrientationFlips) {
            indices[1] = 2;
            indices[2] = 1;
        }
        return indices;
    }

    private Symbol[] drawPlatformTethers(Canvas canvas) {
        Symbol[] symbols = new Symbol[3];
        if (mDrawPlatform == DRAW_PLATFORM_ENABLED) {
            int[] indices = getIndexOrder();
            for (int i = 0; i < 3; i++) {
                int index = indices[i];
                // Draw skeleton of the platform
                canvas.drawLine(
                        (float) mTransExtremities[index][0],
                        (float) mTransExtremities[index][1],
                        mPlatformCoordinates[0],
                        mPlatformCoordinates[1],
                        mTetherPaintPlatform
                );

                // For each strap, draw between the extremity and tree according to extensions
                // Use strap color for first 1ft (placing knot) then next 6m (or 4m), then extension
                // color, with knots every 6m
                Knots knots = Util.getTetherKnots(
                        getScaledPixelsToMeters(1.0),
                        mTransExtremities[index][0],
                        mTransExtremities[index][1],
                        mTethers[i][0],
                        mTethers[i][1],
                        mStrapLength,
                        6, // strap extension default
                        mTreeCircumference
                );
                float[][] strapKnots = knots.knots;
                // Since the symbol goes over the anchor, its index does not flip
                symbols[i] = knots.symbol;

                if (strapKnots.length > 1) {
                    Paint color;
                    int startIdx;
                    for (int j = strapKnots.length - 1; j > 0; j--) {
                        startIdx = j - 1;
                        color = getPaint(j);
                        canvas.drawLine(
                                strapKnots[startIdx][0],
                                strapKnots[startIdx][1],
                                strapKnots[j][0],
                                strapKnots[j][1],
                                color
                        );
                        canvas.drawCircle(
                                strapKnots[j][0],
                                strapKnots[j][1],
                                mRadiusTetherSize / 2,
                                color
                        );
                    }
                }
                canvas.drawCircle(
                        (float) mTransExtremities[index][0],
                        (float) mTransExtremities[index][1],
                        mRadiusTetherSize / 2,
                        mTetherPaintPlatform
                );
            }
        } else {
            drawTethers(canvas);
            symbols[0] = Symbol.safe;
            symbols[1] = Symbol.safe;
            symbols[2] = Symbol.safe;
        }
        return symbols;
    }

    private Paint getPaint(int index) {
        if (index < 3) {
            return mTetherPaintStraps;
        } else if ((index % 2) == 1) {
            return mTetherPaintExtensions;
        } else {
            return mTetherPaintStraps;
        }
    }

    private void drawPlatform(Canvas canvas) {
        if (mDrawPlatform == DRAW_PLATFORM_ENABLED) {
            canvas.drawPath(mTransformedPath, mPlatformPaint);
        }
    }

    private AnchorDetails computeDistancesAndSymbols() {
        Symbol[] symbols = new Symbol[3];
        double[] distances = new double[3];
        if (mDrawPlatform == DRAW_PLATFORM_ENABLED) {
            // Draw the platform and distances if platform not too far past the tether point
            int[] indices = getIndexOrder();
            int index1 = indices[1], index2 = indices[2];
            float diffExtremityAx = mPlatformCoordinates[0] - (float) mTransExtremities[0][0];
            float diffExtremityAy = mPlatformCoordinates[1] - (float) mTransExtremities[0][1];
            float squareExtremityA = diffExtremityAx * diffExtremityAx + diffExtremityAy * diffExtremityAy;
            float diffFullAx = mPlatformCoordinates[0] - mTethers[0][0];
            float diffFullAy = mPlatformCoordinates[1] - mTethers[0][1];
            float squareFullA = diffFullAx * diffFullAx + diffFullAy * diffFullAy;
            if (squareExtremityA < squareFullA) {
                float diffRemainAx = (float) mTransExtremities[0][0] - mTethers[0][0];
                float diffRemainAy = (float) mTransExtremities[0][1] - mTethers[0][1];
                distances[0] = Math.sqrt(diffRemainAx * diffRemainAx + diffRemainAy * diffRemainAy);
                symbols[0] = Symbol.safe;
            } else {
                distances[0] = -1;
                symbols[0] = Symbol.impossible;
            }

            float diffExtremityBx = mPlatformCoordinates[0] - (float) mTransExtremities[index1][0];
            float diffExtremityBy = mPlatformCoordinates[1] - (float) mTransExtremities[index1][1];
            float squareExtremityB = diffExtremityBx * diffExtremityBx + diffExtremityBy * diffExtremityBy;
            float diffFullBx = mPlatformCoordinates[0] - mTethers[1][0];
            float diffFullBy = mPlatformCoordinates[1] - mTethers[1][1];
            float squareFullB = diffFullBx * diffFullBx + diffFullBy * diffFullBy;
            if (squareExtremityB < squareFullB) {
                float diffRemainBx = (float) mTransExtremities[index1][0] - mTethers[1][0];
                float diffRemainBy = (float) mTransExtremities[index1][1] - mTethers[1][1];
                distances[index1] = Math.sqrt(diffRemainBx * diffRemainBx + diffRemainBy * diffRemainBy);
                // Since the symbol goes over the anchor, its index does not flip
                symbols[1] = Symbol.safe;
            } else {
                distances[index1] = -1;
                // Since the symbol goes over the anchor, its index does not flip
                symbols[1] = Symbol.impossible;
            }

            float diffExtremityCx = mPlatformCoordinates[0] - (float) mTransExtremities[index2][0];
            float diffExtremityCy = mPlatformCoordinates[1] - (float) mTransExtremities[index2][1];
            float squareExtremityC = diffExtremityCx * diffExtremityCx + diffExtremityCy * diffExtremityCy;
            float diffFullCx = mPlatformCoordinates[0] - mTethers[2][0];
            float diffFullCy = mPlatformCoordinates[1] - mTethers[2][1];
            float squareFullC = diffFullCx * diffFullCx + diffFullCy * diffFullCy;
            if (squareExtremityC < squareFullC) {
                float diffRemainCx = (float) mTransExtremities[index2][0] - mTethers[2][0];
                float diffRemainCy = (float) mTransExtremities[index2][1] - mTethers[2][1];
                distances[index2] = Math.sqrt(diffRemainCx * diffRemainCx + diffRemainCy * diffRemainCy);
                // Since the symbol goes over the anchor, its index does not flip
                symbols[2] = Symbol.safe;
            } else {
                distances[index2] = -1;
                // Since the symbol goes over the anchor, its index does not flip
                symbols[2] = Symbol.impossible;
            }
        } else {
            distances[0] = -1;
            distances[1] = -1;
            distances[2] = -1;
            symbols[0] = Symbol.safe;
            symbols[1] = Symbol.safe;
            symbols[2] = Symbol.safe;
        }
        return new AnchorDetails(distances, symbols);
    }

    private void drawPlatformLabels(Canvas canvas, double[] distances) {
        if (mDrawPlatform == DRAW_PLATFORM_ENABLED) {
            // Label the distance between the platform corner and tether location
            int[] indices = getIndexOrder();
            if (distances[0] > -1) {
                canvas.drawText(
                        getMeasurementString(scaledInclinedDimension(distances[0])),
                        (float) (mTransExtremities[0][0] + mTethers[0][0]) / 2f,
                        (float) (mTransExtremities[0][1] + mTethers[0][1]) / 2f,
                        mLabelPlatformPaint
                );
            }
            int index1 = indices[1];
            if (distances[index1] > -1) {
                canvas.drawText(
                        getMeasurementString(scaledInclinedDimension(distances[index1])),
                        (float) (mTransExtremities[index1][0] + mTethers[1][0]) / 2f,
                        (float) (mTransExtremities[index1][1] + mTethers[1][1]) / 2f,
                        mLabelPlatformPaint
                );
            }
            int index2 = indices[2];
            if (distances[index2] > -1) {
                canvas.drawText(
                        getMeasurementString(scaledInclinedDimension(distances[index2])),
                        (float) (mTransExtremities[index2][0] + mTethers[2][0]) / 2f,
                        (float) (mTransExtremities[index2][1] + mTethers[2][1]) / 2f,
                        mLabelPlatformPaint
                );
            }
        }
    }

    private void drawAnchorSymbols(Canvas canvas, Symbol[] symbols) {
        int verbosity = mSymbolVerbosity.ordinal();
        // Since the symbol goes over the anchor, its index does not flip
        for (int i = 0; i < 3; i++) {
            if (symbols[i].ordinal() > verbosity) {
                int x = Math.round(mTethers[i][0]);
                int y = Math.round(mTethers[i][1]);
                int halfIconSize = 50;
                Drawable icon;
                switch (symbols[i]) {
                    default:
                    case impossible:
                        icon = mIconCannot;
                        break;
                    case scarce:
                        icon = mIconWarn;
                        break;
                    case tricky:
                        icon = mIconTricky;
                        break;
                }
                if (icon != null) {
                    icon.setBounds(
                            x - halfIconSize,
                            y - halfIconSize,
                            x + halfIconSize,
                            y + halfIconSize
                    );
                    icon.draw(canvas);
                }
            }
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
    @SuppressWarnings("deprecation")
    public int getOpacity() {
        // Must be PixelFormat.UNKNOWN, TRANSLUCENT, TRANSPARENT, or OPAQUE
        return PixelFormat.OPAQUE;
    }

    private void getPlatformCenterOccasionally() {
        if (!mComputingTetherCenter) {
            getPlatformCenter();
        } else {
            mComputeTetherCenterAgain = true;
            getPerimeter();
        }
        invalidateSelf();
    }

    private void getPerimeter() {
        double [] perimeter = Util.getPerimeter(mTethers);
        mDist01 = perimeter[0];
        mDist12 = perimeter[1];
        mDist20 = perimeter[2];
    }

    private void getPlatformCenter() {
        mComputingTetherCenter = true;
        mComputeTetherCenterAgain = false;

        double [] perimeter = Util.getPerimeter(mTethers);
        mDist01 = perimeter[0];
        mDist12 = perimeter[1];
        mDist20 = perimeter[2];

        double angle102 = Math.acos((perimeter[5] + perimeter[3] - perimeter[4]) / 2.0 / mDist20 / mDist01);  // A = 0
        double angle210  = Math.acos((perimeter[4] + perimeter[3] - perimeter[5]) / 2.0 / mDist12 / mDist01); // B = 1
        double angle021 = Math.acos((perimeter[4] + perimeter[5] - perimeter[3]) / 2.0 / mDist12 / mDist20);  // C = 2

        if (
                angle102 < mPlatformTetherAngleSmall &&
                angle210 < mPlatformTetherAngleLarge &&
                angle021 < mPlatformTetherAngleLarge
        ) {
            mDrawTethers = DRAW_TETHERS_ENABLED;
            computePlatformCenter();
        } else {
            mComputingTetherCenter = false;
            mDrawTethers = DRAW_TETHERS_TOO_CLOSE;
            mDrawPlatform = DRAW_PLATFORM_TOO_CLOSE;
        }
    }

    private void computePlatformCenter() {
        mViewOwner.computePlatformCenter(new PlatformCenterRun(this, mTethers, mPlatformTetherAngleSmall));
    }

    @Override
    public void onPlatformComputed(float[] newPlatform, boolean orientation) {
        mComputingTetherCenter = false;
        mPlatformCoordinates = newPlatform;
        mTetherOrientationFlips = orientation;
        if (mComputeTetherCenterAgain) {
            getPlatformCenterOccasionally();
        }
        if (mTetherOrientationFlips != mLatestFlippedState) {
            mViewOwner.triangleHasFlipped(mTetherOrientationFlips);
        }
        mLatestFlippedState = mTetherOrientationFlips;
        invalidateSelf();
    }

    public void configDefault() {
        double lengthReference = 5 * mSmallestDimen / 12;
        double offset = -15 * Math.PI / 180;
        double currentAngle;
        for (int i = 0; i < 3; i++) {
            currentAngle = offset + MATH_ANGLE_FULL_CIRCLE / 3 * i;
            mTethers[i][0] = (float)(mCenter[0] + lengthReference * Math.cos(currentAngle));
            mTethers[i][1] = (float)(mCenter[1] + lengthReference * Math.sin(currentAngle));
        }
        getPlatformCenterOccasionally();
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

    public void setPlatformSymmetricAngle() {
        getPlatformCenterOccasionally();
    }

    public void setPlatformDrawPath(Platform platform) {
        mPlatformPath = platform.getPath();
        mPlatformExtremities = platform.getTetherPoints();
        mStrapLength = platform.getStrapLength();
        mTreeCircumference = platform.getCircumference();
        mPlatformTetherAngleSmall = platform.getTetherAngle();
        mPlatformTetherAngleLarge = (MATH_ANGLE_FULL_CIRCLE - mPlatformTetherAngleSmall) / 2.0;
        getPlatformCenterOccasionally();
    }

    public void rotatePlatform() {
        configRotate();
        getPlatformCenterOccasionally();
    }

    public boolean getIsImperial() {
        return mIsImperial;
    }

    public void setIsImperial(boolean imperial) {
        mIsImperial = imperial;
        invalidateSelf();
    }

    public void setSliderPosition(int position) {
        updateConvertedScale(position);
        invalidateSelf();
    }

    private void updateConvertedScale(int position) {
        double diff, offset, slope;
        if (position < MATH_SEEKBAR_POINT_01) {
            diff = position - MATH_SEEKBAR_POINT_00;
            offset = MATH_SCALE_POINT_00;
            slope = MATH_SCALE_SLOPE_00_01;
        } else if (position < MATH_SEEKBAR_POINT_02) {
            diff = position - MATH_SEEKBAR_POINT_01;
            offset = MATH_SCALE_POINT_01;
            slope = MATH_SCALE_SLOPE_01_02;
        } else { // if (position <= MATH_SEEKBAR_POINT_03) {
            diff = position - MATH_SEEKBAR_POINT_02;
            offset = MATH_SCALE_POINT_02;
            slope = MATH_SCALE_SLOPE_02_03;
        }
        double scale = offset + slope * diff;
        mScaledMetersPerPixel = scale / mPixelsPerMeter;
        mScaledPixelsPerMeter = mPixelsPerMeter / scale;
    }


    private String getMeasurementString(double measure) {
        // This function is meant for labels only
        if (mIsImperial) {
            double [] split = Util.getImperialWithMeterPrecision(measure, mStringPrecision);
            if (mStringPrecision == MATH_PRECISION_HUNDREDTHS) {
                return String.format(mStringImperial, split[0], split[1], split[2]);
            } else if (mStringPrecision == MATH_PRECISION_TENTHS) {
                return String.format(mStringImperial, split[0], split[1]);
            } else { //if (mStringPrecision == MATH_PRECISION_UNITS) {
                return String.format(mStringImperial, split[0]);
            }
        } else {
            return String.format(mStringMeters, measure);
        }
    }

    private double scaledInclinedDimension(double pixels) {
        // Convert the level-length to hypotenuse-length
        return MATH_INCLINATION_MULTIPLIER * getPixelsToMeasure(pixels);
    }

    private double getPixelsToMeasure(double pixels) {
        return getScaledPixelsToMeters(pixels) * (mIsImperial ? MATH_METERS_TO_FEET_CONVERSION : 1);
    }

    private double getMeasureToPixels(double measure) {
        return measure * mScaledPixelsPerMeter * (mIsImperial ? MATH_FEET_TO_METERS_CONVERSION : 1);
    }

    private double getScaledPixelsToMeters(double pixels) {
        return pixels * mScaledMetersPerPixel;
    }

    private double metersToPixels() {
        return mScaledPixelsPerMeter;
    }

    public float[][] getTetherPoints() {
        float[][] offsetTethers = new float[3][2];
        offsetTethers[0][0] = mTethers[0][0] - mCenter[0];
        offsetTethers[0][1] = mTethers[0][1] - mCenter[1];
        offsetTethers[1][0] = mTethers[1][0] - mCenter[0];
        offsetTethers[1][1] = mTethers[1][1] - mCenter[1];
        offsetTethers[2][0] = mTethers[2][0] - mCenter[0];
        offsetTethers[2][1] = mTethers[2][1] - mCenter[1];
        return offsetTethers;
    }

    public void setTetherPoints(float[][] newTethers) {
        mSetupFreshConfiguration = false;

        mTethers[0][0] = mCenter[0] + newTethers[0][0];
        mTethers[0][1] = mCenter[1] + newTethers[0][1];
        mTethers[1][0] = mCenter[0] + newTethers[1][0];
        mTethers[1][1] = mCenter[1] + newTethers[1][1];
        mTethers[2][0] = mCenter[0] + newTethers[2][0];
        mTethers[2][1] = mCenter[1] + newTethers[2][1];
    }

    public void setSymbolIcons(Drawable cannotIcon, Drawable warnIcon, Drawable trickyIcon) {
        // List icons from more to less restrictive
        mIconCannot = cannotIcon;
        mIconTricky = trickyIcon;
        mIconWarn = warnIcon;
    }

    private Symbol[] mergeSymbols(Symbol[] left, Symbol[] right) {
        Symbol[] symbols = new Symbol[3];
        for (int i = 0; i < 3; i++) {
            symbols[i] = Symbol.getMoreRestrictiveSymbol(left[i], right[i]);
        }
        return symbols;
    }

    private void makeAnchorsMatchMeasurements() {
        // We need two fixed points to determine where to place the third.
        // One will be opposite the line segment that is changing.
        // The other will be at the corner next to the shorter side.
        double shortSide;
        double longSide;
        double[] pivot = new double[2];
        double[] opposite = new double[2];
        int multiplier = mLatestFlippedState ? -1 : 1;
        int anchorToUpdate;

        if (mStateTether == TETHER_SELECTION_AB) {
            opposite[0] = mTethers[TETHER_SELECTION_C][0];
            opposite[1] = mTethers[TETHER_SELECTION_C][1];
            if (mDist12 < mDist20) {
                shortSide = mDist12;
                longSide = mDist20;
                pivot[0] = mTethers[TETHER_SELECTION_B][0];
                pivot[1] = mTethers[TETHER_SELECTION_B][1];
                anchorToUpdate = TETHER_SELECTION_A;
            } else {
                multiplier *= -1;
                shortSide = mDist20;
                longSide = mDist12;
                pivot[0] = mTethers[TETHER_SELECTION_A][0];
                pivot[1] = mTethers[TETHER_SELECTION_A][1];
                anchorToUpdate = TETHER_SELECTION_B;
            }
        } else if (mStateTether == TETHER_SELECTION_BC) {
            opposite[0] = mTethers[TETHER_SELECTION_A][0];
            opposite[1] = mTethers[TETHER_SELECTION_A][1];
            if (mDist01 < mDist20) {
                multiplier *= -1;
                shortSide = mDist01;
                longSide = mDist20;
                pivot[0] = mTethers[TETHER_SELECTION_B][0];
                pivot[1] = mTethers[TETHER_SELECTION_B][1];
                anchorToUpdate = TETHER_SELECTION_C;
            } else {
                shortSide = mDist20;
                longSide = mDist01;
                pivot[0] = mTethers[TETHER_SELECTION_C][0];
                pivot[1] = mTethers[TETHER_SELECTION_C][1];
                anchorToUpdate = TETHER_SELECTION_B;
            }
        } else { //if (mStateTether == TETHER_SELECTION_CA) {
            opposite[0] = mTethers[TETHER_SELECTION_B][0];
            opposite[1] = mTethers[TETHER_SELECTION_B][1];
            if (mDist01 < mDist12) {
                shortSide = mDist01;
                longSide = mDist12;
                pivot[0] = mTethers[TETHER_SELECTION_A][0];
                pivot[1] = mTethers[TETHER_SELECTION_A][1];
                anchorToUpdate = TETHER_SELECTION_C;
            } else {
                multiplier *= -1;
                shortSide = mDist12;
                longSide = mDist01;
                pivot[0] = mTethers[TETHER_SELECTION_C][0];
                pivot[1] = mTethers[TETHER_SELECTION_C][1];
                anchorToUpdate = TETHER_SELECTION_A;
            }
        }

        // Determine the angle from pivot to opposite points
        double deltaX = opposite[0] - pivot[0];
        double deltaY = opposite[1] - pivot[1];
        double hypotenuse = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double startingAngle = Util.getDirection(hypotenuse, deltaX, deltaY);

        // Derive the number of pixels the altered segment should equate to
        double changingSide = getMeasureToPixels(mAlteredValue);

        // Determine the new angle inside the triangle at the pivot corner
        double numerator = changingSide * changingSide + shortSide * shortSide - longSide * longSide;
        double denominator = 2 * changingSide * shortSide;
        double insideAngle = Math.acos(numerator / denominator);

        // The multiplier helps determine whether to add or subtract the inside angle
        double totalAngle = startingAngle + multiplier * insideAngle;
        // Update moving anchor relative to pivot point; Only change the anchor that moves
        mTethers[anchorToUpdate][0] = (float)(pivot[0] + changingSide * Math.cos(totalAngle));
        mTethers[anchorToUpdate][1] = (float)(pivot[1] + changingSide * Math.sin(totalAngle));
    }
}