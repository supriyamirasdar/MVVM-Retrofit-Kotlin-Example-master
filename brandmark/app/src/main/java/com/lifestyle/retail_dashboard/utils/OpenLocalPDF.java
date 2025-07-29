package com.lifestyle.retail_dashboard.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.lifestyle.retail_dashboard.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import androidx.annotation.RequiresApi;

public class OpenLocalPDF {
    private static String TAG = OpenLocalPDF.class.getSimpleName();

    private WeakReference<Context> contextWeakReference;
    private String fileName;
    Context ctx;

    public OpenLocalPDF(Context context, String fileName) {
        this.contextWeakReference = new WeakReference<>(context);
        this.fileName = fileName.endsWith("pdf") ? fileName : fileName + ".pdf";
        this.ctx = context;
        Log.e("tag", "fileName ::" + fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void execute() {
        Context context = contextWeakReference.get();
        if (context != null) {
            new CopyFileAsyncTask().execute();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private class CopyFileAsyncTask extends AsyncTask<Void, Void, File> {



        @Override
        protected File doInBackground(Void... params) {
            Context context = contextWeakReference.get();
            AssetManager assetManager = context.getAssets();
            // File file = new File(ctx.getFilesDir(), fileName);
            File apkStorage = new File(Environment.getExternalStorageDirectory() + "/" + "BrandMark_Download");
            Log.d(TAG, "apkStorage::" + apkStorage);
            File file = null;//new File(Environment.getExternalStorageDirectory() + "/BrandMark_Download/" + fileName);  // -> filename = maven.pdf

            InputStream in = null;
            OutputStream out = null;
            try {
                if (!apkStorage.exists()) {
                    apkStorage.mkdir();
                    Log.e(TAG, "Directory Created.");
                }

                file = new File(apkStorage, fileName);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                Log.d(TAG, "file::" + file);
                in = assetManager.open(fileName);
                Log.d(TAG, "In");
                out = new FileOutputStream(file);
                Log.d(TAG, "Out");
                Log.d(TAG, "Copy file");
                //copyFile(in, out);
                //InputStream is = in.getInputStream();//Get InputStream for connection
                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len1);//Write new file
                }
                Log.d(TAG, "Close");
                in.close();
                out.flush();
                out.close();
                return file;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        private void copyFile(InputStream in, OutputStream out) throws IOException {
            try {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            Context context = contextWeakReference.get();

            try {

                File url = new File(Environment.getExternalStorageDirectory() + "/BrandMark_Download/" + fileName);  // -> filename = maven.pdf
                Log.d(TAG, "onPostExecute url::" + url);
                //File url = new File(ctx.getFilesDir(), fileName);  // -> filename = maven.pdf
                Uri uri = Uri.fromFile(url);
                Log.e(TAG, "onPostExecute uri::" + uri);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                //intent.setDataAndType(uri, "application/pdf");
                intent.setDataAndType(uri, "*/*");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.setPackage("com.android.chrome");
                context.startActivity(intent);

            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
            }catch (Exception e) {
                e.printStackTrace();
             }
        }
    }
}
