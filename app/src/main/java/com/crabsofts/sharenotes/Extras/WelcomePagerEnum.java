package com.crabsofts.sharenotes.Extras;

import com.crabsofts.sharenotes.R;

public enum WelcomePagerEnum {
    SCREEN1(R.string.title1, R.layout.welcome_slider1),
    SCREEN2(R.string.title2, R.layout.welcome_slider2),
    SCREEN3(R.string.title3, R.layout.welcome_slider3);

    private int titleResourceId;
    private int layoutResourceId;

    WelcomePagerEnum(int titleResourceId, int layoutResourceId) {
        this.titleResourceId = titleResourceId;
        this.layoutResourceId = layoutResourceId;
    }

    public int getTitleResourceId() {
        return titleResourceId;
    }

    public int getLayoutResourceId() {
        return layoutResourceId;
    }
}
