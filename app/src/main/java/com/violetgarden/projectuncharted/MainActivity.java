package com.violetgarden.projectuncharted;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static TaskFactory factory;
    TaskViewModel viewModel;
    List<Task> dataTasks;

    int currentTimeRemaining;
    String currentTaskName;

    TextView timeRemaining;
    public static boolean timerRunning = false;
    public static boolean START_TIMER_AFTER_UPDATE = false;
    public long timeTillFinish;

    Button pause;
    Button stop;
    Button finishButton;
    CountDownTimer timer;
    TextView timeRemainingLabel;
    RecyclerView recyclerView;
    TextView nothing;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            String taskName = data.getStringExtra("TASK_NAME");
            int taskDuration = data.getIntExtra("TASK_DURATION", -1);

            Log.println(4, "A1", taskDuration + "");
            if (taskDuration == -1) return;

            viewModel.insert(new Task(taskName, taskDuration));

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class TaskFactory implements ViewModelProvider.Factory {
        Application application;

        public TaskFactory(Application application) {
            this.application = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(TaskViewModel.class)) {
                return (T) new TaskViewModel(application);
            }
            throw new IllegalArgumentException("ViewModel is not Task");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        recyclerView = findViewById(R.id.recyclerView);
        nothing = findViewById(R.id.nothingToDo);
        pause = findViewById(R.id.pauseBtn);
        stop = findViewById(R.id.stopBtn);
        finishButton = findViewById(R.id.finishButton);
        FloatingActionButton add = findViewById(R.id.addButton);
        timeRemainingLabel = findViewById(R.id.timeRemaining);
        timeRemaining = findViewById(R.id.timeCounter);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        factory = new TaskFactory(getApplication());
        viewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);
        final TaskAdapter adapter = new TaskAdapter();

        recyclerView.setAdapter(adapter);

        viewModel.getAllTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                dataTasks = tasks;

                if(dataTasks.size() <= 0){
                    START_TIMER_AFTER_UPDATE = false;
                    setStateNothing();
                } else {
                    setStateVisible();
                    //Render the top task to remaining time;
                    currentTimeRemaining = tasks.get(0).timeRequired * 1000;
                    timeTillFinish = currentTimeRemaining;
                    currentTaskName = tasks.get(0).taskName;
                    renderTask(tasks.get(0));
                }

                if(START_TIMER_AFTER_UPDATE){
                    try {
                        Thread.sleep(700);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startStopTimer();
                    START_TIMER_AFTER_UPDATE = false;
                }

                adapter.submitList(tasks);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.START | ItemTouchHelper.END | ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public float getSwipeEscapeVelocity(float defaultValue) {
                return 20f * defaultValue;
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.75f;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewModel.update(dataTasks);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int position = viewHolder.getAdapterPosition();
                int newPosition = target.getAdapterPosition();

                int oldItemId = adapter.getTaskAt(position).getId();
                int newItemId = adapter.getTaskAt(newPosition).getId();

                adapter.getTaskAt(position).setId(newItemId);
                adapter.getTaskAt(newPosition).setId(oldItemId);

                Collections.swap(dataTasks, position, newPosition);
                adapter.notifyItemMoved(position, newPosition);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                viewModel.delete(adapter.getTaskAt(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);

        togglePauseStartButton();


        //setting up the buttons
        pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startStopTimer();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
            }
        });
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!timerRunning) return;
                if (adapter.getTaskAt(0) == null) {
                    setStateNothing();
                    return;
                };
                if(adapter.getItemCount() <= 0){
                    Log.println(4, "A1", "Cannot identify");
                }
                stopTimer();
                viewModel.delete(adapter.getTaskAt(0));
                adapter.notifyItemRemoved(0);
                START_TIMER_AFTER_UPDATE = true;
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void setStateNothing(){
        timeRemaining.setVisibility(View.INVISIBLE);
        timeRemainingLabel.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.INVISIBLE);
        finishButton.setVisibility(View.INVISIBLE);
        nothing.setVisibility(View.VISIBLE);
    }

    public void setStateVisible(){
        timeRemaining.setVisibility(View.VISIBLE);
        timeRemainingLabel.setVisibility(View.VISIBLE);
        stop.setVisibility(View.VISIBLE);
        pause.setVisibility(View.VISIBLE);
        finishButton.setVisibility(View.VISIBLE);
        nothing.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void stopTimer() {
        if (!timerRunning && timeTillFinish == currentTimeRemaining) return;
        if (timerRunning) {
            timer.cancel();
        }
        timerRunning = false;
        timeTillFinish = currentTimeRemaining;
        togglePauseStartButton();
        renderTime(timeTillFinish / 1000);
    }

    public void startStopTimer() {
        if (!timerRunning) {
            timerRunning = true;
            togglePauseStartButton();
            timer = new CountDownTimer(timeTillFinish, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeTillFinish = millisUntilFinished;
                    renderTime((int) timeTillFinish / 1000);
                }

                @Override
                public void onFinish() {
                    Log.println(4, "A1", "DUCKING FINISHED");
                    togglePauseStartButton();
                }
            };
            timer.start();
        } else {
            Log.println(4, "A1", timeTillFinish + "");
            timerRunning = false;
            togglePauseStartButton();
            timer.cancel();
        }
    }

    public void togglePauseStartButton() {
        if (timerRunning) {
            pause.setText("Pause");
            return;
        }
        pause.setText("Start");
    }

    public void renderTask(Task task) {
        timeRemaining.setText(convertToFormat(task.getTimeRequired()));
    }

    public void renderTime(long seconds) {
        timeRemaining.setText(convertToFormat((int) seconds));
    }

    public String convertToFormat(int seconds) {
        int hours = seconds / 3600;
        seconds %= 3600;
        int minutes = seconds / 60;
        seconds %= 60;

        String hourText = hours == 0 || hours < 10 ? "0" + hours + ":" : hours + ":";
        String minuteText = minutes == 0 || minutes < 10 ? "0" + minutes + ":" : minutes + ":";
        String secondText = seconds == 0 || seconds < 10 ? "0" + seconds : seconds + "";

        return hourText + minuteText + secondText;
    }
}
