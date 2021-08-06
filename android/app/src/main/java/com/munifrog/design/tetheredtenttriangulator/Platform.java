package com.munifrog.design.tetheredtenttriangulator;

import android.graphics.Path;

class Platform {
    private final Path mPath;
    private final double [][] mTetherPoints;
    private final double mStrap;

    Platform(Path path, double[][] tetherPoints, double straps) {
        mPath = path;
        mTetherPoints = tetherPoints;
        mStrap = straps;
    }

    Path getPath() {
        return mPath;
    }

    double [][] getTetherPoints() {
        return mTetherPoints;
    }

    double getStrapLength() {
        return mStrap;
    }
}
