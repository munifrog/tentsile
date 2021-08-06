package com.munifrog.design.tetheredtenttriangulator;

class PlatformCenterRun implements Runnable {
    interface PlatformCenterListener {
        void onPlatformComputed(float[] newPlatform, boolean orientation);
    }

    private final PlatformCenterListener mListener;
    private final TetherCenter mTetherCenter;

    PlatformCenterRun(
            PlatformCenterListener listener,
            final float[][] tetherPoints
    ) {
        mListener = listener;
        mTetherCenter = new TetherCenter(
                tetherPoints
        );
    }

    public void run() {
        mTetherCenter.process();
        mListener.onPlatformComputed(
                mTetherCenter.getCenter(),
                mTetherCenter.getOrientationFlip()
        );
    }
}
