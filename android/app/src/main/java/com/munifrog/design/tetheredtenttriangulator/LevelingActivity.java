package com.munifrog.design.tetheredtenttriangulator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

enum ScreenOrientation {
    eUnknown,
    eLandscapeLeft,
    eLandscapeRight,
    ePortraitUp,
    ePortraitDown
}

// This class should only be called if it has already been determined that it will work.
// That would be when the retrieved `Sensor.TYPE_GRAVITY` is not null.
// Otherwise the Leveling option should not appear on the Menu to launch this activity.
public class LevelingActivity extends AppCompatActivity implements GravitySensor.GravityListener {

    private GravitySensor mGravitySensor;
    private LevelingView mDrawableLevel;
    private OrientationEventListener mOrientationEventListener;
    private ScreenOrientation mScreenOrientation;
    private int mPrevRotation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // https://stackoverflow.com/a/2591311
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_leveling);

        mDrawableLevel = new LevelingView(getResources().getDisplayMetrics());
        mDrawableLevel.setBubblePosition(new float[]{0, 0, 0});

        ImageView viewImage = findViewById(R.id.levelImage);
        viewImage.setImageDrawable(mDrawableLevel);
        viewImage.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(
                    View view,
                    int left, int top, int right, int bottom,
                    int prevLeft, int prevTop, int prevRight, int prevBottom
            ) {
                // Update the image dimensions, but only the first time they change
                mDrawableLevel.setDimensions(view.getMeasuredWidth(), view.getMeasuredHeight());
                viewImage.removeOnLayoutChangeListener(this);
            }
        });

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGravitySensor = new GravitySensor(sensorManager, this);

        // Screen configuration changes can be detected with onConfigurationChanged() when switching
        // from landscape to portrait, but we need onOrientationChanged() to detect when the
        // orientation changes between landscape-left to landscape-right, which we need to convert
        // the gravity direction to match the image drawn.
        // This does not work when the user locks the screen orientation so we present a Toast then.
        // https://stackoverflow.com/a/4729068
        // https://stackoverflow.com/a/34636700
        mOrientationEventListener = new OrientationEventListener(getApplicationContext()) {
            @Override
            public void onOrientationChanged(int rotation) {
                // We will receive the device orientation as a degree rotation. Modulus by 360 to
                // place it within the range 0 to 360. Divide by 90 to put the values between 0 and
                // 4, then multiply by 90 to get 0, 90, 180, 270, or 360.
                int nearestAxisDirection = (int)(90 * (Math.round((rotation % 360) / 90.0)));
                // Only detect changes between landscape-left (270) and landscape-right (90)
                if ((mPrevRotation != nearestAxisDirection) &&
                    ((nearestAxisDirection == 90) || (nearestAxisDirection == 270)) &&
                    (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                ){
                    // https://stackoverflow.com/a/34636700
                    if (android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.ACCELEROMETER_ROTATION, 0) != 1) {
                        Toast.makeText(getApplicationContext(), R.string.warn_screen_rotation_locked, Toast.LENGTH_SHORT).show();
                    }
                    mPrevRotation = nearestAxisDirection;
                    if (nearestAxisDirection == 90) {
                        mScreenOrientation = ScreenOrientation.eLandscapeRight;
                    } else {
                        mScreenOrientation = ScreenOrientation.eLandscapeLeft;
                    }
                }
            }
        };

        onConfigurationChanged(getResources().getConfiguration());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGravitySensor.start();
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGravitySensor.stop();
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.disable();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Use this method to detect Portrait or Landscape modes. Then use onOrientationChanged() to
        // detect changes between 90 and 270 degrees for landscape-left or landscape-right
        mScreenOrientation = getScreenOrientation();
    }

    private ScreenOrientation getScreenOrientation() {
        // Use this method to detect changes between portrait and landscape modes.
        // We'll later use onOrientationChanged() to distinguish between landscape-left and -right
        // https://stackoverflow.com/a/10383164
        ScreenOrientation screen = ScreenOrientation.eUnknown;
        int orientation = getResources().getConfiguration().orientation;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (rotation == Surface.ROTATION_90) {
                screen = ScreenOrientation.eLandscapeRight;
            } else if (rotation == Surface.ROTATION_270) {
                screen = ScreenOrientation.eLandscapeLeft;
            }
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (rotation == Surface.ROTATION_0) {
                screen = ScreenOrientation.ePortraitUp;
            } else if (rotation == Surface.ROTATION_180) {
                screen = ScreenOrientation.ePortraitDown;
            }
        }
        return screen;
    }

    @Override
    public void updateBubblePosition(float[] position) {
        mDrawableLevel.setBubblePosition(getRelativeBubblePosition(position));
    }

    private float [] getRelativeBubblePosition(float[] provided) {
        // Gravity is measured relative to the device, so convert it when orientation changes
        switch(mScreenOrientation) {
            case eLandscapeLeft:
                return new float[] { -provided[1], -provided[0], provided[2] };
            case eLandscapeRight:
                return new float[] { provided[1], provided[0], provided[2] };
            default:
            case eUnknown:
            case ePortraitUp:
                return new float[] { provided[0], -provided[1], provided[2] };
            case ePortraitDown:
                return new float[] { -provided[0], provided[1], provided[2] };
        }
    }
}