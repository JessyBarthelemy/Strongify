package com.jessy_barthelemy.strongify.activity;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.constant.Preference;
import com.jessy_barthelemy.strongify.database.model.WorkoutWithExercise;
import com.jessy_barthelemy.strongify.enumeration.FragmentType;
import com.jessy_barthelemy.strongify.fragment.BaseFragment;
import com.jessy_barthelemy.strongify.fragment.ExerciseListFragment;
import com.jessy_barthelemy.strongify.fragment.HistoryFragment;
import com.jessy_barthelemy.strongify.fragment.PreferenceFragment;
import com.jessy_barthelemy.strongify.fragment.WorkoutListFragment;
import com.jessy_barthelemy.strongify.fragment.execution.WorkoutExecutionFragment;
import com.jessy_barthelemy.strongify.fragment.execution.WorkoutExecutionRestFragment;
import com.jessy_barthelemy.strongify.fragment.execution.WorkoutSupersetExecutionFragment;
import com.jessy_barthelemy.strongify.fragment.profile.ProfileFragment;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.interfaces.Dismissible;
import com.jessy_barthelemy.strongify.service.ExecutionService;

import java.util.Map;
import androidx.preference.PreferenceManager;

public class HomeActivity extends AppCompatActivity{

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean(PreferenceFragment.IS_DARK_THEME_PREF_KEY, false))
            setTheme(R.style.DarkTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView menu = findViewById(R.id.navigation);
        menu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_exercise) {
                ExerciseListFragment fragmentExercise = new ExerciseListFragment();
                fragmentExercise.setPickerMode(false);
                replaceFragment(fragmentExercise, null, false);
            } else if (id == R.id.nav_settings) {
                PreferenceFragment preferenceFragment = new PreferenceFragment();
                replaceFragment(preferenceFragment, null, false);
            } else if (id == R.id.nav_history) {
                HistoryFragment historyFragment = new HistoryFragment();
                replaceFragment(historyFragment, null, false);
            } else if (id == R.id.nav_profile) {
                ProfileFragment profileFragment = new ProfileFragment();
                replaceFragment(profileFragment, null, false);
            } else {
                WorkoutListFragment fragmentWorkout = new WorkoutListFragment();
                replaceFragment(fragmentWorkout, null, false);
            }

            return true;
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null && this.getIntent().getExtras().containsKey(ExecutionService.FRAGMENT_TYPE_PARAMETER)){
            WorkoutWithExercise workout = (WorkoutWithExercise)bundle.getSerializable(ExecutionService.WORKOUT_PARAMETER);
            int fragmentToOpen = bundle.getInt(ExecutionService.FRAGMENT_TYPE_PARAMETER);

            switch (fragmentToOpen){
                case FragmentType.WORKOUT_EXECUTION:
                    WorkoutExecutionFragment executionFragment = new WorkoutExecutionFragment();
                    executionFragment.setWorkout(workout);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, executionFragment)
                            .commit();
                    break;
                case FragmentType.WORKOUT_SUPERSET:
                    WorkoutSupersetExecutionFragment supersetFragment = new WorkoutSupersetExecutionFragment();
                    supersetFragment.setWorkout(workout);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, supersetFragment)
                            .commit();
                    break;
                case FragmentType.WORKOUT_REST:
                    WorkoutExecutionRestFragment restFragment = new WorkoutExecutionRestFragment();
                    restFragment.setWorkout(workout);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, restFragment)
                            .commit();
                    break;
                case FragmentType.PARAMETER:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new PreferenceFragment())
                            .commit();
                    break;
            }
        }else{
            WorkoutListFragment fragment = WorkoutListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }

        if(ApplicationHelper.hasToShowReview(this))
            showReviewDialog();

        if(ApplicationHelper.hasToShowStartupDialog(this))
            showStartupDialog();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if (fragment instanceof BaseFragment) {
                    if (((BaseFragment) fragment).isBackPressEnabled()) {
                        if (fragment instanceof Dismissible) {
                            ((Dismissible) fragment).dismiss(new Dismissible.OnDismissedListener() {
                                @Override
                                public void onDismissed() {
                                    ((Dismissible) fragment).hide();
                                    getSupportFragmentManager().beginTransaction()
                                            .remove(fragment)
                                            .commit();
                                }
                            });
                        } else {
                            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                                getSupportFragmentManager().popBackStack();
                            } else {
                                finish();
                            }
                        }
                    } else {
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack();
                        } else {
                            finish();
                        }
                    }
                } else {
                    finish();
                }
            }
        });
    }

    public void openFragment(Fragment fragment){
        openFragment(fragment, null);
    }

    public void openFragment(Fragment fragment, Map<String, View> transitions){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(transitions != null)
            for (Map.Entry<String, View> entry : transitions.entrySet())
            {
                transaction.addSharedElement(entry.getValue(), entry.getKey());
            }

        transaction.add(R.id.fragment_container, fragment).commit();
    }

    public void replaceFragment(Fragment fragment, Map<String, View> transitions, boolean addToBackStack){
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        if(transitions != null)
            for (Map.Entry<String, View> entry : transitions.entrySet())
            {
                if(entry.getKey() != null)
                    transaction.addSharedElement(entry.getValue(), entry.getKey());
            }

        transaction.replace(R.id.fragment_container, fragment);
        if(addToBackStack)
            transaction.addToBackStack(null);
        else{
            clearBackStack();
        }
        transaction.commitAllowingStateLoss();
    }

    private void clearBackStack(){
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public void replaceFragment(Fragment fragment, Map<String, View> transitions){
        replaceFragment(fragment, transitions, true);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            ApplicationHelper.setPermissionChecked(this);
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            if (!Settings.canDrawOverlays(HomeActivity.this)) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                            }
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            ApplicationHelper.setPermissionChecked(HomeActivity.this);
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.overlay_permission)
                    .setTitle(R.string.overlay_permission_title)
                    .setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener).show();
        }
    }

    public void showReviewDialog(){
        if(isFinishing())
            return;

        new AlertDialog.Builder(this)
                .setTitle(R.string.review_title)
                .setMessage(R.string.review_description)
                .setPositiveButton(R.string.review_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ApplicationHelper.setShowReview(HomeActivity.this);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=com.jessy_barthelemy.strongify"));
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.review_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ApplicationHelper.setShowReview(HomeActivity.this);
                    }
                })
                .setNeutralButton(R.string.review_later, null)
                .show();
    }

    public void showStartupDialog(){
        if(isFinishing())
            return;
        new AlertDialog.Builder(this)
                .setTitle(R.string.startup_title)
                .setMessage(R.string.startup_description)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ApplicationHelper.setShowStartup(HomeActivity.this);
                    }
                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ApplicationHelper.setShowStartup(HomeActivity.this);
                    }
                })
                .show();
    }
}