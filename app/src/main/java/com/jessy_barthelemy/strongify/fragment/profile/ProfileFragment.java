package com.jessy_barthelemy.strongify.fragment.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.activity.HomeActivity;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.database.entity.BodyMeasurements;
import com.jessy_barthelemy.strongify.database.entity.Profile;
import com.jessy_barthelemy.strongify.database.entity.WeightMeasure;
import com.jessy_barthelemy.strongify.dialog.ProfileDialog;
import com.jessy_barthelemy.strongify.fragment.BaseFragment;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;

import java.text.DateFormat;
import java.util.Locale;

public class ProfileFragment extends BaseFragment {

    private Profile profile;
    private TextView name;
    private TextView age;
    private TextView size;
    private TextView weight;

    public ProfileFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        name = root.findViewById(R.id.profile_name);
        age = root.findViewById(R.id.profile_age);
        size = root.findViewById(R.id.profile_size);
        weight = root.findViewById(R.id.profile_weight);

        name.setText("-");
        age.setText("-");
        size.setText("-");
        weight.setText("-");

        final String sizeUnitText = ApplicationHelper.getSizeUnit(getContext());

        TextView sizeUnit = root.findViewById(R.id.profile_size_text);
        sizeUnit.setText(sizeUnitText);

        TextView weightUnit = root.findViewById(R.id.profile_weight_text);
        weightUnit.setText(ApplicationHelper.getWeightUnit(getContext()));

        final TextView weightDesc = root.findViewById(R.id.profile_weight_desc);

        if(getActivity() != null){
            AppDatabase.getAppDatabase(getActivity()).profileDao().getProfile().observe(getActivity(), new Observer<Profile>() {
                @Override
                public void onChanged(@Nullable Profile profile) {
                    ProfileFragment.this.profile = profile;
                    updateProfileUI();
                }
            });

            final LiveData<WeightMeasure> liveData = AppDatabase.getAppDatabase(getActivity()).weightMeasureDao().getLast();
            liveData.observe(getActivity(), new Observer<WeightMeasure>() {
                @Override
                public void onChanged(@Nullable WeightMeasure weightMeasure) {
                    if(weightMeasure != null){
                        weight.setText(String.valueOf(weightMeasure.weight));
                        String date = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(weightMeasure.weightDate.getTime());
                        weightDesc.setText(getString(R.string.last_weighing, date));
                    }else
                        weightDesc.setText(getString(R.string.no_data_yet));

                    liveData.removeObserver(this);
                }
            });

            final TextView sizeDesc = root.findViewById(R.id.profile_size_desc);

            final LiveData<BodyMeasurements> liveDataSize = AppDatabase.getAppDatabase(getActivity()).bodyMeasurementsDao().getLast();
            liveDataSize.observe(getActivity(), new Observer<BodyMeasurements>() {
                @Override
                public void onChanged(@Nullable BodyMeasurements measure) {
                    if(measure != null){
                        String date = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault()).format(measure.measureDate.getTime());
                        sizeDesc.setText(getString(R.string.last_measure, date));
                    }else
                        sizeDesc.setText(getString(R.string.no_data_yet));

                    liveDataSize.removeObserver(this);
                }
            });
        }

        ImageView update = root.findViewById(R.id.profile_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getContext() != null) {
                    ProfileDialog dialog = new ProfileDialog(getContext(), profile, sizeUnitText);
                    dialog.setOwnerActivity(getActivity());
                    dialog.show();
                }
            }
        });

        View weightWrapper = root.findViewById(R.id.profile_weight_wrapper);
        weightWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeightGraphicFragment fragment = new WeightGraphicFragment();
                if(getActivity() != null)
                    ((HomeActivity)getActivity()).openFragment(fragment);
            }
        });

        View bodyMeasurementWrapper = root.findViewById(R.id.profile_size_wrapper);
        bodyMeasurementWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BodyMeasurementsGraphicFragment fragment = new BodyMeasurementsGraphicFragment();
                if(getActivity() != null)
                    ((HomeActivity)getActivity()).openFragment(fragment);
            }
        });

        return root;
    }

    private void updateProfileUI(){
        if(profile != null){
            name.setText(profile.name);
            if(profile.age > 0)
                age.setText(String.valueOf(profile.age));
            if(profile.size > 0)
                size.setText(String.valueOf(profile.size));
        }
    }
}
