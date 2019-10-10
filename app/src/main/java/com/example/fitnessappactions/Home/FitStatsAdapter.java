package com.example.fitnessappactions.Home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitnessappactions.Model.FitActivity;
import com.example.fitnessappactions.R;

import java.util.*;
import java.util.concurrent.TimeUnit;

//FitstatsAdapter extends listadpater with the Myviewholder object
public class FitStatsAdapter extends ListAdapter<FitActivity, FitStatsAdapter.MyViewHolder> {
    //variable to store the max duration value
   private Double maxDuration = 7.0;

   //constructor for the adapter of type difference Callback
    protected FitStatsAdapter(@NonNull DiffUtil.ItemCallback<FitActivity> diffCallback) {
        super(diffCallback);
    }
    //object of the call back
public static final DiffUtil.ItemCallback<FitActivity> DIFF_CALLBACK = new DiffUtil.ItemCallback<FitActivity>() {
    @Override
    public boolean areItemsTheSame(@NonNull FitActivity oldItem, @NonNull FitActivity newItem) {
        return oldItem.id .equals(newItem.id);
    }

    @Override
    public boolean areContentsTheSame(@NonNull FitActivity oldItem, @NonNull FitActivity newItem) {
        return oldItem.id .equals(newItem.id);
    }
};
//method in which for each fit acvtivity, we compare the variable maxDuration and get the maximum value of the distanceMeters
    @Override
    public void submitList(@Nullable List<FitActivity> list) {
        if(list != null){
        //    list.forEach(list.get(),(maxDuration = FitActivity.distanceMeters));
for(FitActivity fitActivity : list){
    maxDuration = maxDuration + fitActivity.distanceMeters;
}
            super.submitList(list);
        }

    }
//inflate the view holder
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fit_stats_row,parent,false));
    }
//bind the view holder
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
holder.bind(getItem(position),maxDuration.intValue());
    }

//class extending recyclerview.viewholder
    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView statsRowTitle, statsRowContent;
        ProgressBar rowProgress;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(FitActivity activity, int max){
Context context = itemView.getContext();
Calendar cal =Calendar.getInstance();
long date = cal.getTimeInMillis() ;
        date = activity.date;

        String day = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG,Locale.getDefault());
        statsRowTitle = itemView.findViewById(R.id.statsRowTitle);
statsRowTitle.setText(context.getString(R.string.stat_date,
        day,
        cal.get(Calendar.DAY_OF_MONTH),
        cal.get(Calendar.MONTH)));

            long minutes = TimeUnit.MILLISECONDS.toMinutes(activity.durationMs);
            String km = String.format("%.2f", activity.distanceMeters / 1000);
            String duration = context.getString(R.string.stat_duration, minutes);
            String distance = context.getString(R.string.stat_distance, km);

            statsRowContent = itemView.findViewById(R.id.statsRowContent);
            String row = duration + "\n" + distance;
            statsRowContent.setText(row);

            rowProgress = itemView.findViewById(R.id.statsRowProgress);

            rowProgress.setMax(max);
            Log.d("myapp","max"+ max);
            rowProgress.setProgress((int) activity.durationMs);
            Log.d("myapp","durationMS"+ activity.durationMs);

                }
    }

}
