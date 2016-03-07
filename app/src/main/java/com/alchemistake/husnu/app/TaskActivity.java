package com.alchemistake.husnu.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class TaskActivity extends AppCompatActivity {

    private int taskNo;
    private ArrayList<Task> tasks;
    private Task task;

    private EditText taskName, weeklyHour, weeklyMinute, fixxerHour, fixxerMinute;
    private TextView currentHour;
    private Button save, delete;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_task);

        taskNo = getIntent().getIntExtra(getString(R.string.taskNo), -1);
        sharedPreferences = getSharedPreferences(getString(R.string.db_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();

        try {
            tasks = new ArrayList<Task>(Arrays.asList(gson.fromJson(sharedPreferences.getString(getString(R.string.key), "{}"), Task[].class)));
        } catch (Exception e) {
            tasks = new ArrayList<Task>();
        }

        taskName = (EditText) findViewById(R.id.taskName);
        weeklyHour = (EditText) findViewById(R.id.weeklyHour);
        weeklyMinute = (EditText) findViewById(R.id.weeklyMinute);
        fixxerHour = (EditText) findViewById(R.id.fixxerHour);
        fixxerMinute = (EditText) findViewById(R.id.fixxerMinute);
        currentHour = (TextView) findViewById(R.id.currentHour);
        save = (Button) findViewById(R.id.save);
        delete = (Button) findViewById(R.id.delete);


        if (taskNo < tasks.size()) {
            task = tasks.get(taskNo);
            updateViews();
        }

        fixxerMinute.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    save(null);
                    handled = true;
                }
                return handled;
            }
        });
    }

    public void save(View view) {
        if (task == null) {
            task = new Task(taskName.getText().toString(),inputHandler(weeklyHour,weeklyMinute));
            tasks.add(task);
        } else {
            task.setWeeklyHour(inputHandler(weeklyHour,weeklyMinute));
            task.setName(taskName.getText().toString());
            tasks.set(taskNo, task);
        }

        if (task != null && (!fixxerHour.getText().toString().isEmpty() || !fixxerMinute.getText().toString().isEmpty())) {
            task.setCurrentHour(inputHandler(fixxerHour,fixxerMinute));
        }

        commit();
        finish();
    }

    public void delete(View view) {
        if (task != null) {
            tasks.remove(taskNo);
            commit();
        }
        finish();
    }

    private void commit() {
        Task[] arr = tasks.toArray(new Task[tasks.size()]);
        String save = gson.toJson(arr);

        editor.putString(getString(R.string.key), save).commit();
    }

    private void updateViews() {
        taskName.setText(task.getName());
        int weekH = (int)task.getWeeklyHour();
        int weekM = (int)((task.getWeeklyHour()-weekH) * 60);
        weeklyHour.setText(String.valueOf(weekH));
        weeklyMinute.setText(String.valueOf(weekM / 10) + weekM % 10);

        currentHour.setText(format(task.getCurrentHour()));
    }

    private String format(double d){
        int curH = (int)task.getCurrentHour();
        int curM = (int)((task.getCurrentHour()-curH) * 60);
        return String.valueOf(curH) + ":" + curM / 10 + curM % 10;
    }

    private double inputHandler(EditText hour, EditText minute){
        String h = hour.getText().toString();
        String m = minute.getText().toString();

        if(h.isEmpty())
            h = "0";
        if(m.isEmpty())
            m = "0";

        try{
            return Double.parseDouble(h) + Double.parseDouble(m) / 60.0;
        }catch (Exception e){
            Log.e("Parse Error","Error on hour and minute parsing");
        }
        return  -1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        save(null);
    }
}
