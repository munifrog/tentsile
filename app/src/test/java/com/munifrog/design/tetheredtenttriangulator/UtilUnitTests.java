package com.munifrog.design.tetheredtenttriangulator;

import org.junit.Test;

import static org.junit.Assert.*;

// Note: cosine and sine are computed in radians, not degrees
public class UtilUnitTests {
    private final String TAG = getClass().getSimpleName();

    private static final double MATH_SQUARE_ROOT_OF_THREE = Math.sqrt(3);
    private static final double MATH_SQUARE_ROOT_OF_SEVEN = Math.sqrt(7);
    private static final double MATH_SQUARE_ROOT_OF_THIRTEEN = Math.sqrt(13);
    private static final double MATH_SQUARE_ROOT_OF_NINETEEN = Math.sqrt(19);
    private static final double MATH_BASE_LENGTH_N = 200;
    private static final double MATH_CENTER_X = 300;
    private static final double MATH_CENTER_Y = 300;
    private static final double ALLOWANCE_DELTA_ONE = 0.1;
    private static final double ALLOWANCE_DELTA_TWO = 0.01;
    private static final double ALLOWANCE_DELTA_THREE = 0.001;
    private static final float[][] scalene_tethers = getScalene();

    @Test
    public void getDirection_isWorking() {
        int iterations = 360;
        double angleDiff = 2 * Math.PI / iterations;
        double hypotenuse = 1.0;
        System.out.println(TAG + "angleDiff: \"" + angleDiff + "\"");

        double angle, negAngle, deltaX, deltaY, derivedAngle;
        for (int i = 0; i < iterations; i++) {
            // Positive angles
            angle = i * angleDiff; // technically added to 0.0, the starting point
            deltaX = hypotenuse * Math.cos(angle);
            deltaY = hypotenuse * Math.sin(angle);
            derivedAngle = Util.getDirection(hypotenuse, deltaX, deltaY);
            if (derivedAngle < 0) { derivedAngle += 2 * Math.PI; }
            assertEquals(angle, derivedAngle, ALLOWANCE_DELTA_THREE);

            // Negative angles
            negAngle = angle - 2 * Math.PI;
            deltaX = hypotenuse * Math.cos(negAngle);
            deltaY = hypotenuse * Math.sin(negAngle);
            derivedAngle = Util.getDirection(hypotenuse, deltaX, deltaY);
            if (derivedAngle < 0) { derivedAngle += 2 * Math.PI; }
            assertEquals(angle, derivedAngle, ALLOWANCE_DELTA_THREE);
        }
    }

    @Test
    public void areAnglesEquivalent_isWorking() {
        int iterations = 360;
        double angleDiff = 2 * Math.PI / iterations;
        double angle, negAngle;
        for (int i = 0; i < iterations; i++) {
            // In theory we could handle any two angles, but since the greatest an angle could be in
            // a triangle is 1/2 a circle, it should suffice to test from -pi to +pi.
            // Positive circle (0 to 2pi)
            angle = i * angleDiff;
            assertTrue(Util.areAnglesEquivalent(angle, angle));
            // Negative circle (-2pi to 0)
            negAngle = angle - 2 * Math.PI;
            assertTrue(Util.areAnglesEquivalent(angle, negAngle));
        }
    }

    @Test
    public void getPerimeter_isWorking() {
        // Set up equilateral triangle and test perimeter and angles
        double[] actual = Util.getPerimeter(scalene_tethers);
        double[] expected = new double[6];
        expected[0] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_SEVEN;
        expected[1] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_NINETEEN;
        expected[2] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_THIRTEEN;
        expected[3] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 7;
        expected[4] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 19;
        expected[5] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 13;
        assertArrayEquals(expected, actual, ALLOWANCE_DELTA_TWO);
    }

    private static float[][] getScalene() {
        float[][] tethers = new float[3][2];
        float[] center = { (float) MATH_CENTER_X, (float) MATH_CENTER_Y };

        tethers[0][0] = (float)(center[0] - MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N / 2);
        tethers[0][1] = (float)(center[1] + MATH_BASE_LENGTH_N + MATH_BASE_LENGTH_N / 2);
        tethers[1][0] = (float)(center[0] + MATH_SQUARE_ROOT_OF_THREE * MATH_BASE_LENGTH_N);
        tethers[1][1] = (float)(center[1] + MATH_BASE_LENGTH_N + MATH_BASE_LENGTH_N);
        tethers[2][0] =        (center[0]);
        tethers[2][1] = (float)(center[1] + MATH_BASE_LENGTH_N - 3 * MATH_BASE_LENGTH_N);
        return tethers;
    }
}
