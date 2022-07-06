package com.munifrog.design.tetheredtenttriangulator;

class TetherCenter {
    private static final double MATH_ANGLE_FULL_CIRCLE = 2 * Math.PI;
    private static final double MATH_ANGLE_HALF_CIRCLE = Math.PI;
    private static final double MATH_ANGLE_ONE_THIRD_CIRCLE = MATH_ANGLE_FULL_CIRCLE / 3;
    private static final double MATH_ANGLE_ONE_SIXTH_CIRCLE = MATH_ANGLE_HALF_CIRCLE / 3;
    private static final double MATH_SINE_ONE_THIRD_CIRCLE = Math.sin(MATH_ANGLE_ONE_THIRD_CIRCLE);
    private final float[][] mTethers = new float[3][2];
    private final float[] mNewPlatformCenter = new float[2];

    private boolean mOrientationFlipped;

    TetherCenter(
            final float[][] tetherPoints
    ) {
        mTethers[0][0] = tetherPoints[0][0];
        mTethers[0][1] = tetherPoints[0][1];
        mTethers[1][0] = tetherPoints[1][0];
        mTethers[1][1] = tetherPoints[1][1];
        mTethers[2][0] = tetherPoints[2][0];
        mTethers[2][1] = tetherPoints[2][1];
    }

    void process(double smallAngle) {
        double largeAngle = (MATH_ANGLE_FULL_CIRCLE - smallAngle) / 2.0;
        double sineLargeAngle = Math.sin(largeAngle);

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
        //double dist12 = Math.sqrt(dist12sq); // a
        double dist20 = Math.sqrt(dist20sq); // b

        double angle102 = Math.acos((dist20sq + dist01sq - dist12sq) / 2.0 / dist20 / dist01);  // A = 0
        //double angle210  = Math.acos((dist12sq + dist01sq - dist20sq) / 2.0 / dist12 / dist01); // B = 1
        //double angle021 = Math.acos((dist12sq + dist20sq - dist01sq) / 2.0 / dist12 / dist20);  // C = 2

        double angleTheta = smallAngle - angle102;

        double angleP10 = Math.atan(dist20 * Math.sin(angleTheta) / (dist01 + dist20 * Math.cos(angleTheta))); // beta1
        double angleP01 = MATH_ANGLE_HALF_CIRCLE - largeAngle - angleP10; // alpha2
        double angleP02 = angle102 - angleP01; // alpha1

        double dist0P = dist01 * Math.sin(angleP10) / sineLargeAngle; // d
        //double dist1P = dist01 * Math.sin(angleP01) / sineLargeAngle; // e
        //double dist2P = dist20 * Math.sin(angleP02) / sineLargeAngle; // f

        // Determine the location of the platform center (Q is for quadrant or Y=0 line) relative to the screen
        // These vectors need to be pointing towards A (0) for the next step to work
        double angleQ01 = Util.getDirection(dist01, -diff01x, -diff01y);
        double angleQ02 = Util.getDirection(dist20, diff20x, diff20y);

        // If adding alpha1 to angle AC, then matches subtracting alpha2 from angle AB
        // If subtracting alpha1 from angle AC, then adding alpha2 to angle AB
        double angleP02a = angleQ02 + angleP02; // compare only with angleP01a
        double angleP01m = angleQ01 - angleP01; // compare only with angleP02m
        //double angleP02m = angleQ02 - angleP02; // compare only with angleP01m
        double angleP01a = angleQ01 + angleP01; // compare only with angle02Pa

        // Find the angle pair that give the same (or close) angle
        mOrientationFlipped = Util.areAnglesEquivalent(angleP02a, angleP01m);
        double anglePlatform = mOrientationFlipped ? angleP02a : angleP01a;

        // x = Ax + dist0P * cos(anglePlatform)
        mNewPlatformCenter[0] = (float)(mTethers[0][0] + dist0P * Math.cos(anglePlatform));
        // y = Ay + dist0P * sin(anglePlatform)
        mNewPlatformCenter[1] = (float)(mTethers[0][1] + dist0P * Math.sin(anglePlatform));
    }

    float[] getCenter() {
        return mNewPlatformCenter;
    }

    boolean getOrientationFlip() {
        return mOrientationFlipped;
    }
}
