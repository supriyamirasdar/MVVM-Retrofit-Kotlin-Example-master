package com.lifestyle.buddydetagging.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.base.BaseApplication;

import androidx.appcompat.app.AppCompatActivity;

public class AppToast {

    public static void showToast(Context context, String message, int color) {
        LayoutInflater inflater = ((AppCompatActivity) context).getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_custom_toast, null);
        LinearLayout toastLL = layout.findViewById(R.id.custom_toast_layout);
       // toastLL.setBackgroundColor(color);
        ImageView toastIV = (ImageView) layout.findViewById(R.id.toastIV);
        // change toast icon
        //toastIV.setImageResource(R.drawable.ic_back);
        // hide/show toast icon..
        toastIV.setVisibility(View.GONE);
        TextView toastMessageTV = (TextView) layout.findViewById(R.id.toastMessageTV);
        toastMessageTV.setText("" + message);
        Toast toast = new Toast(BaseApplication.getAppContext());
        toast.setGravity(Gravity.TOP, 10, 200);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
