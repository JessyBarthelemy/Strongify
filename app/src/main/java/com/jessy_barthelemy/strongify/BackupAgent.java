package com.jessy_barthelemy.strongify;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class BackupAgent extends BackupAgentHelper{

    // The name of the SharedPreferences file
    static final String PREFS_BACKUP_KEY = "backup_key";

    @Override
    public void onCreate() {
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, getBaseContext().getPackageName() + "_preferences");
        addHelper(PREFS_BACKUP_KEY, helper);
    }
}
