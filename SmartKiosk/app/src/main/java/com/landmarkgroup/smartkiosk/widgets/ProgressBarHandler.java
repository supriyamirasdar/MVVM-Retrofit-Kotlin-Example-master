package com.landmarkgroup.smartkiosk.widgets;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.lifestyle.retail.utils.CommonUtility;


public class ProgressBarHandler {
    private ProgressBar mProgressBar;
    private Activity activity;

    public ProgressBarHandler(Activity activity) {
        this.activity = activity;
        ViewGroup layout = (ViewGroup) activity.findViewById(android.R.id.content).getRootView();

        mProgressBar = new ProgressBar(activity, null, android.R.attr.progressBarStyleLarge);
        mProgressBar.getIndeterminateDrawable()
                .setColorFilter(Color.parseColor("#ffcf00"), PorterDuff.Mode.SRC_IN);//ProgressBar.setIndeterminateDrawable(context.getDrawable(R.drawable.default_spinner));
        //mProgressBar.setBackground(context.getDrawable(R.drawable.progress_background));
        mProgressBar.setPadding(CommonUtility.convertDpToPx(10, activity), CommonUtility.convertDpToPx(10, activity),
                CommonUtility.convertDpToPx(10, activity), CommonUtility.convertDpToPx(10, activity));
        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout rl = new RelativeLayout(activity);

        rl.setGravity(Gravity.CENTER);
        rl.addView(mProgressBar);

        layout.addView(rl, params);

        hide();
    }

    public void show() {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hide() {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}