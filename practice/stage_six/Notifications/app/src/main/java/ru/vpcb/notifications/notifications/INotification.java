package ru.vpcb.notifications.notifications;

import com.firebase.jobdispatcher.JobParameters;

public  interface INotification {
        void onCallback(JobParameters jobParameters);
    }    