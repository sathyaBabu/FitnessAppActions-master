package com.example.fitnessappactions.tracking;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Observer;
import com.example.fitnessappactions.MainActivity;
import com.example.fitnessappactions.Model.FitActivity;
import com.example.fitnessappactions.Model.FitRepository;
import com.example.fitnessappactions.R;

/**
 * Foreground Android Service that starts an activity and keep tracks of the status showing
 * a notification.
 */
public class FitTrackingService extends Service {
    public FitTrackingService() {
        //empty constructor
    }

    private int ONGOING_NOTIFICATION_ID = 999;

    private String CHANNEL_ID = "TrackingChannel";

private FitRepository fitRepository = FitRepository.getInstance(this);
    Notification.Builder notificationBuilder;

      /**
     * Observer that will update the notification with the ongoing activity status.
     */
Observer<FitActivity > trackingObserver = new Observer<FitActivity>() {
        @Override
        public void onChanged(FitActivity fitActivity) {
            String km = String.format("%.2f", fitActivity.distanceMeters / 1000);
          Notification  notificationTwo = notificationBuilder
                  .setContentText(getString(R.string.stat_distance, km))
                    .build();
            NotificationManagerCompat.from(getApplicationContext()).notify(ONGOING_NOTIFICATION_ID,notificationTwo);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        notificationBuilder = new Notification.Builder(this,CHANNEL_ID)
                .setContentIntent(PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),0))
                .setContentTitle("Keep Going!!")
                .setSmallIcon(R.drawable.ic_run);

        Notification notificationOne = notificationBuilder.build();

        startForeground(ONGOING_NOTIFICATION_ID,notificationOne);

        // Start a new activity and attach the observer
        fitRepository.startActivity();
        fitRepository.getOnGoingActivity().observeForever(trackingObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fitRepository.stopActivity();
        fitRepository.getOnGoingActivity().removeObserver(trackingObserver);
    }

    /**
     * Creates a Notification channel needed for new version of Android
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
