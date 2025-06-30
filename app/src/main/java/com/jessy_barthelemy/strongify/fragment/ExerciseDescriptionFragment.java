package com.jessy_barthelemy.strongify.fragment;


import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.asyncTask.DeleteExerciseTask;
import com.jessy_barthelemy.strongify.database.entity.Equipment;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithGroup;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.interfaces.Dismissible;
import com.jessy_barthelemy.strongify.interfaces.ExerciseManager;
import com.jessy_barthelemy.strongify.interfaces.OnExerciseDeletion;

import java.util.List;

public class ExerciseDescriptionFragment extends BaseFragment implements Dismissible, ExerciseManager, OnExerciseDeletion {

    private ExerciseWithGroup exerciseWithGroup;
    private boolean firstIcon;
    private Handler iconHandler;
    private Runnable iconRunnable;

    private int firstIconId;
    private int secondIconId;
    private ViewGroup equipmentsView;
    private TextView noEquipment;
    private TextView noDescription;
    private TextView description;
    private TextView title;
    private ImageView icon;

    public ExerciseDescriptionFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_exercise_description, container, false);

        if(getActivity() != null && exerciseWithGroup != null){
            title = root.findViewById(R.id.exercise_title);

            description = root.findViewById(R.id.exercise_description);

            if(exerciseWithGroup.exercise.description.startsWith("@") && exerciseWithGroup.exercise.description.length() > 1){
                TextView descriptionReference = root.findViewById(R.id.exercise_description_reference);
                descriptionReference.setVisibility(View.VISIBLE);
                String reference = ApplicationHelper.getExerciseTitle(getActivity(), exerciseWithGroup.exercise.description.substring(1), exerciseWithGroup.exercise.base);
                descriptionReference.setText(getString(R.string.exercise_eq_desc, reference));
            }

            noEquipment = root.findViewById(R.id.no_equipment);
            noDescription = root.findViewById(R.id.no_description);

            equipmentsView = root.findViewById(R.id.exercise_equipment);
            final LiveData<List<Equipment>> model =  AppDatabase.getAppDatabase(getActivity()).equipmentDao().getByExerciseId(exerciseWithGroup.exercise.eId) ;

            model.observe(getActivity(), equipments -> {
                boolean isAnySelected = false;
                equipmentsView.removeAllViews();
                if(equipments != null && !equipments.isEmpty()){
                    for(Equipment equipment : equipments){
                        if(equipment.isSelected){
                            isAnySelected = true;
                            View v = ApplicationHelper.getEquipmentView(getActivity(), equipment.title);
                            if(v != null)
                                equipmentsView.addView(v);
                        }
                    }

                    if(!isAnySelected)
                        noEquipment.setVisibility(View.VISIBLE);
                }
            });

            icon = root.findViewById(R.id.exercise_icon);
            firstIconId = ApplicationHelper.getExerciseIcon(getActivity(), exerciseWithGroup.exercise, true);
            secondIconId = ApplicationHelper.getExerciseIcon(getActivity(), exerciseWithGroup.exercise, false);

            if(exerciseWithGroup.exercise.base){
                icon.setImageResource(firstIconId);

                if(secondIconId > 0){
                    iconHandler = new Handler();
                    iconRunnable = new Runnable(){
                        public void run(){
                            if(firstIcon)
                                icon.setImageResource(firstIconId);
                            else
                                icon.setImageResource(secondIconId);
                            firstIcon = !firstIcon;
                            iconHandler.postDelayed(this, ApplicationHelper.ICON_DELAY);
                        }
                    };
                }
            }else{
                if(exerciseWithGroup.exercise.path != null)
                    icon.setImageBitmap(BitmapFactory.decodeFile(exerciseWithGroup.exercise.path));
                else
                    icon.setImageResource(R.drawable.select_image);
            }

            ImageView update = root.findViewById(R.id.update);
            ImageView delete = root.findViewById(R.id.delete);

            if(!exerciseWithGroup.exercise.base){
                update.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);

                update.setOnClickListener(view -> {
                    ExerciseManagementFragment fragment = new ExerciseManagementFragment();
                    fragment.setExerciseManager(ExerciseDescriptionFragment.this);
                    fragment.setExercise(exerciseWithGroup.exercise);
                    HomeActivity homeActivity = ((HomeActivity)getActivity());
                    if(homeActivity != null) {
                        ((HomeActivity)getActivity()).openFragment(fragment);
                    }
                });

                delete.setOnClickListener(view -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.overlay_delete_exercise)
                            .setTitle(R.string.overlay_confirm)
                            .setPositiveButton(R.string.yes, (dialogInterface, i) -> new DeleteExerciseTask(getActivity(), exerciseWithGroup.exercise, ExerciseDescriptionFragment.this).execute())
                            .setNegativeButton(R.string.no, (dialogInterface, i) -> {}).show();
                });
            }

            FloatingActionButton fab = root.findViewById(R.id.fab);
            fab.setOnClickListener(v -> {
                ExerciseHistoryGraphicFragment fragment = new ExerciseHistoryGraphicFragment();
                fragment.setExerciseId(exerciseWithGroup.exercise.eId);
                fragment.setTitle(title.getText().toString());
                if(getActivity() != null)
                    ((HomeActivity)getActivity()).openFragment(fragment);
            });

            updateExercise();
        }

        return root;
    }



    public void setExerciseWithGroup(ExerciseWithGroup exerciseWithGroup){
        this.exerciseWithGroup = exerciseWithGroup;
    }

    @Override
    public void onStop() {
        if(secondIconId > 0)
            iconHandler.removeCallbacks(iconRunnable);
        super.onStop();
    }

    @Override
    public void onResume() {
        if(secondIconId > 0)
            iconHandler.postDelayed(iconRunnable,  ApplicationHelper.ICON_DELAY);
        super.onResume();
    }

    @Override
    public void dismiss(OnDismissedListener listener) {
        listener.onDismissed();
    }

    @Override
    public void hide() {}

    @Override
    public void exerciseAdded(ExerciseWithGroup exercise) {
        updateExercise();
    }

    private void updateExercise(){
        title.setText(ApplicationHelper.getExerciseTitle(getActivity(), exerciseWithGroup.exercise.title, exerciseWithGroup.exercise.base));
        description.setText(ApplicationHelper.getExerciseDescString(getActivity(), exerciseWithGroup.exercise));

        if(!exerciseWithGroup.exercise.base){
            if(exerciseWithGroup.exercise.path != null)
                icon.setImageBitmap(BitmapFactory.decodeFile(exerciseWithGroup.exercise.path));
            else
                icon.setImageResource(R.drawable.select_image);
        }

        if(description.getText().toString().isEmpty())
            noDescription.setVisibility(View.VISIBLE);
        else
            noDescription.setVisibility(View.GONE);
    }

    @Override
    public void onExerciseDeleted() {
        requireActivity().getOnBackPressedDispatcher().onBackPressed();
    }
}
