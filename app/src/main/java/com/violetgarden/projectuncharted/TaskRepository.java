package com.violetgarden.projectuncharted;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskRepository {

    private TaskDao taskDao;
    private LiveData<List<Task>> tasks;
    private List<Task> nonLiveTask;

    public TaskRepository(Application application){
        TaskDatabase database =TaskDatabase.getDatabase(application);
        taskDao = database.getDao();
        tasks = taskDao.getAllFromDatabase();
    }

    public LiveData<List<Task>> getAllTasks(){
        return tasks;
    }

    public void insert(Task task){
        new InsertTask(taskDao).execute(task);
    }

    public void delete(Task task){
        new DeleteTask(taskDao).execute(task);
    }

    public void update(List<Task> tasks){ new UpdateTask(taskDao).execute(tasks);}

    public class InsertTask extends AsyncTask<Task, Void, Void>{

        private TaskDao taskDao;

        public InsertTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.insert(tasks[0]);
            return null;
        }
    }
    public class DeleteTask extends AsyncTask<Task, Void, Void>{

        private TaskDao taskDao;

        public DeleteTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(Task... tasks) {
            taskDao.delete(tasks[0]);
            return null;
        }
    }
    public class UpdateTask extends AsyncTask<List<Task>, Void, Void>{

        private TaskDao taskDao;

        public UpdateTask(TaskDao taskDao) {
            this.taskDao = taskDao;
        }

        @Override
        protected Void doInBackground(List<Task>... tasks) {
            taskDao.update(tasks[0]);
            return null;
        }
    }

}
