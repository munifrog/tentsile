package com.munifrog.design.tetheredtenttriangulator;

import org.junit.Test;

import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.ALLOWANCE_DELTA_TWO;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.ALLOWANCE_DELTA_TWO_FIVE;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.ALLOWANCE_DELTA_THREE;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.MATH_BASE_LENGTH_N;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.MATH_SQUARE_ROOT_OF_THREE;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.MATH_SQUARE_ROOT_OF_SEVEN;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.MATH_SQUARE_ROOT_OF_THIRTEEN;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.MATH_SQUARE_ROOT_OF_NINETEEN;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.TETHERS_EQUILATERAL;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.TETHERS_ISOSCELES;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.TETHERS_SCALENE;

import static org.junit.Assert.*;

// Note: cosine and sine are computed in radians, not degrees
public class UtilUnitTests {
    @Test
    public void getDirection_isWorking() {
        int iterations = 360;
        double angleDiff = 2 * Math.PI / iterations;
        double hypotenuse = 1.0;

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
        double[] actual = Util.getPerimeter(TETHERS_SCALENE);
        double[] expected = new double[6];
        expected[0] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_SEVEN;
        expected[1] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_NINETEEN;
        expected[2] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_THIRTEEN;
        expected[3] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 7;
        expected[4] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 19;
        expected[5] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 13;
        assertArrayEquals(expected, actual, ALLOWANCE_DELTA_TWO);

        actual = Util.getPerimeter(TETHERS_EQUILATERAL);
        expected[0] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_THREE;
        expected[1] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_THREE;
        expected[2] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_THREE;
        expected[3] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 3;
        expected[4] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 3;
        expected[5] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 3;
        assertArrayEquals(expected, actual, ALLOWANCE_DELTA_TWO);

        actual = Util.getPerimeter(TETHERS_ISOSCELES);
        expected[0] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_THREE * 2;
        expected[1] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_NINETEEN;
        expected[2] = MATH_BASE_LENGTH_N * MATH_SQUARE_ROOT_OF_NINETEEN;
        expected[3] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 12;
        expected[4] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 19;
        expected[5] = MATH_BASE_LENGTH_N * MATH_BASE_LENGTH_N * 19;
        assertArrayEquals(expected, actual, ALLOWANCE_DELTA_TWO_FIVE);
    }

    @Test
    public void shiftedCoordinates_angles_isWorking() {
        double[][] starting = new double[4][2];
        double[][] expected = new double[4][2];
        double[] translation = {0, 0};
        double angle = Math.PI / 6;

        starting[0][0] =  1;
        starting[0][1] =  0;
        starting[1][0] =  0;
        starting[1][1] =  1;
        starting[2][0] = -1;
        starting[2][1] =  0;
        starting[3][0] =  0;
        starting[3][1] = -1;

        expected[0][0] = MATH_SQUARE_ROOT_OF_THREE / 2;
        expected[0][1] = 0.5;
        expected[1][0] = -0.5;
        expected[1][1] = MATH_SQUARE_ROOT_OF_THREE / 2;
        expected[2][0] = -MATH_SQUARE_ROOT_OF_THREE / 2;
        expected[2][1] = -0.5;
        expected[3][0] = 0.5;
        expected[3][1] = -MATH_SQUARE_ROOT_OF_THREE / 2;

        double[][] actual = Util.shiftedCoordinates(starting, angle, 1.0, translation);
        assertArrayEquals(expected[0], actual[0], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[1], actual[1], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[2], actual[2], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[3], actual[3], ALLOWANCE_DELTA_TWO);
        actual = Util.shiftedCoordinates(actual, -angle, 1.0, translation);
        assertArrayEquals(starting[0], actual[0], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(starting[1], actual[1], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(starting[2], actual[2], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(starting[3], actual[3], ALLOWANCE_DELTA_TWO);
    }

    @Test
    public void shiftedCoordinates_translation_isWorking() {
        double[][] starting = new double[4][2];
        double[][] expected = new double[4][2];
        double[] translation = new double[2];
        double angle = 0;

        translation[0] = -3;
        translation[1] = 4;

        starting[0][0] =  1;
        starting[0][1] =  0;
        starting[1][0] =  0;
        starting[1][1] =  1;
        starting[2][0] = -1;
        starting[2][1] =  0;
        starting[3][0] =  0;
        starting[3][1] = -1;

        expected[0][0] = -2;
        expected[0][1] =  4;
        expected[1][0] = -3;
        expected[1][1] =  5;
        expected[2][0] = -4;
        expected[2][1] =  4;
        expected[3][0] = -3;
        expected[3][1] =  3;

        double[][] actual = Util.shiftedCoordinates(starting, angle, 1.0, translation);
        assertArrayEquals(expected[0], actual[0], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[1], actual[1], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[2], actual[2], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[3], actual[3], ALLOWANCE_DELTA_TWO);
        translation[0] =  3;
        translation[1] = -4;
        actual = Util.shiftedCoordinates(actual, -angle, 1.0, translation);
        assertArrayEquals(starting[0], actual[0], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(starting[1], actual[1], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(starting[2], actual[2], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(starting[3], actual[3], ALLOWANCE_DELTA_TWO);
    }

    @Test
    public void shiftedCoordinates_rotate_and_translate_isWorking() {
        double[][] starting = new double[4][2];
        double[][] expected = new double[4][2];
        double[] translation = new double[2];
        double angle = Math.PI / 3;

        translation[0] =  35;
        translation[1] = -24;

        starting[0][0] =  2;
        starting[0][1] =  0;
        starting[1][0] =  0;
        starting[1][1] =  2;
        starting[2][0] = -2;
        starting[2][1] =  0;
        starting[3][0] =  0;
        starting[3][1] = -2;

        expected[0][0] =  36;
        expected[0][1] = -24 + MATH_SQUARE_ROOT_OF_THREE;
        expected[1][0] =  35 - MATH_SQUARE_ROOT_OF_THREE;
        expected[1][1] = -23;
        expected[2][0] =  34;
        expected[2][1] = -24 - MATH_SQUARE_ROOT_OF_THREE;
        expected[3][0] =  35 + MATH_SQUARE_ROOT_OF_THREE;
        expected[3][1] = -25;

        double[][] actual = Util.shiftedCoordinates(starting, angle, 1.0, translation);
        assertArrayEquals(expected[0], actual[0], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[1], actual[1], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[2], actual[2], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[3], actual[3], ALLOWANCE_DELTA_TWO);

        translation[0] = -35;
        translation[1] =  24;
        angle = -Math.PI / 3;

        expected[0][0] = -35 +  1;
        expected[0][1] =  24 + -MATH_SQUARE_ROOT_OF_THREE;
        expected[1][0] = -35 +  MATH_SQUARE_ROOT_OF_THREE;
        expected[1][1] =  24 +  1;
        expected[2][0] = -35 + -1;
        expected[2][1] =  24 +  MATH_SQUARE_ROOT_OF_THREE;
        expected[3][0] = -35 + -MATH_SQUARE_ROOT_OF_THREE;
        expected[3][1] =  24 + -1;

        actual = Util.shiftedCoordinates(starting, angle, 1.0, translation);
        assertArrayEquals(expected[0], actual[0], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[1], actual[1], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[2], actual[2], ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[3], actual[3], ALLOWANCE_DELTA_TWO);
    }
}
