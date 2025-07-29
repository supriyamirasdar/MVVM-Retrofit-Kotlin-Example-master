package com.lifestyle.buddydetagging.view.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.base.BaseApplication;
import com.lifestyle.buddydetagging.model.StatusResponse;
import com.lifestyle.buddydetagging.network.ApiService;
import com.lifestyle.buddydetagging.utils.CommonUtility;
import com.lifestyle.buddydetagging.widget.ProgressBarHandler;

import org.apache.http.client.HttpResponseException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseFragment extends DialogFragment implements View.OnClickListener {

    public BaseCompatActivity activity;
    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "landmarkgroup.in");
    private ProgressBarHandler progressBar;
    protected ViewDataBinding viewDataBinding;

    public void showProgressDialog() {
        try {
            if (progressBar == null) {
                assert getActivity() != null;
                progressBar = new ProgressBarHandler(getActivity());
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
        if (getActivity() != null) {
            assert (getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)) != null;
            ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showKeyboard(Context context) {
        assert context != null;
        ((InputMethodManager) (context).getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    protected ApiService getApiInterface() {
        return BaseApplication.mInstance.apiClient.getApiInterface();
    }

    protected void replaceFragment(Fragment fragment) {
        FragmentActivity activity = getActivity();
        if (activity instanceof BaseCompatActivity) {
            ((BaseCompatActivity) activity).replaceFragment(fragment);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutResId = getLayoutResId();
        if (getActivity() instanceof BaseCompatActivity) {
            activity = ((BaseCompatActivity) getActivity());
        }
        if (layoutResId != -1) {
            return inflater.inflate(layoutResId, container, false);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getLayoutResId() != -1) {
            try {
                viewDataBinding = DataBindingUtil.bind(view);
                onBind(viewDataBinding);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void showAlert(@StringRes int stringResId) {
        showAlert(getString(stringResId));
    }

    protected void showError(Throwable throwable) {
        hideProgressDialog();
        String msg = "";
        try {
            if (throwable instanceof SocketTimeoutException) {
                msg = getResources().getString(R.string.timeoutexcep);
            } else if (throwable instanceof UnknownHostException) {
                msg = getResources().getString(R.string.socketException);
            } else if (throwable instanceof ConnectException) {
                msg = getResources().getString(R.string.httpexcep);
            } else if (throwable instanceof HttpResponseException) {
                msg = getResources().getString(R.string.httpresponseexpection);
            } else if (throwable instanceof SocketException) {
                msg = getResources().getString(R.string.socketException);
            } else if (throwable instanceof TimeoutException) {
                msg = getResources().getString(R.string.timeout);
            } else if (throwable instanceof Exception) {
                msg = getResources().getString(R.string.generalexcep_barcode);
            }

            if (throwable instanceof NullPointerException) {
                Log.d("Abrar", "Null pointer");
            } else
                CommonUtility.showSnackBar(getActivity(), msg);

        } catch (Exception e) {
            Log.d("Abrar", e.getLocalizedMessage());
        }
    }

    protected void showAlert(String messageContent) {
        CommonUtility.showSnackBar(activity, messageContent);
    }

    public void setActivity(BaseCompatActivity activity) {
        this.activity = activity;
    }

    @SuppressLint("CheckResult")
    protected <T> void network(Observable<T> observable, int requestCode) {
        if (CommonUtility.isNotNull(activity)) {
            activity.showProgressDialog();
        }
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        T -> {
                            if (CommonUtility.isNotNull(activity)) {
                                activity.hideProgressDialog();
                            }
                            if (T instanceof StatusResponse) {
                                StatusResponse apiResponse = ((StatusResponse) T);
                                if (apiResponse.getStatusCode().equalsIgnoreCase("200")
                                        || apiResponse.getStatusCode().equalsIgnoreCase("400")) {
                                    onSuccess(apiResponse, requestCode);
                                } else {
                                    showAlert(apiResponse.getStatusErrMessage());
                                }
                            }
                        }, error -> {
                            //activity.hideProgressDialog();

                            if (activity != null)
                                activity.hideProgressDialog();
                            Log.d("Abrar", "Response: " + error.toString());
                            showError(error);
                        }
                );
    }

    @SuppressLint("CheckResult")
    protected <T> void network(Observable<T> observable, int requestCode, BaseCompatActivity activity) {
        activity.showProgressDialog();
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        T -> {
                            activity.hideProgressDialog();

                            if (T instanceof StatusResponse) {
                                StatusResponse apiResponse = ((StatusResponse) T);
                                if (apiResponse.getStatusCode().equalsIgnoreCase("200")
                                        || apiResponse.getStatusCode().equalsIgnoreCase("400")) {
                                    onSuccess(apiResponse, requestCode);
                                } else {
                                    showAlert(apiResponse.getStatusErrMessage());
                                }
                            }
                        }, error -> {
                            activity.hideProgressDialog();
                            Log.d("Abrar", "Response: " + error.toString());
                            showError(error);
                        }
                );
    }

    public abstract void onSuccess(StatusResponse baseResponse, int requestCode);

    public int getTitle() {
        return -1;
    }

    public void setTitle(String title) {
        assert getActivity() != null;
        if (isVisible()) getActivity().setTitle(title);
    }

    public int getLayoutResId() {
        return -1;
    }

    public void onBind(ViewDataBinding viewDataBinding) {
    }

    public boolean checkEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void goToNextScreen(Activity activity, Class<?> destination) {
        Intent intent = new Intent(activity, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}