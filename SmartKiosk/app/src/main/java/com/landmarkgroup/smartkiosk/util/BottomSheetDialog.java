package com.landmarkgroup.smartkiosk.util;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.landmarkgroup.smartkiosk.R;
import com.landmarkgroup.smartkiosk.ui.pdppage.MissingSizeActivity;

import java.util.concurrent.TimeUnit;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_custom_toast,
                container, false);
        TextView toastMessageTV = (TextView) v.findViewById(R.id.toastMessageTV);
        //toastMessageTV.setText("" + message);

        textViewTime = (TextView) v.findViewById(R.id.toastMessageTimeTV);
        a = getActivity();
        startClock();
        return v;
    }


    //Starting Clock
    private static void startClock() {
        startCountDownTimer();
    }

    private static TextView textViewTime;
    public static int showIdelBefore = (20 * 1000);
    private static CountDownTimer countDownTimer;
    private static boolean blink = true;
    private static Activity a;

    //Start CountdownTimer
    private static void startCountDownTimer() {
        try {
            countDownTimer = new CountDownTimer(showIdelBefore, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    textViewTime.setText(hmsTimeFormatter(millisUntilFinished) +" sec");

                    textViewTime.setTextColor(Color.BLACK);

                    if (Integer.parseInt(hmsTimeFormatter(millisUntilFinished)) <= 5) {
                        textViewTime.setTextColor(Color.RED);
                        if (blink)
                            textViewTime.setVisibility(View.INVISIBLE);
                        else
                            textViewTime.setVisibility(View.VISIBLE);
                        blink = !blink;
                    } else {
                        //textViewTime.setTextColor(Color.WHITE);

                        textViewTime.setTextColor(Color.BLACK);

                    }

                }

                @Override
                public void onFinish() {
                    textViewTime.setText("00 sec");

                    textViewTime.setTextColor(Color.RED);

                    if (a instanceof MissingSizeActivity)
                        ((MissingSizeActivity) a).onBackPressed();


                }
            }.start();
            countDownTimer.start();
        } catch (Exception e) {
            Log.d("Abrar", e.getLocalizedMessage());
        }
    }

    //Stop CountdownTimer
    public static void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    private static String hmsTimeFormatter(long milliSeconds) {
        return String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
    }
}
