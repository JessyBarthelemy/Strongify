package com.jessy_barthelemy.strongify.database;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.jessy_barthelemy.strongify.R;
import com.jessy_barthelemy.strongify.database.dao.BodyMeasurementsDao;
import com.jessy_barthelemy.strongify.database.dao.EquipmentDao;
import com.jessy_barthelemy.strongify.database.dao.ExerciseDao;
import com.jessy_barthelemy.strongify.database.dao.ExerciseEquipmentDao;
import com.jessy_barthelemy.strongify.database.dao.ExerciseGroupDao;
import com.jessy_barthelemy.strongify.database.dao.ExerciseHistoryDao;
import com.jessy_barthelemy.strongify.database.dao.ProfileDao;
import com.jessy_barthelemy.strongify.database.dao.SetExecutionDao;
import com.jessy_barthelemy.strongify.database.dao.SupersetDao;
import com.jessy_barthelemy.strongify.database.dao.WeightMeasureDao;
import com.jessy_barthelemy.strongify.database.dao.WorkoutDao;
import com.jessy_barthelemy.strongify.database.dao.WorkoutExecutionDao;
import com.jessy_barthelemy.strongify.database.dao.WorkoutHistoryDao;
import com.jessy_barthelemy.strongify.database.entity.BodyMeasurements;
import com.jessy_barthelemy.strongify.database.entity.Equipment;
import com.jessy_barthelemy.strongify.database.entity.Exercise;
import com.jessy_barthelemy.strongify.database.entity.ExerciseEquipment;
import com.jessy_barthelemy.strongify.database.entity.ExerciseGroup;
import com.jessy_barthelemy.strongify.database.entity.ExerciseHistory;
import com.jessy_barthelemy.strongify.database.entity.Profile;
import com.jessy_barthelemy.strongify.database.entity.SetExecution;
import com.jessy_barthelemy.strongify.database.entity.Superset;
import com.jessy_barthelemy.strongify.database.entity.WeightMeasure;
import com.jessy_barthelemy.strongify.database.entity.Workout;
import com.jessy_barthelemy.strongify.database.entity.WorkoutExecution;
import com.jessy_barthelemy.strongify.database.entity.WorkoutHistory;


import java.io.IOException;

