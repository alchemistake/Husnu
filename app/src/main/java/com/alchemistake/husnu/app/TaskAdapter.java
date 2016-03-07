package com.alchemistake.husnu.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.gson.Gson;

/**
 * Created by Alchemistake on 08/02/16.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private Context context;
    private Task[] tasks;
    private String arrGson;
    private Gson gson;
    private SharedPreferences sh;
    private SharedPreferences.Editor edit;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tile_tasks, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OnClickListener listener = new OnClickListener(holder, position);

        holder.taskName.setText(listener.task.getName());
        holder.base.setBackgroundColor(listener.task.statusColor());

        if(listener.task.isRunning())
            holder.startStop.setText("Stop");
        holder.base.setOnClickListener(listener);
        holder.startStop.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        if(tasks != null)
            return tasks.length;
        return 0;
    }

    public TaskAdapter(Context c) {
        context = c;
        gson = new Gson();
        sh = context.getSharedPreferences(context.getString(R.string.db_name), Context.MODE_PRIVATE);
        edit = sh.edit();
        arrGson = sh.getString(context.getString(R.string.key), "{}");
        updateDataSet();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName;
        Button startStop;
        CardView base;

        public ViewHolder(View itemView) {
            super(itemView);
            taskName = (TextView) itemView.findViewById(R.id.taskName);
            startStop = (Button) itemView.findViewById(R.id.startStop);
            base = (CardView) itemView.findViewById(R.id.base);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void updateColor(int oldColor, int newColor) {
            ColorDrawable[] color = {new ColorDrawable(oldColor), new ColorDrawable(newColor)};
            TransitionDrawable trans = new TransitionDrawable(color);
            base.setBackground(trans);
            trans.startTransition(1000);
        }
    }

    private class OnClickListener implements View.OnClickListener {
        public Task task;
        private ViewHolder holder;
        private int taskNo;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.base:
                    Intent intent = new Intent(context,TaskActivity.class);
                    intent.putExtra(context.getString(R.string.taskNo),taskNo);
                    context.startActivity(intent);
                    break;
                case R.id.startStop:
                    if (task.isRunning()) {
                        int old = task.statusColor();
                        task.stop();
                        holder.updateColor(old,task.statusColor());
                        ((Button) v).setText("Start");
                    } else {
                        task.start();
                        ((Button) v).setText("Stop");
                    }
                    updateGSON(taskNo,task);
                    break;
            }
        }

        public OnClickListener(ViewHolder holder, int position) {
            this.task = tasks[position];
            this.holder = holder;
            this.taskNo = position;
        }
    }


    public void updateDataSet(){
        try {
            tasks = gson.fromJson(arrGson, Task[].class);
            Log.v("Update Dataset","Success");
        } catch (Exception e) {
            tasks = new Task[0];
            Log.v("Update Dataset","Unsuccess");
        }
    }

    public void updateGSON(int taskNo,Task task){
        tasks[taskNo] = task;
        updateGSON();
    }

    public void updateGSON(){
        edit.putString(context.getString(R.string.key),gson.toJson(tasks)).commit();
    }

    public void weekly(){
        for (int i = 0; i < tasks.length; i++) {
            tasks[i].update();
        }
        updateGSON();
    }

    public void undoWeekly(){
        for (int i = 0; i < tasks.length; i++) {
            tasks[i].downdate();
        }
        updateGSON();
    }
}
