package com.munifrog.design.tetheredtenttriangulator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final Clearing mClearing = new Clearing();

    private int mPlatformSelection;

    private int mCanvasLeft = 0;
    private int mCanvasTop = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner models = findViewById(R.id.sp_models);
        mPlatformSelection = R.array.tent_models;
        String [] array = getResources().getStringArray(mPlatformSelection);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                R.layout.text_spinner,
                array
        );
        arrayAdapter.setDropDownViewResource(R.layout.text_spinner);
        models.setAdapter(arrayAdapter);
        models.setOnItemSelectedListener(this);
        int platformSelection = arrayAdapter.getPosition(getString(R.string.default_selection_model));
        models.setSelection(platformSelection);

        ImageButton platformRotation = findViewById(R.id.im_rotate);
        platformRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClearing.rotatePlatform();
            }
        });

        ImageButton changeScaleUnits = findViewById(R.id.im_units);
        changeScaleUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        ImageView iv_clearing = findViewById(R.id.iv_clearing);
        iv_clearing.setImageDrawable(mClearing);
        iv_clearing.setContentDescription(getResources().getString(R.string.desc_clearing));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ImageView iv_clearing = findViewById(R.id.iv_clearing);
        int [] viewLoc = new int[2];
        iv_clearing.getLocationOnScreen(viewLoc);
        mCanvasLeft = viewLoc[0];
        mCanvasTop = viewLoc[1];
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x, y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getX();
                y = (int) event.getY();
                mClearing.selectTether(x - mCanvasLeft,y - mCanvasTop);
                break;
            case MotionEvent.ACTION_UP:
                x = (int) event.getX();
                y = (int) event.getY();
                mClearing.releaseTether();
                break;
            case MotionEvent.ACTION_MOVE:
                x = (int) event.getX();
                y = (int) event.getY();
                mClearing.updateTether(x - mCanvasLeft, y - mCanvasTop);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String platform = (String) adapterView.getItemAtPosition(position);
        // These should match the strings in the arrays
        if (platform.equals("Stingray (3) Tent")) {
            mClearing.setPlatformSymmetricAngle(2 * Math.PI / 3);
        } else if (platform.equals("Vista (3) Tent")) {
            mClearing.setPlatformSymmetricAngle(2 * Math.PI / 3);
        } else if (platform.equals("Trillium (3) Hammock")) {
            mClearing.setPlatformSymmetricAngle(2 * Math.PI / 3);
        } else if (platform.equals("Universe (5) Tent")) {
            mClearing.setPlatformSymmetricAngle(2 * Math.PI / 3);
        } else if (platform.equals("Trilogy (6) Tent")) {
            mClearing.setPlatformSymmetricAngle(2 * Math.PI / 3);
        } else if (platform.equals("Trillium XL (6) Hammock")) {
            mClearing.setPlatformSymmetricAngle(2 * Math.PI / 3);
        } else if (platform.equals("Una (1) Tent")) {
            mClearing.setPlatformSymmetricAngle(140 * Math.PI / 180);
        } else if (platform.equals("Flite (2) Tent")) {
            mClearing.setPlatformSymmetricAngle(140 * Math.PI / 180);
        } else if (platform.equals("Connect (2) Tent")) {
            mClearing.setPlatformSymmetricAngle(140 * Math.PI / 180);
        } else if (platform.equals("T-Mini (2) Hammock")) {
            mClearing.setPlatformSymmetricAngle(140 * Math.PI / 180);
        } else if (platform.equals("Custom")) {
            mClearing.setPlatformSymmetricAngle(140 * Math.PI / 180);
        } else {
            mClearing.setPlatformSymmetricAngle(2 * Math.PI / 3);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // This should not happen
    }
}