package com.lifestyle.retail.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

 import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.landmarkgroup.smartkiosk.R;
import com.lifestyle.retail.StylusApp;
import com.lifestyle.retail.base.BaseCompatActivity;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


@SuppressLint("SimpleDateFormat")
public class CommonUtility {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    //Check object is null
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    //Check object is null
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static boolean isValidString(Object obj) {
        if (isNotNull(obj)) {
            if (obj instanceof String) {
                return !((String) obj).isEmpty() && !obj.equals("null");
            }
        }
        return false;
    }



    public static String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static int convertDpToPx(int dp, Context context) {
        return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    /**
     * get user logout from application
     *
     * @param activity the activity
     */

    /**
     * to show view when softkey keyboard open
     */
    public static void showHideVisibility(View contentView, View showHideView) {
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {

            Rect r = new Rect();
            contentView.getWindowVisibleDisplayFrame(r);
            int screenHeight = contentView.getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                // keyboard is opened
                showHideView.setVisibility(View.VISIBLE);
            } else {
                showHideView.setVisibility(View.GONE);
                // keyboard is closed
            }
        });
    }



    /**
     * check is it simulator
     *
     * @return the return true if simulator
     */
    private static boolean isSimulator() {
        boolean isSimulator = "google_sdk".equals(Build.PRODUCT)
                || "vbox86p".equals(Build.PRODUCT)
                || "sdk".equals(Build.PRODUCT);


        return isSimulator;
    }



    /**
     * to get location from Address
     *
     * @param context    the context
     * @param strAddress the address
     * @return the reurn LatLng
     */
    /*public static LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if ((address == null) || address.isEmpty()) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {
        }

        return p1;
    }*/

    /**
     * get small address from large address
     *
     * @param address the address
     * @return the return string
     */
    public static String address(String address) {
        if (isNotNull(address)) {
            return address.trim();
        }
        return null;
    }

    /**
     * get color from color code dynamicaly
     *
     * @param context the context
     * @param color   the color
     * @return the return color
     */
    public static int getColor(Context context, int color) {
        return ContextCompat.getColor(context, color);
    }


    /**
     * Method to check if a GSTIN is valid. Checks the GSTIN format and the
     * check digit is valid for the passed input GSTIN
     *
     * @param gstin
     * @return boolean - valid or not
     * @throws Exception
     */
    public static boolean validGSTIN(String gstin) {
        boolean isValidFormat = false;
        if (checkPattern(gstin, GeneralConstant.GSTINFORMAT_REGEX)) {
            try {
                isValidFormat = verifyCheckDigit(gstin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isValidFormat;

    }

    /**
     * Method for checkDigit verification.
     *
     * @param gstinWCheckDigit
     * @return
     * @throws Exception
     */
    private static boolean verifyCheckDigit(String gstinWCheckDigit) throws Exception {
        Boolean isCDValid = false;
        String newGstninWCheckDigit = getGSTINWithCheckDigit(
                gstinWCheckDigit.substring(0, gstinWCheckDigit.length() - 1));

        if (gstinWCheckDigit.trim().equals(newGstninWCheckDigit)) {
            isCDValid = true;
        }
        return isCDValid;
    }

    /**
     * Method to check if an input string matches the regex pattern passed
     *
     * @param inputval
     * @param regxpatrn
     * @return boolean
     */
    public static boolean checkPattern(String inputval, String regxpatrn) {
        boolean result = false;
        if ((inputval.trim()).matches(regxpatrn)) {
            result = true;
        }
        return result;
    }

    /**
     * Method to get the check digit for the gstin (without checkdigit)
     *
     * @param gstinWOCheckDigit
     * @return : GSTIN with check digit
     * @throws Exception
     */
    public static String getGSTINWithCheckDigit(String gstinWOCheckDigit) throws Exception {
        int factor = 2;
        int sum = 0;
        int checkCodePoint = 0;
        char[] cpChars;
        char[] inputChars;

        try {
            if (gstinWOCheckDigit == null) {
                throw new Exception("GSTIN supplied for checkdigit calculation is null");
            }
            cpChars = GeneralConstant.GSTN_CODEPOINT_CHARS.toCharArray();
            inputChars = gstinWOCheckDigit.trim().toUpperCase().toCharArray();

            int mod = cpChars.length;
            for (int i = inputChars.length - 1; i >= 0; i--) {
                int codePoint = -1;
                for (int j = 0; j < cpChars.length; j++) {
                    if (cpChars[j] == inputChars[i]) {
                        codePoint = j;
                    }
                }
                int digit = factor * codePoint;
                factor = (factor == 2) ? 1 : 2;
                digit = (digit / mod) + (digit % mod);
                sum += digit;
            }
            checkCodePoint = (mod - (sum % mod)) % mod;
            return gstinWOCheckDigit + cpChars[checkCodePoint];
        } finally {
            inputChars = null;
            cpChars = null;
        }
    }



    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(CommonUtility.convertDpToPx(250, view.getContext()),
                CommonUtility.convertDpToPx(270, view.getContext()), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    /*public static Bitmap getMarkerBitmapFromView(@DrawableRes int resId, Context context, String shopName) {

        View markerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_pin_layout, null);
        ImageView markerImageView = markerView.findViewById(R.id.ivAppIcon);
        markerImageView.setImageResource(resId);
        TextView tvShopName = markerView.findViewById(R.id.tvShopName);
        tvShopName.setText(shopName);
        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
        markerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = markerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        markerView.draw(canvas);
        return returnedBitmap;

    }
*/
    public static void showSnackBar(Activity activity, String msg) {
        if (activity != null) {
            Snackbar snack = Snackbar.make(activity.findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
            View view = snack.getView();
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
            params.gravity = Gravity.BOTTOM;
            params.width = getWidth(activity);
            view.setLayoutParams(params);
            snack.show();
        }
    }

    public static void changeUpperCase(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())) {
                    s = s.toUpperCase();
                    editText.setText(s);
                    editText.setSelection(s.length());
                }
            }
        });
    }

    public static int getWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width;
    }

    public static void deleteCroppedImage(Uri imageUri) {
        File fdelete = new File(imageUri.toString());

        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :");
            } else {
                System.out.println("file not Deleted :");
            }
        }
    }

    /**
     * Get notification icons for Android 5 or belos
     *
     * @return
     */
    @SuppressWarnings("JavaDoc")
    public static int getNotificationIcon() {
        return R.mipmap.ic_launcher;
    }

    public static boolean isAndroidO() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    public static void showToast(String message) {
        Toast toast = Toast.makeText(StylusApp.mInstance, message, Toast.LENGTH_SHORT);
        toast.setGravity(10, 0, 0);
        toast.show();
    }

    public static List<String> getList(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<List<String>>() {
        }.getType());

    }

    public static String setDate(int dayOfMonth, int monthOfYear, int year) {
        String day;
        String month;
        if (dayOfMonth < 10) {
            day = String.format(Locale.getDefault(), "%s%d", "0", dayOfMonth);
        } else {
            day = "" + dayOfMonth;

        }
        if (monthOfYear < 10) {
            month = String.format(Locale.getDefault(), "%s%d", "0", monthOfYear);
        } else {
            month = "" + monthOfYear;

        }
        return String.format(Locale.getDefault(), "%s%s%s%s%d", day, "/", month, "/", year);
    }

    public static String setTime(int hourOfDay, int minuteOfDay) {
        String hour;
        String minute;

        if (hourOfDay < 10) {
            hour = String.format(Locale.getDefault(), "%s%d", "0", hourOfDay);
        } else {
            hour = "" + hourOfDay;

        }
        if (minuteOfDay < 10) {
            minute = String.format(Locale.getDefault(), "%s%d", "0", minuteOfDay);
        } else {
            minute = "" + minuteOfDay;

        }
        return String.format(Locale.getDefault(), "%s%s%s", hour, ":", minute);
    }

    public static String setDateString(Activity activity, String[] startDateArray, String startTime) {
        String date = String.format(Locale.getDefault(), "%s%s%s%s%s %s", startDateArray[2], activity.getString(R.string.dash),
                startDateArray[1], activity.getString(R.string.dash), startDateArray[0], startTime);
        return date;
    }

    /*public static void setSelectedResource(Activity activity, int position, ImageView imageView, TextView textView) {
        switch (position) {
            case 0:
                textView.setText(R.string.my_store);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                textView.setTextColor(activity.getResources().getColor(R.color.colorThemeRed));
                imageView.setImageResource(R.drawable.ic_inventory_selected_footer);
                break;
            case 1:
                textView.setText(R.string.common_signin_button_text);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                textView.setTextColor(activity.getResources().getColor(R.color.colorThemeRed));
                imageView.setImageResource(R.drawable.ic_inventory_selected_footer);
                break;
            case 2:
                textView.setText(R.string.order);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                textView.setTextColor(activity.getResources().getColor(R.color.colorThemeRed));
                imageView.setImageResource(R.drawable.ic_order_selected_footer);
                break;
           *//* case 3:
                textView.setText(R.string.create_campaign);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                textView.setTextColor(activity.getResources().getColor(R.color.colorThemeRed));
                imageView.setImageResource(R.drawable.footer_create_select);
                break;
            default:
                textView.setText(R.string.setting);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                textView.setTextColor(activity.getResources().getColor(R.color.colorThemeRed));
                imageView.setImageResource(R.drawable.ic_settings_selected_footer);
                break;*//*
        }
//        textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.dark_green_blue));
//        textView.setTypeface(ResourcesCompat.getFont(textView.getContext(), R.font.helvetica_bold));
    }

    public static void setUnSelectedResource(Activity activity, int position, ImageView imageView, TextView textView) {
        switch (position) {
            case 0:
                textView.setText(R.string.my_store);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                textView.setTextColor(activity.getResources().getColor(R.color.color_75000000));
                imageView.setImageResource(R.drawable.ic_inventory_unselected_footer);
                break;
            *//*case 1:
                textView.setText(R.string.pos);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                textView.setTextColor(activity.getResources().getColor(R.color.color_75000000));
                imageView.setImageResource(R.drawable.ic_pos_unselected_footer);
                break;
            case 2:
                textView.setText(R.string.order);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                textView.setTextColor(activity.getResources().getColor(R.color.color_75000000));
                imageView.setImageResource(R.drawable.ic_order_unselected_footer);
                break;
            case 3:
                textView.setText(R.string.create_campaign);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                textView.setTextColor(activity.getResources().getColor(R.color.color_75000000));
                imageView.setImageResource(R.drawable.ic_create_unselected_footer);
                break;
            default:
                textView.setText(R.string.setting);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                textView.setTextColor(activity.getResources().getColor(R.color.color_75000000));
                imageView.setImageResource(R.drawable.ic_settings_unselected_footer);
                break;*//*
        }

        // textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.black_46));
        // textView.setTypeface(ResourcesCompat.getFont(textView.getContext(), R.font.helvetic_regular));
    }
*/
    public static void setRecyclerViewHeight(RecyclerView recyclerView, List list, int height) {
        if (isNotNull(list)) {
            ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
            params.height = convertDpToPx(height, recyclerView.getContext()) * list.size();
            recyclerView.setLayoutParams(params);
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertTimeTo12HourFormat(String _24HourTime) {
        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            return _12HourSDF.format(_24HourDt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertTimeTo24HourFormat(String _12HourTime) {
        try {
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _12HourDt = _12HourSDF.parse(_12HourTime);
            return _24HourSDF.format(_12HourDt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToRequiredFormat(String date) {
        try {
            SimpleDateFormat actFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat reqFormat = new SimpleDateFormat("MMM d yyyy");
            Date reqFormatDate = actFormat.parse(date);
            return reqFormat.format(reqFormatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";

    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateFormat(String date) {
        try {
            SimpleDateFormat actFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat reqFormat = new SimpleDateFormat("MMM d yyyy");
            Date reqFormatDate = actFormat.parse(date);
            return reqFormat.format(reqFormatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";

    }

    @SuppressLint("SimpleDateFormat")
    public static String reverseDateFormat(String date) {
        try {
            SimpleDateFormat reqFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat actFormat = new SimpleDateFormat("MMM d yyyy");
            Date reqFormatDate = actFormat.parse(date);
            return reqFormat.format(reqFormatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";

    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime(String actualtime, int time) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        Date d = null;
        try {
            d = df.parse(actualtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MINUTE, time);
        return df.format(cal.getTime());
    }


    @SuppressLint("SimpleDateFormat")
    public static boolean isTimeElapsed(String t1, String t2) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        try {
            Date d1 = sdf.parse(t1);
            Date d2 = sdf.parse(t2);
            long elapsed = d2.getTime() - d1.getTime();
            if (elapsed > 0) return true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressLint("SimpleDateFormat")
    public static int daysElapsed(String t1, String t2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sdf.parse(t1);
            Date d2 = sdf.parse(t2);
            long elapsed = d2.getTime() - d1.getTime();
            long diffTime = elapsed / (1000 * 60 * 60 * 24);
            return (int) diffTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getTimeDiff(String receivedTime) {
        Date date = Calendar.getInstance().getTime();
//        Wed, 20 Feb 2019 07:20:48 GMT
//        ("E, dd MMMM yyyy hh:mm ss GMT");
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
//        df.setTimeZone(TimeZone.getDefault());
        String currentDate = df.format(date);
        return compareDates(currentDate, receivedTime);
    }

    public static String compareDates(String currentDate, String beforeDate) {
        String difference = "";
        try {
            String pattern = "E, dd MMM yyyy HH:mm:ss";
            SimpleDateFormat formatter = new SimpleDateFormat(pattern);
            Date date1 = formatter.parse(currentDate);
            Date date2 = formatter.parse(beforeDate);

            long diff = date1.getTime() - date2.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) {
                difference = "" + days + " days ago";
            } else if (hours > 0) {
                difference = "" + hours + " hours ago";
            } else if (minutes > 0) {
                difference = "" + minutes + " minutes ago";
            } else if (seconds > 0) {
                difference = "Just Now";
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return difference;
    }

    public static String getOrderTimeFormat(String deliveryType, String deliveryDateTime, String serverDateTime) {
        String formattedDateTime = "";

        String serverDate = serverDateTime.split(" ")[0];
        String deliveryDate = CommonUtility.reverseDateFormat(deliveryDateTime.split(", ")[0]);
//        String formattedServerDate = convertDateToRequiredFormat(serverDate);

        if (CommonUtility.isValidString(deliveryDateTime) && daysElapsed(serverDate, deliveryDate) == 0) {
            if (deliveryDateTime.split(",").length > 1)
                formattedDateTime = String.format("Today, %s", deliveryDateTime.split(", ")[1]);
        } else if (CommonUtility.isValidString(deliveryDateTime) && daysElapsed(deliveryDate, serverDate) == 1) {
            if (deliveryDateTime.split(",").length > 1)
                formattedDateTime = String.format("Yesterday, %s", deliveryDateTime.split(", ")[1]);
        } else {
            formattedDateTime = deliveryDateTime;
        }

        return formattedDateTime;
    }

    public static String getDeliveryDate(String deliverydate) {
        String[] date = deliverydate.split("/");
        return date[2] + "-" + date[1] + "-" + date[0];
    }

    public static String getColoredSpanned(String text, String color) {
        return "<font color=" + color + ">" + text + "</font>";
    }

    public static String getBoldString(String text) {
        return "<b>" + text + "</b>";
    }


    /**
     * password field for show hide
     */
    /*public static void showHidePassword(EditText edPassword, ImageView ivShowHide) {

        if (edPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            edPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            edPassword.setSelection(edPassword.length());
            ivShowHide.setImageResource(R.drawable.icon_visibility_hidden);
        } else {
            edPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edPassword.setSelection(edPassword.length());
            ivShowHide.setImageResource(R.drawable.icon_visibility);

        }
    }*/

    /*public static void getNewFirebaseToken() {
        if (CommonUtility.isNull(PreferenceUtils.getFireBaseToken()) || PreferenceUtils.getFireBaseToken().length() == 0) {
            new Thread(() -> {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(command -> {
                        PreferenceUtils.setFireBaseToken(command.getToken());
                    });

                } catch (Exception e) {
                    try {
                        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(command -> {
                            PreferenceUtils.setFireBaseToken(command.getToken());
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }).start();
        }
    }*/

    /* to get deviceid
     *
     * @return the return
     */
    public static String getDeviceId() {
        return UUID.randomUUID().toString();
    }

    public static void hideKeyboard(BaseCompatActivity activity) {

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidPhone(String phone) {
        String expression = "[6-9][0-9]{9}";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }


    public static String getAppVersionCode() {
        String version = "";

        try {
            PackageInfo pInfo = StylusApp.mInstance.getPackageManager().getPackageInfo(StylusApp.mInstance.getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    public static String parseDate(String date) {
        String time = null;
        String inputpattern = "yyyy-MM-dd";
        String outputpattern = "dd-MMM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(inputpattern);
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(outputpattern);
        try {
            Date date1 = simpleDateFormat.parse(date);
            time = simpleDateFormat1.format(date1);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String parseDatewithFormate(String inputDate,String myFormate) {
        SimpleDateFormat format = new SimpleDateFormat(myFormate);
        try {
            Date date = format.parse(inputDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            return dateFormat.format(date);
        } catch (ParseException e) {
            Log.d("Abrar", e.getLocalizedMessage());
            return inputDate;
        } catch (Exception e) {
            Log.d("Abrar", e.getLocalizedMessage());
            return inputDate;
        }
    }

    public static void showCenterToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static String printObject(Object obj){
        try {
            Gson gson = new Gson();
            return gson.toJson(obj);
        } catch (Exception e){
            return "";
        }
    }


}
