package ru.vpcb.notifications;

import com.firebase.jobdispatcher.JobParameters;

public  interface INotification {
        void onCallback(JobParameters jobParameters);
    }    