package com.munifrog.design.tetheredtenttriangulator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
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
import android.widget.TextView;

import androidx.preference.PreferenceManager;

import java.util.Objects;

public class ComposeBaseActivity
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
    private static final String SAVE_STATE_SYMBOL_VERBOSITY = "remembered_selection_symbol_verbosity";

    public final Clearing mClearing = new Clearing(this);

    private static final int MATH_SEEKBAR_INITIAL = 25;
    private static final int MATH_FINE_TUNE_SEEKBAR_ALLOWANCE = 25;
    private static final int UNIT_PRECISION = Util.MATH_PRECISION_HUNDREDTHS;

    public ImageButton mPlatformRotation;
    private Menu mToolbarMenu;
    private SeekBar mSeekBar;
    private Spinner mSpinner;
    private View mFineTuneLayout;
    private SeekBar mFineTuneSeekBar;
    private TextView mFineTuneText;
    private ArrayAdapter<String> mSpinAdapter;
    private Symbol mSymbolVerbosity = Symbol.safe; // Initially draw the least restrictive symbols
    private VersionSupport mVersionSupport;

    private int mCanvasLeft = 0;
    private int mCanvasTop = 0;
    private boolean mFineTuneActive = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVersionSupport = new VersionSupport(this);
        Rect actualSize = mVersionSupport.getBounds();
        mClearing.setFullWindowBounds(actualSize);

        int meterReference;
        int imperialReference;
        if (UNIT_PRECISION == Util.MATH_PRECISION_HUNDREDTHS) {
            meterReference = R.string.unit_meters_with_number_hundredths;
            imperialReference = R.string.unit_imperial_with_number_hundredths;
        } else if (UNIT_PRECISION == Util.MATH_PRECISION_TENTHS) {
            meterReference = R.string.unit_meters_with_number_tenths;
            imperialReference = R.string.unit_imperial_with_number_tenths;
        } else { // UNIT_PRECISION = Util.MATH_PRECISION_UNITS
            meterReference = R.string.unit_meters_with_number_units;
            imperialReference = R.string.unit_imperial_with_number_units;
        }
        mClearing.setUnitStrings(
                getString(meterReference),
                getString(imperialReference),
                UNIT_PRECISION
        );

        Resources r = getResources();
        String packageName = getPackageName();
        Resources.Theme theme = getTheme();
        // https://stackoverflow.com/a/22931750
        // https://stackoverflow.com/a/10141607
        int idCannot = r.getIdentifier("@android:drawable/ic_menu_close_clear_cancel", null, packageName);
        Drawable cannotIcon = ResourcesCompat.getDrawable(r, idCannot, theme);
        mVersionSupport.colorDrawable(cannotIcon, Color.RED);
        int idWarn = r.getIdentifier("@android:drawable/stat_sys_warning", null, packageName);
        Drawable warnIcon = ResourcesCompat.getDrawable(r, idWarn, theme);
        mVersionSupport.colorDrawable(warnIcon, Color.YELLOW);
        int idTricky = r.getIdentifier("@android:drawable/stat_notify_error", null, packageName);
        Drawable trickyIcon = ResourcesCompat.getDrawable(r, idTricky, theme);
        mVersionSupport.colorDrawable(trickyIcon, 0xFFFF7F00);
        mClearing.setSymbolIcons(cannotIcon, warnIcon, trickyIcon);

        Toolbar toolbar = findViewById(R.id.tb_main);
        if (r.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setTitle(R.string.app_name_landscape);
        } else {
            toolbar.setTitle(R.string.app_name_portrait);
        }
        setSupportActionBar(toolbar);

        mVersionSupport.hideVirtualButtons();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mSpinner = findViewById(R.id.sp_models);
        int mPlatformSelection = R.array.tent_models;
        String [] array = getResources().getStringArray(mPlatformSelection);
        mSpinAdapter = new ArrayAdapter<>(
                this,
                R.layout.text_spinner,
                array
        );
        mSpinAdapter.sort(String.CASE_INSENSITIVE_ORDER);
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
                mClearing.setSliderPosition(position);
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

        mFineTuneLayout = findViewById(R.id.inc_fine_tune); // TODO: Make visible when label touched
        mFineTuneText = findViewById(R.id.tx_fine_tune);
        mFineTuneSeekBar = findViewById(R.id.sk_fine_tune);
        mFineTuneSeekBar.setMax(2 * MATH_FINE_TUNE_SEEKBAR_ALLOWANCE);
        mFineTuneSeekBar.setProgress(MATH_FINE_TUNE_SEEKBAR_ALLOWANCE);
        mFineTuneSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {
                int offset = position - MATH_FINE_TUNE_SEEKBAR_ALLOWANCE;
                mFineTuneText.setText(mClearing.getOffsetString(offset));
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
        updateMenu();
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
        if (hasFocus) {
            mVersionSupport.hideVirtualButtons();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x, y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mFineTuneActive) {
                    fineTuneLabelFinish();
                } else {
                    mVersionSupport.hideVirtualButtons();
                    x = (int) event.getX();
                    y = (int) event.getY();
                    mClearing.pointSelection(x - mCanvasLeft,y - mCanvasTop);
                }
                break;
            case MotionEvent.ACTION_UP:
                x = (int) event.getX();
                y = (int) event.getY();
                mClearing.releaseSelection(x - mCanvasLeft,y - mCanvasTop);
                break;
            case MotionEvent.ACTION_MOVE:
                x = (int) event.getX();
                y = (int) event.getY();
                mClearing.updateSelection(x - mCanvasLeft, y - mCanvasTop);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        // Override this method in the lite or full ComposeActivity.java
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // This should not happen
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_default_tether_points) {
            mClearing.configDefault();
            mClearing.resetSelection();
            return true;
        } else if (id == R.id.action_faq) {
            launchFrequentlyAskedQuestions();
            return true;
        } else if (id == R.id.action_browser_tentsile) {
            launchTentsile();
            return true;
        } else if (id == R.id.action_browser_source_code) {
            launchSourceCode();
            return true;
        } else if (id == R.id.action_enable_imperial) {
            setUnits(true);
            return true;
        } else if (id == R.id.action_enable_meters) {
            setUnits(false);
            return true;
        } else if (id == R.id.action_decrease_symbols) {
            updateSymbol(Symbol.next(mSymbolVerbosity));
            updateMenu();
            return true;
        } else if (id == R.id.action_increase_symbols) {
            updateSymbol(Symbol.prev(mSymbolVerbosity));
            updateMenu();
            return true;
        } else if (id == android.R.id.home) {
            closeApp();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeApp();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
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
        edit.putInt(SAVE_STATE_SLIDER, mSeekBar.getProgress());
        if (mSpinner.getSelectedItem() != null) {
            edit.putString(SAVE_STATE_PLATFORM, mSpinner.getSelectedItem().toString());
        }
        edit.putString(SAVE_STATE_SYMBOL_VERBOSITY, mSymbolVerbosity.name());
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
            mClearing.resetSelection();
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
        if (prefs.contains(SAVE_STATE_SYMBOL_VERBOSITY)) {
            String verbosity = prefs.getString(SAVE_STATE_SYMBOL_VERBOSITY, "safe");
            updateSymbol(Symbol.valueOf(verbosity));
        }
    }

    private void setSeekBarPosition(int position) {
        mSeekBar.setProgress(position);
        mClearing.setSliderPosition(position);
    }

    private void launchFrequentlyAskedQuestions() {
        Intent faqIntent = new Intent(this, FAQActivity.class);
        startActivity(faqIntent);
    }

    private void launchTentsile() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_tentsile_website_main)));
        startActivity(browserIntent);
    }

    private void launchSourceCode() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_source_code_android)));
        startActivity(browserIntent);
    }

    private void closeApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

    private void initializeUnits() {
        mClearing.setIsImperial(false);
    }

    private void setUnits(boolean isImperial) {
        mClearing.setIsImperial(isImperial);
        updateMenu();
    }

    private void updateMenu() {
        boolean isImperial = mClearing.getIsImperial();
        // The unit displayed should be what it will become if pushed, not what it currently is
        mToolbarMenu.findItem(R.id.action_enable_imperial).setVisible(!isImperial);
        mToolbarMenu.findItem(R.id.action_enable_meters).setVisible(isImperial);
        int current = mSymbolVerbosity.ordinal();
        mToolbarMenu.findItem(R.id.action_decrease_symbols).setVisible(current < Symbol.impossible.ordinal());
        mToolbarMenu.findItem(R.id.action_increase_symbols).setVisible(current > Symbol.safe.ordinal());
    }

    private void updateSymbol(Symbol symbol) {
        mSymbolVerbosity = symbol;
        mClearing.setSymbolVerbosity(symbol);
    }

    @Override
    public void computePlatformCenter(PlatformCenterRun run) {
        runOnUiThread(run);
    }

    @Override
    public void triangleHasFlipped(boolean isFlipped) {
        mPlatformRotation.setScaleX(isFlipped ? -1 : 1);
    }

    @Override
    public void fineTuneLabelStart() {
        mFineTuneActive = true;
        mFineTuneText.setText(mClearing.getOffsetString(0));
        mFineTuneLayout.setVisibility(View.VISIBLE);
    }

    public void fineTuneLabelFinish() {
        mFineTuneActive = false;
        mFineTuneLayout.setVisibility(View.GONE);
        mClearing.updateSelection(0, 0);
        mFineTuneSeekBar.setProgress(MATH_FINE_TUNE_SEEKBAR_ALLOWANCE);
    }

    private void loadPresets(PreSets setting) {
        mClearing.setTetherPoints(PreSets.getTethers(setting));
        mClearing.resetSelection();
        mClearing.setIsImperial(PreSets.isImperial(setting));
        setSeekBarPosition(PreSets.getSlider(setting));
        int platformPosition = mSpinAdapter.getPosition(getString(PreSets.getPlatformStringId(setting)));
        mSpinner.setSelection(platformPosition);
    }
}