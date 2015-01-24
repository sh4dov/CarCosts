package com.sh4dov.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ProgressIndicator extends AsyncTask<Void, Integer, Void>
        implements ProgressPointer {
    ProgressDialog mProgress;
    private Runnable job;
    private Context mContext;
    private int mProgressDialog = 0;

    public ProgressIndicator(Context context, int progressDialog, Runnable job) {
        this.mContext = context;
        this.mProgressDialog = progressDialog;
        this.job = job;
    }

    @Override
    public void onPreExecute() {
        mProgress = new ProgressDialog(mContext);
        mProgress.setMessage("Please wait...");
        if (mProgressDialog == ProgressDialog.STYLE_HORIZONTAL) {

            mProgress.setIndeterminate(false);
            mProgress.setMax(100);
            mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        mProgress.setCancelable(false);
        mProgress.show();
    }

    @Override
    public void setMax(int max) {
        mProgress.setMax(max);
    }

    @Override
    public void setProgress(int progress) {
        publishProgress(progress);
    }

    @Override
    protected Void doInBackground(Void... values) {
        try {
            job.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mProgress.dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (mProgressDialog == ProgressDialog.STYLE_HORIZONTAL) {
            mProgress.setProgress(values[0]);
        }
    }
}