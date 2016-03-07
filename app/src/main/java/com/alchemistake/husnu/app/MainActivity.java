package com.alchemistake.husnu.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import com.melnykov.fab.FloatingActionButton;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private TaskAdapter taskAdapter;
    private FloatingActionButton fab;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView)findViewById(R.id.rv);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        sharedPreferences = getSharedPreferences(getString(R.string.db_name),MODE_PRIVATE);
        editor = sharedPreferences.edit();

        taskAdapter = new TaskAdapter(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setAdapter(taskAdapter);
        rv.setHasFixedSize(false);

        fab.attachToRecyclerView(rv);

        Calendar calendar = new GregorianCalendar();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int oldWeek = sharedPreferences.getInt("week",week);
        Log.w("OLD WEEK", String.valueOf(oldWeek));
        Log.w("WEEK", String.valueOf(week));

        //taskAdapter.undoWeekly();

        if(oldWeek != week){
            taskAdapter.weekly();
            editor.putInt("week",week).apply();
            Log.w("WEEK", "WEEK IS UPDATED");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskAdapter = new TaskAdapter(this);
        rv.setAdapter(taskAdapter);
    }

    public void addTask (View view){
        Intent intent = new Intent(this,TaskActivity.class);
        intent.putExtra(getString(R.string.taskNo),taskAdapter.getItemCount());
        startActivity(intent);
    }
}