@Database(entities = {Workout.class, WorkoutHistory.class, Exercise.class,
        WorkoutExecution.class, SetExecution.class, Superset.class,
        ExerciseEquipment.class, Equipment.class, ExerciseGroup.class,
        Profile.class, WeightMeasure.class, BodyMeasurements.class, ExerciseHistory.class}, version = 6
        , exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "strongify";

    private static AppDatabase instance;

    public abstract WorkoutDao workoutDao();

    public abstract ExerciseDao exerciseDao();

    public abstract SetExecutionDao setDao();

    public abstract WorkoutHistoryDao historyDao();

    public abstract WorkoutExecutionDao workoutExecutionDao();

    public abstract EquipmentDao equipmentDao();

    public abstract ExerciseEquipmentDao exerciseEquipmentDao();

    public abstract ExerciseGroupDao exerciseGroupDao();

    public abstract SupersetDao supersetDao();

    public abstract ProfileDao profileDao();

    public abstract WeightMeasureDao weightMeasureDao();

    public abstract BodyMeasurementsDao bodyMeasurementsDao();

    public abstract ExerciseHistoryDao exerciseHistoryDao();

    public static AppDatabase getAppDatabase(final Context context) {
        if (instance == null) {
            RoomDatabase.Callback callback = new RoomDatabase.Callback() {
                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);
                    try {
                        PopulateDatabase.insertFromFile(context, R.raw.strongify, db);
                    } catch (IOException e) {
                    }
                }
            };

            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .addCallback(callback)
                    .addMigrations(new Migration(1, 2) {
                        @Override
                        public void migrate(@NonNull SupportSQLiteDatabase database) {
                            database.execSQL("ALTER TABLE 'Exercise' ADD COLUMN 'path' TEXT");

                            //Workout execution
                            database.execSQL("BEGIN TRANSACTION;");
                            database.execSQL("CREATE TABLE IF NOT EXISTS `WorkoutExecution_temp` " +
                                    "(`executionId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                    " `workoutId` INTEGER NOT NULL, " +
                                    " `order` INTEGER NOT NULL," +
                                    " `restTime` INTEGER NOT NULL, " +
                                    " `supersetOrder` INTEGER NOT NULL," +
                                    " `exerciseId` INTEGER NOT NULL," +
                                    " `supersetId` INTEGER," +
                                    " FOREIGN KEY(`workoutId`) REFERENCES `Workout`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`exerciseId`) REFERENCES `Exercise`(`eId`) ON UPDATE NO ACTION ON DELETE CASCADE );");

                            database.execSQL("INSERT INTO WorkoutExecution_temp SELECT executionId, workoutId, `order`, restTime, supersetOrder, eId, supersetId FROM WorkoutExecution;");
                            database.execSQL("DROP TABLE WorkoutExecution;");
                            database.execSQL("ALTER TABLE WorkoutExecution_temp RENAME TO WorkoutExecution;");
                            database.execSQL("COMMIT;");

                            //Workout
                            database.execSQL("BEGIN TRANSACTION;");
                            database.execSQL("CREATE TABLE IF NOT EXISTS `Workout_temp` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `periodicity` TEXT, `order` INTEGER NOT NULL)");

                            database.execSQL("INSERT INTO Workout_temp SELECT id, name, periodicity, `order` FROM Workout;");
                            database.execSQL("DROP TABLE Workout;");
                            database.execSQL("ALTER TABLE Workout_temp RENAME TO Workout;");
                            database.execSQL("COMMIT;");
                        }
                    }).addMigrations(new Migration(2, 3) {
                        @Override
                        public void migrate(@NonNull SupportSQLiteDatabase database) {
                            database.execSQL("ALTER TABLE 'Workout' ADD COLUMN 'reminderHour' INTEGER");
                            database.execSQL("ALTER TABLE 'Workout' ADD COLUMN 'reminderMinute' INTEGER");
                        }
                    }).addMigrations(new Migration(3, 4) {
                        @Override
                        public void migrate(@NonNull SupportSQLiteDatabase database) {
                            database.execSQL("CREATE TABLE IF NOT EXISTS `Profile` " +
                                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                    " `name` TEXT, " +
                                    " `size` INTEGER NOT NULL, " +
                                    " `goal` INTEGER NOT NULL," +
                                    " `age` INTEGER NOT NULL " +
                                    ");");

                            database.execSQL("CREATE INDEX IF NOT EXISTS `IndexGroupId` ON `Exercise` (`groupId`)");
                            database.execSQL("CREATE INDEX IF NOT EXISTS `IndexWorkoutId` ON `WorkoutExecution` (`workoutId`)");
                            database.execSQL("CREATE INDEX IF NOT EXISTS `IndexExerciseId` ON `WorkoutExecution` (`exerciseId`)");
                            database.execSQL("CREATE INDEX IF NOT EXISTS `IndexExecId` ON `SetExecution` (`execId`)");

                            database.execSQL("CREATE TABLE IF NOT EXISTS `WeightMeasure` " +
                                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                    " `weight` REAL NOT NULL, " +
                                    " `fat` REAL NOT NULL, " +
                                    " `water` REAL NOT NULL," +
                                    " `bones` REAL NOT NULL, " +
                                    " `muscle` REAL NOT NULL, " +
                                    " `weightDate` INTEGER NOT NULL " +
                                    ");");
                        }
                    }).addMigrations(new Migration(4, 5) {
                        @Override
                        public void migrate(@NonNull SupportSQLiteDatabase database) {
                            database.execSQL("CREATE TABLE IF NOT EXISTS `BodyMeasurements` " +
                                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                    " `shoulders` REAL NOT NULL, " +
                                    " `chest` REAL NOT NULL, " +
                                    " `bicepsLeft` REAL NOT NULL," +
                                    " `bicepsRight` REAL NOT NULL, " +
                                    " `forearmLeft` REAL NOT NULL, " +
                                    " `forearmRight` REAL NOT NULL, " +
                                    " `neck` REAL NOT NULL, " +
                                    " `wristRight` REAL NOT NULL, " +
                                    " `wristLeft` REAL NOT NULL, " +
                                    " `waist` REAL NOT NULL, " +
                                    " `hips` REAL NOT NULL, " +
                                    " `thighLeft` REAL NOT NULL, " +
                                    " `thighRight` REAL NOT NULL, " +
                                    " `calfLeft` REAL NOT NULL, " +
                                    " `calfRight` REAL NOT NULL, " +
                                    " `anklesLeft` REAL NOT NULL, " +
                                    " `anklesRight` REAL NOT NULL, " +
                                    " `measureDate` INTEGER NOT NULL " +
                                    ");");
                        }
                    }).addMigrations(new Migration(5, 6) {
                        @Override
                        public void migrate(@NonNull SupportSQLiteDatabase database) {
                            database.execSQL("CREATE TABLE IF NOT EXISTS `ExerciseHistory` " +
                                    "(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                                    " `repetitions` INTEGER NOT NULL, " +
                                    " `weight` REAL NOT NULL, " +
                                    " `measureDate` INTEGER NOT NULL, " +
                                    " `exerciseId` INTEGER NOT NULL, " +
                                    " FOREIGN KEY(`exerciseId`) REFERENCES `Exercise`(`eId`) ON UPDATE NO ACTION ON DELETE CASCADE"+
                                    ");");


                            database.execSQL("ALTER TABLE 'ExerciseHistory' ADD COLUMN 'historyId' TEXT");

                            database.execSQL("ALTER TABLE 'WorkoutHistory' ADD COLUMN 'hId' TEXT");
                        }

                    }).build();
        }
        return instance;
    }

    public static void closeDatabase(){
        if(instance != null)
            instance.close();
        instance = null;
    }
}

