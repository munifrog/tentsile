package com.munifrog.design.tetheredtenttriangulator;

import android.graphics.Path;

class Platform {
    private final Path mPath;
    private final double [][] mTetherPoints;

    Platform(Path path, double[][] tetherPoints) {
        mPath = path;
        mTetherPoints = tetherPoints;
    }

    Path getPath() {
        return mPath;
    }

    double [][] getTetherPoints() {
        return mTetherPoints;
    }
}
