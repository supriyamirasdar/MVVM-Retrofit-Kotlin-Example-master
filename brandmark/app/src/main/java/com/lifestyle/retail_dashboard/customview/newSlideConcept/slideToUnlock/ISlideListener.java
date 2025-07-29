package com.lifestyle.retail_dashboard.customview.newSlideConcept.slideToUnlock;

public interface ISlideListener {

    /**
     * Called when user have done or interrupted the sliding
     * @param done - true if the sliding is done
     */
    void onSlideDone(SlideLayout slider, boolean done);

}
