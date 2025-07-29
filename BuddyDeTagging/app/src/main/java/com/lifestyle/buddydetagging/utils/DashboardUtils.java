package com.lifestyle.buddydetagging.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.lifestyle.buddydetagging.R;

import java.text.DecimalFormat;

@SuppressLint("SetTextI18n")
public class DashboardUtils {
    public static DecimalFormat floatingDecimalFormate = new DecimalFormat("#,##,#00.0");
    public static DecimalFormat floatingDecimalFormate1 = new DecimalFormat("#,##,##0.0");
    public static DecimalFormat floatingDecimalFormate2 = new DecimalFormat("#,##,###.#");
    public static DecimalFormat floatingDecimalFormate3 = new DecimalFormat("######.#");
    public static DecimalFormat floatingDecimalFormate4 = new DecimalFormat("#,##,##0.00");
    public static DecimalFormat floatingDecimalFormate5 = new DecimalFormat("#,##,#00.00");
    public static DecimalFormat decimalFormat = new DecimalFormat("#,##,#00");
    public static DecimalFormat decimalFormat1 = new DecimalFormat("#,##,##0");
    public static final double LACS = 100000;
    public static final double CRORE = 10000000;
    public static int count = 0;
    public static int tabSelected = 0;
    public static boolean isOverall = false;
    public static boolean isBrand = false;
    public static boolean isPrivateLabel = false;
    public static final String IS_CHECKED = "isChecked";
    public static final String ARG_SECTION_NUMBER = "salePerformance";
    public static final String SALE_TYPE = "saleType";

  /*  @BindingAdapter({"bind:percent","bind:colorCombination","bind:symbol","bind:decimalFormat"})
    public static void setDataFormatting(TextView textView, double percent, String colorCombination, String symbol, DecimalFormat decimalFormat){
        formattingStoreData(textView.getContext(), textView, percent, symbol, colorCombination, decimalFormat);
    }*/

    public static void formattingStoreData(Context context, TextView tv, double value, String symbol,
                                           String color, DecimalFormat decimalFormat) {
        if (symbol.equalsIgnoreCase("₹")
                ||symbol.equalsIgnoreCase(": ")) {
            if (Double.isNaN(value))
                tv.setText(symbol+decimalFormat.format(0));
            else if (Double.isInfinite(value))
                tv.setText(symbol+decimalFormat.format(0));
            else
                tv.setText(symbol+decimalFormat.format(value));
        }else if (symbol.equalsIgnoreCase("₹:L")) {
            String[] symbols = symbol.split(":");
            if (Double.isNaN(value))
                tv.setText(symbols[0]+decimalFormat.format(0)+symbols[1]);
            else if (Double.isInfinite(value))
                tv.setText(symbols[0]+decimalFormat.format(0)+symbols[1]);
            else
                tv.setText(symbols[0]+decimalFormat.format(value)+symbols[1]);
        }else if (symbol.equalsIgnoreCase(": N%")) {
            String[] symbols = symbol.split("N");
            if (Double.isNaN(value))
                tv.setText(symbols[0]+decimalFormat.format(0)+symbols[1]);
            else if (Double.isInfinite(value))
                tv.setText(symbols[0]+decimalFormat.format(0)+symbols[1]);
            else
                tv.setText(symbols[0]+decimalFormat.format(value)+symbols[1]);
        }else {
            if (Double.isNaN(value))
                tv.setText(decimalFormat.format(0) + symbol);
            else if (Double.isInfinite(value))
                tv.setText(decimalFormat.format(0) + symbol);
            else
                tv.setText(decimalFormat.format(value) + symbol);
        }

        if (color.equalsIgnoreCase("Change")) {
            if (value > 0)
                tv.setTextColor(context.getResources().getColor(R.color.green));
            else
                tv.setTextColor(context.getResources().getColor(R.color.red));
        } else if (color.equalsIgnoreCase("Budget")) {
            if (value >= 100)
                tv.setTextColor(context.getResources().getColor(R.color.green));
            else if (value >= 95 && value < 100)
                tv.setTextColor(context.getResources().getColor(R.color.yellowDash));
            else
                tv.setTextColor(context.getResources().getColor(R.color.red));
        } else
            tv.setTextColor(context.getResources().getColor(R.color.text));
    }

  /*  @BindingAdapter({"bind:orignalDate","bind:inputDateFormat","bind:outputDateFormat","bind:symbol"})
    public static void setDateFormat(TextView textView, String orignalDate, SimpleDateFormat inputDateFormat, SimpleDateFormat outputDateFormat, String symbol){
        String outputDate = Utility.parsingDateFormate(orignalDate,inputDateFormat,outputDateFormat);
        textView.setText(symbol + outputDate);
    }*/


   /* @BindingAdapter("bind:imageUrl")
    public static void imageUrl(ImageView img, String imageUrl){
        if (imageUrl != null){
            Glide.with(img.getContext()).load(imageUrl).centerCrop()
                    .placeholder(R.drawable.no_image)
                    .into(img);
        }else
            img.setImageResource(R.drawable.no_image);
    }*/

    public static void exitByBackKey(Activity activity) {
        new AlertDialog.Builder(activity)
                .setMessage("Do you want to Exit Dashboard")
                .setPositiveButton("Yes", (arg0, arg1) -> activity.finish())
                .setNegativeButton("No", (arg0, arg1) -> {})
                .show();

    }
}