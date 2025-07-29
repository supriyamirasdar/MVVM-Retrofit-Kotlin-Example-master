package com.landmarkgroup.smartkiosk.ui.welcome_screen;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.landmarkgroup.smartkiosk.ui.homescreen.HomeScreenActivity;
import com.landmarkgroup.smartkiosk.util.Logger;
import com.landmarkgroup.smartkiosk.util.PrefManager;
import com.landmarkgroup.smartkiosk.R;

import static com.landmarkgroup.smartkiosk.util.Utils.addBottomDots;
import static com.landmarkgroup.smartkiosk.util.Utils.changeDrawableColor;
import static com.landmarkgroup.smartkiosk.util.Utils.changeStatusBarColor;
import static com.landmarkgroup.smartkiosk.util.Utils.displaySnackBar;

public class ChooseOUCodeActivity extends AppCompatActivity {


    private ImageView btnLifeStyle, btnMax, btnSplash, btnEazyBuy, btnHomecenter;
    private boolean ouSelected = false;
    private ChooseOUCodeActivity activity;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        activity = this;
        intView();

        choosingOuCode();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNext();
            }
        });
    }

    private void goNext() {
        if (ouSelected) {
            finish();
            Intent intent = new Intent(activity, StoreCodeActivity.class);
//            Intent intent = new Intent(activity, HomeScreenActivity.class);
            startActivity(intent);
        } else
            displaySnackBar(activity, "Choose OU Code");
    }

    private void choosingOuCode() {
        final ImageView[] viewList = {btnLifeStyle, btnMax, btnSplash, btnEazyBuy, btnHomecenter};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            btnLifeStyle.setBackground(changeDrawableColor(activity, R.drawable.round_box, getResources().getColor(R.color.whiteText)));
            btnMax.setBackground(changeDrawableColor(activity, R.drawable.round_box, getResources().getColor(R.color.whiteText)));
            btnSplash.setBackground(changeDrawableColor(activity, R.drawable.round_box, getResources().getColor(R.color.whiteText)));
            btnEazyBuy.setBackground(changeDrawableColor(activity, R.drawable.round_box, getResources().getColor(R.color.whiteText)));
            btnHomecenter.setBackground(changeDrawableColor(activity, R.drawable.round_box, getResources().getColor(R.color.whiteText)));
        }

        btnLifeStyle.setOnClickListener(v -> applyBackgroundDrawable(v, viewList, 3, "LifeStyle", getResources().getString(R.string.ou_lifestyle_url)));

        btnMax.setOnClickListener(v -> applyBackgroundDrawable(v, viewList, 5, "Max", getResources().getString(R.string.ou_max_url)));

        btnSplash.setOnClickListener(v -> applyBackgroundDrawable(v, viewList, 9, "Splash", getResources().getString(R.string.ou_splash_url)));

        btnEazyBuy.setOnClickListener(v -> applyBackgroundDrawable(v, viewList, 5, "EazyBuy",""));

        btnHomecenter.setOnClickListener(v -> applyBackgroundDrawable(v, viewList, 3, "HomeCenter", getResources().getString(R.string.ou_homecenter_url)));
    }

    FrameLayout frameLayout;
    LinearLayout dotsLayout;
    Button btnNext;
    Button btnTyrAgain;

    private void intView() {
        frameLayout = findViewById(R.id.frameLayout);
        dotsLayout = findViewById(R.id.layoutDots);
        btnNext = findViewById(R.id.btn_next);
        btnTyrAgain = findViewById(R.id.btnTyrAgain);


        prefManager = new PrefManager(activity);
        if (!prefManager.isFirstTimeLaunch()
                && !prefManager.getStoreId().equalsIgnoreCase("")
                && !prefManager.getTabSelected().equalsIgnoreCase("")) {
            launchHomeScreen();
            finish();
        }
        ButterKnife.bind(activity, activity);
        changeStatusBarColor(activity);
        btnTyrAgain.setVisibility(View.GONE);

        View view = LayoutInflater.from(activity).inflate(R.layout.welcome_slide1, frameLayout, false);
        btnLifeStyle = view.findViewById(R.id.btnLifeStyle);
        btnMax = view.findViewById(R.id.btnMax);
        btnSplash = view.findViewById(R.id.btnSplash);
        btnEazyBuy = view.findViewById(R.id.btnEazyBuy);
        btnHomecenter = view.findViewById(R.id.btnHomecenter);
        frameLayout.addView(view);

        addBottomDots(activity, 0, dotsLayout);
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(activity, HomeScreenActivity.class));
        finish();
    }

    private void applyBackgroundDrawable(View v, ImageView[] viewList, int ouCode, String ouName, String ouUrl) {
        ouSelected = true;
        for (ImageView view : viewList) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (v.equals(view)) {
                    v.setBackground(getResources().getDrawable(R.drawable.ic_background));
                } else {
                    view.setBackground(changeDrawableColor(activity, R.drawable.round_box,
                            getResources().getColor(R.color.whiteText)));
                }
            }
        }

        prefManager.setOuCode(ouCode);
        prefManager.setOuName(ouName);
        prefManager.setOuUrl(ouUrl);
        prefManager.setFirstTimeLaunch(false);
        Logger.writeToFile("Display Log: on click of ouCode: " + ouCode + ", ouName: " + ouName);
        new Handler().postDelayed(this::goNext, 200);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}