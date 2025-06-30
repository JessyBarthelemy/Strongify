package com.jessy_barthelemy.strongify.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.entity.Equipment;
import com.jessy_barthelemy.strongify.database.entity.Exercise;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithSet;
import com.jessy_barthelemy.strongify.enumeration.ExerciseSetType;
import com.jessy_barthelemy.strongify.fragment.PreferenceFragment;
import com.jessy_barthelemy.strongify.layout.FlowLayout;

import java.util.List;

public class ApplicationHelper {

    private static final String PERIODICITY_SEPARATOR = " - ";
    public static final int MIN_SET_COUNT = 1;
    public static final int MIN_WEIGHT = 0;
    private static final String DEFAULT_WEIGHT = "10";
    private static final String DEFAULT_REPS = "10";
    public static final int DEFAULT_REST = 90;

    private static final String WEIGHT_KG = "KG";
    private static final String DISTANCE_KM = "KM";

    public static final int ICON_DELAY = 1200;
    private static final String EXERCISE_SEPARATOR = " - ";

    public static final String TUTORIAL_WORKOUT = "workout";
    public static final String TUTORIAL_SET = "set";
    public static final String TUTORIAL_PLAY = "play";

    public static Boolean isPremiumUnlocked;

    public static String getPeriodicityText(Context context, List<Integer> periodicity){
        StringBuilder builder = new StringBuilder();
        String[] week = context.getResources().getStringArray(R.array.periodicity);

        boolean firstDay = true;
        for(Integer day : periodicity){
            if(!firstDay)
                builder.append(PERIODICITY_SEPARATOR);
            else
                firstDay = false;

            builder.append(week[day]);
        }

        return builder.toString();
    }

    public static void makeVibration(Context context){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if(v != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                v.vibrate(VibrationEffect.createOneShot(100,VibrationEffect.DEFAULT_AMPLITUDE));
            else
                v.vibrate(100);
        }
    }

    public static int getWeightIcon(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultUnit = context.getString(R.string.default_weight_unit);
        String unity = preferences.getString(PreferenceFragment.WEIGHT_UNIT, defaultUnit);

        if(unity.equals(WEIGHT_KG))
            return R.drawable.ic_weight_kg;
        else
            return R.drawable.ic_weight_lb;
    }

    private static String getDistanceUnit(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultUnit = context.getString(R.string.default_distance_unit);
        return preferences.getString(PreferenceFragment.DISTANCE_UNIT, defaultUnit).toLowerCase();
    }

    public static String getSizeUnit(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultUnit = context.getString(R.string.default_size_unit);
        return preferences.getString(PreferenceFragment.SIZE_UNIT, defaultUnit).toLowerCase();
    }

    public static String getWeightUnit(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultUnit = context.getString(R.string.default_weight_unit);
        return preferences.getString(PreferenceFragment.WEIGHT_UNIT, defaultUnit).toLowerCase();
    }

    public static int getDistanceIcon(Context context){
        String unity = getDistanceUnit(context);
        if(unity.equalsIgnoreCase(DISTANCE_KM))
            return R.drawable.ic_distance_km;
        else
            return R.drawable.ic_distance_mi;
    }

    public static TimeSpan getTimeSpan(int seconds){
        TimeSpan result = new TimeSpan();

        while(seconds >= 3600){
            seconds -= 3600;
            result.hours++;
        }

        while(seconds >= 60){
            seconds -= 60;
            result.minutes++;
        }

        result.seconds = seconds;

        return result;
    }

    public static SetExecution getDefaultSetExecution(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SetExecution execution = new SetExecution();

        execution.reps = convertStringToInt(preferences.getString(PreferenceFragment.DEFAULT_REP_PREF_KEY, DEFAULT_REPS));
        execution.weight = convertStringToDouble(preferences.getString(PreferenceFragment.DEFAULT_WEIGHT_PREF_KEY, DEFAULT_WEIGHT));
        execution.rest = DEFAULT_REST;

        return execution;
    }

    public static String getExerciseTitle(Context context, String title, boolean base){
        if(base)
            return getTranslation(context, "ex_"+title);
        else
            return title;
    }

