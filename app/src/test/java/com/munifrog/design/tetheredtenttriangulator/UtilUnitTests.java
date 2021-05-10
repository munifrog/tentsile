package com.munifrog.design.tetheredtenttriangulator;

import org.junit.Test;

import static org.junit.Assert.*;

// Note: cosine and sine are computed in radians, not degrees
public class UtilUnitTests {
    private final String TAG = getClass().getSimpleName();

    @Test
    public void getDirection_isWorking() {
        double allowance = 0.001;
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
            assertEquals(angle, derivedAngle, allowance);

            // Negative angles
            negAngle = angle - 2 * Math.PI;
            deltaX = hypotenuse * Math.cos(negAngle);
            deltaY = hypotenuse * Math.sin(negAngle);
            derivedAngle = Util.getDirection(hypotenuse, deltaX, deltaY);
            if (derivedAngle < 0) { derivedAngle += 2 * Math.PI; }
            assertEquals(angle, derivedAngle, allowance);
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
}
