package com.example.fitnessappactions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.SearchManager;
import android.app.assist.AssistContent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.fitnessappactions.Home.FitStatsFragment;
import com.example.fitnessappactions.Model.FitActivity;
import com.example.fitnessappactions.Model.FitRepository;
import com.example.fitnessappactions.tracking.FitTrackingFragment;
import com.example.fitnessappactions.tracking.FitTrackingService;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements FitTrackingFragment.FitTrackingActions, FitStatsFragment.FitStatsActions{
boolean actionHandled;
FitTrackingFragment.FitTrackingActions actionCallBackTrack;
FitStatsFragment.FitStatsActions actionCallBackStats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the action and data from the intent to handle it.
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        if(action != null){
            switch (action){
                // When the action is triggered by a deep-link, Intent.Action_VIEW will be used
                case Intent.ACTION_VIEW:
                    handleDeepLink(data);
                    // When the action is triggered by the Google search action, the ACTION_SEARCH will be used
                case Intent.ACTION_SEARCH:
                    handleDeepLink(Uri.parse(intent.getStringExtra(SearchManager.QUERY)));
                    // Otherwise start the app as you would normally do.
                    default:
showDefaultView();
            }
        }
    }
    /**
     * When a fragment is attached add the required callback methods.
     */
    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof FitStatsFragment) {
            actionCallBackStats= this;
          //  ((FitStatsFragment)fragment).setActionsCallback((FitStatsFragment.FitStatsActions)this);
        } else if (fragment instanceof FitTrackingFragment) {
          //  ((FitTrackingFragment)fragment).setActionsCallback((FitTrackingFragment.FitTrackingActions)this);
            actionCallBackTrack = this;
        }

    }



    /**
     * When the user invokes an App Action while in your app, users will see a suggestion
     * to share their foreground content.
     *
     * By implementing onProvideAssistContent(), you provide the Assistant with structured information
     * about the current foreground content.
     *
     * This contextual information enables the Assistant to continue being helpful after the user enters your app.
     */
    @Override
    public void onProvideAssistContent(AssistContent outContent) {
        super.onProvideAssistContent(outContent);

        // JSON-LD object based on Schema.org structured data
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // This is just an example, more accurate information should be provided
            try {
                outContent.setStructuredData(new JSONObject().put("@type", "ExerciseObservation")
                        .put("name", "My last runs")
                        .put("url", "https://fit-actions.firebaseapp.com/stats")
                        .toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    private void handleDeepLink(Uri data) {
        // path is normally used to indicate which view should be displayed
        // i.e https://fit-actions.firebaseapp.com/start?exerciseType="Running" -> "start" will be the path
        actionHandled = true;
String path = data.getPath();

switch(path){
    case "/stop" :
stopService(new Intent(this,FitTrackingService.class));

break;

    case "/start" :
       String exerciseType = data.getQueryParameter("exerciseType");
       FitActivity.Type type = FitActivity.Type.valueOf(exerciseType);
       Bundle arguments = new Bundle();
       arguments.putSerializable(FitTrackingFragment.PARAM_TYPE,type);
       updateView(FitTrackingFragment.class, arguments,false);
break;
default:
    showDefaultView();
    actionHandled = false;
}
notifyActionSuccess(actionHandled);
    }

    /**
     * In the event where Google Assistant is confident that the user wishes to access your app via the Assistant,
     * but is unable to resolve the query to a built-in intent, a search intent will be sent to the app.
     *
     * Handle the given query
     */
    private void handleSearchIntent(String searchQuery) {
        // The app does not have a search functionality, we could parse the search query, but the normal use case would
        // would be to use the query in a search box. For this sample we just show the home screen
        showDefaultView();
    }

    /**
     * Log a success or failure of the received action based on if your app could handle the action
     *
     * Required to help giving Assistant visibility over success or failure of an action sent to the app.
     * Otherwise, it can’t confidently send user’s to your app for fulfillment.
     */
    private boolean notifyActionSuccess(boolean succeed) {
        /*
        ("ConstantConditionIf")
        if (!BuildConfig.FIREBASE_ENABLED) {
            return;
        }

        intent.getStringExtra("actions.fulfillment.extra.ACTION_TOKEN")?.let { actionToken ->
                val actionStatus = if (succeed) {
            Action.Builder.STATUS_TYPE_COMPLETED
        } else {
            Action.Builder.STATUS_TYPE_FAILED
        }
            val action = AssistActionBuilder()
                    .setActionToken(actionToken)
                    .setActionStatus(actionStatus)
                    .build()

            // Send the end action to the Firebase app indexing.
            FirebaseUserActions.getInstance().end(action)
        }
        */
        return true;
    }

    /**
     * Show ongoing activity or stats if none
     */
    private void showDefaultView() {
        Class fragmentClass;
                if(FitRepository.getInstance(this).getOnGoingActivity().getValue() !=null){
     fragmentClass = FitTrackingFragment.class;

           // updateView(defaultActivity,null,false);

        } else {
        fragmentClass = FitStatsFragment.class;

        }
        updateView(fragmentClass,null,false);
    }
    /**
     * Utility method to update the Fragment with the given arguments.
     */
    private void updateView(Class fragmentClass, Bundle arguments, boolean toBackStack) {
        String backStack = null;
        if (toBackStack) {
            backStack = null;
        }

        Fragment currentFragment = getSupportFragmentManager().getFragmentFactory().instantiate(
               fragmentClass.getClassLoader(),fragmentClass.getName());
        currentFragment.setArguments(arguments);

getSupportFragmentManager().beginTransaction().replace(R.id.fitActivityContainer, currentFragment)
        .addToBackStack(backStack).commit();

          }
    /**
     * Callback method from the FitStatsFragment to indicate that the tracking activity flow should be shown.
     */
    @Override
    public void onStartActivity() {
        Bundle arguments = new Bundle();
                arguments.putSerializable(FitTrackingFragment.PARAM_TYPE,FitActivity.Type.RUNNING);
        updateView(FitTrackingFragment.class,arguments,true);
    }

    /**
     * Callback method when an activity stops.
     * We could show a details screen, for now just go back to home screen.
     */
    @Override
    public void onActivityStopped(String activityId) {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            updateView(FitStatsFragment.class,null,false);
        }
    }
}
