package com.pfa.pfaapp.customviews.custominputlayout;

import android.graphics.PorterDuff;

class CustomViewUtils {
    static PorterDuff.Mode parseTintMode(int value) {//, PorterDuff.Mode defaultMode) {
        switch (value) {
            case 3:
                return PorterDuff.Mode.SRC_OVER;
            case 5:
                return PorterDuff.Mode.SRC_IN;
            case 9:
                return PorterDuff.Mode.SRC_ATOP;
            case 14:
                return PorterDuff.Mode.MULTIPLY;
            case 15:
                return PorterDuff.Mode.SCREEN;
            default:
                return null;
//                return defaultMode;
        }
    }

}
