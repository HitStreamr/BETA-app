package com.hitstreamr.hitstreamrbeta;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hitstreamr.hitstreamrbeta.Authentication.Splash;
import com.transloadit.android.sdk.AndroidAsyncAssembly;
import com.transloadit.android.sdk.AndroidTransloadit;
import com.transloadit.sdk.async.AssemblyProgressListener;
import com.transloadit.sdk.response.AssemblyResponse;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoUploadService extends Service implements AssemblyProgressListener {
    private static final String TAG = "Video Upload Service";
    private static final String CHANNEL_ID = "VIDEO UPLOAD NOTIFICATION";
    private final IBinder mBinder = new VideoUploadService.LocalBinder();
    private int startID;
    private NotificationManager mNM;
    private Notification notification;
    private NotificationCompat.Builder notifB;
    private int notifID;
    private VideoUploadCallback mVUC;
    // Issue the initial notification with zero progress
    private final int PROGRESS_MAX = 100;
    private final int PROGRESS_START = 0;
    private static int SPLASH_TIME_OUT = 3000;

    private AtomicBoolean successVideoUpload;
    private AtomicBoolean successFirestoreUpload;
    private AtomicBoolean downloadURI;
    private boolean cancel;
    private String storageTitle;

    //Firestore Database
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference database;
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    //Firebase Storage
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference mStorageRef;
    private StorageReference videoRef;
    private String downloadVideoURI;
    private Map<String, Object> artistVideo;

    private static final String VIDEO_DOWNLOAD_LINK = "url";

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

    public void setCallbacks(VideoUploadCallback vupc){
        mVUC = vupc;
    }


    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        successVideoUpload = new AtomicBoolean(false);
        successFirestoreUpload = new AtomicBoolean(false);
        downloadURI = new AtomicBoolean(false);
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    public void deleteBeforeWork(){
        if (mVUC != null){
            mVUC.unbindUploadService();
        }
        stopForeground(true);
        stopSelf(startID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getBooleanExtra("CANCEL", false)){
            startID = startId;
            cancel = true;
            deleteFail();
            notifB.setContentTitle("Canceling Upload")
                    .setContentText("Canceling Upload")
                    .setProgress(0,0,true);
            mNM.notify(intent.getIntExtra("NOTIF", 0), notifB.build());
            return super.onStartCommand(intent,flags,startId);
        }else{
            Log.i(TAG, "Received start id " + startId + ": " + intent);
            startID = startId;
            return START_STICKY;
        }

    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        createNotificationChannel();
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Preparing to Upload Your Video...";
        notifID = UploadNotificationIdGenerator.getID(this);
        /*// The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Splash.class), 0);*/


        // Set the info for the views that show in the notification panel.
        notifB = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.new_hitstreamr_h_logo_wht_w_)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("VIDEO UPLOAD")  // the label of the entry
                .setContentText(text)
                .setOnlyAlertOnce(true)
                .setOngoing(true);// the contents of the entry
                //.addAction(act);
                //.setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                //.setProgress(PROGRESS_MAX, PROGRESS_START, false);

        notification = notifB.build();

        // Send the notification.
        //mNM.notify(notifID, notification);
        startForeground(notifID, notification);
    }

    private void cancelAction(){
        Intent cancel = new Intent(this, VideoUploadService.class);
        cancel.putExtra("CANCEL", true);
        cancel.putExtra("NOTIF", notifID);
        PendingIntent cancelUploadIntent = PendingIntent.getBroadcast(this, 0, cancel, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Action act = new NotificationCompat.Action(R.drawable.exo_icon_stop, "Cancel Upload", cancelUploadIntent);
        notifB.addAction(act);
        mNM.notify(notifID, notifB.build());
    }

    public void setMap(Map<String, Object> artistVid){
        artistVideo = artistVid;
    }

    public void setTitle(String title){
        storageTitle = title;
    }

    private void getDownloadURL(){

        mStorageRef = storage.getReference();
        videoRef = mStorageRef.child("videos").child(currentFirebaseUser.getUid()).child("mp4").child(storageTitle);
        videoRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadVideoURI = uri.toString();
                        downloadURI.set(true);
                        registerFirebase();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                downloadURI.set(false);
                deleteFail();
                //Toast.makeText(getApplicationContext(), "Something went wrong. Please try again.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void registerFirebase() {
        if (!cancel) {
            notifB.mActions.clear();
            mNM.notify(notifID, notifB.build());
            artistVideo.put(VIDEO_DOWNLOAD_LINK, downloadVideoURI);
            DocumentReference intermediate = db.collection("Videos").document();
                    intermediate.set(artistVideo, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            successFirestoreUpload.set(true);
                            Toast.makeText(VideoUploadService.this, "Video Uploaded successfully", Toast.LENGTH_SHORT).show();
                            if (mVUC != null){
                                mVUC.unbindUploadService();
                            }
                            stopSelf(startID);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            successFirestoreUpload.set(false);
                            deleteFail();
                            //Toast.makeText(VideoUploadService.this, "Video not uploaded, please try again", Toast.LENGTH_SHORT).show();
                            //Delete storage refs and firestore ref
                        }
                    });
        }
    }

    private void deleteFail(){
        if(successVideoUpload.get() && (!downloadURI.get() || !successFirestoreUpload.get())){
            //Assembly Completed Successfully
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference to the file to delete
            String storage = "videos/" + currentFirebaseUser.getUid() + "/mp4" + "/" + storageTitle;
            String original = "videos/" + currentFirebaseUser.getUid() + "/original" + "/" + storageTitle;
            StorageReference videoOriginalRef = storageRef.child(original);
            StorageReference videoMPRef = storageRef.child(storage);

            // Delete the original file
            videoOriginalRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Log.e("TAG", exception.toString());
                }
            });

            // Delete the mp4 file
            videoMPRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Log.e("TAG", exception.toString());
                }
            });
        }

        Toast.makeText(VideoUploadService.this, "Video not uploaded, please try again", Toast.LENGTH_SHORT).show();


        if (mVUC != null){
            mVUC.unbindUploadService();
        }

        stopForeground(true);
        stopSelf(startID);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "VIDEO UPLOAD NOTIFICATIONS";
            String description = "NOTIFICATIONS FOR THE VIDEO UPLOAD";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //TRANSLOADIT IMPLEMENTATION

    @Override
    public void onUploadFinished() {
        Log.e(TAG, "Your AndroidAsyncAssembly Upload is done and it's now executing");
        cancelAction();
        notifB.setContentTitle("Storing Video")
                .setContentText("Storing Video")
                .setProgress(0,0,true);
        mNM.notify(notifID, notifB.build());
    }

    @Override
    public void onUploadPogress(long uploadedBytes, long totalBytes) {
        if (cancel){
            notifB.setContentTitle("Preparing to Cancel Upload")
                    .setContentText("Preparing to Cancel Upload");
            notifB.mActions.clear();
            mNM.notify(notifID, notifB.build());
        }else {
            int PROGRESS_CURRENT = (int) (((double) uploadedBytes) / totalBytes * 100.0);
            notifB.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            mNM.notify(notifID, notifB.build());
        }
    }

    @Override
    public void onAssemblyFinished(AssemblyResponse response) {
        try {
            String res = response.json().getString("ok");
            Log.e(TAG , "Your AndroidAsyncAssembly is done executing with status: " + res);
            if (res.equalsIgnoreCase("ASSEMBLY_COMPLETED")){
                if (cancel){
                    notifB.setContentTitle("Preparing to Cancel Upload")
                            .setContentText("Preparing to Cancel Upload");
                    notifB.mActions.clear();
                    mNM.notify(notifID, notifB.build());
                    deleteFail();
                }else{
                    successVideoUpload.set(true);
                    getDownloadURL();
                }
            }else {
                successVideoUpload.set(false);
            }
        } catch (JSONException e) {
            successVideoUpload.set(false);
            e.printStackTrace();
        }
    }

    @Override
    public void onUploadFailed(Exception exception) {
        successVideoUpload.set(false);
        notifB.setContentTitle("Upload Failed")
                .setContentText("Upload Failed");
        notifB.mActions.clear();
        mNM.notify(notifID, notifB.build());
        deleteFail();
        Toast.makeText(getApplicationContext(), "Upload Failed. Please Try Again.",Toast.LENGTH_LONG).show();
        Log.e(TAG , "Upload Failed: " + exception.getMessage());
    }

    @Override
    public void onAssemblyStatusUpdateFailed(Exception exception) {
        successVideoUpload.set(false);
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
