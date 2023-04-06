package com.munifrog.design.tetheredtenttriangulator;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

class GravitySensor implements SensorEventListener {
    private final SensorManager mSensorManager;
    private final Sensor mGravitySensor;
    private final GravityListener mListener;
    private float[] mLastReading = {0, 0, 0};

    public interface GravityListener {
        void updateBubblePosition(float[] bubble);
    }

    GravitySensor(SensorManager sensorManager, GravityListener listener) {
        this.mSensorManager = sensorManager;
        this.mListener = listener;
        this.mGravitySensor = this.mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    @SuppressLint("NewApi")
    public void start() {
        if (mGravitySensor != null) {
            mSensorManager.registerListener(
                    this,
                    mGravitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL,
                    SensorManager.SENSOR_DELAY_UI
            );
        }
    }

    public void stop() {
        if (mGravitySensor != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    private boolean isEqual(float[] a, float[] b) {
        final float TOLERANCE = 0.00001f;
        return Math.abs(a[0] - b[0]) < TOLERANCE &&
                Math.abs(a[1] - b[1]) < TOLERANCE &&
                Math.abs(a[2] - b[2]) < TOLERANCE;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
            float[] reading = sensorEvent.values.clone();
            if (!isEqual(mLastReading, reading)) {
                mLastReading = reading.clone();
                float length = (float)Math.sqrt(reading[0] * reading[0] + reading[1] * reading[1] + reading[2] * reading[2]);
                reading[0] /= length;
                reading[1] /= length;
                reading[2] /= length;
                mListener.updateBubblePosition(reading);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Accuracy changes do not matter here
    }
}