    public static int getExerciseIcon(Context context, Exercise exercise, boolean first){
        if(exercise.base){
            StringBuilder builder = new StringBuilder();
            builder.append("ex_");
            builder.append(exercise.title);
            if(first)
                builder.append("_1");
            else
                builder.append("_2");
            return getDrawable(context, builder.toString());
        }
        else
            return exercise.iconId;
    }

    public static void setExerciseIcon(Context context, ImageView image, Exercise exercise, boolean first){
        if(exercise.base){
            StringBuilder builder = new StringBuilder();
            builder.append("ex_");
            builder.append(exercise.title);
            if(first)
                builder.append("_1");
            else
                builder.append("_2");
            image.setImageResource(getDrawable(context, builder.toString()));
        }
        else if(exercise.path != null)
            image.setImageBitmap(BitmapFactory.decodeFile(exercise.path));
        else
            image.setImageResource(R.drawable.select_image);
    }

    public static String getExerciseDescString(Context context, Exercise exercise){
        if(exercise.base){
            if(exercise.description.startsWith("@"))
                return getTranslation(context, "desc_"+exercise.description.substring(1));
            else
                return getTranslation(context, "desc_"+exercise.title);
        }
        else
            return exercise.description;
    }

    public static int getDrawable(Context context, String value){
        if(context == null)
            return 0;
        return context.getResources().getIdentifier(value, "drawable", context.getPackageName());
    }

    public static String getEquipmentTitle(Context context, Equipment equipment){
        if(equipment.base)
            return getTranslation(context, equipment.title);
        else
            return equipment.title;
    }

    private static int convertStringToInt(String value){
        try{
            return Integer.valueOf(value);
        }catch (NumberFormatException e){
            return 0;
        }
    }

    private static double convertStringToDouble(String value){
        try{
            return Double.valueOf(value);
        }catch (NumberFormatException e){
            return 0;
        }
    }

    public static String getTranslation(Context context, String value){
        if(context == null)
            return "";

        try{
            int resId = context.getResources().getIdentifier(value, "string", context.getPackageName());
            return context.getString(resId);
        }catch(Resources.NotFoundException e){
            return "";
        }
    }

    public static Goal getGoalIconId(Context context, int exerciseSetType, int value, boolean maxRepetition){
        Goal goal = new Goal();
        switch (exerciseSetType){
            case ExerciseSetType.REPETITION:
                goal.goalIconId = R.drawable.ic_set;
                goal.tint = R.color.colorAccent;
                goal.text = String.valueOf(value);
                break;
            case ExerciseSetType.DURATION:
                goal.goalIconId = R.drawable.ic_time;
                goal.tint = R.color.colorAccent2;

                TimeSpan timeSpan = ApplicationHelper.getTimeSpan(value);
                goal.text = context.getString(R.string.workout_rest, timeSpan.minutes, timeSpan.seconds);
                break;
            case ExerciseSetType.DISTANCE:
                goal.goalIconId = ApplicationHelper.getDistanceIcon(context);
                goal.tint = R.color.colorAccent;
                goal.text = String.valueOf(value);
                break;
        }

        if(maxRepetition)
            goal.text = context.getString(R.string.exercise_max);

        return goal;
    }

