package com.jessy_barthelemy.strongify.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import android.text.Html;
import android.widget.Toast;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.AppDatabase;
import com.jessy_barthelemy.strongify.enumeration.FragmentType;
import com.jessy_barthelemy.strongify.helper.ApplicationHelper;
import com.jessy_barthelemy.strongify.service.ExecutionService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PreferenceFragment extends PreferenceFragmentCompat {
    public String DEFAULT_SET_COUNT_PREF_KEY = "default_rep";
    public static final String DEFAULT_WEIGHT_PREF_KEY = "default_weight";
    public static final String DEFAULT_REP_PREF_KEY = "default_rep";
    public static final String DEFAULT_TICK_COUNT_PREF_KEY = "sound_tick_count";
    public static final String DEFAULT_RINGER_ACTIVATION_PREF_KEY = "ringer_activation";
    public static final String TIMER_HEAD_PREF_KEY = "timer_head";
    public static final String IS_DARK_THEME_PREF_KEY = "dark_theme";
    public static final String FULLSCREEN_PREF_KEY = "fullscreen_mode";

    public static final int PERMISSION_RESULT_SAVE = 1;
    public static final int PERMISSION_RESULT_RESTORE = 2;
    public static final int RESTORE_DATA = 1;

    //hidden
    public static final String DEFAULT_PERMISSION_CHECKED_PREF_KEY = "permission_checked";
    public static final String REVIEW_PREF = "REVIEW_PREF";
    public static final String STARTUP_PREF = "STARTUP_PREF";
    public static final String NETWORK_PREF = "NETWORK";
    public static final String IS_UNLOCKED = "IS_UNLOCKED";
    public static final String ALREADY_BOUGHT_CHECK = "ALREADY_BOUGHT_CHECK";

    public static final String WEIGHT_UNIT = "weight_unit";
    public static final String DISTANCE_UNIT = "distance_unit";
    public static final String SIZE_UNIT = "size_unit";

    public PreferenceFragment(){}

    private ActivityResultLauncher<Intent> createFileLauncher;

    private ActivityResultLauncher<Intent> openFileLauncher;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();

        final EditTextPreference setCountPreference = (EditTextPreference) findPreference(DEFAULT_SET_COUNT_PREF_KEY);
        String setCount = preferences.getString(DEFAULT_SET_COUNT_PREF_KEY, getString(R.string.default_set_count));
        setCountPreference.setSummary(getString(R.string.preference_set_repetition_desc, setCount));
        setCountPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                ApplicationHelper.closeKeyboard(getActivity());
                try{
                    int setCount  = Integer.parseInt(o.toString());
                    if(setCount > ApplicationHelper.MIN_SET_COUNT)
                        setCountPreference.setSummary(getString(R.string.preference_set_repetition_desc, o.toString()));
                    else
                        Toast.makeText(getContext(), getString(R.string.error_set_count), Toast.LENGTH_SHORT).show();
                }catch (NumberFormatException e){
                    Toast.makeText(getContext(), getString(R.string.error_set_count), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        final EditTextPreference weightPreference = (EditTextPreference) findPreference(DEFAULT_WEIGHT_PREF_KEY);
        final String weight = preferences.getString(DEFAULT_WEIGHT_PREF_KEY, getString(R.string.default_weight));
        weightPreference.setSummary(getString(R.string.preference_set_weight_desc, weight));
        weightPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                ApplicationHelper.closeKeyboard(getActivity());
                try{
                    int weightValue = Integer.parseInt(weight);
                    if(weightValue > ApplicationHelper.MIN_WEIGHT)
                        weightPreference.setSummary(getString(R.string.preference_set_weight_desc, o.toString()));
                    else
                        Toast.makeText(getContext(), getString(R.string.error_weight), Toast.LENGTH_SHORT).show();
                }catch (NumberFormatException e){
                    Toast.makeText(getContext(), getString(R.string.error_weight), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        final EditTextPreference tickCountPreference = (EditTextPreference) findPreference(DEFAULT_TICK_COUNT_PREF_KEY);
        final String tickCount = preferences.getString(DEFAULT_TICK_COUNT_PREF_KEY, getString(R.string.default_weight));
        tickCountPreference.setSummary(getString(R.string.preference_ringer_tick_count_desc, tickCount));
        tickCountPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                ApplicationHelper.closeKeyboard(getActivity());
                tickCountPreference.setSummary(getString(R.string.preference_ringer_tick_count_desc, o.toString()));
                return true;
            }
        });

        if(getActivity() != null){
            Preference save = findPreference("save");
            save.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    requestSaveDatabase();

                    return true;
                }
            });

            Preference restore = findPreference("restore");
            restore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*"); // ou "application/octet-stream" selon le format
                    openFileLauncher.launch(intent);

                    return true;
                }
            });

            Preference about = findPreference("about");
            about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String versionName = "";
                    try {
                        versionName = requireContext().getPackageManager()
                                .getPackageInfo(requireContext().getPackageName(), 0).versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        versionName = "N/A";
                    }
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.preference_about)
                            .setMessage(Html.fromHtml(getString(R.string.about, versionName)))
                            .show();
                    return true;
                }
            });

            final CheckBoxPreference themePreference = (CheckBoxPreference) findPreference(IS_DARK_THEME_PREF_KEY);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                themePreference.setEnabled(false);
                themePreference.setVisible(false);
            }else{
                themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(PreferenceFragment.IS_DARK_THEME_PREF_KEY, (boolean)newValue); // ou false
                        editor.apply();

                        getActivity().getIntent().putExtra(ExecutionService.FRAGMENT_TYPE_PARAMETER, FragmentType.PARAMETER);
                        getActivity().recreate();
                        return true;
                    }
                });
            }

            findPreference("facebook").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.preference_facebook_link)));
                    startActivity(browserIntent);
                    return true;
                }
            });

            findPreference("store").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=com.jessy_barthelemy.strongify"));
                    startActivity(intent);
                    return true;
                }
            });
        }

        createFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            saveDatabaseToUri(uri);
                        }
                    }
                }
        );

        openFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            restoreDatabaseFromUri(uri);
                        }
                    }
                }
        );
    }

    public void requestSaveDatabase() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream"); // ou "application/x-sqlite3"
        intent.putExtra(Intent.EXTRA_TITLE, AppDatabase.DATABASE_NAME + ".backup");

        createFileLauncher.launch(intent);
    }

    private void saveDatabaseToUri(Uri uri) {
        try {
            AppDatabase.closeDatabase();
            File currentDB = getContext().getDatabasePath(AppDatabase.DATABASE_NAME);
            try (InputStream in = new FileInputStream(currentDB);
                 OutputStream out = getContext().getContentResolver().openOutputStream(uri)) {
                if (out == null) throw new IOException("OutputStream is null");
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.flush();
                Toast.makeText(getContext(), R.string.save_success, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.save_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void restoreDatabaseFromUri(Uri uri) {
        try {
            AppDatabase.closeDatabase();

            try (InputStream in = requireContext().getContentResolver().openInputStream(uri);
                 OutputStream out = new FileOutputStream(requireContext().getDatabasePath(AppDatabase.DATABASE_NAME))) {

                if (in == null) throw new Exception("InputStream is null");

                byte[] buffer = new byte[8192];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                out.flush();

                Toast.makeText(requireContext(), "Restauration réussie", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Erreur lors de la restauration", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case RESTORE_DATA:
                if (getContext() != null && data != null)
                    backupData(data.getData());
                break;
        }
    }

    private void backupData(Uri databasePath){
        AppDatabase.closeDatabase();

        File currentDb = getContext().getDatabasePath(AppDatabase.DATABASE_NAME);
        InputStream is = null;
        OutputStream os = null;
        try {
            if(databasePath != null){
                is = getContext().getContentResolver().openInputStream(databasePath);
                if(is != null){
                    os = new FileOutputStream(currentDb);
                    byte[] buffer = new byte[4096];
                    int read;

                    while ((read = is.read(buffer)) != -1) {
                        os.write(buffer, 0, read);
                    }

                    os.flush();

                    Toast.makeText(getContext(), R.string.backup_restored, Toast.LENGTH_LONG).show();
                }else
                    Toast.makeText(getContext(), R.string.backup_restored_error, Toast.LENGTH_LONG).show();
            }else
                Toast.makeText(getContext(), R.string.backup_restored_error, Toast.LENGTH_LONG).show();
        } catch (IOException ignore) {
            Toast.makeText(getContext(), R.string.backup_restored_error, Toast.LENGTH_LONG).show();
        }finally {
            try { if(is != null) is.close(); } catch (IOException ignore) {}
            try { if(os != null) os.close(); } catch (IOException ignore) {}
        }
    }
}
