package com.landmarkgroup.smartkiosk.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.landmarkgroup.smartkiosk.ui.homescreen.HomeScreenActivity;
import com.landmarkgroup.smartkiosk.ui.pdppage.BrowserCatelogActivity;
import com.landmarkgroup.smartkiosk.ui.pdppage.MissingSizeActivity;
import com.landmarkgroup.smartkiosk.R;
import com.landmarkgroup.smartkiosk.ui.pdppage.SizeOptionsActivity;

import java.util.concurrent.TimeUnit;


@SuppressLint({"SetTextI18n", "DefaultLocale", "StaticFieldLeak"})
public class CustomDialogs {
    public static int showIdelBefore = (60 * 1000);
    public static int idealTimeOut = (2 * showIdelBefore);
    private static Dialog dialog = null;
    private static Handler finishHandler = null;
    private static Runnable finishrunnable = null;
    private static CountDownTimer countDownTimer;
    private static ProgressBar progressBarCircle;
    private static TextView textViewTime;
    private static Activity a;
    private static boolean blink = true;

    //Time Out
    public static void showTimeoutDialog(Activity activity, int ouCode) {
        a = activity;
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        if (ouCode == 3) {
            dialog.setContentView(R.layout.dialog_time_out_ls);
        } else {
            dialog.setContentView(R.layout.dialog_time_out);
        }
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnYes = dialog.findViewById(R.id.btnYes);

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            stopCountDownTimer();
        });

        progressBarCircle = dialog.findViewById(R.id.progress_circular);
        textViewTime = dialog.findViewById(R.id.text);

        dialog.setOnCancelListener(dialog -> {
            dialog.dismiss();
            stopCountDownTimer();
        });

        if (!activity.isFinishing())
            if (!dialog.isShowing()) {
                hideKeyboard(activity);
                new Handler().postDelayed(() -> {
                    dialog.show();
                    startClock(ouCode);
                }, 200);
            }
    }

    private static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Back to Home
    public static void showGoingHome(final Activity activity, int ouCode) {
        if (activity instanceof MissingSizeActivity)
            ((MissingSizeActivity) activity).stopHandler();
        else if (activity instanceof SizeOptionsActivity)
            ((SizeOptionsActivity) activity).stopHandler();
        else
            ((BrowserCatelogActivity) activity).stopHandler();
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        if (ouCode == 3) {
            dialog.setContentView(R.layout.dialog_back_button_ls);
        } else {
            dialog.setContentView(R.layout.dialog_back_button);
        }
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnNo = dialog.findViewById(R.id.btnNo);
        Button btnYes = dialog.findViewById(R.id.btnYes);

        btnYes.setOnClickListener(v -> dialog.dismiss());

        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
            if (activity instanceof MissingSizeActivity) {
                ((MissingSizeActivity) activity).deleteCache(activity);
                ((MissingSizeActivity) activity).clearEmpId();
            }
            activity.finish();
        });

        dialog.setOnCancelListener(dialog -> {
            dialog.dismiss();
            if (activity instanceof MissingSizeActivity)
                ((MissingSizeActivity) activity).startHandler();
            else if (activity instanceof SizeOptionsActivity)
                ((SizeOptionsActivity) activity).stopHandler();
            else
                ((BrowserCatelogActivity) activity).startHandler();
        });

        if (!activity.isFinishing())
            if (!dialog.isShowing())
                dialog.show();
    }

    //Employee Code
    static EditText etEmployeeCode;
    public static void enterEmployeeCode(final Activity activity, int ouCode) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        if (ouCode == 3) {
            dialog.setContentView(R.layout.dialog_employee_ls);
        } else {
            dialog.setContentView(R.layout.dialog_employee);
        }
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        etEmployeeCode = dialog.findViewById(R.id.etEmployeeCode);
        Button btnNo = dialog.findViewById(R.id.btnNo);
        Button btnYes = dialog.findViewById(R.id.btnYes);

        //supriya added

        /////////////



        // added 24-Nov-2022 gurpreet added
        //etEmployeeCode.InputType = (etEmployeeCode.INPUT_TYPE_TEXT, 4096)
        etEmployeeCode.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                return Character.isLetterOrDigit(c);//|| Character.isSpaceChar(c)
            }
        }, new InputFilter.LengthFilter(11)});

        // open dt: 24-09-2021
        final PrefManager prefManager = new PrefManager(activity);
        /*if (prefManager.getEmployeeId() != null)
            etEmployeeCode.setText(prefManager.getEmployeeId());*/

        btnYes.setOnClickListener(v -> {
            String employeeCode = etEmployeeCode.getText().toString().trim();
            if (employeeCode.length() > 0) {
                if (employeeCode.length() < 7) {
                    etEmployeeCode.setError("Enter correct Employee Id");
                } else {
                    // commented on 01-12-2022
                  //  prefManager.setEmployeeId(employeeCode);
                  //  dialog.dismiss();

                    // added 01-12-2022
                    // call api to validate employee
                    ((HomeScreenActivity) activity).validateEmployeeId(employeeCode);
                }

            } else
                etEmployeeCode.setError("Enter Employee Id");

        });

        btnNo.setOnClickListener(v -> dialog.dismiss());
        dialog.setOnCancelListener(dialog -> dialog.cancel());

        if (!activity.isFinishing())
            if (!dialog.isShowing())
                dialog.show();
    }
    // added 01-12-2022
    public static void saveEmpIdandDismissDialog(HomeScreenActivity homeScreenActivity, String empCode) {
        PrefManager prefManager = new PrefManager(homeScreenActivity);
        prefManager.setEmployeeId(empCode);
        if (dialog != null)
            dialog.dismiss();
    }
    // added 01-12-2022
    public static void showErrorEmpDialog(String errormsg) {
        etEmployeeCode.setError(""+ errormsg);

    }

    //Mobile Number
    public static void enterMobileNo(final Activity activity, final DialogOkClickListner clickListner, int ouCode) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        if (ouCode == 3) {
            dialog.setContentView(R.layout.dialog_mobileno_ls);
        } else {
            dialog.setContentView(R.layout.dialog_mobileno);
        }
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final EditText etEmployeeCode = dialog.findViewById(R.id.etEmployeeCode);
        Button btnYes = dialog.findViewById(R.id.btnYes);

        etEmployeeCode.setOnEditorActionListener((v, actionId, event) -> {
            if (etEmployeeCode.getText().toString().trim().length() > 0
                    && etEmployeeCode.getText().toString().trim().length() == 10) {
                dialog.dismiss();
                clickListner.onOkClick(etEmployeeCode.getText().toString().trim());
            } else
                etEmployeeCode.setError("Enter 10 digit  valid Mobile No");
            return false;
        });

        btnYes.setOnClickListener(v -> {
            if (etEmployeeCode.getText().toString().trim().length() > 0
                    && etEmployeeCode.getText().toString().trim().length() == 10) {
                dialog.dismiss();
                clickListner.onOkClick(etEmployeeCode.getText().toString().trim());
            } else
                etEmployeeCode.setError("Enter 10 digit  valid Mobile No");
        });

        dialog.setOnCancelListener(dialog -> dialog.dismiss());

        if (!activity.isFinishing())
            if (!dialog.isShowing())
                dialog.show();
    }

    public static void stopFinishHandler() {
        if (finishHandler != null && finishrunnable != null)
            finishHandler.removeCallbacks(finishrunnable);
    }

    private static void startFinishHandler() {
        finishHandler.postDelayed(finishrunnable, showIdelBefore);
    }

    public static Dialog getDialogInstance() {
        return dialog;
    }

    public interface DialogOkClickListner {
        void onOkClick(String data);
    }

    //Starting Clock
    private static void startClock(int ouCode) {
        setProgressBarValues();
        startCountDownTimer(ouCode);
    }

    //Start CountdownTimer
    private static void startCountDownTimer(int ouCode) {
        try {
            countDownTimer = new CountDownTimer(showIdelBefore, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                    if (ouCode == 3) {
                        textViewTime.setTextColor(Color.BLACK);
                    } else {
                        textViewTime.setTextColor(Color.WHITE);
                    }
                    if (Integer.parseInt(hmsTimeFormatter(millisUntilFinished)) <= 10) {
                        textViewTime.setTextColor(Color.RED);
                        if (blink)
                            textViewTime.setVisibility(View.INVISIBLE);
                        else
                            textViewTime.setVisibility(View.VISIBLE);
                        blink = !blink;
                    } else {
                        //textViewTime.setTextColor(Color.WHITE);
                        if (ouCode == 3) {
                            textViewTime.setTextColor(Color.BLACK);
                        } else {
                            textViewTime.setTextColor(Color.WHITE);
                        }
                    }
                    progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    textViewTime.setText("00 sec");
                    if (ouCode == 3) {
                        textViewTime.setTextColor(Color.BLACK);
                    } else {
                        textViewTime.setTextColor(Color.WHITE);
                    }
                    progressBarCircle.setProgress(0);
                    if (a instanceof MissingSizeActivity)
                        ((MissingSizeActivity) a).clearEmpId();

                    a.finish();
                }
            }.start();
            countDownTimer.start();
        } catch (Exception e) {
            Log.d("Abrar", e.getLocalizedMessage());
        }
    }

    //Stop CountdownTimer
    private static void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    //Intializing Progressbar
    private static void setProgressBarValues() {
        progressBarCircle.setMax((int) showIdelBefore / 1000);
        progressBarCircle.setProgress((int) showIdelBefore / 1000);
    }

    private static String hmsTimeFormatter(long milliSeconds) {
        return String.format("%02d",
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));
    }

    public static void showLog(String message) {
        Log.e("EMPLOYEE ID", "" + message);
    }


    public static class AlphaNumericInputFilter implements InputFilter {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            // Only keep characters that are alphanumeric
            StringBuilder builder = new StringBuilder();
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (Character.isLetterOrDigit(c)) {
                    builder.append(c);
                }
            }

            // If all characters are valid, return null, otherwise only return the filtered characters
            boolean allCharactersValid = (builder.length() == end - start);
            return allCharactersValid ? null : builder.toString();
        }
    }
}
