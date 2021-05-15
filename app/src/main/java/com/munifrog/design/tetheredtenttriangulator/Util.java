package com.munifrog.design.tetheredtenttriangulator;

import android.graphics.Matrix;
import android.graphics.Path;

class Util {
    private static final double MATH_ANGLE_FOUR_THIRDS_PI = 4 * Math.PI / 3;
    private static final double MATH_ANGLE_FULL_CIRCLE = Math.PI * 2;
    private static final double MATH_ANGLE_PRECISION_ALLOWANCE = 0.001;
    private static final double MATH_ANGLE_TWO_THIRDS_PI = 2 * Math.PI / 3;
    private static final double MATH_DIVIDE_BY_SQRT_THREE = Math.sqrt(3) / 3;
    private static final double MATH_COS_ZERO = Math.cos(0);
    private static final double MATH_COS_TWO_THIRDS_PI = Math.cos(MATH_ANGLE_TWO_THIRDS_PI);
    private static final double MATH_COS_THREE_THIRDS_PI = Math.cos(Math.PI);
    private static final double MATH_COS_FOUR_THIRDS_PI = Math.cos(MATH_ANGLE_FOUR_THIRDS_PI);
    private static final double MATH_SIN_ZERO = Math.sin(0);
    private static final double MATH_SIN_TWO_THIRDS_PI = Math.sin(MATH_ANGLE_TWO_THIRDS_PI);
    private static final double MATH_SIN_THREE_THIRDS_PI = Math.sin(Math.PI);
    private static final double MATH_SIN_FOUR_THIRDS_PI = Math.sin(MATH_ANGLE_FOUR_THIRDS_PI);

    private static final double TENTSILE_CENTER_HOLE_HYPOTENUSE = 0.6;
    private static final double TENTSILE_NOTCH_SCALE = 0.5;
    private static final double TENTSILE_NOTCH_SCALED_COS_PI = TENTSILE_NOTCH_SCALE * MATH_COS_THREE_THIRDS_PI;
    private static final double TENTSILE_NOTCH_SCALED_SIN_PI = TENTSILE_NOTCH_SCALE * MATH_SIN_THREE_THIRDS_PI;

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

        return new Platform(path, extremities);
    }

    static Platform getTentsileIsosceles(double point, double barb, double notch) {
        double [][] extremities = new double[3][2];
        extremities[0][0] = point * MATH_COS_ZERO;
        extremities[0][1] = point * MATH_SIN_ZERO;
        extremities[1][0] = barb * MATH_COS_TWO_THIRDS_PI;
        extremities[1][1] = barb * MATH_SIN_TWO_THIRDS_PI;
        extremities[2][0] = barb * MATH_COS_FOUR_THIRDS_PI;
        extremities[2][1] = barb * MATH_SIN_FOUR_THIRDS_PI;

        Path path = new Path();
        path.moveTo(
                (float)extremities[0][0],
                (float)extremities[0][1]
        );
        path.lineTo(
                (float)(notch * TENTSILE_NOTCH_SCALED_COS_PI),
                (float)(notch * TENTSILE_NOTCH_SCALED_SIN_PI)
        );
        path.lineTo(
                (float)extremities[1][0],
                (float)extremities[1][1]
        );
        path.close();
        path.moveTo(
                (float)extremities[0][0],
                (float)extremities[0][1]
        );
        path.lineTo(
                (float)(notch * TENTSILE_NOTCH_SCALED_COS_PI),
                (float)(notch * TENTSILE_NOTCH_SCALED_SIN_PI)
        );
        path.lineTo(
                (float)extremities[2][0],
                (float)extremities[2][1]
        );
        path.close();

        return new Platform(path, extremities);
    }

    static Platform getTentsileTrilogy(double hypotenuse, double base) {
        double [] measurements = getIsoscelesMeasurements(hypotenuse, base);
        Path path = new Path();
        double [][] extremities = new double[3][2];

        Matrix matrix = new Matrix();
        matrix.postRotate(120);

        double distal = measurements[1] + measurements[0];
        extremities[0][0] = distal * MATH_COS_ZERO;
        extremities[0][1] = distal * MATH_SIN_ZERO;
        extremities[1][0] = distal * MATH_COS_TWO_THIRDS_PI;
        extremities[1][1] = distal * MATH_SIN_TWO_THIRDS_PI;
        extremities[2][0] = distal * MATH_COS_FOUR_THIRDS_PI;
        extremities[2][1] = distal * MATH_SIN_FOUR_THIRDS_PI;

        for (int i = 0; i < 3; i++) {
            path.transform(matrix);
            path.moveTo(
                    (float)extremities[0][0],
                    (float)extremities[0][1]
            );
            path.lineTo(
                    (float)(measurements[1] + measurements[2] * TENTSILE_NOTCH_SCALED_COS_PI),
                    (float)(measurements[2] * TENTSILE_NOTCH_SCALED_SIN_PI)
            );
            path.lineTo(
                    (float)(measurements[1] + measurements[1] * MATH_COS_TWO_THIRDS_PI),
                    (float)(measurements[1] * MATH_SIN_TWO_THIRDS_PI)
            );
            path.close();
            path.moveTo(
                    (float)extremities[0][0],
                    (float)extremities[0][1]
            );
            path.lineTo(
                    (float)(measurements[1] + measurements[2] * TENTSILE_NOTCH_SCALED_COS_PI),
                    (float)(measurements[2] * TENTSILE_NOTCH_SCALED_SIN_PI)
            );
            path.lineTo(
                    (float)(measurements[1] + measurements[1] * MATH_COS_FOUR_THIRDS_PI),
                    (float)(measurements[1] * MATH_SIN_FOUR_THIRDS_PI)
            );
            path.close();
        }

        return new Platform(path, extremities);
    }

    static double [] getIsoscelesMeasurements(double hypotenuse, double base) {
        // point[0], barb[1], notch[2]
        double [] measurements = new double[3];
        measurements[2] = base * MATH_DIVIDE_BY_SQRT_THREE / 2;
        measurements[1] = measurements[2] * 2;
        measurements[0] = Math.sqrt(hypotenuse * hypotenuse - 3 * measurements[2] * measurements[2]) - measurements[2];
        return measurements;
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
}
