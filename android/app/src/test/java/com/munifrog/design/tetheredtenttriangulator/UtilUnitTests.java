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
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.STRAP_DEFAULT;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.STRAP_EXTENSION;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.TENTSILE_CIRCUMFERENCE_DEFAULT;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.TETHERS_EQUILATERAL;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.TETHERS_ISOSCELES;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.TETHERS_SCALENE;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.MATH_ANGLE_FULL_CIRCLE;

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
            angle = i * angleDiff;
            assertTrue(Util.areAnglesEquivalent(angle, angle + MATH_ANGLE_FULL_CIRCLE));
            assertTrue(Util.areAnglesEquivalent(angle, angle + 2 * MATH_ANGLE_FULL_CIRCLE));
            assertTrue(Util.areAnglesEquivalent(angle, angle - MATH_ANGLE_FULL_CIRCLE));
            assertTrue(Util.areAnglesEquivalent(angle, angle - 2 * MATH_ANGLE_FULL_CIRCLE));
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

    @Test
    public void getTetherKnots_x_isWorking() {
        double pixelsToMetersConversion = 0.01;

        // X-Axis
        double startX = 50;
        double startY = 50;
        double finishX = 1310; // expecting 50 + 30 + 600 + 600 + 30
        double finishY = 50;

        Knots knots = Util.getTetherKnots(
                pixelsToMetersConversion,
                startX, startY,
                finishX, finishY,
                STRAP_DEFAULT, STRAP_EXTENSION,
                TENTSILE_CIRCUMFERENCE_DEFAULT
        );
        float[][] results = knots.knots;

        assertEquals(5, results.length);
        float[][] expected = { { 50, 50 }, { 80.48f, 50 }, { 680.48f, 50 }, { 1280.48f, 50 }, { 1310, 50} };
        assertArrayEquals(expected[0], results[0], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[1], results[1], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[2], results[2], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[3], results[3], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[4], results[4], (float) ALLOWANCE_DELTA_TWO);

        assertEquals(knots.symbol, Symbol.safe);
    }

    @Test
    public void getTetherKnots_y_isWorking() {
        double pixelsToMetersConversion = 0.01;

        // Y-Axis
        double startX = 50;
        double startY = 50;
        double finishX = 50;
        double finishY = 710; // expecting 50 + 30 + 600 + 30

        Knots knots = Util.getTetherKnots(
                pixelsToMetersConversion,
                startX, startY,
                finishX, finishY,
                STRAP_DEFAULT, STRAP_EXTENSION,
                TENTSILE_CIRCUMFERENCE_DEFAULT
        );
        float[][] results = knots.knots;

        assertEquals(4, results.length);
        float[][] expected = { { 50, 50 }, { 50, 80.48f }, { 50, 680.48f }, { 50, 710 } };
        assertArrayEquals(expected[0], results[0], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[1], results[1], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[2], results[2], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[3], results[3], (float) ALLOWANCE_DELTA_TWO);

        assertEquals(knots.symbol, Symbol.safe);
    }

    @Test
    public void getTetherKnots_45_isWorking() {
        double pixelsToMetersConversion = 0.01;

        double startX = 50;
        double startY = 50;
        double finishX = 516; // 50 + 21.1 + 424.3 + 21.2
        double finishY = 516;

        Knots knots = Util.getTetherKnots(
                pixelsToMetersConversion,
                startX, startY,
                finishX, finishY,
                STRAP_DEFAULT, STRAP_EXTENSION,
                TENTSILE_CIRCUMFERENCE_DEFAULT
        );
        float[][] results = knots.knots;

        assertEquals(4, results.length);
        float[][] expected = { { 50, 50 }, { 71.55f, 71.55f }, { 495.81f, 495.81f }, { 516, 516 } };
        assertArrayEquals(expected[0], results[0], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[1], results[1], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[2], results[2], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[3], results[3], (float) ALLOWANCE_DELTA_TWO);

        assertEquals(knots.symbol, Symbol.safe);
    }

    @Test
    public void getTetherKnots_135_isWorking() {
        double pixelsToMetersConversion = 0.01;

        double startX = 50;
        double startY = 516;
        double finishX = 516; // 50 + 21.2 + 424.3 + 21.2
        double finishY = 50;

        Knots knots = Util.getTetherKnots(
                pixelsToMetersConversion,
                startX, startY,
                finishX, finishY,
                STRAP_DEFAULT, STRAP_EXTENSION,
                TENTSILE_CIRCUMFERENCE_DEFAULT
        );
        float[][] results = knots.knots;

        assertEquals(4, results.length);
        float[][] expected = { { 50, 516 }, { 71.55f, 494.45f }, { 495.81f, 70.18f }, { 516, 50 } };
        assertArrayEquals(expected[0], results[0], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[1], results[1], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[2], results[2], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[3], results[3], (float) ALLOWANCE_DELTA_TWO);

        assertEquals(knots.symbol, Symbol.safe);
    }

    @Test
    public void getTetherKnots_135_in_range_isWorking() {
        double pixelsToMetersConversion = 0.01;

        double startX = 50;
        double startY = 490;
        double finishX = 490; // 50 + 21.2 + 418.8
        double finishY = 50;

        Knots knots = Util.getTetherKnots(
                pixelsToMetersConversion,
                startX, startY,
                finishX, finishY,
                STRAP_DEFAULT, STRAP_EXTENSION,
                TENTSILE_CIRCUMFERENCE_DEFAULT
        );
        float[][] results = knots.knots;

        assertEquals(3, results.length);
        float[][] expected = { { 50, 490 }, { 71.55f, 468.45f }, { 490, 50 } };
        assertArrayEquals(expected[0], results[0], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[1], results[1], (float) ALLOWANCE_DELTA_TWO);
        assertArrayEquals(expected[2], results[2], (float) ALLOWANCE_DELTA_TWO);

        assertEquals(knots.symbol, Symbol.scarce);
    }

    @Test
    public void getTetherKnots_45_too_close_isWorking() {
        double pixelsToMetersConversion = 0.01;

        double startX = 50;
        double startY = 50;
        double finishX = 70; // 50 + 20
        double finishY = 70;

        Knots knots = Util.getTetherKnots(
                pixelsToMetersConversion,
                startX, startY,
                finishX, finishY,
                STRAP_DEFAULT, STRAP_EXTENSION,
                TENTSILE_CIRCUMFERENCE_DEFAULT
        );
        float[][] results = knots.knots;

        assertEquals(2, results.length);
        float[][] expected = { { 50, 50 } };
        assertArrayEquals(expected[0], results[0], (float) ALLOWANCE_DELTA_TWO);

        assertEquals(knots.symbol, Symbol.tricky);
    }

    @Test
    public void getImperialWithMeterPrecision_isWorking() {
        double inchDecimal = 1.0 / 12.0;
        double eighthInchDecimal = inchDecimal / 8.0;
        double offset = inchDecimal / 64.0; // 1/64 of an inch; Before and after offset that should round to the target
        double precision = 0.00001;

        double target;
        double[][] expectations;
        double [] results;

        // One foot increments
        expectations = new double[][]{
                // Use different feet so it is obvious which one fails
                // [0] target            // [1] -feet // [2] +feet
                {  (1 +  1 * inchDecimal),   1,           1 },
                {  (2 +  2 * inchDecimal),   2,           2 },
                {  (3 +  3 * inchDecimal),   3,           3 },
                {  (4 +  4 * inchDecimal),   4,           4 },
                {  (5 +  5 * inchDecimal),   5,           5 },
                {  (6 +  6 * inchDecimal),   6,           7 },
                {  (7 +  7 * inchDecimal),   8,           8 },
                {  (8 +  8 * inchDecimal),   9,           9 },
                {  (9 +  9 * inchDecimal),  10,          10 },
                { (10 + 10 * inchDecimal),  11,          11 },
                { (11 + 11 * inchDecimal),  12,          12 },
                { (12 + 12 * inchDecimal),  13,          13 },
        };
        for (int i = 0; i < expectations.length; i++) {
            results = Util.getImperialWithMeterPrecision(expectations[i][0] - offset, Util.MATH_PRECISION_UNITS);
            assertEquals(expectations[i][1], results[0], precision);
            results = Util.getImperialWithMeterPrecision(expectations[i][0] + offset, Util.MATH_PRECISION_UNITS);
            assertEquals(expectations[i][2], results[0], precision);
        }

        // 4-inch increments (within one foot); Approximately 0.1 meters
        expectations = new double[][] {
                // Use different feet so it can be obvious which one fails
                // [0] target           // [1] -feet // [2] -inches // [3] +feet // [4] +inches
                { (1 + 1 * inchDecimal),    1,           0,             1,           0 },
                { (2 + 2 * inchDecimal),    2,           0,             2,           4 },
                { (3 + 3 * inchDecimal),    3,           4,             3,           4 },
                { (4 + 4 * inchDecimal),    4,           4,             4,           4 },
                { (5 + 5 * inchDecimal),    5,           4,             5,           4 },
                { (6 + 6 * inchDecimal),    6,           4,             6,           8 },
                { (7 + 7 * inchDecimal),    7,           8,             7,           8 },
                { (8 + 8 * inchDecimal),    8,           8,             8,           8 },
                { (9 + 9 * inchDecimal),    9,           8,             9,           8 },
                { (10 + 10 * inchDecimal), 10,           8,            11,           0 },
                { (11 + 11 * inchDecimal), 12,           0,            12,           0 },
                { (12 + 12 * inchDecimal), 13,           0,            13,           0 },
        };
        for (int i = 0; i < expectations.length; i++) {
            results = Util.getImperialWithMeterPrecision(expectations[i][0] - offset, Util.MATH_PRECISION_TENTHS);
            assertEquals(expectations[i][1], results[0], precision);
            assertEquals(expectations[i][2], results[1], precision);
            results = Util.getImperialWithMeterPrecision(expectations[i][0] + offset, Util.MATH_PRECISION_TENTHS);
            assertEquals(expectations[i][3], results[0], precision);
            assertEquals(expectations[i][4], results[1], precision);
        }

        // 3/8 inch increments (within one foot); Approximately 0.01 meters
        expectations = new double[][] {
                // Use different feet so it can be obvious which one fails
                // |..;..;.|;..;..;|.;..;..|
                // [0] target                     // [1] -feet // [2] -inches // [3] -eighths // [4] +feet // [5] +inches // [6] +eighths
                {  (1 +    0 * eighthInchDecimal),    1,           0,             0,              1,           0,             0 },
                {  (1 +    1 * eighthInchDecimal),    1,           0,             0,              1,           0,             0 },
                {  (2 +  1.5 * eighthInchDecimal),    2,           0,             0,              2,           0,             3 },
                {  (3 +    2 * eighthInchDecimal),    3,           0,             3,              3,           0,             3 },
                {  (4 +    3 * eighthInchDecimal),    4,           0,             3,              4,           0,             3 },
                {  (5 +    4 * eighthInchDecimal),    5,           0,             3,              5,           0,             3 },
                {  (6 +  4.5 * eighthInchDecimal),    6,           0,             3,              6,           0,             6 },
                {  (7 +    5 * eighthInchDecimal),    7,           0,             6,              7,           0,             6 },
                {  (8 +    6 * eighthInchDecimal),    8,           0,             6,              8,           0,             6 },
                {  (9 +    7 * eighthInchDecimal),    9,           0,             6,              9,           0,             6 },
                { (10 +  7.5 * eighthInchDecimal),   10,           0,             6,             10,           1,             1 },
                { (11 +    8 * eighthInchDecimal),   11,           1,             1,             11,           1,             1 },
                { (12 +    9 * eighthInchDecimal),   12,           1,             1,             12,           1,             1 },
                { (13 +   10 * eighthInchDecimal),   13,           1,             1,             13,           1,             1 },
                { (14 + 10.5 * eighthInchDecimal),   14,           1,             1,             14,           1,             4 },
                { (15 +   11 * eighthInchDecimal),   15,           1,             4,             15,           1,             4 },
                { (16 +   12 * eighthInchDecimal),   16,           1,             4,             16,           1,             4 },
                { (17 +   13 * eighthInchDecimal),   17,           1,             4,             17,           1,             4 },
                { (18 + 13.5 * eighthInchDecimal),   18,           1,             4,             18,           1,             7 },
                { (19 +   14 * eighthInchDecimal),   19,           1,             7,             19,           1,             7 },
                { (20 +   15 * eighthInchDecimal),   20,           1,             7,             20,           1,             7 },
                { (21 +   16 * eighthInchDecimal),   21,           1,             7,             21,           1,             7 },
                { (22 + 16.5 * eighthInchDecimal),   22,           1,             7,             22,           2,             2 },
                { (23 +   17 * eighthInchDecimal),   23,           2,             2,             23,           2,             2 },
                { (24 +   18 * eighthInchDecimal),   24,           2,             2,             24,           2,             2 },
                { (25 +   19 * eighthInchDecimal),   25,           2,             2,             25,           2,             2 },
                { (26 + 19.5 * eighthInchDecimal),   26,           2,             2,             26,           2,             5 },
                { (27 +   20 * eighthInchDecimal),   27,           2,             5,             27,           2,             5 },
                { (28 +   21 * eighthInchDecimal),   28,           2,             5,             28,           2,             5 },
                { (29 +   22 * eighthInchDecimal),   29,           2,             5,             29,           2,             5 },
                { (30 + 22.5 * eighthInchDecimal),   30,           2,             5,             30,           3,             0 },
                { (31 +   23 * eighthInchDecimal),   31,           3,             0,             31,           3,             0 },
                { (32 +   24 * eighthInchDecimal),   32,           3,             0,             32,           3,             0 },
        };
        double expectedFoot;
        double expectedInch;
        double expectedFraction;
        double lowestInch;
        for (int i = 0; i < expectations.length; i++) {
            // Each iteration is 3 inches; 4 iterations to span entire foot
            for (int j = 0; j < 4; j++) {
                lowestInch = 3.0 * j;
                target = expectations[i][0] + (lowestInch * inchDecimal);

                expectedFoot = expectations[i][1];
                expectedInch = expectations[i][2] + lowestInch;
                expectedFraction = expectations[i][3];
                if (expectedInch == 12) {
                    expectedFoot++;
                    expectedInch = 0;
                }
                results = Util.getImperialWithMeterPrecision(target - offset, Util.MATH_PRECISION_HUNDREDTHS);
                assertEquals(expectedFoot, results[0], precision);
                assertEquals(expectedInch, results[1], precision);
                assertEquals(expectedFraction, results[2], precision);

                expectedFoot = expectations[i][4];
                expectedInch = expectations[i][5] + lowestInch;
                expectedFraction = expectations[i][6];
                if (expectedInch == 12) {
                    expectedFoot++;
                    expectedInch = 0;
                }
                results = Util.getImperialWithMeterPrecision(target + offset, Util.MATH_PRECISION_HUNDREDTHS);
                assertEquals(expectedFoot, results[0], precision);
                assertEquals(expectedInch, results[1], precision);
                assertEquals(expectedFraction, results[2], precision);
            }
        }
    }
}