    public static boolean isRingerActivated(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PreferenceFragment.DEFAULT_RINGER_ACTIVATION_PREF_KEY, true);
    }

    public static int getTickCount(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean ringerActivated = isRingerActivated(context);

        if(ringerActivated){
            String tickCount = preferences.getString(PreferenceFragment.DEFAULT_TICK_COUNT_PREF_KEY, context.getString(R.string.default_tick_count));
            try{
                return Integer.parseInt(tickCount);
            }catch (NumberFormatException e){
                return 0;
            }
        }else
            return 0;
    }

    public static boolean isPermissionChecked(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PreferenceFragment.DEFAULT_PERMISSION_CHECKED_PREF_KEY, false);
    }

    public static boolean isTimerHeadEnabled(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(PreferenceFragment.TIMER_HEAD_PREF_KEY, true);
    }

    public static void setPermissionChecked(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean(PreferenceFragment.DEFAULT_PERMISSION_CHECKED_PREF_KEY, true).apply();
    }

    public static void formatExercise(Context context, ExerciseWithSet exercise, StringBuilder setRepetition, StringBuilder setWeight, StringBuilder setRest, boolean liftedValues){
        TimeSpan timeSpan;
        Goal goal;
        for(int i = 0, len = exercise.sets.size(); i < len; i++){

            if(liftedValues)
                goal = ApplicationHelper.getGoalIconId(context, exercise.sets.get(i).exerciseSetType,
                        exercise.sets.get(i).repsDone, false);
            else
                goal = ApplicationHelper.getGoalIconId(context, exercise.sets.get(i).exerciseSetType,
                        exercise.sets.get(i).reps, exercise.sets.get(i).maxRepetition);
            setRepetition.append(goal.text);
            if(exercise.sets.get(i).exerciseSetType == ExerciseSetType.DISTANCE){
                setRepetition.append(" ");
                setRepetition.append(getDistanceUnit(context));
            }

            if(liftedValues)
                setWeight.append(exercise.sets.get(i).weightLifted);
            else
                setWeight.append(exercise.sets.get(i).weight);

            timeSpan = ApplicationHelper.getTimeSpan(exercise.sets.get(i).rest);
            setRest.append(context.getString(R.string.workout_rest, timeSpan.minutes, timeSpan.seconds));

            if(i < len - 1){
                setRepetition.append(EXERCISE_SEPARATOR);
                setWeight.append(EXERCISE_SEPARATOR);
                setRest.append(EXERCISE_SEPARATOR);
            }
        }
    }

    public static boolean hasToShowReview(Context context){
        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean alreadyReviewed = prefs.getBoolean(PreferenceFragment.REVIEW_PREF, false);
            //date + 14 days
            return !alreadyReviewed && System.currentTimeMillis() > context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .firstInstallTime + 1209600000L;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void setShowReview(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(PreferenceFragment.REVIEW_PREF, true);
        edit.apply();
    }

    public static void setTutorial(Context context, String tutorial){
        if(context == null)
            return;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(tutorial, true);
        edit.apply();
    }

    public static void resetTutorial(Context context){
        if(context == null)
            return;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(TUTORIAL_PLAY);
        edit.remove(TUTORIAL_SET);
        edit.remove(TUTORIAL_WORKOUT);
        edit.apply();
    }

    public static boolean hasToShowTutorial(Context context, String tutorial){
        if(context == null)
            return false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean alreadyReviewed = prefs.getBoolean(tutorial, false);
        return !alreadyReviewed;
    }

    public static boolean hasToShowStartupDialog(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PreferenceFragment.STARTUP_PREF, true);
    }

    public static void setShowStartup(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(PreferenceFragment.STARTUP_PREF, false);
        edit.apply();
    }

    public static boolean isUnlocked(Context context){
       if(isPremiumUnlocked == null){
           if(context == null)
               return false;
           SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
           isPremiumUnlocked = prefs.getBoolean(PreferenceFragment.IS_UNLOCKED, false);
       }

       return isPremiumUnlocked;
    }

    public static void setIsUnlocked(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();

        edit.putBoolean(PreferenceFragment.IS_UNLOCKED, isPremiumUnlocked);
        edit.apply();
    }

    public static void closeKeyboard(Activity activity){
        if(activity != null && activity.getCurrentFocus() != null){
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(inputManager != null)
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static View getEquipmentView(Context context, String title){
        if(context != null){
            ImageView equipmentView = new ImageView(context);
            equipmentView.setImageResource(ApplicationHelper.getDrawable(context, title));

            FlowLayout.LayoutParams layoutParams = new FlowLayout.LayoutParams(100, 100);
            equipmentView.setLayoutParams(layoutParams);

            return equipmentView;
        }

        return null;
    }

    public static boolean isAlreadyBoughtChecked(Context context){
        if(context == null)
            return false;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PreferenceFragment.ALREADY_BOUGHT_CHECK, false);
    }

    public static void setIsAlreadyBoughtChecked(Context context, boolean value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(PreferenceFragment.ALREADY_BOUGHT_CHECK, value);
        edit.apply();
    }

    public static int getThemeColor(Context context, int color){
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(color, typedValue, true);
        return typedValue.data;
    }
}