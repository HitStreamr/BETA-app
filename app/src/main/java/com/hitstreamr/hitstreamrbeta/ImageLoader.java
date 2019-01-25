package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoader extends AsyncTask<String, Void, Bitmap> {
    public AsyncResponse delegate = null;

    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(Bitmap output);
    }

    public ImageLoader(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        System.out.println("doInBackground");

        Bitmap bitmap = null;

        bitmap = downloadImage(params[0]);

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        delegate.processFinish(bitmap);
    }

    private Bitmap downloadImage(String url) {

        Bitmap bmp = null;
        try{
            URL ulrn = new URL(url);
            HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
            InputStream is = con.getInputStream();
            bmp = BitmapFactory.decodeStream(is);
            if (null != bmp)
                return bmp;

        }catch(Exception e){}
        return bmp;
    }


}
