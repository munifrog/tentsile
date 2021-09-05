package com.munifrog.design.tetheredtenttriangulator;

class TetherCenter {
    private static final double MATH_ANGLE_ONE_THIRD_CIRCLE = 2 * Math.PI / 3;
    private static final double MATH_ANGLE_ONE_SIXTH_CIRCLE = Math.PI / 3;
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

    void process() {
        double diff01x = mTethers[0][0] - mTethers[1][0]; // Ax - Bx
        double diff01y = mTethers[0][1] - mTethers[1][1]; // Ay - By
        double diff12x = mTethers[1][0] - mTethers[2][0]; // Bx - Cx
        double diff12y = mTethers[1][1] - mTethers[2][1]; // By - Cy
        double diff20x = mTethers[2][0] - mTethers[0][0]; // Cx - Ax
        double diff20y = mTethers[2][1] - mTethers[0][1]; // Cy - Ay
        double dist01sq = diff01x * diff01x + diff01y * diff01y;
        double dist12sq = diff12x * diff12x + diff12y * diff12y;
        double dist20sq = diff20x * diff20x + diff20y * diff20y;

        //double dist01 = Math.sqrt(dist01sq); // c
        double dist12 = Math.sqrt(dist12sq); // a
        double dist20 = Math.sqrt(dist20sq); // b

        //double angle102 = Math.acos((dist20sq + dist01sq - dist12sq) / 2.0 / dist20 / dist01);  // A = 0
        //double angle210  = Math.acos((dist12sq + dist01sq - dist20sq) / 2.0 / dist12 / dist01); // B = 1
        double angle021 = Math.acos((dist12sq + dist20sq - dist01sq) / 2.0 / dist12 / dist20);  // C = 2

        // Equilateral triangle (simple) case: 2P0, 1P2 and 0P1 are all 120(o) or 2 * PI / 3
        // Rather than computing the sines of these angles, could compute them ahead of time and load per tent

        double sine2P0 = Math.sin(MATH_ANGLE_ONE_THIRD_CIRCLE); // rho
        double sine1P2 = Math.sin(MATH_ANGLE_ONE_THIRD_CIRCLE); // lambda
        //double sine0P1 = Math.sin(MATH_ANGLE_ONE_THIRD_CIRCLE); // psi

        double angleTheta =  MATH_ANGLE_ONE_THIRD_CIRCLE - angle021;
        double angleP12 = Math.atan(dist20 * Math.sin(angleTheta) * sine1P2 / (dist12 * sine2P0 + dist20 * sine1P2 * Math.cos(angleTheta)));
        double angleP21 = MATH_ANGLE_ONE_SIXTH_CIRCLE - angleP12;
        double angleP20 = angle021 - angleP21;

        //double dist0P = dist20 * Math.sin(angleP20) / sine2P0;
        //double dist1P = dist12 * Math.sin(angleP21) / sine1P2;
        double dist2P = dist12 * Math.sin(angleP12) / sine1P2;

        // Determine the location of the platform center (Q is for quadrant or Y=0 line)
        double angleQ21 = Util.getDirection(dist12, diff12x, diff12y);
        double angleQ20 = Util.getDirection(dist20, -diff20x, -diff20y);

        // If adding lambda1 to angle CA, then subtracting lambda2 from angle CB
        // If subtracting lambda1 from angle CA, then adding lambda2 to angle CB
        double angleP20d1 = angleQ20 + angleP20; // compare only with angle12Pd2
        //double angleP21d2 = angleQ21 - angleP21; // compare only with angleP20d1
        double angleP20d2 = angleQ20 - angleP20; // compare only with angle12Pd1
        double angleP21d1 = angleQ21 + angleP21; // compare only with angle02Pd2

        // Find the angle pair that give the same (or close) angle
        mOrientationFlipped = Util.areAnglesEquivalent(angleP20d2, angleP21d1);
        double anglePlatform = mOrientationFlipped ? angleP21d1 : angleP20d1;

        // x = Cx + dist2P * cos(anglePlatform)
        mNewPlatformCenter[0] = (float)(mTethers[2][0] + dist2P * Math.cos(anglePlatform));
        // y = Cy + dist2P * sin(anglePlatform)
        mNewPlatformCenter[1] = (float)(mTethers[2][1] + dist2P * Math.sin(anglePlatform));
    }

    float[] getCenter() {
        return mNewPlatformCenter;
    }

    boolean getOrientationFlip() {
        return mOrientationFlipped;
    }
}