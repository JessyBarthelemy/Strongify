package com.jessy_barthelemy.strongify.database;

import android.content.Context;

import androidx.sqlite.db.SupportSQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PopulateDatabase {
    public static void insertFromFile(Context context, int resourceCode, SupportSQLiteDatabase db) throws IOException{

        InputStream insertsStream = context.getResources().openRawResource(resourceCode);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

        while (insertReader.ready()) {
            String insertStmt = insertReader.readLine();
            if(insertStmt != null){
                if(insertStmt.isEmpty()) continue;
                db.execSQL(insertStmt);
            }
        }
        insertReader.close();
    }
}