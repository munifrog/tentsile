package com.munifrog.design.tetheredtenttriangulator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final Clearing mClearing = new Clearing();

    private static final double MATH_SEEKBAR_MIN = 0.0;
    private static final double MATH_SEEKBAR_MAX = 100.0;
    private static final double MATH_SCALE_MIN = 1.0;
    private static final double MATH_SCALE_MAX = 30.0;
    private static final double MATH_SCALE_QUOTIENT =
            (MATH_SCALE_MAX - MATH_SCALE_MIN) / (MATH_SEEKBAR_MAX - MATH_SEEKBAR_MIN);
    private static final int MATH_SEEKBAR_INITIAL = 25;

    private int mPlatformSelection;
    private ImageButton mPlatformRotation;
    private Button mUnitToggle;
    private SeekBar mSeekBar;

    private int mCanvasLeft = 0;
    private int mCanvasTop = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClearing.setUnitStrings(
                getString(R.string.unit_meters_with_number),
                getString(R.string.unit_imperial_with_number)
        );

        Toolbar toolbar = findViewById(R.id.tb_main);
        setSupportActionBar(toolbar);

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

        mPlatformRotation = findViewById(R.id.im_rotate);
        mPlatformRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClearing.rotatePlatform();
            }
        });

        mUnitToggle = findViewById(R.id.im_units);
        mUnitToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleUnits();
            }
        });
        initializeUnits();

        mSeekBar = findViewById(R.id.sk_scale);
        setSeekBarPosition(MATH_SEEKBAR_INITIAL);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                // Setting minimum requires API 26, so scale values for ourselves:
                mClearing.setSliderScale(MATH_SCALE_MIN + MATH_SCALE_QUOTIENT * position);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Unnecessary at this time
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Unnecessary at this time
            }
        });

        ImageView iv_clearing = findViewById(R.id.iv_clearing);
        iv_clearing.setImageDrawable(mClearing);
        iv_clearing.setContentDescription(getResources().getString(R.string.desc_clearing));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
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
        if (platform.equals(getString(R.string.tentsile_tent_stingray))) {
            setEquilateral();
        } else if (platform.equals(getString(R.string.tenstile_tent_vista))) {
            setEquilateral();
        } else if (platform.equals(getString(R.string.tentsile_base_trillium))) {
            setEquilateral();
        } else if (platform.equals(getString(R.string.tentsile_test_universe))) {
            setEquilateral();
        } else if (platform.equals(getString(R.string.tentsile_tent_trilogy))) {
            setEquilateral();
        } else if (platform.equals(getString(R.string.tentsile_base_trillium_xl))) {
            setEquilateral();
        } else if (platform.equals(getString(R.string.tentsile_tent_una))) {
            setIsosceles(140);
        } else if (platform.equals(getString(R.string.tentsile_tent_flite))) {
            setIsosceles(140);
        } else if (platform.equals(getString(R.string.tentsile_tent_connect))) {
            setIsosceles(140);
        } else if (platform.equals(getString(R.string.tentsile_base_t_mini))) {
            setIsosceles(140);
        } else if (platform.equals(getString(R.string.none_custom))) {
            setIsosceles(140);
        } else {
            setEquilateral();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // This should not happen
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_default_tether_points:
                mClearing.configDefault();
                return true;
            case R.id.action_browser_tentsile:
                launchTentsile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSeekBarPosition(int position) {
        mSeekBar.setProgress(position);
        mClearing.setSliderScale(MATH_SCALE_MIN + MATH_SCALE_QUOTIENT * position);
    }

    private void launchTentsile() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_tentsile_website_main)));
        startActivity(browserIntent);
    }

    private void initializeUnits() {
        setUnits(false);
    }

    private void toggleUnits() {
        setUnits(!mClearing.getIsImperial());
    }

    private void setUnits(boolean isImperial) {
        mClearing.setIsImperial(isImperial);
        // The unit displayed should be what it will become if pushed, not what it currently is
        mUnitToggle.setText(mClearing.getIsImperial() ? R.string.unit_meters : R.string.unit_imperial);
    }

    private void setEquilateral() {
        mClearing.setPlatformSymmetricAngle(2 * Math.PI / 3);
        mPlatformRotation.setVisibility(View.GONE);
    }

    private void setIsosceles(double angle) {
        mClearing.setPlatformSymmetricAngle(angle * Math.PI / 180);
        mPlatformRotation.setVisibility(View.VISIBLE);
    }
}