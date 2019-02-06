package com.hitstreamr.hitstreamrbeta;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hitstreamr.hitstreamrbeta.Authentication.Splash;
import com.transloadit.android.sdk.AndroidAsyncAssembly;
import com.transloadit.android.sdk.AndroidTransloadit;
import com.transloadit.sdk.async.AssemblyProgressListener;
import com.transloadit.sdk.response.AssemblyResponse;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class VideoUploadService extends Service implements AssemblyProgressListener {
    private static final String TAG = "Video Upload Service";
    private final IBinder mBinder = new VideoUploadService.LocalBinder();
    private NotificationManager mNM;
    private int notifID;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        VideoUploadService getService() {
            // Return this instance of LocalService so clients can call public methods
            return VideoUploadService.this;
        }
    }

    public VideoUploadService() {
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Preparing to Upload Your Video...";

        /*// The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Splash.class), 0);*/

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.hitstreamr_icon)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.logo_white))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                //.setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        notifID = UploadNotificationIdGenerator.getID(this);

        // Send the notification.
        mNM.notify(notifID, notification);
    }

    //TRANSLOADIT IMPLEMENTATION

    @Override
    public void onUploadFinished() {
        Log.e(TAG, "Your AndroidAsyncAssembly Upload is done and it's now executing");
    }

    @Override
    public void onUploadPogress(long uploadedBytes, long totalBytes) {
        progressBar.setProgress((int) (((double) uploadedBytes) / totalBytes * 100.0));
        Log.e(TAG, "Percentage: " + (int) (((double) uploadedBytes)/ totalBytes * 100.0));
    }

    @Override
    public void onAssemblyFinished(AssemblyResponse response) {
        try {
            String res = response.json().getString("ok");
            Log.e(TAG , "Your AndroidAsyncAssembly is done executing with status: " + res);
            if (res.equalsIgnoreCase("ASSEMBLY_COMPLETED")){
                successVideoUpload.set(true);
                getDownloadURL();
            }else {
                successVideoUpload.set(false);
                retryUploadBtn.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUploadFailed(Exception exception) {
        successVideoUpload.set(false);
        retryUploadBtn.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), "Upload Failed. Please Try Again.",Toast.LENGTH_LONG).show();
        Log.e(TAG , "Upload Failed: " + exception.getMessage());
    }

    @Override
    public void onAssemblyStatusUpdateFailed(Exception exception) {
        successVideoUpload.set(false);
        retryUploadBtn.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), "Upload Failed. Please Try Again.",Toast.LENGTH_LONG).show();
        Log.e(TAG , "Assembly Status Update Failed: " + exception.getMessage());
        exception.printStackTrace();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
}
