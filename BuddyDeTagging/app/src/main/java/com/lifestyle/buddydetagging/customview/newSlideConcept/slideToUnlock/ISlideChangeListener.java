package com.lifestyle.buddydetagging.customview.newSlideConcept.slideToUnlock;

public interface ISlideChangeListener {

    void onSlideStart(SlideLayout slider);

    void onSlideChanged(SlideLayout slider, float percentage);

    void onSlideFinished(SlideLayout slider, boolean done);

}
