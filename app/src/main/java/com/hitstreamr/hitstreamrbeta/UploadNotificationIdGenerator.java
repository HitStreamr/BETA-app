package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.atomic.AtomicInteger;

public class UploadNotificationIdGenerator {
    private static AtomicInteger id = new AtomicInteger(0);
    private static final String PREFERENCE_LAST_UPLOAD_NOTIF_ID = "LAST_UPLOAD_NOTIF_ID";

    public static int getID(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        id.set(sharedPreferences.getInt(PREFERENCE_LAST_UPLOAD_NOTIF_ID, 0));
        int temp = id.incrementAndGet();
        if (temp == Integer.MAX_VALUE) { id.set(0); }
        sharedPreferences.edit().putInt(PREFERENCE_LAST_UPLOAD_NOTIF_ID, id.get()).apply();
        return id.get();
    }
}