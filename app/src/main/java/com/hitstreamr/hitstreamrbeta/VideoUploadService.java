package com.hitstreamr.hitstreamrbeta;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VideoUploadService extends Service {
    public VideoUploadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
