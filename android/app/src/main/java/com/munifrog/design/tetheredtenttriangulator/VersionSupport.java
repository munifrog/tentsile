package com.munifrog.design.tetheredtenttriangulator;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Display;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

class VersionSupport {
    private final AppCompatActivity mActivity;

    VersionSupport(AppCompatActivity activity) {
        mActivity = activity;
    }

    Display getDisplay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return getDisplayR();
        } else {
            return getDisplayQ();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    private Display getDisplayR() {
        return mActivity.getApplicationContext().getDisplay();
    }
    @SuppressWarnings("deprecation")
    private Display getDisplayQ() {
        return mActivity.getWindowManager().getDefaultDisplay();
    }

    Rect getBounds() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return getBoundsR();
        } else {
            return getBoundsQ(getDisplay());
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.R)
    private Rect getBoundsR() {
        return mActivity.getWindowManager().getCurrentWindowMetrics().getBounds();
    }
    @SuppressWarnings("deprecation")
    private Rect getBoundsQ(Display window) {
        Rect rect = new Rect();
        window.getRectSize(rect);
        return rect;
    }

    void colorDrawable(Drawable icon, int colorCode) {
        if (icon != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                colorDrawableQ(icon, colorCode);
            } else {
                colorDrawableP(icon, colorCode);
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    void colorDrawableQ(Drawable icon, int colorCode) {
        icon.setColorFilter(new BlendModeColorFilter(colorCode, BlendMode.SRC_IN));
    }
    @SuppressWarnings("deprecation")
    void colorDrawableP(Drawable icon, int colorCode) {
        icon.setColorFilter(colorCode, PorterDuff.Mode.MULTIPLY);
    }

    // https://developer.android.com/training/system-ui/immersive
    void hideVirtualButtons() {
        WindowInsetsControllerCompat windowsInsetController =
                ViewCompat.getWindowInsetsController(mActivity.getWindow().getDecorView());
        if (windowsInsetController == null) {
            return;
        }
        windowsInsetController.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );
        windowsInsetController.hide(WindowInsetsCompat.Type.systemBars());
    }
}
