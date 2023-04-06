package com.munifrog.design.tetheredtenttriangulator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

// This class should only be called if it has already been determined that it will work.
// That would be when the retrieved `Sensor.TYPE_GRAVITY` is not null.
// Otherwise the Leveling option should not appear on the Menu to launch this activity.
public class LevelingActivity extends AppCompatActivity implements GravitySensor.GravityListener {

    private GravitySensor mGravitySensor;
    private LevelingView mDrawableLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                mDrawableLevel.setDimensions(view.getMeasuredWidth(), view.getMeasuredHeight());
                viewImage.removeOnLayoutChangeListener(this);
            }
        });

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGravitySensor = new GravitySensor(sensorManager, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGravitySensor.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGravitySensor.stop();
    }

    @Override
    public void updateBubblePosition(float[] position) {
        mDrawableLevel.setBubblePosition(position);
    }
}