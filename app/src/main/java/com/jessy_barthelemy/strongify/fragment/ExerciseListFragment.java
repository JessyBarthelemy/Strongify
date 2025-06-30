package com.jessy_barthelemy.strongify.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.database.entity.Superset;
import com.jessy_barthelemy.strongify.dialog.ExerciseFilterDialog;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.adapter.exercisePicker.ExercisePickerAdapter;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.Equipment;
import com.jessy_barthelemy.strongify.database.entity.ExerciseGroup;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithGroup;
import com.jessy_barthelemy.strongify.helper.AnimationHelper;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.RevealAnimationSettings;
import com.jessy_barthelemy.strongify.interfaces.EquipmentManager;
import com.jessy_barthelemy.strongify.interfaces.ExerciseManager;
import com.jessy_barthelemy.strongify.interfaces.OnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExerciseListFragment extends BaseFragment implements EquipmentManager, ExerciseManager {

    private OnFragmentInteractionListener listener;
    private ExercisePickerAdapter adapter;
    private List<Equipment> equipments;
    private ExpandableListView exerciseList;
    private List<ExerciseGroup> groups;
    private List<List<ExerciseWithGroup>> exercisesGroup;
    private String queryFilter;
    private RevealAnimationSettings settings;
    private int exerciseType;
    private boolean isPickerMode;
    private boolean autoClose;
    private boolean isMultipleMode;
    private Superset superset;
    private ViewGroup equipmentView;

    public ExerciseListFragment() {
        this.autoClose = true;
        this.isMultipleMode = true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_exercise_list, container, false);

        Context context = getContext();
        if(getActivity() != null && settings != null && context != null){
            int backgroundColor = ApplicationHelper.getThemeColor(context, android.R.attr.windowBackground);

            AnimationHelper.registerCircularRevealAnimation(getActivity(), root, settings,
                    ContextCompat.getColor(getActivity(), R.color.colorAccent), backgroundColor);
        }

        exerciseList = root.findViewById(R.id.exercise_list);

        exerciseList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if(isPickerMode){
                if (isMultipleMode) {
                    ExerciseWithGroup exercise = adapter.getDataSetFiltered().get(groupPosition).get(childPosition);
                    exercise.selected = !exercise.selected;
                    adapter.notifyDataSetChanged();
                }else{
                    for(List<ExerciseWithGroup> group : adapter.getDataSetFiltered()){
                        for(ExerciseWithGroup exercise : group){
                            exercise.selected = false;
                        }
                    }

                    ExerciseWithGroup exerciseSelected = adapter.getDataSetFiltered().get(groupPosition).get(childPosition);
                    exerciseSelected.selected = true;
                    adapter.notifyDataSetChanged();
                }

            }else{
                ExerciseWithGroup exercise = adapter.getDataSetFiltered().get(groupPosition).get(childPosition);
                ExerciseDescriptionFragment fragment = new ExerciseDescriptionFragment();
                fragment.setExerciseWithGroup(exercise);

                ((HomeActivity)getActivity()).openFragment(fragment);
            }
            return false;
        });

        exerciseList.setScrollingCacheEnabled(false);

        AppDatabase.getAppDatabase(getActivity()).exerciseDao().getAll().observe(getViewLifecycleOwner(), exerciseWithGroups -> {
            if(exerciseWithGroups != null){
                formatExercisesResult(exerciseWithGroups);

                adapter = new ExercisePickerAdapter(getActivity(), groups, exercisesGroup);
                exerciseList.setAdapter(adapter);
            }
        });

        SearchView searchView = root.findViewById(R.id.search_exercise);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryFilter = query;
                proceedQueryFilter();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if(query.isEmpty()){
                    queryFilter = query;
                    proceedQueryFilter();
                }
                return false;
            }
        });

        AppDatabase.getAppDatabase(getActivity()).equipmentDao().getAll().observe(getActivity(), equipments -> ExerciseListFragment.this.equipments = equipments);

        ImageView searchFilter = root.findViewById(R.id.search_filter);
        searchFilter.setOnClickListener(v -> {
            if(equipments != null)
                new ExerciseFilterDialog(getActivity(), equipments, ExerciseListFragment.this).show();
        });

        Button addExercises = root.findViewById(R.id.add_exercises);

        if(isPickerMode) {
            addExercises.setVisibility(View.VISIBLE);
            addExercises.setOnClickListener(v -> {
                List<ExerciseWithGroup> selectedExercises = new ArrayList<>();
                for (int i = 0, len = adapter.getDataSet().size(); i < len; i++) {
                    for (int j = 0, exeLen = adapter.getDataSet().get(i).size(); j < exeLen; j++) {
                        if (adapter.getDataSet().get(i).get(j).selected)
                            selectedExercises.add(adapter.getDataSet().get(i).get(j));
                    }
                }
                listener.onFragmentInteraction(selectedExercises, exerciseType, superset);
                if(autoClose)
                    getActivity().getSupportFragmentManager().popBackStack();
            });
        }

        equipmentView = root.findViewById(R.id.exercise_equipment);


        final FloatingActionButton fab = root.findViewById(R.id.fab);

        //if we are adding exercise during execution
        if(isPickerMode && !isMultipleMode){
            fab.setVisibility(View.GONE);
        }else{
            fab.setOnClickListener(view -> {
                RevealAnimationSettings settings = new RevealAnimationSettings();

                settings.centerX = (int)(fab.getX() + (float) fab.getWidth() / 2);
                settings.centerY = (int)(fab.getY() + (float) fab.getHeight() / 2);
                settings.width = root.getWidth();
                settings.height = root.getHeight();
                ExerciseManagementFragment fragment = new ExerciseManagementFragment();
                fragment.setSettings(settings);
                fragment.setExerciseManager(ExerciseListFragment.this);
                ((HomeActivity)getActivity()).openFragment(fragment);
            });
        }

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
            listener = (OnFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void setListener(OnFragmentInteractionListener listener){
        this.listener = listener;
    }

    @Override
    public void onEquipmentChosen(List<Equipment> equipmentList) {
        equipments = equipmentList;
        proceedQueryFilter();
        exerciseList.requestFocus();

        int padding = getResources().getDimensionPixelOffset(R.dimen.little_margin);

        equipmentView.removeAllViews();
        for(Equipment equipment : equipmentList){
            if(equipment.isSelected){
                TextView eqTitle = new TextView(getActivity());
                eqTitle.setText(ApplicationHelper.getEquipmentTitle(getActivity(), equipment));
                eqTitle.setBackgroundResource(R.drawable.fab_options_background);
                eqTitle.setTextColor(getResources().getColor(android.R.color.white));
                eqTitle.setPadding(padding, 0, padding, 0);
                equipmentView.addView(eqTitle);
            }
        }
    }

    private void proceedFilterQueryData(List<ExerciseWithGroup> exerciseWithGroups){
        formatExercisesResult(exerciseWithGroups);
        adapter.setDataSet(exercisesGroup);
        adapter.setGroups(groups);

        if(queryFilter != null && !queryFilter.isEmpty())
            adapter.getFilter().filter(queryFilter);
        adapter.notifyDataSetChanged();
    }

    private void proceedQueryFilter(){
        List<Long> ids = new ArrayList<>();

        if(equipments != null){
            for (Equipment equipment : equipments){
                if(equipment.isSelected)
                    ids.add(equipment.equipmentId);
            }
        }

        if(!ids.isEmpty() && getActivity() != null){

            AppDatabase.getAppDatabase(getActivity()).exerciseDao().getByEquipments(ids).observe(getActivity(), this::proceedFilterQueryData);
        }else if(queryFilter != null && !queryFilter.isEmpty() && adapter != null && adapter.getFilter() != null){
            adapter.clearFilter();
            adapter.getFilter().filter(queryFilter);
        }else{
            assert adapter != null;
            adapter.clearFilter();
            adapter.notifyDataSetChanged();
        }
    }

    private void formatExercisesResult (List<ExerciseWithGroup> exerciseWithGroups){
        exercisesGroup = new ArrayList<>();
        groups = new ArrayList<>();

        long lastGroupId = -1;
        List<ExerciseWithGroup> exercises =  new ArrayList<>();

        for(ExerciseWithGroup exerciseWithGroup : exerciseWithGroups){

            if(exerciseWithGroup.group != null){
                if(lastGroupId != exerciseWithGroup.group.id){
                    groups.add(exerciseWithGroup.group);

                    if(lastGroupId != -1){
                        Collections.sort(exercises);
                        exercisesGroup.add(exercises);
                        exercises = new ArrayList<>();
                    }
                }

                lastGroupId = exerciseWithGroup.group.id;
                exerciseWithGroup.exercise.translatedTitle = ApplicationHelper.getExerciseTitle(getActivity(), exerciseWithGroup.exercise.title, exerciseWithGroup.exercise.base);
                if(getActivity() != null && exerciseWithGroup.exercise.iconId == 0)
                    exerciseWithGroup.exercise.iconId = ApplicationHelper.getDrawable(getActivity(), "th_" + exerciseWithGroup.exercise.title);
                exercises.add(exerciseWithGroup);
            }
        }

        //adding the last group exercises
        if(!exercises.isEmpty()){
            Collections.sort(exercises);
            exercisesGroup.add(exercises);
        }
    }

    public void setSettings(RevealAnimationSettings settings) {
        this.settings = settings;
    }

    public void setExerciseType(int exerciseType) {
        this.exerciseType = exerciseType;
    }

    public void setPickerMode(boolean pickerMode) {
        isPickerMode = pickerMode;
    }

    public void setMultipleMode(boolean isMultipleMode) {
        this.isMultipleMode = isMultipleMode;
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    public void setSuperset(Superset superset) {
        this.superset = superset;
    }

    @Override
    public void exerciseAdded(ExerciseWithGroup exercise) {
        adapter.getDataSet().get(exercise.group.sortOrder).add(exercise);
        adapter.notifyDataSetChanged();
    }
}