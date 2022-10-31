package com.munifrog.design.tetheredtenttriangulator;

enum PreSets {
    ePhoneMetersLandscape,
    ePhoneMetersPortrait,
    ePhoneImperialLandscape,
    ePhoneImperialPortrait,
    eTabletMetersLandscape,
    eTabletMetersPortrait,
    eTabletImperialLandscape,
    eTabletImperialPortrait;

    public static boolean isImperial(PreSets ps) {
        switch (ps) {
            default:
            case ePhoneMetersLandscape:
            case ePhoneMetersPortrait:
            case eTabletMetersLandscape:
            case eTabletMetersPortrait:
                return false;
            case ePhoneImperialLandscape:
            case ePhoneImperialPortrait:
            case eTabletImperialLandscape:
            case eTabletImperialPortrait:
                return true;
        }
    }

    private static float [] getCenter(PreSets ps) {
        switch (ps) {
            case ePhoneMetersLandscape:
            case ePhoneImperialLandscape:
                return new float[] { 904, 441 };
            default:
            case ePhoneMetersPortrait:
            case ePhoneImperialPortrait:
                return new float[] { 540, 747 };
            case eTabletMetersLandscape:
            case eTabletImperialLandscape:
                return new float[] { 1200, 736 };
            case eTabletMetersPortrait:
            case eTabletImperialPortrait:
                return new float[] { 800, 1101 };
        }
    }

    public static float [][] getTethers(PreSets ps) {
        switch (ps) {
            default:
            case ePhoneMetersLandscape:
                return getOffset(ps, new float [][] { { 1680, 349 }, { 311, 821 }, { 109, 68 } } );
            case ePhoneMetersPortrait:
                return getOffset(ps, new float [][] { { 147, 1332 }, { 117, 193 }, { 909, 372 } } );
            case ePhoneImperialPortrait:
                return getOffset(ps, new float [][] { { 914, 960 }, { 155, 1352 }, { 56, 144 } } );
            case ePhoneImperialLandscape:
                return getOffset(ps, new float [][] { { 211, 81 }, { 1535, 246 }, { 990, 816 } } );
            case eTabletMetersLandscape:
                return getOffset(ps, new float [][] { { 2117, 1231 }, { 331, 1191 }, { 889, 153 } } );
            case eTabletImperialLandscape:
                return getOffset(ps, new float [][] { { 2078, 292 }, { 600, 1387 }, { 330, 105 } } );
            case eTabletMetersPortrait:
                return getOffset(ps, new float [][] { { 789, 194 }, { 1232, 1975 }, { 274, 1560 } } );
            case eTabletImperialPortrait:
                return getOffset(ps, new float [][] { { 658, 1965 }, { 163, 258 }, { 1280, 577 } } );
        }
    }

    public static float [][] getOffset(PreSets ps, float[][] xy) {
        float [] center = getCenter(ps);
        return new float[][] {
                { (xy[0][0] - center[0]), (xy[0][1] - center[1])},
                { (xy[1][0] - center[0]), (xy[1][1] - center[1])},
                { (xy[2][0] - center[0]), (xy[2][1] - center[1])}
        };
    }

    public static int getSlider(PreSets ps) {
        switch (ps) {
            case ePhoneImperialLandscape:
                return 10;
            case eTabletMetersPortrait:
                return 8;
            default:
            case ePhoneMetersLandscape:
            case ePhoneMetersPortrait:
            case ePhoneImperialPortrait:
            case eTabletMetersLandscape:
            case eTabletImperialLandscape:
            case eTabletImperialPortrait:
                return 25;
        }
    }

    public static int getPlatformStringId(PreSets ps) {
        switch (ps) {
            case ePhoneMetersLandscape:
                return R.string.tenstile_tent_vista;
            case ePhoneImperialLandscape:
            case eTabletMetersPortrait:
                return R.string.tentsile_tent_una;
            case ePhoneMetersPortrait:
                return R.string.tentsile_base_duo;
            case ePhoneImperialPortrait:
            case eTabletImperialLandscape:
                return R.string.tentsile_tent_stingray;
            case eTabletMetersLandscape:
                return R.string.tentsile_tent_connect;
            case eTabletImperialPortrait:
                return R.string.tentsile_base_t_mini;
            default:
                return R.string.default_selection_model;
        }
    }
}
