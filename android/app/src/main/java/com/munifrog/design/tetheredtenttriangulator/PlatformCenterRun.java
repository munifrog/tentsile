package com.munifrog.design.tetheredtenttriangulator;

class PlatformCenterRun implements Runnable {
    interface PlatformCenterListener {
        void onPlatformComputed(float[] newPlatform, boolean orientation);
    }

    private final PlatformCenterListener mListener;
    private final TetherCenter mTetherCenter;
    private final double mTetherAngle;

    PlatformCenterRun(
            PlatformCenterListener listener,
            final float[][] tetherPoints,
            double tetherAngle
    ) {
        mListener = listener;
        mTetherCenter = new TetherCenter(
                tetherPoints
        );
        mTetherAngle = tetherAngle;
    }

    public void run() {
        mTetherCenter.process(mTetherAngle);
        mListener.onPlatformComputed(
                mTetherCenter.getCenter(),
                mTetherCenter.getOrientationFlip()
        );
    }
}
