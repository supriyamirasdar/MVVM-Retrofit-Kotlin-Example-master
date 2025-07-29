package com.lifestyle.buddydetagging.customview.newSlideConcept.ios;

import android.view.View;

import com.lifestyle.buddydetagging.customview.newSlideConcept.slideToUnlock.ISlidingData;
import com.lifestyle.buddydetagging.customview.newSlideConcept.slideToUnlock.SlideLayout;
import com.lifestyle.buddydetagging.customview.newSlideConcept.slideToUnlock.renderers.TranslateRenderer;


public class BtnRenderer extends TranslateRenderer {

    private SlideLayout mSlideLayout;

    public BtnRenderer(SlideLayout slideLayout) {
        mSlideLayout = slideLayout;
    }

    @Override
    public int onSlideReset(ISlidingData slidingData, View child) {
        mSlideLayout.setAlpha(1);
        return super.onSlideReset(slidingData, child);
    }

    @Override
    public int onSlideDone(ISlidingData slidingData, View child) {
        int duration = super.onSlideDone(slidingData, child);
        mSlideLayout.animate()
                .alpha(0)
                .setDuration(duration)
                .start();
        return duration;
    }
}
