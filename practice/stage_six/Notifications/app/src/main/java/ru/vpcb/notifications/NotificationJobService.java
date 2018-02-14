package ru.vpcb.notifications;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.lang.ref.WeakReference;

import static ru.vpcb.notifications.Config.EMPTY_NOTIFICATION;
import static ru.vpcb.notifications.Config.EMPTY_NOTIFICATION_ID;
import static ru.vpcb.notifications.Config.NT_ACTION_CREATE;
import static ru.vpcb.notifications.Config.NT_BUNDLE_INTENT_NOTIFICATION_BODY;


/**
 * Exercise for course : Android Developer Nanodegree
 * Created: Vadim Voronov
 * Date: 13-Feb-18
 * Email: vadim.v.voronov@gmail.com
 */
public class NotificationJobService extends JobService implements INotification {
    private static NotificationAsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        Bundle bundle = job.getExtras();
        String s = EMPTY_NOTIFICATION;
        if (bundle != null) {
            s = job.getExtras().getString(NT_BUNDLE_INTENT_NOTIFICATION_BODY);

        }

        mBackgroundTask = new NotificationAsyncTask(this, job);
        mBackgroundTask.execute(s);


        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }

    @Override
    public void onCallback(JobParameters jobParameters) {
        jobFinished(jobParameters, false);
    }

    public static class NotificationAsyncTask extends AsyncTask<String, Void, Void> {
        private final WeakReference<Context> weakContext;
        private com.firebase.jobdispatcher.JobParameters jobParameters;

        NotificationAsyncTask(Context context, com.firebase.jobdispatcher.JobParameters jobParameters) {
            this.weakContext = new WeakReference<>(context);
            this.jobParameters = jobParameters;
        }

        @Override
        protected Void doInBackground(String... params) {
            Context context = weakContext.get();
            if (context != null) {
                NotificationUtils.executeTask(context, NT_ACTION_CREATE, params[0], EMPTY_NOTIFICATION_ID);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void o) {
                /*
                 * Once the AsyncTask is finished, the job is finished. To inform JobManager that
                 * you're done, you call jobFinished with the jobParamters that were passed to your
                 * job and a boolean representing whether the job needs to be rescheduled. This is
                 * usually if something didn't work and you want the job to try running again.
                 */
            Context context = weakContext.get();
            if (context == null) return;
            ((INotification) context).onCallback(jobParameters);

        }
    }
}
