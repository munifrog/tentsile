package com.munifrog.design.tetheredtenttriangulator;

import org.junit.Test;

import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.ALLOWANCE_DELTA_THREE;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.MATH_CENTER_X;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.MATH_CENTER_Y;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.TETHERS_EQUILATERAL;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.TETHERS_ISOSCELES;
import static com.munifrog.design.tetheredtenttriangulator.UnitTestUtil.TETHERS_SCALENE;
import static org.junit.Assert.*;

public class TetherCenterUnitTests {

    final double MATH_ANGLE_BALANCED = Math.PI * 2.0 / 3.0;

    @Test
    public void PlatformCenterRun_equilateral_isWorking() {
        TetherCenter platformCenter = new TetherCenter(TETHERS_EQUILATERAL);
        platformCenter.process(MATH_ANGLE_BALANCED);
        float[] actual = platformCenter.getCenter();
        float[] expected = { (float) MATH_CENTER_X, (float) MATH_CENTER_Y };
        assertArrayEquals(expected, actual, (float)ALLOWANCE_DELTA_THREE);
    }

    @Test
    public void PlatformCenterRun_isosceles_isWorking() {
        TetherCenter platformCenter = new TetherCenter(TETHERS_ISOSCELES);
        platformCenter.process(MATH_ANGLE_BALANCED);
        float[] actual = platformCenter.getCenter();
        float[] expected = { (float) MATH_CENTER_X, (float) MATH_CENTER_Y };
        assertArrayEquals(expected, actual, (float)ALLOWANCE_DELTA_THREE);
    }

    @Test
    public void PlatformCenterRun_scalene_isWorking() {
        TetherCenter platformCenter = new TetherCenter(TETHERS_SCALENE);
        platformCenter.process(MATH_ANGLE_BALANCED);
        float[] actual = platformCenter.getCenter();
        float[] expected = { (float) MATH_CENTER_X, (float) MATH_CENTER_Y };
        assertArrayEquals(expected, actual, (float)ALLOWANCE_DELTA_THREE);
    }
}
