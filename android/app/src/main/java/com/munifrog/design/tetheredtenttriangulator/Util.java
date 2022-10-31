package com.munifrog.design.tetheredtenttriangulator;

import android.graphics.Matrix;
import android.graphics.Path;

class Util {
    private static final double MATH_ANGLE_FOUR_THIRDS_PI = 4 * Math.PI / 3;
    private static final double MATH_ANGLE_FULL_CIRCLE = Math.PI * 2;
    private static final double MATH_ANGLE_QUARTER_CIRCLE = MATH_ANGLE_FULL_CIRCLE / 4;
    private static final double MATH_ANGLE_PRECISION_ALLOWANCE = 0.001;
    private static final double MATH_ANGLE_TWO_THIRDS_PI = 2 * Math.PI / 3;
    private static final double MATH_DIVIDE_BY_SQRT_THREE = Math.sqrt(3) / 3;
    private static final double MATH_COS_ZERO = Math.cos(0);
    private static final double MATH_COS_TWO_THIRDS_PI = Math.cos(MATH_ANGLE_TWO_THIRDS_PI);
    private static final double MATH_COS_THREE_THIRDS_PI = Math.cos(Math.PI);
    private static final double MATH_COS_FOUR_THIRDS_PI = Math.cos(MATH_ANGLE_FOUR_THIRDS_PI);
    private static final double MATH_METERS_TO_FEET_CONVERSION = 3.2808399;
    private static final double MATH_FEET_TO_METERS_CONVERSION = 1.0 / MATH_METERS_TO_FEET_CONVERSION;
    private static final double MATH_SIN_ZERO = Math.sin(0);
    private static final double MATH_SIN_TWO_THIRDS_PI = Math.sin(MATH_ANGLE_TWO_THIRDS_PI);
    private static final double MATH_SIN_THREE_THIRDS_PI = Math.sin(Math.PI);
    private static final double MATH_SIN_FOUR_THIRDS_PI = Math.sin(MATH_ANGLE_FOUR_THIRDS_PI);

    public static final int MATH_PRECISION_UNITS = 0;
    public static final int MATH_PRECISION_TENTHS = 1;
    public static final int MATH_PRECISION_HUNDREDTHS = 2;

    private static final double TENTSILE_CENTER_HOLE_HYPOTENUSE = 0.6;
    private static final double TENTSILE_NOTCH_APPARENT = 0.25; // Una indents by 20cm; Flite and Connect indents by 30cm

    private static final double TENTSILE_STRAPS_DEFAULT = 6.0;
    private static final double TENTSILE_CIRCUMFERENCE_DEFAULT = 0.785398163397448; // pi * 25cm or 10inch diameter

    // Arcsine ranges from -pi/2 (Quadrant 4) to +pi/2 (Quadrant 1)
    // When deltaX is positive then we are in Quadrant 1 or 4, corresponding to the arcsine results;
    // When deltaX is negative then we are in Quadrant 2 or 3.
    // The same deltaY at a negative deltaX, occurs exactly pi away from the arcsine results;
    static double getDirection(double hypotenuse, double deltaX, double deltaY) {
        double angle = Math.asin(deltaY / hypotenuse);
        if (deltaX < 0) {
            angle = Math.PI - angle;
        }
        return angle;
    }

    static boolean areAnglesEquivalent(double angleA, double angleB) {
        double rawDelta = Math.abs(angleB - angleA);
        if (rawDelta < MATH_ANGLE_PRECISION_ALLOWANCE) {
            return true;
        } else {
            long numCircles = Math.round(rawDelta / MATH_ANGLE_FULL_CIRCLE);
            double modularDelta = Math.abs(rawDelta - numCircles * MATH_ANGLE_FULL_CIRCLE);
            return modularDelta < MATH_ANGLE_PRECISION_ALLOWANCE;
        }
    }

    static double[] getPerimeter(float[][] tethers) {
        double [] returnable = new double[6];
        double diff01x = tethers[0][0] - tethers[1][0]; // Ax - Bx
        double diff01y = tethers[0][1] - tethers[1][1]; // Ay - By
        double diff12x = tethers[1][0] - tethers[2][0]; // Bx - Cx
        double diff12y = tethers[1][1] - tethers[2][1]; // By - Cy
        double diff20x = tethers[2][0] - tethers[0][0]; // Cx - Ax
        double diff20y = tethers[2][1] - tethers[0][1]; // Cy - Ay
        // Supposed to speed processing; is it faster to multiply again or pass copies?
        returnable[3] = diff01x * diff01x + diff01y * diff01y; // dist01sq
        returnable[4] = diff12x * diff12x + diff12y * diff12y; // dist12sq
        returnable[5] = diff20x * diff20x + diff20y * diff20y; // dist20sq
        // Users are probably more interested in the actual distances, so return these first
        returnable[0] = Math.sqrt(returnable[3]); // c; mDist01
        returnable[1] = Math.sqrt(returnable[4]); // a; mDist12
        returnable[2] = Math.sqrt(returnable[5]); // b; mDist20
        return returnable;
    }

