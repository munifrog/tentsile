package com.munifrog.design.tetheredtenttriangulator;

import android.view.View;
import android.widget.AdapterView;

public class ComposeActivity extends ComposeBaseActivity {
    private static final double TENTSILE_BASE_CONNECT = 2.7;
    private static final double TENTSILE_BASE_DUO = 2.7;
    private static final double TENTSILE_BASE_FLITE = 2.7;
    private static final double TENTSILE_BASE_T_MINI = 2.7;
    private static final double TENTSILE_BASE_UNA = 1.6;
    private static final double TENTSILE_BASE_TRILOGY = TENTSILE_BASE_CONNECT;
    private static final double TENTSILE_HYPOTENUSE_CONNECT = 4.0;
    private static final double TENTSILE_HYPOTENUSE_DUO = 4.0;
    private static final double TENTSILE_HYPOTENUSE_FLITE = 3.25;
    private static final double TENTSILE_HYPOTENUSE_STINGRAY = 4.1;
    private static final double TENTSILE_HYPOTENUSE_T_MINI = 3.25;
    private static final double TENTSILE_HYPOTENUSE_TRILLIUM = 4.1;
    private static final double TENTSILE_HYPOTENUSE_TRILLIUM_XL = 6.0;
    private static final double TENTSILE_HYPOTENUSE_VISTA = 4.1;
    private static final double TENTSILE_HYPOTENUSE_UNA = 2.9;
    private static final double TENTSILE_HYPOTENUSE_UNIVERSE = 4.4;
    private static final double TENTSILE_HYPOTENUSE_TRILOGY = TENTSILE_HYPOTENUSE_CONNECT;
    private static final double TENTSILE_STRAPS_DEFAULT = 6.0;
    private static final double TENTSILE_STRAPS_UNA = 4.0;

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
            setIsosceles(TENTSILE_HYPOTENUSE_UNA, TENTSILE_BASE_UNA, TENTSILE_STRAPS_UNA);
        } else if (platform.equals(getString(R.string.tentsile_tent_flite))) {
            setIsosceles(TENTSILE_HYPOTENUSE_FLITE, TENTSILE_BASE_FLITE, TENTSILE_STRAPS_DEFAULT);
        } else if (platform.equals(getString(R.string.tentsile_tent_connect))) {
            setIsosceles(TENTSILE_HYPOTENUSE_CONNECT, TENTSILE_BASE_CONNECT, TENTSILE_STRAPS_DEFAULT);
        } else if (platform.equals(getString(R.string.tentsile_base_duo))) {
            setIsosceles(TENTSILE_HYPOTENUSE_DUO, TENTSILE_BASE_DUO, TENTSILE_STRAPS_DEFAULT);
        } else if (platform.equals(getString(R.string.tentsile_base_t_mini))) {
            setIsosceles(TENTSILE_HYPOTENUSE_T_MINI, TENTSILE_BASE_T_MINI, TENTSILE_STRAPS_DEFAULT);
        } else {
            setEquilateral(Util.getTentsileEquilateral(TENTSILE_HYPOTENUSE_TRILLIUM));
        }
    }

    private void setEquilateral(Platform platform) {
        mClearing.setPlatformSymmetricAngle();
        mPlatformRotation.setVisibility(View.GONE);
        mClearing.setPlatformDrawPath(platform);
    }

    private void setIsosceles(double hypotenuse, double base, double strap) {
        double [] measurements = Util.getIsoscelesMeasurements(hypotenuse, base);
        Platform platform = Util.getTentsileIsosceles(
                measurements[0],
                measurements[1],
                measurements[2],
                strap
        );
        mClearing.setPlatformDrawPath(platform);
        mClearing.setPlatformSymmetricAngle();
        mPlatformRotation.setVisibility(View.VISIBLE);
    }
}