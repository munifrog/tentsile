package com.munifrog.design.tetheredtenttriangulator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

public class MainActivity
        extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, Clearing.ClearingListener
{
    private static final String SAVE_STATE_UNITS = "remembered_units";
    private static final String SAVE_STATE_SLIDER = "remembered_slider_position";
    private static final String SAVE_STATE_PLATFORM = "remembered_selection_platform";
    private static final String SAVE_STATE_TETHERS_0X = "remembered_tether_position_0x";
    private static final String SAVE_STATE_TETHERS_0Y = "remembered_tether_position_0y";
    private static final String SAVE_STATE_TETHERS_1X = "remembered_tether_position_1x";
    private static final String SAVE_STATE_TETHERS_1Y = "remembered_tether_position_1y";
    private static final String SAVE_STATE_TETHERS_2X = "remembered_tether_position_2x";
    private static final String SAVE_STATE_TETHERS_2Y = "remembered_tether_position_2y";

    private final Clearing mClearing = new Clearing(this);

    private static final double MATH_SEEKBAR_POINT_00 = 0.0;
    private static final double MATH_SEEKBAR_POINT_01 = 50.0;
    private static final double MATH_SEEKBAR_POINT_02 = 75.0;
    private static final double MATH_SEEKBAR_POINT_03 = 100.0;
    private static final double MATH_SCALE_POINT_00 = 1.0;
    private static final double MATH_SCALE_POINT_01 = 3.0;
    private static final double MATH_SCALE_POINT_02 = 8.0;
    private static final double MATH_SCALE_POINT_03 = 10.0;
    private static final double MATH_SCALE_SLOPE_00_01 = (MATH_SCALE_POINT_01 - MATH_SCALE_POINT_00) /
            (MATH_SEEKBAR_POINT_01 - MATH_SEEKBAR_POINT_00);
    private static final double MATH_SCALE_SLOPE_01_02 = (MATH_SCALE_POINT_02 - MATH_SCALE_POINT_01) /
            (MATH_SEEKBAR_POINT_02 - MATH_SEEKBAR_POINT_01);
    private static final double MATH_SCALE_SLOPE_02_03 = (MATH_SCALE_POINT_03 - MATH_SCALE_POINT_02) /
            (MATH_SEEKBAR_POINT_03 - MATH_SEEKBAR_POINT_02);

    private static final int MATH_SEEKBAR_INITIAL = 25;

    private static final double TENTSILE_BASE_CONNECT = 2.7;
    private static final double TENTSILE_BASE_FLITE = 2.7;
    private static final double TENTSILE_BASE_T_MINI = 2.7;
    private static final double TENTSILE_BASE_UNA = 1.6;
    private static final double TENTSILE_BASE_TRILOGY = TENTSILE_BASE_CONNECT;
    private static final double TENTSILE_HYPOTENUSE_CONNECT = 4.0;
    private static final double TENTSILE_HYPOTENUSE_FLITE = 3.25;
    private static final double TENTSILE_HYPOTENUSE_STINGRAY = 4.1;
    private static final double TENTSILE_HYPOTENUSE_T_MINI = 3.25;
    private static final double TENTSILE_HYPOTENUSE_TRILLIUM = 4.1;
    private static final double TENTSILE_HYPOTENUSE_TRILLIUM_XL = 6.0;
    private static final double TENTSILE_HYPOTENUSE_VISTA = 4.1;
    private static final double TENTSILE_HYPOTENUSE_UNA = 2.9;
    private static final double TENTSILE_HYPOTENUSE_UNIVERSE = 4.4;
    private static final double TENTSILE_HYPOTENUSE_TRILOGY = TENTSILE_HYPOTENUSE_CONNECT;

    private int mPlatformSelection;
    private ImageButton mPlatformRotation;
    private Menu mToolbarMenu;
    private SeekBar mSeekBar;
    private Spinner mSpinner;
    private ArrayAdapter<String> mSpinAdapter;

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

        mSpinner = findViewById(R.id.sp_models);
        mPlatformSelection = R.array.tent_models;
        String [] array = getResources().getStringArray(mPlatformSelection);
        mSpinAdapter = new ArrayAdapter<>(
                this,
                R.layout.text_spinner,
                array
        );
        mSpinAdapter.setDropDownViewResource(R.layout.text_spinner);
        mSpinner.setAdapter(mSpinAdapter);
        mSpinner.setOnItemSelectedListener(this);
        int platformSelection = mSpinAdapter.getPosition(getString(R.string.default_selection_model));
        mSpinner.setSelection(platformSelection);

        mPlatformRotation = findViewById(R.id.im_rotate);
        mPlatformRotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClearing.rotatePlatform();
            }
        });

        initializeUnits();

        mSeekBar = findViewById(R.id.sk_scale);
        setSeekBarPosition(MATH_SEEKBAR_INITIAL);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                // Setting minimum requires API 26, so scale values for ourselves:
                mClearing.setSliderScale(getSeekbarScale(position));
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
        mToolbarMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        matchUnits();
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
            setEquilateral(Util.getTentsileEquilateral(TENTSILE_HYPOTENUSE_STINGRAY));
        } else if (platform.equals(getString(R.string.tenstile_tent_vista))) {
            setEquilateral(Util.getTentsileEquilateral(TENTSILE_HYPOTENUSE_VISTA));
        } else if (platform.equals(getString(R.string.tentsile_base_trillium))) {
            setEquilateral(Util.getTentsileEquilateral(TENTSILE_HYPOTENUSE_TRILLIUM));
        } else if (platform.equals(getString(R.string.tentsile_test_universe))) {
            setEquilateral(Util.getTentsileEquilateral(TENTSILE_HYPOTENUSE_UNIVERSE));
        } else if (platform.equals(getString(R.string.tentsile_tent_trilogy))) {
            setEquilateral(Util.getTentsileTrilogy(TENTSILE_HYPOTENUSE_TRILOGY, TENTSILE_BASE_TRILOGY));
        } else if (platform.equals(getString(R.string.tentsile_base_trillium_xl))) {
            setEquilateral(Util.getTentsileEquilateral(TENTSILE_HYPOTENUSE_TRILLIUM_XL));
        } else if (platform.equals(getString(R.string.tentsile_tent_una))) {
            setIsosceles(TENTSILE_HYPOTENUSE_UNA, TENTSILE_BASE_UNA);
        } else if (platform.equals(getString(R.string.tentsile_tent_flite))) {
            setIsosceles(TENTSILE_HYPOTENUSE_FLITE, TENTSILE_BASE_FLITE);
        } else if (platform.equals(getString(R.string.tentsile_tent_connect))) {
            setIsosceles(TENTSILE_HYPOTENUSE_CONNECT, TENTSILE_BASE_CONNECT);
        } else if (platform.equals(getString(R.string.tentsile_base_t_mini))) {
            setIsosceles(TENTSILE_HYPOTENUSE_T_MINI, TENTSILE_BASE_T_MINI);
        } else {
            setEquilateral(Util.getTentsileEquilateral(TENTSILE_HYPOTENUSE_TRILLIUM));
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
            case R.id.action_enable_imperial:
                setUnits(true);
                return true;
            case R.id.action_enable_meters:
                setUnits(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(SAVE_STATE_UNITS, mClearing.getIsImperial());
        float[][] tethers = mClearing.getTetherPoints();
        edit.putFloat(SAVE_STATE_TETHERS_0X, tethers[0][0]);
        edit.putFloat(SAVE_STATE_TETHERS_0Y, tethers[0][1]);
        edit.putFloat(SAVE_STATE_TETHERS_1X, tethers[1][0]);
        edit.putFloat(SAVE_STATE_TETHERS_1Y, tethers[1][1]);
        edit.putFloat(SAVE_STATE_TETHERS_2X, tethers[2][0]);
        edit.putFloat(SAVE_STATE_TETHERS_2Y, tethers[2][1]);
        edit.putFloat(SAVE_STATE_SLIDER, (float) mClearing.getSliderScale());
        edit.putInt(SAVE_STATE_SLIDER, mSeekBar.getProgress());
        if (mSpinner.getSelectedItem() != null) {
            edit.putString(SAVE_STATE_PLATFORM, mSpinner.getSelectedItem().toString());
        }
        edit.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreState();
    }

    private void restoreState() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.contains(SAVE_STATE_TETHERS_0X) &&
                prefs.contains(SAVE_STATE_TETHERS_0X) &&
                prefs.contains(SAVE_STATE_TETHERS_1X) &&
                prefs.contains(SAVE_STATE_TETHERS_1Y) &&
                prefs.contains(SAVE_STATE_TETHERS_2X) &&
                prefs.contains(SAVE_STATE_TETHERS_2Y)
        ) {
            float[][] tethers = new float[3][2];
            tethers[0][0] = prefs.getFloat(SAVE_STATE_TETHERS_0X, 100);
            tethers[0][1] = prefs.getFloat(SAVE_STATE_TETHERS_0Y, 100);
            tethers[1][0] = prefs.getFloat(SAVE_STATE_TETHERS_1X, 100);
            tethers[1][1] = prefs.getFloat(SAVE_STATE_TETHERS_1Y, 100);
            tethers[2][0] = prefs.getFloat(SAVE_STATE_TETHERS_2X, 100);
            tethers[2][1] = prefs.getFloat(SAVE_STATE_TETHERS_2Y, 100);
            mClearing.setTetherPoints(tethers);
        }
        if (prefs.contains(SAVE_STATE_UNITS)) {
            mClearing.setIsImperial(prefs.getBoolean(SAVE_STATE_UNITS, false));
        }
        if (prefs.contains(SAVE_STATE_SLIDER)) {
            setSeekBarPosition(prefs.getInt(SAVE_STATE_SLIDER, MATH_SEEKBAR_INITIAL));
        }
        if (prefs.contains(SAVE_STATE_PLATFORM)) {
            int platformSelection = mSpinAdapter.getPosition(
                    prefs.getString(
                            SAVE_STATE_PLATFORM,
                            getString(R.string.default_selection_model)
                    )
            );
            if (platformSelection > -1) {
                mSpinner.setSelection(platformSelection);
            }
        }
    }

    private void setSeekBarPosition(int position) {
        mSeekBar.setProgress(position);
        mClearing.setSliderScale(getSeekbarScale(position));
    }

    private double getSeekbarScale(int position) {
        double diff, offset, slope;
        if (position < MATH_SEEKBAR_POINT_01) {
            diff = position - MATH_SEEKBAR_POINT_00;
            offset = MATH_SCALE_POINT_00;
            slope = MATH_SCALE_SLOPE_00_01;
        } else if (position < MATH_SEEKBAR_POINT_02) {
            diff = position - MATH_SEEKBAR_POINT_01;
            offset = MATH_SCALE_POINT_01;
            slope = MATH_SCALE_SLOPE_01_02;
        } else { // if (position <= MATH_SEEKBAR_POINT_03) {
            diff = position - MATH_SEEKBAR_POINT_02;
            offset = MATH_SCALE_POINT_02;
            slope = MATH_SCALE_SLOPE_02_03;
        }
        return offset + slope * diff;
    }

    private void launchTentsile() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_tentsile_website_main)));
        startActivity(browserIntent);
    }

    private void initializeUnits() {
        mClearing.setIsImperial(false);
    }

    private void setUnits(boolean isImperial) {
        mClearing.setIsImperial(isImperial);
        matchUnits();
    }

    private void matchUnits() {
        boolean isImperial = mClearing.getIsImperial();
        // The unit displayed should be what it will become if pushed, not what it currently is
        mToolbarMenu.findItem(R.id.action_enable_imperial).setVisible(!isImperial);
        mToolbarMenu.findItem(R.id.action_enable_meters).setVisible(isImperial);
    }

    private void setEquilateral(Platform platform) {
        mClearing.setPlatformSymmetricAngle(2 * Math.PI / 3);
        mPlatformRotation.setVisibility(View.GONE);
        mClearing.setPlatformDrawPath(platform);
    }

    private void setIsosceles(double hypotenuse, double base) {
        double [] measurements = Util.getIsoscelesMeasurements(hypotenuse, base);
        Platform platform = Util.getTentsileIsosceles(measurements[0], measurements[1], measurements[2]);
        mClearing.setPlatformDrawPath(platform);
        mClearing.setPlatformSymmetricAngle(2 * Math.PI / 3);
        mPlatformRotation.setVisibility(View.VISIBLE);
    }

    @Override
    public void computePlatformCenter(PlatformCenterRun run) {
        runOnUiThread(run);
    }
}