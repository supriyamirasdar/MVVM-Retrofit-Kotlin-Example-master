package com.landmarkgroup.smartkiosk.ui.pdppage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.landmarkgroup.smartkiosk.R;

public class AlertDialogListener extends DialogFragment {

    AlertPositiveListener alertPositiveListener;
    private Context context;
    private String[] modes;
    private String message;
    private String title;

    private boolean isOopsBtn = false;

    public AlertDialogListener() {
    }


    public interface AlertPositiveListener {
        public void onPositiveClick(int position);

        public void onNegativeClick(int position);
    }

    @SuppressLint("ValidFragment")
    public AlertDialogListener(Context context, String[] strings) {
        this.context = context;
        this.modes = strings;
        this.message = null;
    }

    @SuppressLint("ValidFragment")
    public AlertDialogListener(Context context, String[] strings, String message) {
        this.context = context;
        this.modes = strings;
        this.message = message;
    }

    @SuppressLint("ValidFragment")
    public AlertDialogListener(Context context, String message) {
        this.context = context;
        this.modes = null;
        this.message = message;
    }

    @SuppressLint("ValidFragment")
    public AlertDialogListener(Context context, String title, String message) {
        this.context = context;
        this.title = title;
        this.message = message;
    }

    @SuppressLint("ValidFragment")
    public AlertDialogListener(Context context, String title, String message, boolean isOopsBtn) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.isOopsBtn = isOopsBtn;
    }

    @SuppressLint("ValidFragment")
    public AlertDialogListener(Context context, String title, String message, boolean isOopsBtn, AlertPositiveListener listener) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.isOopsBtn = isOopsBtn;
        this.alertPositiveListener = listener;
    }

    @SuppressLint("ValidFragment")
    public AlertDialogListener(Context context, String title, String message, AlertPositiveListener listener) {
        this.context = context;
        this.title = title;
        this.message = message;
        this.isOopsBtn = isOopsBtn;
        this.alertPositiveListener = listener;
    }

    /**
     * This is a callback method executed when this fragment is attached to an
     * activity. This function ensures that, the hosting activity implements the
     * interface AlertPositiveListener
     */
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
     /*   try {
            alertPositiveListener = (AlertPositiveListener) activity;
        } catch (ClassCastException e) {
            // The hosting activity does not implemented the interface
            // AlertPositiveListener
            throw new ClassCastException(activity.toString()
                    + " must implement AlertPositiveListener");
        }*/

        /*
        try {
            alertPositiveListener = (AlertPositiveListener) context;
        } catch (ClassCastException e) {
            // The hosting activity does not implemented the interface
            // AlertPositiveListener
            throw new ClassCastException(activity.toString()
                    + " must implement AlertPositiveListener");
        }*/
    }

    /**
     * This is the OK button listener for the alert dialog, which in turn
     * invokes the method onPositiveClick(position) of the hosting activity
     * which is supposed to implement it
     */
    DialogInterface.OnClickListener positiveListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog) dialog;

            if (alert.getListView() != null) {
                int position = alert.getListView().getCheckedItemPosition();
                alertPositiveListener.onPositiveClick(position);
            }
            if (which == -1) {
                Log.d("positiveListener", "positive clicked" + which);
                alertPositiveListener.onPositiveClick(1);
            } else if (which == -2) {
                Log.d("positiveListener", "negative clicked" + which);
                alertPositiveListener.onNegativeClick(0);
            }
        }
    };

    /**
     * This is a callback method which will be executed on creating this
     * fragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /** Getting the arguments passed to this fragment */
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");
        /** Creating a builder for the alert dialog window */
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        /** Setting a title for the window */
        /** Setting items to the alert dialog */
        Button declineButton = null;
        Button doneButton = null;


        if (message != null && title != null) {
            b.setTitle(title);
            b.setMessage(message);
        }

        if (message == null && modes != null) {
            b.setSingleChoiceItems(modes, position, null);
            b.setTitle("Select option");
        }

        if (message != null && modes != null) {
            b.setSingleChoiceItems(modes, position, null);
            b.setTitle(message);
        }

		/*if(message != null && modes == null) {
			b.setTitle("Please Note");
			b.setMessage(message);
		}
*/


        if (message != null && modes == null) {
            //b.setTitle("Please Note");
            //b.setMessage("");


            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.dialog_layout, null);
            //  b = new AlertDialog.Builder(new ContextThemeWrapper(context, null));//android.R.style.Theme_DeviceDefault_Dialog_Alert));
            b = new AlertDialog.Builder(context);//android.R.style.Theme_DeviceDefault_Dialog_Alert));

            /*View titleView = getActivity().getLayoutInflater().inflate(R.layout.alert_dialog_custom_title, null);
            TextView title = titleView.findViewById(R.id.title);
            title.setText("Please Note");*/

            //b.setTitle("Please Note");
            // b.setCustomTitle(titleView);


            declineButton = (Button) dialoglayout.findViewById(R.id.btn_cancel);
            doneButton = (Button) dialoglayout.findViewById(R.id.btn_done);

            declineButton.setText("ONLINE");
            doneButton.setText("CANCEL");


            final TextView dialogTitle = dialoglayout.findViewById(R.id.dialogTitle);
            dialogTitle.setText(title);
            // added 06-07-2021
            if (title.equals("no")) {
                dialogTitle.setVisibility(View.GONE);
            } else {
                dialogTitle.setVisibility(View.VISIBLE);
            }

            final TextView text = dialoglayout.findViewById(R.id.eonText);
            text.setText(message);
            //text.setText(Html.fromHtml(message));
            text.setTextSize(20);
            text.setTextIsSelectable(true);
            b.setView(dialoglayout);


            //	28/03/2017  working fine except copy option theme

		/*	LayoutInflater inflater = getActivity().getLayoutInflater();
			View dialoglayout = inflater.inflate(R.layout.dialog_layout, null);
			b = new AlertDialog.Builder(getActivity());
			b.setTitle("Please Note");

			final TextView text = (TextView) dialoglayout.findViewById(R.id.eonText);
			text.setText(message);
			text.setTextSize(20);
			text.setTextIsSelectable(true);
			b.setView(dialoglayout);*/


            // 27/0/2017

			/*LinearLayout diagLayout = new LinearLayout(getActivity());
			diagLayout.setOrientation(LinearLayout.VERTICAL);
			final TextView text = new TextView(getActivity());
			text.setText(message);
			text.setTextIsSelectable(true);
			text.setPadding(10, 0, 10, 20);
			text.setGravity(Gravity.TOP|Gravity.LEFT);
			text.setTextSize(20);
			diagLayout.addView(text);
			b.setView(diagLayout);*/

			/*ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData clip = ClipData.newPlainText("abcd", "abcd");
			clipboard.setPrimaryClip(clip);*/
        }

        /** Setting a positive button and its listener */
        if (isOopsBtn) {

            declineButton.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.VISIBLE);
            declineButton.setText("ONLINE");
            doneButton.setText("CANCEL");

        } else {
            declineButton.setVisibility(View.GONE);
            doneButton.setVisibility(View.VISIBLE);
            doneButton.setText("OK");
        }

        /** Setting a positive button and its listener */
        //  b.setPositiveButton("OK", positiveListener);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog

                // alertPositiveListener.onNegativeClick();
                // positiveListener.onClick(alertPositiveListener,1);
                alertPositiveListener.onNegativeClick(0);
                dismiss();
                // b.setPositiveButton("OK", positiveListener);
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                try {
                    alertPositiveListener.onPositiveClick(1);
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        /** Creating the alert dialog window using the builder class */
        AlertDialog d = b.create();
        /** Return the alert dialog window */
     /*   Window window = d.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);*/

        return d;
    }

    /*public void showDialog(String value) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sent_oops_dialog);

        dialog.setTitle("Custom Dialog");
        if (value.equalsIgnoreCase("send")) {
            TextView text = (TextView) dialog.findViewById(R.id.dialog_title);
            text.setText("Send Request");
            text.setBackgroundResource(R.color.fra_sent_color_dialog);
            Drawable img = context.getResources().getDrawable(R.drawable.ic_search);
            img.setBounds(0, 0, 60, 60);
            text.setCompoundDrawables(img, null, null, null);
            dialog.show();
            Button declineButton = (Button) dialog.findViewById(R.id.cancel);
            Button doneButton = (Button) dialog.findViewById(R.id.done);
            // if decline button is clicked, close the custom dialog
            declineButton.setOnClickListener(v -> {
                // Close dialog
                dialog.dismiss();
            });
            doneButton.setOnClickListener(v -> {
                // Close dialog
                dialog.dismiss();
            });
        } else if (value.equalsIgnoreCase("oops")) {
            TextView text = (TextView) dialog.findViewById(R.id.dialog_title);
            text.setText("OOPS");
            text.setBackgroundResource(R.color.fra_oops_color_dialog);
            Drawable img = context.getResources().getDrawable(R.drawable.ic_barcode_black);
            img.setBounds(0, 0, 60, 60);
            text.setCompoundDrawables(img, null, null, null);
            dialog.show();
            Button declineButton = (Button) dialog.findViewById(R.id.cancel);
            Button doneButton = (Button) dialog.findViewById(R.id.done);
            // if decline button is clicked, close the custom dialog
            declineButton.setOnClickListener(v -> {
                // Close dialog
                dialog.dismiss();
            });
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Close dialog
                    dialog.dismiss();
                }
            });
        }
        // set values for custom dialog components - text, image and button

    }*/

}
