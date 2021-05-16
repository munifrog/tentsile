package com.munifrog.design.tetheredtenttriangulator;

import android.view.View;
import android.widget.AdapterView;

public class ComposeActivity extends ComposeBaseActivity {
    private static final double TENTSILE_HYPOTENUSE_STINGRAY = 4.1;
    private static final double TENTSILE_HYPOTENUSE_TRILLIUM = 4.1;
    private static final double TENTSILE_HYPOTENUSE_VISTA = 4.1;

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String platform = (String) adapterView.getItemAtPosition(position);
        if (platform.equals(getString(R.string.tentsile_tent_stingray))) {
            setEquilateral(Util.getTentsileEquilateral(TENTSILE_HYPOTENUSE_STINGRAY));
        } else if (platform.equals(getString(R.string.tenstile_tent_vista))) {
            setEquilateral(Util.getTentsileEquilateral(TENTSILE_HYPOTENUSE_VISTA));
        } else if (platform.equals(getString(R.string.tentsile_base_trillium))) {
            setEquilateral(Util.getTentsileEquilateral(TENTSILE_HYPOTENUSE_TRILLIUM));
        } else {
            setEquilateral(Util.getTentsileEquilateral(TENTSILE_HYPOTENUSE_TRILLIUM));
        }
    }

    private void setEquilateral(Platform platform) {
        mClearing.setPlatformSymmetricAngle();
        mPlatformRotation.setVisibility(View.GONE);
        mClearing.setPlatformDrawPath(platform);
    }
}