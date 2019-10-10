package com.example.fitnessappactions.Home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessappactions.Model.FitActivity;
import com.example.fitnessappactions.Model.FitRepository;
import com.example.fitnessappactions.Model.FitStats;
import com.example.fitnessappactions.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class FitStatsFragment extends Fragment {

     FitStatsActions actionsCallback;//action call back for this fragment
     RecyclerView statsList; //recyclerview to show the stats
     FloatingActionButton statsStartButton; //FAB to stats the activity
    TextView statsActivityCount, statsDistanceCount,statsDurationCount;

    //on fragment created inflate the view with the fragment layout resource file
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
         return inflater.inflate(R.layout.fit_stats_fragment, container,false);
    }
//on attach of fragment , when fragments attaches to activity, pass the activity associated with fragment to the callback object.
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        actionsCallback = (FitStatsFragment.FitStatsActions)getActivity();
    }

    //initialize the buttons and adapter
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        statsList = view.findViewById(R.id.statsList);
        statsStartButton = view.findViewById(R.id.statsStartButton);
        statsActivityCount = view.findViewById(R.id.statsActivityCount);
        statsDistanceCount = view.findViewById(R.id.statsDistanceCount);
        statsDurationCount = view.findViewById(R.id.statsDurationCount);
        final FitStatsAdapter adapter = new FitStatsAdapter(new DiffUtil.ItemCallback<FitActivity>() {
            @Override
            public boolean areItemsTheSame(@NonNull FitActivity oldItem, @NonNull FitActivity newItem) {
                return false;
            }

            @Override
            public boolean areContentsTheSame(@NonNull FitActivity oldItem, @NonNull FitActivity newItem) {
                return false;
            }
        });
        statsList.setAdapter(adapter);

        FitRepository repository = FitRepository.getInstance(getContext());//repository object
        //add observer to the getstats method and for every change ni the method
        repository.getStats().observe(getViewLifecycleOwner(),
                new Observer<FitStats>() {
                    @Override
                    public void onChanged(FitStats fitStats) {
                        String ActivityCount = getString(
                                R.string.stats_total_count,
                                fitStats.totalCount);
                        statsActivityCount.setText(ActivityCount);

                        String DistanceCount = getString(
                                R.string.stats_total_distance,
                                (int)fitStats.totalDistanceMeters);
                        statsDistanceCount.setText(DistanceCount);

                        long durationInMin = TimeUnit.MILLISECONDS.toMinutes(fitStats.totalDurationMs);
                        String DurationCount = getString(R.string.stats_total_duration, durationInMin);
                        statsDurationCount.setText(DurationCount);
                    }
                });


//add observer to the getLast activities method and for every change call submit list method to update the maxDuration
            repository.getLastActivities(10,null).observe(getViewLifecycleOwner(),
                    new Observer<List<FitActivity>>() {
                        @Override
                        public void onChanged(List<FitActivity> fitActivities) {
                         adapter.submitList(fitActivities);
                         statsList.smoothScrollToPosition(0);
                        }
                    });
//on click of the FAB, call the start activity method of the callback interface
statsStartButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
actionsCallback.onStartActivity();
    }
});
    }
   public interface FitStatsActions {
        void onStartActivity();
    }
    }




