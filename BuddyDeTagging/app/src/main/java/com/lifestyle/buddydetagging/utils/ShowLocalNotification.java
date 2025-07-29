package com.lifestyle.buddydetagging.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import com.lifestyle.buddydetagging.R;
import com.lifestyle.buddydetagging.view.splash.SplashActivity;

import java.io.File;

import androidx.core.app.NotificationCompat;

public class ShowLocalNotification {
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";

    private int currentNotificationID = 0;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private String notificationTitle;
    private String notificationText;
    private Bitmap icon;
    private int combinedNotificationCounter;
    private Context context;
    String fileName;

    public ShowLocalNotification(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
        notificationTitle = "Download Complete";
        notificationText = "Click to open " + fileName;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // setDataForExpandLayoutNotification();

        scheduleNotification(getNotification("5 second delay"), 10);
    }

    public void setDataForExpandLayoutNotification() {
        notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle(notificationTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .setContentText(notificationText);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        sendNotification();
    }

    private void setDataForNotificationWithActionButton() {
        /*notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
                .setContentTitle(notificationTitle)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                .setContentText(notificationText);
        Intent answerIntent = new Intent(this, AnswerReceiveActivity.class);
        answerIntent.setAction("Open File");
        PendingIntent pendingIntentYes = PendingIntent.getActivity(context, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.thumbs_up, "Open File", pendingIntentYes);
        answerIntent.setAction("Cancel");
        PendingIntent pendingIntentNo = PendingIntent.getActivity(context, 1, answerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.thumbs_down, "Cancel", pendingIntentNo);
        sendNotification();*/
    }

    private void sendNotification() {

        /*File pdfFile = new File(Environment.getExternalStorageDirectory() + "/BrandMark_Download/" + fileName);  // -> filename = maven.pdf
        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);*/
        /*try {
            context.startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }*/

        Intent notificationIntent = new Intent(context, SplashActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(contentIntent);
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        currentNotificationID++;
        int notificationId = currentNotificationID;
        if (notificationId == Integer.MAX_VALUE - 1)
            notificationId = 0;
        //notificationManager.notify(notificationId, notification);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "BrandMark",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        } else {
            // notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            notificationManager.notify(notificationId, notification);
        }

    }


    private void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(context, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = 0;//SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/BrandMark_Download/" + fileName);  // -> filename = maven.pdf

        //Uri path = Uri.fromFile(pdfFile);
       // Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        //pdfIntent.setDataAndType(path, "application/pdf");
       // pdfIntent.setDataAndType(path, "*/*");
       // pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // PendingIntent resultIntent = PendingIntent.getActivity(context, 0, pdfIntent, 0);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            File url = new File(Environment.getExternalStorageDirectory() + "/BrandMark_Download/" + fileName);  // -> filename = maven.pdf
            Uri uri = Uri.fromFile(url);
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip")) {
                // ZIP file
                intent.setDataAndType(uri, "application/zip");
            } else if (url.toString().contains(".rar")){
                // RAR file
                intent.setDataAndType(uri, "application/x-rar-compressed");
            } else if (url.toString().contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                    url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }

        PendingIntent resultIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, default_notification_channel_id);
        builder.setContentTitle("" + notificationTitle);
        builder.setContentText(notificationText);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(true);
        builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        builder.setContentIntent(resultIntent);
        return builder.build();
    }

}
/*

https://www.tutorialspoint.com/how-to-schedule-local-notifications-in-android

http://www.theappguruz.com/blog/easy-way-to-send-local-notification-to-user-in-android

https://stackoverflow.com/questions/19422075/open-a-selected-file-image-pdf-programmatically-from-my-android-applicat

 */