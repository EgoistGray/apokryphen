package com.violetgarden.projectuncharted;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    public static SimpleDateFormat timeFormatter;

    public Task getTaskAt(int position) {
        return getItem(position);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView timeRemaining;
        TextView taskName;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            timeRemaining = itemView.findViewById(R.id.timeRemainingForThisTask);
            taskName = itemView.findViewById(R.id.taskTitle);

        }
    }

    public TaskAdapter() {
        super(new DiffUtil.ItemCallback<Task>() {
            @Override
            public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return newItem.getId() == oldItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.getTaskName().equals(newItem.getTaskName()) && oldItem.getTimeRequired() == newItem.getTimeRequired();
            }
        });
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);
        return new TaskAdapter.TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task current_task = getItem(position);
        holder.timeRemaining.setText(convertForItem(current_task.getTimeRequired()));
        holder.taskName.setText(current_task.getTaskName());
    }


    public String convertForItem(int seconds) {
        int hours = seconds / 3600;
        seconds %= 3600;
        int minutes = seconds / 60;
        seconds %= 60;

        String hourText = hours != 0 ? hours + " hours " : "";
        String minuteText = minutes != 0 ? minutes + " minutes " : "";
        String secondText = seconds != 0 ? seconds + " seconds " : "";

        if (hours < 10 && hours != 0) {
            hourText = "0" + hourText;
        }
        if (minutes < 10 && minutes != 0) {
            minuteText = "0" + minuteText;
        }
        if (seconds < 10 && seconds != 0) {
            secondText = "0" + secondText;
        }

        return hourText + minuteText + secondText;
    }

}

