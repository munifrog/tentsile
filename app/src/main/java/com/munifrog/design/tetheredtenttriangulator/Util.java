package com.munifrog.design.tetheredtenttriangulator;

import android.graphics.Matrix;
import android.graphics.Path;

class Util {
    private static final double MATH_ANGLE_FOUR_THIRDS_PI = 4 * Math.PI / 3;
    private static final double MATH_ANGLE_FULL_CIRCLE = Math.PI * 2;
    private static final double MATH_ANGLE_PRECISION_ALLOWANCE = 0.001;
    private static final double MATH_ANGLE_TWO_THIRDS_PI = 2 * Math.PI / 3;
    private static final double MATH_DIVIDE_BY_SQRT_THREE = Math.sqrt(3) / 3;

    private static final double TENTSILE_CENTER_HOLE_HYPOTENUSE = 0.6;
    private static final double TENTSILE_NOTCH_SCALE = 0.5;

    private static final int MATH_DEGREES_OF_PRECISION = 1;


    private static final double mPrecision = Math.pow(10, MATH_DEGREES_OF_PRECISION);

    static double forcePrecision(double input) {
        return Math.round(mPrecision * input) / mPrecision;
    }

    // deltaY = (distal Y - proximal Y); if deltaY >= 0, then angle is within Q1 or Q2;
    // if deltaY < 0, then angle within Q3 or Q4; allowing us to narrow down the quadrant
    static double getDirection(double hypotenuse, double deltaX, double deltaY) {
        double angle = Math.asin(deltaY / hypotenuse);
        if ((angle >= 0) && (deltaX < 0) || ((angle < 0) && (deltaX < 0))) {
            // symmetric with respect to line (x = 0)
            angle = Math.PI - angle;
        }
        return angle;
    }

