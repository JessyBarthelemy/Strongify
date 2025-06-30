package com.jessy_barthelemy.strongify.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.asyncTask.UpsertProfileTask;
import com.jessy_barthelemy.strongify.database.entity.Profile;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;

public class ProfileDialog extends Dialog{

    private Profile profile;
    private String sizeUnit;

    public ProfileDialog(@NonNull Context context, Profile profile, String sizeUnit) {
        super(context);
        if(profile == null)
            this.profile = new Profile();
        else
            this.profile = profile;
        this.sizeUnit = sizeUnit;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_profile);

        final TextInputLayout size = findViewById(R.id.profile_size);
        size.setHint(getContext().getString(R.string.size, sizeUnit));

        final Spinner spinner = findViewById(R.id.profile_goal);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.goal, R.layout.profile_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final TextInputLayout name = findViewById(R.id.profile_name);
        final TextInputLayout age = findViewById(R.id.profile_age);

        if(profile != null){
             if(name.getEditText() != null)
                name.getEditText().setText(profile.name);

            if(age.getEditText() != null && profile.age > 0)
                age.getEditText().setText(String.valueOf(profile.age));

            if(size.getEditText() != null && profile.size > 0)
                size.getEditText().setText(String.valueOf(profile.size));

            spinner.setSelection(profile.goal);
        }

        Button action = findViewById(R.id.profile_action);
        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null)
                    imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                profile.name = name.getEditText().getText().toString();
                if(!size.getEditText().getText().toString().isEmpty())
                    profile.size = Integer.parseInt(size.getEditText().getText().toString());
                if(!age.getEditText().getText().toString().isEmpty())
                    profile.age = Integer.parseInt(age.getEditText().getText().toString());
                profile.goal = spinner.getSelectedItemPosition();

                UpsertProfileTask task = new UpsertProfileTask(getContext(), profile);
                task.execute();
                dismiss();
            }
        });
    }

}