    static Platform getTentsileEquilateral(double baseLength) {
        double distal = baseLength * MATH_DIVIDE_BY_SQRT_THREE;
        double proximal = TENTSILE_CENTER_HOLE_HYPOTENUSE * MATH_DIVIDE_BY_SQRT_THREE;

        double [][] extremities = new double[3][2];
        extremities[0][0] = distal * MATH_COS_ZERO;
        extremities[0][1] = distal * MATH_SIN_ZERO;
        extremities[1][0] = distal * MATH_COS_TWO_THIRDS_PI;
        extremities[1][1] = distal * MATH_SIN_TWO_THIRDS_PI;
        extremities[2][0] = distal * MATH_COS_FOUR_THIRDS_PI;
        extremities[2][1] = distal * MATH_SIN_FOUR_THIRDS_PI;

        Path path = new Path();
        path.moveTo(
                (float)extremities[0][0],
                (float)extremities[0][1]
        );
        path.lineTo(
                (float)(proximal * MATH_COS_ZERO),
                (float)(proximal * MATH_SIN_ZERO)
        );
        path.lineTo(
                (float)(proximal * MATH_COS_TWO_THIRDS_PI),
                (float)(proximal * MATH_SIN_TWO_THIRDS_PI)
        );
        path.lineTo(
                (float)extremities[1][0],
                (float)extremities[1][1]
        );
        path.close();

        path.moveTo(
                (float)extremities[1][0],
                (float)extremities[1][1]
        );
        path.lineTo(
                (float)(proximal * MATH_COS_TWO_THIRDS_PI),
                (float)(proximal * MATH_SIN_TWO_THIRDS_PI)
        );
        path.lineTo(
                (float)(proximal * MATH_COS_FOUR_THIRDS_PI),
                (float)(proximal * MATH_SIN_FOUR_THIRDS_PI)
        );
        path.lineTo(
                (float)extremities[2][0],
                (float)extremities[2][1]
        );
        path.close();

        path.moveTo(
                (float)extremities[2][0],
                (float)extremities[2][1]
        );
        path.lineTo(
                (float)(proximal * MATH_COS_FOUR_THIRDS_PI),
                (float)(proximal * MATH_SIN_FOUR_THIRDS_PI)
        );
        path.lineTo(
                (float)(proximal * MATH_COS_ZERO),
                (float)(proximal * MATH_SIN_ZERO)
        );
        path.lineTo(
                (float)extremities[0][0],
                (float)extremities[0][1]
        );
        path.close();

        return new Platform(
                path,
                extremities,
                MATH_ANGLE_TWO_THIRDS_PI,
                TENTSILE_STRAPS_DEFAULT,
                TENTSILE_CIRCUMFERENCE_DEFAULT
        );
    }

    static Platform getTentsileIsosceles(
            double hypotenuse,
            double base,
            double tetherangle,
            double strap,
            double circumference
    ) {
        double [] measurements = Util.getIsoscelesMeasurements(hypotenuse, base, tetherangle);

        double [][] extremities = new double[][]{
                { measurements[0], 0 },
                { -measurements[1], measurements[2] },
                { -measurements[1], -measurements[2] }
        };
        // We want the indentation to appear even when we have assumed none
        double[] notch = new double[] { TENTSILE_NOTCH_APPARENT - measurements[1], 0 };

        Path path = new Path();
        path.moveTo(
                (float)extremities[0][0],
                (float)extremities[0][1]
        );
        path.lineTo(
                (float)extremities[1][0],
                (float)extremities[1][1]
        );
        path.lineTo(
                (float)(notch[0]),
                (float)(notch[1])
        );
        path.lineTo(
                (float)extremities[2][0],
                (float)extremities[2][1]
        );
        path.close();

        return new Platform(path, extremities, tetherangle, strap, circumference);
    }

