package com.example.fitnessappactions.Slices;

import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;

import com.example.fitnessappactions.MainActivity;
import com.example.fitnessappactions.R;


/**
 * Base class that defines a Slice for the app.
 *
 * Every slice implementation should extend this class and implement getSlice method.
 */
public abstract class FitSlice {
    Uri sliceUri;
    Context context;

    public FitSlice(Uri uri, Context context) {
        this.sliceUri = uri;
        this.context = context;
    }

    /**
     * Main thread handler to execute tasks that requires Android Main Thread
     */
   protected Handler handler = new Handler(Looper.getMainLooper());

    /**
     * @return the specific slice implementation to be used by SliceProvider
     */
abstract Slice getSlice();

    /**
     * Call refresh to notify the SliceProvider to load again.
     */
void refresh(){
context.getContentResolver().notifyChange(sliceUri,null);
}

    /**
     * Utility method to create a SliceAction that launches the main activity.
     */

    SliceAction createActivityAction(){
Intent i = new Intent(context, MainActivity.class);
return SliceAction.create(
        PendingIntent.getActivity(context,0,i,0),
        IconCompat.createWithResource(context, R.mipmap.ic_launcher),
        ListBuilder.SMALL_IMAGE,context.getString(R.string.slice_enter_app_hint));
    }
    /**
     * Default implementation of FitSlice when the uri could not be handled.
     */
  public static class fitSliceDefault extends FitSlice{

        public fitSliceDefault(Uri uri, Context context) {
            super(uri, context);
        }

        @Override
        Slice getSlice() {
            return new ListBuilder(context,sliceUri,ListBuilder.INFINITY)
                    .addRow(new ListBuilder.RowBuilder().setPrimaryAction(createActivityAction())
                            .setTitle(context.getString(R.string.slice_uri_not_found))).build();           }
        // Mark the slice as error type slice.

        }
    }

