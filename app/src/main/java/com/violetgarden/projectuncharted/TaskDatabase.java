package com.violetgarden.projectuncharted;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    public static TaskDatabase database;
    public abstract TaskDao getDao();

    public static synchronized TaskDatabase getDatabase(Context context){
        if(database == null){
            database = Room.databaseBuilder(context.getApplicationContext(), TaskDatabase.class, "apocyptic_message")
                        .fallbackToDestructiveMigration()
                        .addCallback(callback)
                        .build();
        }
        return database;
    }

    public static RoomDatabase.Callback callback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //new PopulateAsyncTask(database).execute();
        }
    };


    private static class PopulateAsyncTask extends AsyncTask<Void, Void, Void> {
        private TaskDao taskDao;

        public PopulateAsyncTask(TaskDatabase db) {
            this.taskDao = db.getDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.println(4, "IMPORTANT", "ITS WORKING");
            taskDao.insert(new Task("Example", 122222));
            taskDao.insert(new Task("Example", 60 * 10));
            taskDao.insert(new Task("Example", 60 * 10));
            taskDao.insert(new Task("Example", 60 * 10));
            taskDao.insert(new Task("Example", 60 * 10));
            taskDao.insert(new Task("Example", 60 * 10));
            taskDao.insert(new Task("Example", 60 * 10));
            taskDao.insert(new Task("Example", 60 * 10));
            return null;
        }
    }

}