    static Platform getTentsileTrilogy(double hypotenuse, double base, double tetherangle) {
        double [] measurements = getIsoscelesMeasurements(hypotenuse, base, tetherangle);
        Path path = new Path();

        Matrix matrix = new Matrix();
        matrix.postRotate(120);

        double translation = base * MATH_DIVIDE_BY_SQRT_THREE / 2.0 + measurements[1];
        double distal = translation + measurements[0];
        double [][] extremities = new double[][] {
                { distal * MATH_COS_ZERO, distal * MATH_SIN_ZERO },
                { distal * MATH_COS_TWO_THIRDS_PI, distal * MATH_SIN_TWO_THIRDS_PI },
                { distal * MATH_COS_FOUR_THIRDS_PI, distal * MATH_SIN_FOUR_THIRDS_PI }
        };
        double[] notch = new double[] { translation + TENTSILE_NOTCH_APPARENT - measurements[1], 0 };
        double[] barb = new double[] { translation - measurements[1], measurements[2] };

        for (int i = 0; i < 3; i++) {
            path.transform(matrix);
            path.moveTo(
                    (float)extremities[0][0],
                    (float)extremities[0][1]
            );
            path.lineTo(
                    (float)(barb[0]),
                    (float)(barb[1])
            );
            path.lineTo(
                    (float)(notch[0]),
                    (float)(notch[1])
            );
            path.lineTo(
                    (float)(barb[0]),
                    (float)(-barb[1])
            );
            path.close();
        }

        return new Platform(
                path,
                extremities,
                MATH_ANGLE_TWO_THIRDS_PI,
                TENTSILE_STRAPS_DEFAULT,
                TENTSILE_CIRCUMFERENCE_DEFAULT
        );
    }

    static double[] getIsoscelesMeasurements(
            double hypotenuse,
            double base,
            double tetherangle
    ) {
        double largeAngle = (MATH_ANGLE_FULL_CIRCLE - tetherangle) / 2.0;
        double sineLargeAngle = Math.sin(largeAngle);
        double centerHeight = Math.sqrt(hypotenuse * hypotenuse - base * base / 4.0);

        // barbSide : distance away from center line to short tip
        double barbSide = base / 2.0;
        // barbTether : distance between tether center and short tip
        double barbTether = barbSide / sineLargeAngle;
        // barbCenter : distance from tether center to short tip
        double barbCenter = Math.sqrt(barbTether * barbTether - barbSide * barbSide);
        // pointTether : distance between tether center and long tip
        double pointTether = centerHeight - barbCenter;

        double betaAngle = Math.asin(pointTether * sineLargeAngle / hypotenuse);
        double gammaAngle = Math.asin(barbSide / hypotenuse);
        double alphaAngle = MATH_ANGLE_QUARTER_CIRCLE - gammaAngle - betaAngle - betaAngle;

        double indent = barbSide * Math.tan(alphaAngle);
        // gap : distance between tether center and indentation
        double gap = centerHeight - pointTether - indent;

        return new double[] { pointTether, barbCenter, barbSide, gap };
    }

    static Knots getTetherKnots(
            double pixelsToMetersConversion,
            double startX, double startY,
            double finishX, double finishY,
            double strap, double extend,
            double circumference
    ) {
        float[][] knots;
        Symbol symbol;
        double metersToPixelsConversion = 1.0 / pixelsToMetersConversion;
        // Between the platform extremity and anchor point, determine where the colors transition:
        // First 1 foot is connected directly to tent, and should therefore be strap color.
        // The strap (either 6m or 4m) will define the next section.
        // Any further distance will be split into lengths equal to extension straps (6m)
        double pixelDiffX = finishX - startX;
        double pixelDiffY = finishY - startY;
        double pixelDistTotal = Math.sqrt(pixelDiffX * pixelDiffX + pixelDiffY * pixelDiffY);

        double meterDistTotal = pixelsToMetersConversion * pixelDistTotal;
        double ratchetDist = MATH_FEET_TO_METERS_CONVERSION;
        if (meterDistTotal >= ratchetDist) {
            double angle = getDirection(pixelDistTotal, pixelDiffX, pixelDiffY);
            double angleSine = Math.sin(angle);
            double angleCosine = Math.cos(angle);
            // Count how many knots there are
            double meterDefaultRange = ratchetDist + strap;
            if (meterDistTotal > meterDefaultRange) {
                double meterDistRemains = meterDistTotal - meterDefaultRange;
                double numExtends = meterDistRemains / extend;
                int numExtensions = (int) Math.ceil(numExtends);
                meterDistRemains = numExtensions * extend - meterDistRemains;
                int count = 3 + numExtensions; // 0 = start, 1 = ratchet, 2 = default strap
                knots = new float[count][2];
                // First knot after ratchet (length depends on platform)
                double pixelsDefaultRange = meterDefaultRange * metersToPixelsConversion;
                knots[2][0] = (float) (startX + pixelsDefaultRange * angleCosine);
                knots[2][1] = (float) (startY + pixelsDefaultRange * angleSine);
                int last = count - 1;
                double reach;
                int index;
                for (int i = 1; i < numExtensions; i++) {
                    // Extension knots (consistently six meters)
                    reach = (meterDefaultRange + i * extend) * metersToPixelsConversion;
                    index = 2 + i;
                    knots[index][0] = (float) (startX + reach * angleCosine);
                    knots[index][1] = (float) (startY + reach * angleSine);
                }
                // Anchor knots
                knots[last][0] = (float) finishX;
                knots[last][1] = (float) finishY;
                if (meterDistRemains < circumference) {
                    symbol = Symbol.scarce;
                } else {
                    symbol = Symbol.safe;
                }
            } else {
                // The tent is in an acceptable range to reach the anchor point
                knots = new float[3][2];
                knots[2][0] = (float) finishX;
                knots[2][1] = (float) finishY;
                // anchor > knot - circumference -OR- anchor + circumference > knot
                if (meterDistTotal + circumference > meterDefaultRange) {
                    symbol = Symbol.scarce;
                } else {
                    symbol = Symbol.safe;
                }
            }
            // The first knot away from the starting point
            double firstStrap = MATH_FEET_TO_METERS_CONVERSION * metersToPixelsConversion;
            knots[1][0] = (float) (startX + firstStrap * angleCosine);
            knots[1][1] = (float) (startY + firstStrap * angleSine);
        } else {
            // No need to compute angle and new coordinates
            knots = new float[2][2];
            knots[1][0] = (float) finishX;
            knots[1][1] = (float) finishY;
            symbol = Symbol.tricky;
        }
        // All paths set the starting knot
        knots[0][0] = (float) startX;
        knots[0][1] = (float) startY;
        return new Knots(knots, symbol);
    }

