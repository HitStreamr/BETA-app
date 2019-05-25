package com.hitstreamr.hitstreamrbeta;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoUploadService extends Service{
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
    private AtomicBoolean downloadURISet;
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
        downloadURISet = new AtomicBoolean(false);
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
            Log.i(TAG, "Received start id " + startId + ": " + intent);
            startID = startId;
            return START_STICKY;
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        createNotificationChannel();
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = "Preparing to Upload Your Video...";
        notifID = UploadNotificationIdGenerator.getID(this);

        // Set the info for the views that show in the notification panel.
        notifB = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.new_hitstreamr_h_logo_wht_w_)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("VIDEO UPLOAD")  // the label of the entry
                .setContentText(text)
                .setOnlyAlertOnce(true)
                .setOngoing(true);// the contents of the entry

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

    @SuppressLint("RestrictedApi")
    private void registerFirebase() {
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
                            stopForeground(true);
                            stopSelf(startID);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            successFirestoreUpload.set(false);
                            deleteFail();
                        }
                    });
        }

    private void deleteFail(){
        if(successVideoUpload.get() && (!downloadURISet.get() || !successFirestoreUpload.get())){
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

    public void uploadVideo(final Uri fileUri, String title){
        cancelAction();
        notifB.setContentTitle("Storing Video")
                .setContentText("Storing Video")
                .setProgress(0,0,true);
        mNM.notify(notifID, notifB.build());

        if(mVUC != null){
            mVUC.goBack();
        }

        final StorageReference videoRef = FirebaseStorage.getInstance().getReference().child("videos").child(currentFirebaseUser.getUid()).child("mp4").child(title);

        videoRef.putFile(fileUri)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        showProgress(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount());
                    }
                })
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        // Forward any exceptions
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        //Log.d(TAG, "uploadFromUri: upload success");

                        // Request the public download URL
                        return videoRef.getDownloadUrl();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(@NonNull Uri downloadUri) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri: getDownloadUri success");
                        downloadVideoURI = downloadUri.toString();
                        downloadURISet.set(true);
                        registerFirebase();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);
                        deleteFail();
                    }
                });
    }

    public void showProgress(long uploadedBytes, long totalBytes) {
            int PROGRESS_CURRENT = (int) (((double) uploadedBytes) / totalBytes * 100.0);
            notifB.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            mNM.notify(notifID, notifB.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
}
