package com.jessy_barthelemy.strongify.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.adapter.ExerciseGroupAdapter;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.asyncTask.AddOrUpdateExerciseTask;
import com.jessy_barthelemy.strongify.database.asyncTask.SaveImageToDiskTask;
import com.jessy_barthelemy.strongify.database.entity.Equipment;
import com.jessy_barthelemy.strongify.database.entity.Exercise;
import com.jessy_barthelemy.strongify.database.entity.ExerciseGroup;
import com.jessy_barthelemy.strongify.database.model.ExerciseWithGroup;
import com.jessy_barthelemy.strongify.dialog.ExerciseFilterDialog;
import com.jessy_barthelemy.strongify.helper.AnimationHelper;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.helper.RevealAnimationSettings;
import com.jessy_barthelemy.strongify.interfaces.Dismissible;
import com.jessy_barthelemy.strongify.interfaces.EquipmentManager;
import com.jessy_barthelemy.strongify.interfaces.ExerciseManager;
import com.jessy_barthelemy.strongify.interfaces.OnImageSavedToDisk;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExerciseManagementFragment extends BaseFragment implements Dismissible, EquipmentManager, OnImageSavedToDisk {

    private Exercise exercise;
    private RevealAnimationSettings settings;
    private View root;
    private List<Equipment> exerciseEquipments;
    private List<Equipment> allEquipments;
    private ViewGroup equipmentsView;
    private TextView noEquipment;
    private ExerciseGroupAdapter adapter;
    private ExerciseManager exerciseManager;
    private ImageView exerciseImage;

    private int requiredWith;
    private int requiredHeight;

    private ActivityResultLauncher<Intent> getImageLauncher;

    public ExerciseManagementFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_exercise_management, container, false);
        if (getActivity() != null && settings != null) {
            int backgroundColor = ApplicationHelper.getThemeColor(getContext(), android.R.attr.windowBackground);

            AnimationHelper.registerCircularRevealAnimation(getActivity(), root, settings,
                    ContextCompat.getColor(getActivity(), R.color.colorAccent), backgroundColor);
        }

        final boolean creation = exercise == null;
        if (creation)
            exercise = new Exercise();

        exerciseImage = root.findViewById(R.id.exercise_icon);

        final ImageView updateEquipment = root.findViewById(R.id.exercise_update_equipment);

        final TextInputLayout title = root.findViewById(R.id.exercise_title);
        final TextInputLayout description = root.findViewById(R.id.exercise_description);

        noEquipment = root.findViewById(R.id.no_equipment);

        equipmentsView = root.findViewById(R.id.exercise_equipment);
        final LiveData<List<Equipment>> model = exercise.eId > 0 ? AppDatabase.getAppDatabase(getActivity()).equipmentDao().getByExerciseId(exercise.eId) : AppDatabase.getAppDatabase(getActivity()).equipmentDao().getAll();
        model.observe(getActivity(), new Observer<List<Equipment>>() {
            @Override
            public void onChanged(@Nullable List<Equipment> equipments) {
                boolean isAnySelected = false;
                if (equipments != null && equipments.size() > 0) {
                    allEquipments = equipments;
                    exerciseEquipments = new ArrayList<>();

                    for (Equipment equipment : equipments) {
                        if (equipment.isSelected) {
                            isAnySelected = true;
                            exerciseEquipments.add(equipment.clone());
                            equipmentsView.addView(ApplicationHelper.getEquipmentView(getActivity(), equipment.title));
                        }
                    }

                    if (!isAnySelected)
                        noEquipment.setVisibility(View.VISIBLE);

                    updateEquipment.setOnClickListener(v -> new ExerciseFilterDialog(getActivity(), allEquipments, ExerciseManagementFragment.this, true).show());

                    model.removeObserver(this);
                }
            }
        });

        final Spinner groupList = root.findViewById(R.id.exercise_group);
        adapter = null;
        final LiveData<List<ExerciseGroup>> groups = AppDatabase.getAppDatabase(getActivity()).exerciseGroupDao().getAll();
        groups.observe(getActivity(), new Observer<List<ExerciseGroup>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseGroup> exerciseGroups) {
                adapter = new ExerciseGroupAdapter(getActivity(), exerciseGroups);
                groupList.setAdapter(adapter);

                if (exercise.eId > 0) {
                    ExerciseGroup group = new ExerciseGroup();
                    group.id = exercise.groupId;
                }

                if (!creation) {
                    title.getEditText().setText(exercise.title);
                    description.getEditText().setText(exercise.description);

                    for (int i = 0; i < adapter.getData().size(); i++) {
                        if (exercise.groupId == adapter.getData().get(i).id)
                            groupList.setSelection(i);
                    }
                }

                groups.removeObserver(this);
            }
        });

        Button action = root.findViewById(R.id.exercise_action);
        if (exercise.eId > 0)
            action.setText(R.string.workout_update_action);
        else
            action.setText(R.string.workout_creation_action);

        action.setOnClickListener(view -> {
            exercise.base = false;

            if (title.getEditText() != null)
                exercise.title = title.getEditText().getText().toString();
            if (description.getEditText() != null)
                exercise.description = description.getEditText().getText().toString();

            if (adapter != null)
                exercise.groupId = ((ExerciseGroup) groupList.getSelectedItem()).id;

            ExerciseWithGroup exerciseWithGroup = new ExerciseWithGroup();
            exerciseWithGroup.exercise = exercise;
            exerciseWithGroup.group = ((ExerciseGroup) groupList.getSelectedItem());

            exerciseManager.exerciseAdded(exerciseWithGroup);

            new AddOrUpdateExerciseTask(getActivity(), exercise, exerciseEquipments, allEquipments).execute();
            requireActivity().getOnBackPressedDispatcher().onBackPressed();
        });

        if (exercise.path != null)
            exerciseImage.setImageBitmap(BitmapFactory.decodeFile(exercise.path));
        else
            exerciseImage.setImageResource(R.drawable.select_image);

        getImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        handleImageResult(result.getData());
                    }
                });

        exerciseImage.setOnClickListener(view -> {
            requiredHeight = exerciseImage.getHeight();
            requiredWith = exerciseImage.getWidth();

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            try {
                getImageLauncher.launch(intent);
            } catch (ActivityNotFoundException ignore) {
            }
        });

        TextView exerciseActionTitle = root.findViewById(R.id.exercise_action_title);

        if (creation)
            exerciseActionTitle.setText(R.string.exercise_creation_title);
        else
            exerciseActionTitle.setText(R.string.exercise_update_title);

        return root;
    }

    private void handleImageResult(Intent resultData) {
        Uri picturePath = resultData.getData();
        if (picturePath == null || getContext() == null) return;

        Cursor filenameCursor = null;
        try {
            InputStream stream = getContext().getContentResolver().openInputStream(picturePath);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream, null, options);

            options.inSampleSize = calculateInSampleSize(options, requiredWith, requiredHeight);
            options.inJustDecodeBounds = false;

            filenameCursor = requireActivity().getContentResolver().query(picturePath, null, null, null, null);

            if (filenameCursor != null && filenameCursor.moveToFirst()) {
                int columnIndex = filenameCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex >= 0) {
                    String filename = filenameCursor.getString(columnIndex);
                    InputStream actualStream = getContext().getContentResolver().openInputStream(picturePath);
                    SaveImageToDiskTask saveTask = new SaveImageToDiskTask(
                            BitmapFactory.decodeStream(actualStream, null, options),
                            requireActivity(),
                            filename,
                            exercise.path,
                            this
                    );
                    saveTask.execute();
                }
            }
        } catch (IOException ignore) {
        } finally {
            if (filenameCursor != null)
                filenameCursor.close();
        }
    }

    @Override
    public void dismiss(Dismissible.OnDismissedListener listener) {
        if (getActivity() != null && settings != null) {
            int backgroundColor = ApplicationHelper.getThemeColor(getContext(), android.R.attr.windowBackground);
            AnimationHelper.startCircularExitAnimation(getActivity(), getView(), settings, ContextCompat.getColor(getActivity(), R.color.colorAccent), backgroundColor, listener);
        } else
            listener.onDismissed();
    }

    public void hide() {
        root.setVisibility(View.INVISIBLE);
    }

    public void setSettings(RevealAnimationSettings settings) {
        this.settings = settings;
    }

    @Override
    public void onEquipmentChosen(List<Equipment> equipmentList) {
        equipmentsView.removeAllViews();
        boolean isAnySelected = false;
        for (Equipment equipment : equipmentList) {
            if (equipment.isSelected) {
                isAnySelected = true;
                equipmentsView.addView(ApplicationHelper.getEquipmentView(getActivity(), equipment.title));
            }
        }

        if (isAnySelected)
            noEquipment.setVisibility(View.GONE);
        else
            noEquipment.setVisibility(View.VISIBLE);
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public void setExerciseManager(ExerciseManager exerciseManager) {
        this.exerciseManager = exerciseManager;
    }

    @Override
    public void onImageSaved(String path) {
        exercise.path = path;
        exerciseImage.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