    static double[][] shiftedCoordinates(double[][] points, double angle, double scale, double[] translation) {
        // [ x0 y0 ][  cos()  sin() ] = [ x1 y1 ]     x1 =  x0 cos() - y0 sin()
        //          [ -sin()  cos() ]                 y1 =  x0 sin() + y0 cos()
        double cosine = Math.cos(angle), sine = Math.sin(angle);
        int numPoints = points.length;
        double [][] newPoints = new double[numPoints][2];
        for (int i = 0; i < numPoints; i++) {
            newPoints[i][0] = translation[0] + scale * points[i][0] * cosine - scale * points[i][1] * sine;
            newPoints[i][1] = translation[1] + scale * points[i][1] * cosine + scale * points[i][0] * sine;
        }
        return newPoints;
    }

    static double getSmallAngleGivenIndent(
            double hypotenuse,
            double base,
            double indent
    ) {
        double alpha = Math.atan(2 * indent / base);
        double gamma = Math.asin( base / 2 / hypotenuse);
        return MATH_ANGLE_QUARTER_CIRCLE + gamma - alpha;
    }

    static double[] getImperialWithMeterPrecision(double measure, int degree) {
        // In order to make meters and feet measurements about the same accuracy, we try to match
        // with foot and inch fractions. (We could do similarly with meters, but those are so clean!)
        double[] split;
        double[] wholeAndPart;
        switch (degree) {
            case MATH_PRECISION_UNITS:
                // We only care about the nearest imperial foot (not use floor like the other cases)
                split = new double[1];
                split[0] = Math.round(measure);
                break;
            default:
            case MATH_PRECISION_TENTHS:
                // One imperial foot equals twelve inches.
                // And 4 inches (1/3 foot) is about 1/10 meter.
                // To convert 1/10 of a foot to 1/12 of a foot:
                //   fraction / 10 = inches / 12 OR inches = 12 * fraction / 10
                // Similarly, to convert 1/10 of a foot to 1/3 of a foot:
                //   fraction / 10 = inches / 3 OR inches = 3 * fraction / 10
                // (Note that the fraction portion is already divided by 10 here.)
                // Rounding after multiplying discards further precision and spreads units equally.
                // Multiplying by 4 afterwards stretches the 3 inches to full foot.
                split = new double[2];
                wholeAndPart = getSplit(measure); // feet
                split[0] = wholeAndPart[0];
                split[1] = Math.round(wholeAndPart[1] * 3) * 4; // 4 inch precision
                if (split[1] == 12) {
                    split[0] += 1;
                    split[1] = 0;
                }
                break;
            case MATH_PRECISION_HUNDREDTHS:
                // One imperial foot equals twelve inches.
                // One centimeter is about 3/8 inches.
                // 3/8 inches fits into 3 inches 8 times or 32 times in 1 foot.
                // Either way, we can deal with feet separately.
                split = new double[3];
                wholeAndPart = getSplit(measure);
                split[0] = wholeAndPart[0]; // feet
                // Multiply by 32 to get 32 equally-spaced units (for the 3/8) and snap to the
                // nearest using round(). Then stretch the results (multiplying by 3) to entire foot.
                double numEighths = Math.round(wholeAndPart[1] * 32) * 3;
                split[1] = Math.floor(numEighths / 8.0);
                split[2] = numEighths - (split[1] * 8);
                if (split[1] >= 12) {
                    split[0] += 1;
                    split[1] -= 12;
                }
                break;
        }
        return split;
    }

    private static double[] getSplit(double measure) {
        double[] split = new double[2];
        split[0] = Math.floor(measure);
        split[1] = measure - split[0];
        return split;
    }
}
