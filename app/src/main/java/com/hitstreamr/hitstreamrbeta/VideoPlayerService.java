package com.hitstreamr.hitstreamrbeta;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class VideoPlayerService extends Service {
    //probably need to merge with Dioni
    //needs to be a started bound service
    public VideoPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
