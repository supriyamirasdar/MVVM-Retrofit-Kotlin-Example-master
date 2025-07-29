package com.lifestyle.buddydetagging.view.login;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.constant.GeneralConstant;
import com.lifestyle.buddydetagging.databinding.ActivityDtChangePasswordBinding;
import com.lifestyle.buddydetagging.databinding.ActivityDtForgotPasswordBinding;
import com.lifestyle.buddydetagging.model.StatusResponse;
import com.lifestyle.buddydetagging.utils.CommonUtility;
import com.lifestyle.buddydetagging.utils.CryptoUtil;
import com.lifestyle.buddydetagging.utils.PreferenceUtils;
import com.lifestyle.buddydetagging.utils.Utility;
import com.lifestyle.buddydetagging.view.base.BaseCompatActivity;
import com.lifestyle.buddydetagging.view.login.dto.ResponseDTO;
import com.lifestyle.buddydetagging.view.login.dto.UserLoginDTO;
import com.lifestyle.buddydetagging.view.login.model.ChangePasswordRequest;
import com.lifestyle.buddydetagging.view.login.model.ChangePasswordResponse;
import com.lifestyle.buddydetagging.view.login.model.ForgotPasswordRequest;
import com.lifestyle.buddydetagging.view.login.model.ForgotPasswordResponse;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class DtChangePasswordActivity extends BaseCompatActivity {

    ActivityDtChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dt_forgot_password);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dt_change_password);
        binding.toolbarTop.tvToolBarTitle.setText("Change Password");
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.GONE);
        //binding.btnSubmit.setComponentName("Submit");


        binding.etOldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchingOrder = s.toString();
                if (validateValue()) {
                    binding.btnSubmit.setBackgroundResource(R.drawable.toolbar_bg);
                } else {
                    binding.btnSubmit.setBackgroundResource(R.drawable.button_disable_bg);
                }
            }
        });
        binding.etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchingOrder = s.toString();
                if (validateValue()) {
                    binding.btnSubmit.setBackgroundResource(R.drawable.toolbar_bg);
                } else {
                    binding.btnSubmit.setBackgroundResource(R.drawable.button_disable_bg);
                }
            }
        });
        binding.etConfirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchingOrder = s.toString();
                if (validateValue()) {
                    binding.btnSubmit.setBackgroundResource(R.drawable.toolbar_bg);
                } else {
                    binding.btnSubmit.setBackgroundResource(R.drawable.button_disable_bg);
                }
            }
        });

        //binding.btnSubmit.setBackgroundResource(R.drawable.toolbar_bg);
        binding.btnSubmit.setBackgroundResource(R.drawable.button_disable_bg);
        binding.btnSubmit.setOnClickListener(view -> {
            if (validateValue()) {
                callChangePassword();
            }
        });

        binding.etConfirmPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // If user press done key
                if (i == EditorInfo.IME_ACTION_DONE) {
                    // Get the input method manager
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // Hide the soft keyboard
                    inputMethodManager.hideSoftInputFromWindow(binding.etConfirmPass.getWindowToken(), 0);
                    String itemCode = binding.etConfirmPass.getText().toString().trim();

                    if (validateValue()) {
                        callChangePassword();
                    }

                    return true;
                }
                return false;
            }
        });

        binding.showPassBtn.setOnClickListener(view -> {
            ShowHidePass(1);
        });
        binding.showNewpassBtn.setOnClickListener(view -> {
            ShowHidePass(2);
        });
        binding.showConpassBtn.setOnClickListener(view -> {
            ShowHidePass(3);
        });
    }

    public boolean validateValue() {
        String oldPass = binding.etOldPassword.getText().toString();
        String newPass = binding.etNewPassword.getText().toString();
        String confirmPass = binding.etConfirmPass.getText().toString();

        if (TextUtils.isEmpty(oldPass)) {
            //showAlert("Enter Old Password.");
            return false;
        } else if (TextUtils.isEmpty(newPass)) {
            //showAlert("Enter New Password.");
            return false;
        } else if (newPass.length() < 6) {
            //showAlert("Enter New Password.");
            return false;
        } else if (TextUtils.isEmpty(confirmPass)) {
            //showAlert("Enter Confirm Password");
            return false;
        } else if (!newPass.equals(confirmPass)) {
            //showAlert("New & Confirm Password does not match.");
            return false;
        } else {
            return true;
        }

    }

    private void callChangePassword() {
        String oldPass = binding.etOldPassword.getText().toString();
        String newPass = binding.etNewPassword.getText().toString();
        String confirmPass = binding.etConfirmPass.getText().toString();

        if (TextUtils.isEmpty(oldPass)) {
            //showAlert("Enter Old Password.");
            String titleST = "De-Tagging";
            String message = "Enter One Time Password.";
            showAlertDialog(DtChangePasswordActivity.this, titleST, message, 0);
            return;
        }

        if (TextUtils.isEmpty(newPass)) {
            //showAlert("Enter New Password.");
            String titleST = "De-Tagging";
            String message = "Enter New Password.";
            showAlertDialog(DtChangePasswordActivity.this, titleST, message, 0);
            return;
        }

        if (newPass.length() < 6) {
            //showAlert("Enter New Password.");
            String titleST = "De-Tagging";
            String message = "Your New Password must be at least 6 character.";
            showAlertDialog(DtChangePasswordActivity.this, titleST, message, 0);
            return;
        }

        if (TextUtils.isEmpty(confirmPass)) {
            //showAlert("Enter Confirm Password");
            String titleST = "De-Tagging";
            String message = "Enter Confirm Password.";
            showAlertDialog(DtChangePasswordActivity.this, titleST, message, 0);
            return;
        }
        if (!newPass.equals(confirmPass)) {
            //showAlert("New & Confirm Password does not match.");
            String titleST = "De-Tagging";
            String message = "New & Confirm Password does not match.";
            showAlertDialog(DtChangePasswordActivity.this, titleST, message, 0);
            return;
        }
        UserLoginDTO userLoginDTO = new UserLoginDTO();

        try {
            userLoginDTO.setUserId(PreferenceUtils.getUserId());
           /* userLoginDTO.setPwd(CryptoUtil.encrypt(oldPass));
            userLoginDTO.setNewPwd(CryptoUtil.encrypt(newPass));
            userLoginDTO.setConfirmPwd(CryptoUtil.encrypt(newPass));*/
            userLoginDTO.setPwd(oldPass);
            userLoginDTO.setNewPwd(newPass);
            userLoginDTO.setConfirmPwd(newPass);
            userLoginDTO.setRole(PreferenceUtils.getRole());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("ChangePasswordRequest==", new Gson().toJson(userLoginDTO));
        network(getApiInterface().changePassword(userLoginDTO), GeneralConstant.CHANGE_PASSWORD);
    }

    @Override
    public void onSuccess(ResponseDTO statusResponse, int requestCode) {
        if (requestCode == GeneralConstant.CHANGE_PASSWORD) {

            if (statusResponse.getStatusCode() == 200) {
                //CommonUtility.showSnackBar(DtChangePasswordActivity.this, statusResponse.getStatusMessage());
                PreferenceUtils.setUserPass(null);
                String titleST = "De-Tagging";
                showAlertDialog(DtChangePasswordActivity.this, titleST, statusResponse.getStatusMessage(), 1);
                new android.os.Handler().postDelayed(() -> {
                    alertDialog.dismiss();
                    Utility.goToNextScreen(DtChangePasswordActivity.this, LoginActivityNew.class);//20-04-2023 supriya chnage call intent loginActivity to LoginActivitynew
                    finish();
                }, 3000);

            } else {
                //CommonUtility.showSnackBar(DtChangePasswordActivity.this, statusResponse.getStatusMessage());
                String titleST = "De-Tagging";
                showAlertDialog(DtChangePasswordActivity.this, titleST, statusResponse.getStatusMessage(), 0);
            }
        }
    }


    @Override
    public void onClick(View view) {

    }

    AlertDialog alertDialog;

    public void showAlertDialog(final Context context, String title, String message, int isMoveToLogin) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View deleteDialogView = factory.inflate(R.layout.layout_dt_alert_dialog, null);
        alertDialog = new AlertDialog.Builder(context).setInverseBackgroundForced(true).create();
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setView(deleteDialogView);
        TextView titleTV = deleteDialogView.findViewById(R.id.titleTV);
        TextView alertMessageTV = deleteDialogView.findViewById(R.id.alertMessageTV);
        TextView yesBt = deleteDialogView.findViewById(R.id.yesBt);
        TextView noBt = deleteDialogView.findViewById(R.id.noBt);

       /* if (isDisplayNo == 1) {
            noBt.setVisibility(View.VISIBLE);
        } else if (isDisplayNo == 0) {
            noBt.setVisibility(View.GONE);
        }*/
        noBt.setVisibility(View.GONE);

        titleTV.setText("" + title);
        alertMessageTV.setText("" + message);
        yesBt.setText("Okay");
        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMoveToLogin == 0) {
                    alertDialog.dismiss();
                } else if (isMoveToLogin == 1) {
                    alertDialog.dismiss();
                    Utility.goToNextScreen(DtChangePasswordActivity.this, LoginActivity.class);
                    finish();
                }
            }
        });

        noBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }



    public void ShowHidePass(int view) {

        if (view ==1) {

            if (binding.etOldPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                binding.showPassBtn.setImageResource(R.drawable.ic_eye_open);
                //Show Password
                binding.etOldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                binding.showPassBtn.setImageResource(R.drawable.ic_eye_close);
                //Hide Password
                binding.etOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        } else if (view== 2) {

            if (binding.etNewPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                binding.showNewpassBtn.setImageResource(R.drawable.ic_eye_open);
                //Show Password
                binding.etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                binding.showNewpassBtn.setImageResource(R.drawable.ic_eye_close);
                //Hide Password
                binding.etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        } else if (view == 3) {

            if (binding.etConfirmPass.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                binding.showConpassBtn.setImageResource(R.drawable.ic_eye_open);
                //Show Password
                binding.etConfirmPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                binding.showConpassBtn.setImageResource(R.drawable.ic_eye_close);
                //Hide Password
                binding.etConfirmPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
}