package com.example.fitnessappactions.tracking;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.fitnessappactions.MainActivity;
import com.example.fitnessappactions.Model.FitActivity;
import com.example.fitnessappactions.Model.FitRepository;
import com.example.fitnessappactions.R;

import java.util.concurrent.TimeUnit;

/**
 * Fragment that handles the starting of an activity and tracks the status.
 *
 * When the fragments starts, it will start a countdown and launch the foreground service
 * that will keep track of the status.
 *
 * The view will observe the status and update its content.
 */
public class FitTrackingFragment extends Fragment {

    ImageButton startActivityButton;
    TextView startActivityCountDown, startActivityTitle;
    Context mcontext;
    Intent fitServiceIntent;
   public static String PARAM_TYPE = "type";
    FitTrackingActions actionCallBack;
    CountDownTimer countDownTimer;
    CountDownTimer timer;
    long countDownMs = TimeUnit.SECONDS.toMillis(5);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fit_tracking_fragment,container,false);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mcontext = context;
        actionCallBack = (FitTrackingActions) getActivity();
    }

    FitRepository fitRepository = FitRepository.getInstance(mcontext);


  //  private long countDownMs = TimeUnit.SECONDS.toMillis(5);
    // CountDownTimer countDownTimer;
/*
    CountDownTimer countDownTimer = new CountDownTimer(countDownMs,1000) {
        @Override
        public void onTick(long l) {
            //keep track of remaining count
countDownMs = l;
String secondsLeft = String.valueOf((TimeUnit.MILLISECONDS.toSeconds(l)));
            startActivityCountDown.setText(secondsLeft);
        }

        @Override
        public void onFinish() {
            // Countdown finished, start tracking service
            countDownMs =0;
            startTrackingService();

        }
    };*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startActivityButton = view.findViewById(R.id.startActivityButton);
        startActivityCountDown = view.findViewById(R.id.startActivityCountDown);
        startActivityTitle = view.findViewById(R.id.startActivityTitle);
        fitServiceIntent = new Intent(mcontext,FitTrackingService.class);
       // timer = new countDownTimer(countDownMs,1000);

        startActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FitActivity fitActivity = fitRepository.getOnGoingActivity().getValue();
                if(fitActivity ==null) {
                    startTimer();
                  // timer.start();
                    onCountDown();
             //     startTrackingService();
                }else{
mcontext.stopService(fitServiceIntent);
actionCallBack.onActivityStopped(fitActivity.id);
                }
            }
        });

fitRepository.getOnGoingActivity().observe(this, new Observer<FitActivity>() {
    @Override
    public void onChanged(FitActivity fitActivity) {
if (fitActivity == null){//meaning fresh activity is going to start in that case start the timer and start the activity
if(countDownMs > 0){
//timer.start();
    startTimer();
onCountDown();
}else {
onTracking(fitActivity);//keep tracking the activity
}
}
    }
});
    }

    private void startTimer() {
       timer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                countDownMs = l;
                String secondsLeft = String.valueOf((TimeUnit.MILLISECONDS.toSeconds(l)));
                startActivityCountDown.setText(secondsLeft);
            }

            @Override
            public void onFinish() {
                // Countdown finished, start tracking service
                countDownMs =0;
                startTrackingService();
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        countDownTimer.cancel();//stop the count down timer
        super.onDestroyView();
    }

    /**
     * Stop the countdown if running, and start a foreground service
     */
    private void startTrackingService() {
        countDownMs = 0;
        timer.cancel();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(fitServiceIntent != null) {
                requireActivity().startService(fitServiceIntent);//fire the intent to start the service in foreground
            }
            else
                Log.d("myapp","trackservice intent is null");
        } else {
            requireActivity().startService(fitServiceIntent);//start the service via intent
        }
    }

    /**
     * Update the count down view
     */
    private void onCountDown() {
        FitActivity.Type type = (FitActivity.Type) getArguments().getSerializable(PARAM_TYPE);
        if (type == null) {
            type = FitActivity.Type.UNKNOWN;}
            startActivityButton.setSelected(false);
            String name = type.name().toLowerCase();
            startActivityTitle.setText(getString(R.string.start_activity_title) + (name ));
            String countDownText = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(countDownMs));
            startActivityCountDown.setText(countDownText);

    }

    /**
     * Update the tracking view
     */
    private void onTracking( FitActivity activity) {
        startActivityTitle.setText(R.string.tracking_notification_title);
        String countDownText = getString(
                R.string.stats_tracking_distance,
                activity.distanceMeters);
        startActivityCountDown.setText(countDownText);


        startActivityButton.setSelected(true);
    }

  public  interface FitTrackingActions {
        /**
         * Called when the activity has stopped.
         */
      void onActivityStopped( String activityId);
    }
/*
   public class countDownTimer extends CountDownTimer{

        public countDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            countDownMs = l;
            String secondsLeft = String.valueOf((TimeUnit.MILLISECONDS.toSeconds(l)));
            startActivityCountDown.setText(secondsLeft);

        }

        @Override
        public void onFinish() {
            // Countdown finished, start tracking service
            countDownMs =0;
            startTrackingService();

        }
    }
    */
}