    static boolean areAnglesEquivalent(double angleA, double angleB) {
        double rawDelta = Math.abs(angleB - angleA);
        if (rawDelta < MATH_ANGLE_PRECISION_ALLOWANCE) {
            return true;
        } else if (Math.signum(angleA) != Math.signum(angleB)) {
            // In theory we would need to handle N 2*PI, but in practice it suffices to compare +/-
            return (Math.abs(MATH_ANGLE_FULL_CIRCLE - rawDelta) < MATH_ANGLE_PRECISION_ALLOWANCE);
        }
        return false;
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

    static Path getTentsileEquilateral(double baseLength) {
        Path platform = new Path();

        double distal = baseLength * MATH_DIVIDE_BY_SQRT_THREE;
        double proximal = TENTSILE_CENTER_HOLE_HYPOTENUSE * MATH_DIVIDE_BY_SQRT_THREE;

        platform.moveTo(
                (float)(distal * Math.cos(0)),
                (float)(distal * Math.sin(0))
        );
        platform.lineTo(
                (float)(proximal * Math.cos(0)),
                (float)(proximal * Math.sin(0))
        );
        platform.lineTo(
                (float)(proximal * Math.cos(MATH_ANGLE_TWO_THIRDS_PI)),
                (float)(proximal * Math.sin(MATH_ANGLE_TWO_THIRDS_PI))
        );
        platform.lineTo(
                (float)(distal * Math.cos(MATH_ANGLE_TWO_THIRDS_PI)),
                (float)(distal * Math.sin(MATH_ANGLE_TWO_THIRDS_PI))
        );
        platform.close();

        platform.moveTo(
                (float)(distal * Math.cos(MATH_ANGLE_TWO_THIRDS_PI)),
                (float)(distal * Math.sin(MATH_ANGLE_TWO_THIRDS_PI))
        );
        platform.lineTo(
                (float)(proximal * Math.cos(MATH_ANGLE_TWO_THIRDS_PI)),
                (float)(proximal * Math.sin(MATH_ANGLE_TWO_THIRDS_PI))
        );
        platform.lineTo(
                (float)(proximal * Math.cos(MATH_ANGLE_FOUR_THIRDS_PI)),
                (float)(proximal * Math.sin(MATH_ANGLE_FOUR_THIRDS_PI))
        );
        platform.lineTo(
                (float)(distal * Math.cos(MATH_ANGLE_FOUR_THIRDS_PI)),
                (float)(distal * Math.sin(MATH_ANGLE_FOUR_THIRDS_PI))
        );
        platform.close();

        platform.moveTo(
                (float)(distal * Math.cos(MATH_ANGLE_FOUR_THIRDS_PI)),
                (float)(distal * Math.sin(MATH_ANGLE_FOUR_THIRDS_PI))
        );
        platform.lineTo(
                (float)(proximal * Math.cos(MATH_ANGLE_FOUR_THIRDS_PI)),
                (float)(proximal * Math.sin(MATH_ANGLE_FOUR_THIRDS_PI))
        );
        platform.lineTo(
                (float)(proximal * Math.cos(0)),
                (float)(proximal * Math.sin(0))
        );
        platform.lineTo(
                (float)(distal * Math.cos(0)),
                (float)(distal * Math.sin(0))
        );
        platform.close();

        return platform;
    }

    static Path getTentsileIsosceles(double point, double barb, double notch) {
        Path platform = new Path();

        platform.moveTo(
                (float)(point * Math.cos(0)),
                (float)(point * Math.sin(0))
        );
        platform.lineTo(
                (float)(notch * TENTSILE_NOTCH_SCALE * Math.cos(Math.PI)),
                (float)(notch * TENTSILE_NOTCH_SCALE * Math.sin(Math.PI))
        );
        platform.lineTo(
                (float)(barb * Math.cos(MATH_ANGLE_TWO_THIRDS_PI)),
                (float)(barb * Math.sin(MATH_ANGLE_TWO_THIRDS_PI))
        );
        platform.close();
        platform.moveTo(
                (float)(point * Math.cos(0)),
                (float)(point * Math.sin(0))
        );
        platform.lineTo(
                (float)(notch * TENTSILE_NOTCH_SCALE * Math.cos(Math.PI)),
                (float)(notch * TENTSILE_NOTCH_SCALE * Math.sin(Math.PI))
        );
        platform.lineTo(
                (float)(barb * Math.cos(MATH_ANGLE_FOUR_THIRDS_PI)),
                (float)(barb * Math.sin(MATH_ANGLE_FOUR_THIRDS_PI))
        );
        platform.close();

        return platform;
    }

    static Path getTentsileTrilogy(double hypotenuse, double base) {
        double [] measurements = getIsoscelesMeasurements(hypotenuse, base);
        Path platform = new Path();

        Matrix matrix = new Matrix();
        matrix.postRotate(120);

        for (int i = 0; i < 3; i++) {
            platform.transform(matrix);
            platform.moveTo(
                    (float)(measurements[1] + measurements[0] * Math.cos(0)),
                    (float)(measurements[0] * Math.sin(0))
            );
            platform.lineTo(
                    (float)(measurements[1] + measurements[2] * TENTSILE_NOTCH_SCALE * Math.cos(Math.PI)),
                    (float)(measurements[2] * TENTSILE_NOTCH_SCALE * Math.sin(Math.PI))
            );
            platform.lineTo(
                    (float)(measurements[1] + measurements[1] * Math.cos(MATH_ANGLE_TWO_THIRDS_PI)),
                    (float)(measurements[1] * Math.sin(MATH_ANGLE_TWO_THIRDS_PI))
            );
            platform.close();
            platform.moveTo(
                    (float)(measurements[1] + measurements[0] * Math.cos(0)),
                    (float)(measurements[0] * Math.sin(0))
            );
            platform.lineTo(
                    (float)(measurements[1] + measurements[2] * TENTSILE_NOTCH_SCALE * Math.cos(Math.PI)),
                    (float)(measurements[2] * TENTSILE_NOTCH_SCALE * Math.sin(Math.PI))
            );
            platform.lineTo(
                    (float)(measurements[1] + measurements[1] * Math.cos(MATH_ANGLE_FOUR_THIRDS_PI)),
                    (float)(measurements[1] * Math.sin(MATH_ANGLE_FOUR_THIRDS_PI))
            );
            platform.close();
        }

        return platform;
    }

    static double [] getIsoscelesMeasurements(double hypotenuse, double base) {
        // point[0], barb[1], notch[2]
        double [] measurements = new double[3];
        measurements[2] = base * MATH_DIVIDE_BY_SQRT_THREE / 2;
        measurements[1] = measurements[2] * 2;
        measurements[0] = Math.sqrt(hypotenuse * hypotenuse - 3 * measurements[2] * measurements[2]) - measurements[2];
        return measurements;
    }
}
