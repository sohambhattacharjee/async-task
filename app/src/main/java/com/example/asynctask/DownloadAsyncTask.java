package com.example.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadAsyncTask extends AsyncTask {
    private Context context;

    public DownloadAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        wl.acquire();
        try {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            String root = Environment.getExternalStorageDirectory().toString();
            for (Object urlObj : objects) {
                try {
                    String strUrl = urlObj.toString();
                    URL url = new URL(strUrl);
                    String fileName = strUrl.substring(strUrl.lastIndexOf('/') + 1, strUrl.length());
                    System.out.println("Downloading " + fileName );



                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();
                    // download the file
                    input = new BufferedInputStream(url.openStream(), 8192);
                    output = new FileOutputStream(root+"/"+fileName);
                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled())
                            return null;
                        total += count;
                        output.write(data, 0, count);
                    }

                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }
                    if (connection != null)
                        connection.disconnect();
                }
            }
        } finally {
            wl.release();
        }
        return null;
    }



}
