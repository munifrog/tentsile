package com.munifrog.design.tetheredtenttriangulator;

class UnitTestUtil {
    static final double ALLOWANCE_DELTA_ONE = 0.1;
    static final double ALLOWANCE_DELTA_TWO = 0.01;
    static final double ALLOWANCE_DELTA_TWO_FIVE = 0.015;
    static final double ALLOWANCE_DELTA_THREE = 0.001;
    static final double MATH_BASE_LENGTH_N = 200;
    static final double MATH_CENTER_X = 300;
    static final double MATH_CENTER_Y = 300;
    static final double MATH_SQUARE_ROOT_OF_THREE = Math.sqrt(3);
    static final double MATH_SQUARE_ROOT_OF_SEVEN = Math.sqrt(7);
    static final double MATH_SQUARE_ROOT_OF_THIRTEEN = Math.sqrt(13);
    static final double MATH_SQUARE_ROOT_OF_NINETEEN = Math.sqrt(19);
    static final float[][] TETHERS_EQUILATERAL = UnitTestUtil.getEquilateral();
    static final float[][] TETHERS_ISOSCELES = UnitTestUtil.getIsosceles();
    static final float[][] TETHERS_SCALENE = UnitTestUtil.getScalene();
    static final double TENTSILE_CIRCUMFERENCE_DEFAULT = 0.785398163397448; // pi * 25cm or 10inch diameter
    static final double TENTSILE_CIRCUMFERENCE_UNA = 0.628318530717959; // pi * 20cm or 8inch diameter
    static final double MATH_ANGLE_FULL_CIRCLE = Math.PI * 2;

    static final double STRAP_EXTENSION = 6;
    static final double STRAP_DEFAULT = 6;
    static final double STRAP_UNA = 4;

    private static float[][] getEquilateral() {
        float[][] tethers = new float[3][2];
        float[] center = { (float) MATH_CENTER_X, (float) MATH_CENTER_Y };

        tethers[0][0] =        (center[0]);
        tethers[0][1] = (float)(center[1] + MATH_BASE_LENGTH_N);
        tethers[1][0] = (float)(center[0] - MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2);
        tethers[1][1] = (float)(center[1] - MATH_BASE_LENGTH_N / 2);
        tethers[2][0] = (float)(center[0] + MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2);
        tethers[2][1] = (float)(center[1] - MATH_BASE_LENGTH_N / 2);
        return tethers;
    }

    private static float[][] getIsosceles() {
        float[][] tethers = new float[3][2];
        float[] center = { (float) MATH_CENTER_X, (float) MATH_CENTER_Y };

        tethers[0][0] = (float)(center[0] - MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_THREE);
        tethers[0][1] = (float)(center[1] + MATH_BASE_LENGTH_N);
        tethers[1][0] = (float)(center[0] + MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_THREE);
        tethers[1][1] = (float)(center[1] + MATH_BASE_LENGTH_N);
        tethers[2][0] =        (center[0]);
        tethers[2][1] = (float)(center[1] - MATH_BASE_LENGTH_N * 3);
        return tethers;
    }

    private static float[][] getScalene() {
        float[][] tethers = new float[3][2];
        float[] center = { (float) MATH_CENTER_X, (float) MATH_CENTER_Y };

        // Length N
        tethers[0][0] = (float)(center[0] - MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2);
        tethers[0][1] = (float)(center[1] + MATH_BASE_LENGTH_N / 2);
        // Length 2N
        tethers[1][0] = (float)(center[0] + MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N);
        tethers[1][1] = (float)(center[1] + MATH_BASE_LENGTH_N);
        // Length 3N
        tethers[2][0] =        (center[0]);
        tethers[2][1] = (float)(center[1] - 3 * MATH_BASE_LENGTH_N);
        return tethers;
    }
}
