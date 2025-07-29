package com.lifestyle.buddydetagging.view.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.load.HttpException;
import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.model.StatusResponse;
import com.lifestyle.buddydetagging.network.ApiService;
import com.lifestyle.buddydetagging.network.NetworkModule;
import com.lifestyle.buddydetagging.network.NetworkModuleNew;
import com.lifestyle.buddydetagging.utils.CommonUtility;
import com.lifestyle.buddydetagging.view.login.dto.ResponseDTO;
import com.lifestyle.buddydetagging.widget.ProgressBarHandler;



import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseCompatActivity extends AppCompatActivity implements View.OnClickListener {
    protected FragmentManager supportFragmentManager;
    private TextView toolbarTitle;
    private int fragmentContainerId = R.id.fragment_container;
    private ProgressBarHandler progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.addOnBackStackChangedListener(() -> {
            if (supportFragmentManager.getBackStackEntryCount() == 0) {
                finish();
                return;
            }
            Fragment fragmentById = supportFragmentManager.findFragmentById(fragmentContainerId);
            if (fragmentById instanceof BaseFragment) {
                setTitle(((BaseFragment) fragmentById).getTitle());
            }
        });
    }

    public void onBackPressed(View view) {
        onBackPressed();
    }

    public void replaceFragment(Fragment fragment) {
        if (fragmentContainerId == -1) return;
        replace(fragment);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean ret = false;
        try {
            View v = getCurrentFocus();
            ret = super.dispatchTouchEvent(event);
            if (v instanceof EditText) {
                View w = getCurrentFocus();
                int scrcoords[] = new int[2];
                assert w != null;
                w.getLocationOnScreen(scrcoords);
                float x = event.getRawX() + w.getLeft() - scrcoords[0];
                float y = event.getRawY() + w.getTop() - scrcoords[1];

                //  Log.d(Activity”, “Touch event ”+ event.getRawX() + “,”+ event.getRawY() + “ ”+ x + “,”+ y + “ rect ”+ w.getLeft() + “,”+ w.getTop() + “,”+ w.getRight() + “,”+ w.getBottom() + “ coords ”+ scrcoords[0] + “,”+ scrcoords[1]);
                if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            Log.d("Abrar","Exception: "+e);
        }
        return ret;
    }

    private void replace(Fragment fragment) {
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(fragmentContainerId, fragment, fragment.getClass().getSimpleName());
        Fragment fragmentById = supportFragmentManager.findFragmentById(fragmentContainerId);
        if (fragmentById != null) fragmentTransaction.hide(fragmentById);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        /*View toolbarView = findViewById(R.id.toolbar);
        if (toolbarView instanceof Toolbar) {

            setSupportActionBar((Toolbar) toolbarView);
            ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setTitle("");
            }
        }*/
        toolbarTitle = findViewById(R.id.tvToolBarTitle);
    }


    @Override
    public void setTitle(CharSequence title) {
        if (toolbarTitle != null) toolbarTitle.setText(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showAlert(@StringRes int stringResId) {
        showAlert(getString(stringResId));
    }

    protected void showError(Throwable throwable) {
        String msg = "";
        try {
            if (throwable instanceof SocketTimeoutException) {
                if (throwable.getMessage().startsWith("failed to connect"))
                    msg = getResources().getString(R.string.socketException);
                else
                    msg = getResources().getString(R.string.timeoutexcep);
            } else if (throwable instanceof UnknownHostException) {
                msg = getResources().getString(R.string.socketException);
            } else if (throwable instanceof ConnectException) {
                msg = getResources().getString(R.string.httpexcep);
            } else if (throwable instanceof HttpException) {
                msg = getResources().getString(R.string.httpresponseexpection);
            } else if (throwable instanceof SocketException) {
                msg = getResources().getString(R.string.socketException);
            } else if (throwable instanceof TimeoutException) {
                msg = getResources().getString(R.string.timeout);
            } else if (throwable instanceof Exception) {
                msg = getResources().getString(R.string.generalexcep_barcode);
            }

            showAlert(msg);

        } catch (Exception e) {
            Log.d("Abrar", e.getLocalizedMessage());
        }
    }


    public void showAlert(String messageContent) {
        hideProgressDialog();
        CommonUtility.showSnackBar(this, messageContent);
    }

    @SuppressLint("CheckResult")
    public <T> void network(Observable<T> observable, int requestCode) {
        showProgressDialog();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        T -> {
                            hideProgressDialog();
                            if (T instanceof ResponseDTO) {
                                ResponseDTO apiResponse = ((ResponseDTO) T);

                                if (apiResponse.getStatusCode()== 200 ) {
                                    onSuccess(apiResponse, requestCode);
                                } else {
                                    showAlert(apiResponse.getStatusMessage());
                                }
                            }
                        }, error -> {
                            hideProgressDialog();
                            Log.d("Abrar", "Response:" + error.toString());
                            showError(error);
                        }
                );
    }

    public abstract void onSuccess(ResponseDTO statusResponse, int requestCode);

    public ApiService getApiInterface() {
        return NetworkModule.getInstance().getApiInterface();
    }

    public ApiService getApiInterfaceNew() {
        return NetworkModuleNew.getInstance().getApiInterface();
    }

    protected void startActivity(Class aClass) {
        Intent intent = new Intent(this, aClass);
        startActivity(intent);
    }

    public void showProgressDialog() {
        try {
            if (progressBar == null) {
                progressBar = new ProgressBarHandler(this);
            } else {
                progressBar.hide();
            }
            progressBar.show();
        } catch (Exception e) {
            Log.d("Abrar", e.getLocalizedMessage());
        }
    }

    public void hideProgressDialog() {
        if (CommonUtility.isNotNull(progressBar)) {
            progressBar.hide();
        }
    }


    public void hideSoftKeyboard(View view) {
        assert (getSystemService(Context.INPUT_METHOD_SERVICE)) != null;
        ((InputMethodManager) Objects.requireNonNull(getSystemService(Context.INPUT_METHOD_SERVICE))).hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = getCurrentFocus();
        if (v != null)
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    public void showKeyboard(Context context) {
        ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void goToNextScreen(Activity activity, Class<?> destination) {
        Intent intent = new Intent(activity, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static void goToNextScreen(Context context, Class<?> destination, String parameter) {
        Intent intent = new Intent(context, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("itemSelected", parameter);
        context.startActivity(intent);
    }


    public void addDatalist() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    public static String nullCheck(String result) {
        return (result != null && !result.equals("null") && result.trim().length() > 0) ? result : "";
    }
}