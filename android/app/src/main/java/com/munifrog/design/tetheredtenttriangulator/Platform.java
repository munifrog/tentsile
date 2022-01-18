package com.munifrog.design.tetheredtenttriangulator;

import android.graphics.Path;

class Platform {
    private final Path mPath;
    private final double [][] mTetherPoints;
    private final double mStrap;
    private final double mCircumference;

    Platform(Path path, double[][] tetherPoints, double straps, double circumference) {
        mPath = path;
        mTetherPoints = tetherPoints;
        mStrap = straps;
        mCircumference = circumference;
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

    double getCircumference() {
        return mCircumference;
    }
}
