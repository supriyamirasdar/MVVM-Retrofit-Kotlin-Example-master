package com.lifestyle.buddydetagging.view.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.constant.GeneralConstant;
import com.lifestyle.buddydetagging.databinding.ActivityDtForgotPasswordBinding;
import com.lifestyle.buddydetagging.model.StatusResponse;
import com.lifestyle.buddydetagging.utils.CommonUtility;
import com.lifestyle.buddydetagging.utils.PreferenceUtils;
import com.lifestyle.buddydetagging.utils.Utility;
import com.lifestyle.buddydetagging.view.base.BaseCompatActivity;
import com.lifestyle.buddydetagging.view.login.dto.ResponseDTO;
import com.lifestyle.buddydetagging.view.login.model.ForgotPasswordRequest;
import com.lifestyle.buddydetagging.view.login.model.ForgotPasswordResponse;

import androidx.databinding.DataBindingUtil;

public class DtForgotPasswordActivity extends BaseCompatActivity {

    ActivityDtForgotPasswordBinding binding;

    String errorMsg;
    String status;
    protected boolean changePwdSuccessful = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_dt_forgot_password);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dt_forgot_password);

        //binding.btnSubmit.setComponentName("Submit");
        binding.toolbarTop.tvToolBarTitle.setText("Forgot Password");
        binding.toolbarTop.tvToolBarSubtitle.setVisibility(View.GONE);
        binding.toolbarTop.tvToolBarTitle.setGravity(Gravity.CENTER_VERTICAL);
        binding.toolbarTop.btnBack.setOnClickListener(view -> {
            finish();
        });


        binding.btnSubmit.setBackgroundResource(R.drawable.button_disable_bg);
        binding.loginUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchingOrder = s.toString();

                /*if (searchingOrder.length() == 5) {
                    //seachOrderFromList(searchingOrder.trim());
                    // enable proceed button...
                    binding.btnSubmit.setBackgroundResource(R.drawable.toolbar_bg);
                } else {
                    binding.btnSubmit.setBackgroundResource(R.drawable.button_disable_bg);
                }*/

                if (validateValue()) {
                    binding.btnSubmit.setBackgroundResource(R.drawable.toolbar_bg);
                } else {
                    binding.btnSubmit.setBackgroundResource(R.drawable.button_disable_bg);
                }
            }
        });

        /*binding.emailId.addTextChangedListener(new TextWatcher() {
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
        });*/


        binding.btnSubmit.setOnClickListener(view -> {
            if (validateValue()) {
                callForgotPassword();
            }
        });

        /*binding.emailId .setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                // If user press done key
                if (i == EditorInfo.IME_ACTION_DONE) {
                    // Get the input method manager
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // Hide the soft keyboard
                    inputMethodManager.hideSoftInputFromWindow(binding.emailId.getWindowToken(), 0);
                    String itemCode = binding.emailId.getText().toString().trim();

                    if (validateValue()) {
                        callForgotPassword();
                    }

                    return true;
                }
                return false;
            }
        });*/


    }

    public boolean validateValue() {
        if (binding.loginUserId.getText().length() != 7) {
            //showAlert("Please enter valid employee Id.");
            return false;
        } /*else if (binding.emailId.getText().length() == 0) {
            //showAlert("Please enter email.");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailId.getText().toString()).matches()) {
            //showAlert("Please enter valid email id.");
            return false;
        }*/ else {
            return true;
        }
    }


    @Override
    public void onSuccess(ResponseDTO statusResponse, int requestCode) {
        if (requestCode == GeneralConstant.FORGOT_PASSWORD) {

            if (statusResponse.getStatusCode() == 200) {
                //CommonUtility.showSnackBar(DtForgotPasswordActivity.this, statusResponse.getStatusMessage());
                PreferenceUtils.setUserPass(null);
                String titleST = "De-Tagging";
                showAlertDialog(DtForgotPasswordActivity.this, titleST, statusResponse.getStatusMessage(), 1);
                // dt 07-10-2021
                /*new android.os.Handler().postDelayed(() -> {
                    alertDialog.dismiss();
                    Utility.goToNextScreen(DtForgotPasswordActivity.this, LoginActivity.class);
                    finish();
                }, 3000);*/

            } else {
                String titleST = "De-Tagging";
                showAlertDialog(DtForgotPasswordActivity.this, titleST, statusResponse.getStatusMessage(), 0);

                // CommonUtility.showSnackBar(DtForgotPasswordActivity.this, statusResponse.getStatusMessage());

            }

            /*if (statusResponse instanceof ForgotPasswordResponse) {
                ForgotPasswordResponse forgotPasswordResponse = (ForgotPasswordResponse) statusResponse;
                Log.e("forgotPasdResponse==", new Gson().toJson(forgotPasswordResponse));
                if (forgotPasswordResponse.getServerErrormsg() != null) {
                    errorMsg = forgotPasswordResponse.getServerErrormsg();

                }
                if (forgotPasswordResponse.getSuccessIndi() != null) {
                    status = forgotPasswordResponse.getSuccessIndi();
                }
                display();

            }*/
        }
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    private void callForgotPassword() {
        if (binding.loginUserId.getText().length() != 7) {
            //showAlert("Please enter valid employee Id.");
            String titleST = "De-Tagging";
            String message= "Please enter valid employee Id.";
            showAlertDialog(DtForgotPasswordActivity.this, titleST, message, 0);
            return;
        }
        /*if (binding.emailId.getText().length() == 0) {
            //showAlert("Please enter email.");
            String titleST = "De-Tagging";
            String message= "Please enter email.";
            showAlertDialog(DtForgotPasswordActivity.this, titleST, message, 0);
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.emailId.getText().toString()).matches()) {
            //showAlert("Please enter valid email id.");
            String titleST = "De-Tagging";
            String message= "Please enter valid email id.";
            showAlertDialog(DtForgotPasswordActivity.this, titleST, message, 0);
            return;
        }*/
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();

        String emailId= PreferenceUtils.getEmailId();
        if(TextUtils.isEmpty(emailId)){
            emailId="";
        }
        forgotPasswordRequest.setEmail(emailId);
        //forgotPasswordRequest.setEmail(binding.emailId.getText().toString());
        forgotPasswordRequest.setUserId(binding.loginUserId.getText().toString());
        forgotPasswordRequest.setRole(PreferenceUtils.getRole());
        Log.e("forgotPasswordRequest==", new Gson().toJson(forgotPasswordRequest));
        network(getApiInterface().forgotPassword(forgotPasswordRequest), GeneralConstant.FORGOT_PASSWORD);

    }

    private void display() {

        /*try {
            if (errorMsg != null) {
                binding.forgotDescription.setText(errorMsg);
                binding.forgotDescription.setTextColor(Color.RED);
            } else {
                if (status.equals("true")) {
                    binding.forgotDescription.setText("Password sent to mail");
                    binding.forgotDescription.setTextColor(Color.DKGRAY);
                    changePwdSuccessful = true;


                } else if (status.equals("false")) {
                    binding.forgotDescription.setText("User ID or Email address is wrong");
                    binding.forgotDescription.setTextColor(Color.RED);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    @Override
    public void onBackPressed() {
        if (changePwdSuccessful) {
            changePwdSuccessful = false;
            finish();
            startActivity(new Intent(DtForgotPasswordActivity.this, LoginActivityNew.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            super.onBackPressed();
        }
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
                    Utility.goToNextScreen(DtForgotPasswordActivity.this, DtChangePasswordActivity.class);//change supriya loginActivity to DtForgotP
                    finish();
                }
                // finish();
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
}