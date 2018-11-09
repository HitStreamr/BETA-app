package com.hitstreamr.hitstreamrbeta;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.transloadit.android.sdk.AndroidAsyncAssembly;
import com.transloadit.sdk.exceptions.LocalOperationException;
import com.transloadit.sdk.exceptions.RequestException;
import com.transloadit.sdk.response.AssemblyResponse;


class SaveTask extends AsyncTask<Boolean, Void, AssemblyResponse> {
    private final String TAG = "SAVE_TASK";
    private VideoUploadActivity activity;
    private AndroidAsyncAssembly assembly;

    SaveTask(VideoUploadActivity activity, AndroidAsyncAssembly assembly) {
        this.activity = activity;
        this.assembly = assembly;
    }

    @Override
    protected void onPostExecute(AssemblyResponse response) {
        Toast.makeText(activity, "Your androidAsyncAssembly is running on " + response.getUrl(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCancelled(AssemblyResponse assemblyResponse) {
        super.onCancelled(assemblyResponse);
    }

    @Override
    protected AssemblyResponse doInBackground(Boolean... params) {
        try {
            return assembly.save(params[0]);
        } catch (LocalOperationException e) {
            Log.e(TAG, "Assembly Status Update Failed: " + e.getMessage());
            e.printStackTrace();
        } catch (RequestException e) {
            Log.e(TAG, "Assembly Status Update Failed: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
