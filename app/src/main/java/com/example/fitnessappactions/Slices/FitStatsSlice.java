package com.example.fitnessappactions.Slices;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.slice.Slice;
import androidx.slice.builders.GridRowBuilder;
import androidx.slice.builders.ListBuilder;

import com.example.fitnessappactions.Model.FitActivity;
import com.example.fitnessappactions.Model.FitRepository;
import com.example.fitnessappactions.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class FitStatsSlice extends FitSlice{
    Context context;
    Uri sliceUri;
    FitRepository fitRepo;
    FitActivity.Type activityType;
    LiveData<List<FitActivity>> lastActivities;
    Observer<List<FitActivity>> observer;


    public FitStatsSlice(Context context, Uri sliceUri, FitRepository fitRepo) {
        super(sliceUri, context);
        this.context = context;
        this.sliceUri = sliceUri;
        this.fitRepo = fitRepo;
        /**
         * Get the activity type from the uri and map it to our enum types.
         */
        activityType = FitActivity.Type.valueOf(sliceUri.getQueryParameter("exerciseType"));

        /**
         * Observer that will refresh the slice once data is available
         */
 observer = new Observer<List<FitActivity>>() {
    @Override
    public void onChanged(List<FitActivity> fitActivities) {
if(fitActivities !=null){
    refresh();
}
    }
};

        /**
         * Create and observe the last activities LiveData.
         */
        lastActivities = fitRepo.getLastActivities(5,activityType);
        handler.post(new Runnable() {
            @Override
            public void run() {
                lastActivities.observeForever(observer);
            }
        });

    }

    @Override
    Slice getSlice() {
        List<FitActivity> activity = lastActivities.getValue();
        if (activity != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
lastActivities.removeObserver(observer);
                }
            });
           return createStatsSlice(activity);
        }else
        return createLoadingSlice();
    }
    /**
     * Simple loading Slice while the DB is still loading the last activities.
     */
    private Slice createLoadingSlice() {
       return new ListBuilder(context,sliceUri,ListBuilder.INFINITY).addRow(new ListBuilder.RowBuilder()
                .setTitle( context.getString(R.string.slice_stats_loading,activityType.name()))).build();
    }
    /**
     * Create the stats slices showing the data provided by the DB.
     */
    private Slice createStatsSlice(List<FitActivity> activity) {
        String subTitle;

        if (activity.isEmpty())
            subTitle = context.getString(R.string.slice_stats_subtitle_no_data);
            else
                subTitle = context.getString(R.string.slice_stats_subtitle);
            GridRowBuilder gridRowBuilder = new GridRowBuilder();
            //fer each activity,build a cell with the fit activity data
            for (FitActivity fitActivity : activity){
                String distKm =  String.format("%.2f", fitActivity.distanceMeters / 1000);
                String distance = context.getString(R.string.slice_stats_distance, distKm);
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(fitActivity.date);
             gridRowBuilder.addCell(new GridRowBuilder.CellBuilder().addText(distance)
             .addTitleText(cal.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG, Locale.getDefault())));
            }

        ListBuilder builder = new ListBuilder(context,sliceUri,ListBuilder.INFINITY).setHeader(new ListBuilder.HeaderBuilder()
               .setTitle(context.getString(R.string.slice_stats_title, activityType.name()))
        .setSubtitle(subTitle))
                .addGridRow(gridRowBuilder)
                .addAction(createActivityAction());

        return builder.build();


    }

}
