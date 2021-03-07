package com.munifrog.design.tetheredtenttriangulator;

class Util {
    private static final double MATH_ANGLE_FULL_CIRCLE = Math.PI * 2;
    private static final double MATH_ANGLE_PRECISION_ALLOWANCE = 0.001;
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
}
