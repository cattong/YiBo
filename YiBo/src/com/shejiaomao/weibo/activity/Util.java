package com.shejiaomao.weibo.activity;

import android.app.ProgressDialog;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Collection of utility functions used in this package.
 */
public class Util {

    private static OnClickListener sNullOnClickListener;

    private Util() {
    }

    public static void debugWhere(String tag, String msg) {
        Log.d(tag, msg + " --- stack trace begins: ");
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        // skip first 3 element, they are not related to the caller
        for (int i = 3, n = elements.length; i < n; ++i) {
            StackTraceElement st = elements[i];
            String message = String.format("    at %s.%s(%s:%s)",
                    st.getClassName(), st.getMethodName(), st.getFileName(),
                    st.getLineNumber());
            Log.d(tag, message);
        }
        Log.d(tag, msg + " --- stack trace ends.");
    }

    public static synchronized OnClickListener getNullOnClickListener() {
        if (sNullOnClickListener == null) {
            sNullOnClickListener = new OnClickListener() {
                public void onClick(View v) {
                }
            };
        }
        return sNullOnClickListener;
    }

    private static class BackgroundJob
            extends MonitoredActivity.LifeCycleAdapter implements Runnable {

        private final MonitoredActivity mActivity;
        private final ProgressDialog mDialog;
        private final Runnable mJob;
        private final Handler mHandler;
        private final Runnable mCleanupRunner = new Runnable() {
            public void run() {
                mActivity.removeLifeCycleListener(BackgroundJob.this);
                if (mDialog.getWindow() != null) mDialog.dismiss();
            }
        };

        public BackgroundJob(MonitoredActivity activity, Runnable job,
                ProgressDialog dialog, Handler handler) {
            mActivity = activity;
            mDialog = dialog;
            mJob = job;
            mActivity.addLifeCycleListener(this);
            mHandler = handler;
        }

        public void run() {
            try {
                mJob.run();
            } finally {
                mHandler.post(mCleanupRunner);
            }
        }


        @Override
        public void onActivityDestroyed(MonitoredActivity activity) {
            // We get here only when the onDestroyed being called before
            // the mCleanupRunner. So, run it now and remove it from the queue
            mCleanupRunner.run();
            mHandler.removeCallbacks(mCleanupRunner);
        }

        @Override
        public void onActivityStopped(MonitoredActivity activity) {
            mDialog.hide();
        }

        @Override
        public void onActivityStarted(MonitoredActivity activity) {
            mDialog.show();
        }
    }

    public static void startBackgroundJob(MonitoredActivity activity,
            String title, String message, Runnable job, Handler handler) {
        // Make the progress dialog uncancelable, so that we can gurantee
        // the thread will be done before the activity getting destroyed.
        ProgressDialog dialog = ProgressDialog.show(
                activity, title, message, true, false);
        new Thread(new BackgroundJob(activity, job, dialog, handler)).start();
    }

}
