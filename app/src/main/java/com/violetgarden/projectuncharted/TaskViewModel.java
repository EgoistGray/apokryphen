package com.violetgarden.projectuncharted;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository repository;
    private LiveData<List<Task>> tasks;
    private List<Task> nonLiveTask;

    public TaskViewModel(@NonNull Application application) {
        super(application);

        repository = new TaskRepository(application);
        tasks = repository.getAllTasks();
    }

    public void insert(Task task){
        repository.insert(task);
    }

    public void delete(Task task){
        repository.delete(task);
    }

    public void update(List<Task> tasks){
        repository.update(tasks);
    }

    public LiveData<List<Task>> getAllTasks(){
        return tasks;
    }
}
