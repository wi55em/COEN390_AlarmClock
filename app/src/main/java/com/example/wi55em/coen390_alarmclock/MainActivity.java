package com.example.wi55em.coen390_alarmclock;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected FloatingActionButton fab;
    protected ListView listView;
    protected ArrayList<Alarm> myAlarms = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupUI();
    }

    private void setupUI() {

        listView = findViewById(R.id.editListView);

        fab = findViewById(R.id.fab_plus);
        fab.setImageResource(R.drawable.ic_action_add);
        fab.setBackgroundColor(R.color.violet);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCreateAlarmActivity();
            }
        });

        loadListView();
    }

    protected void loadListView() {

        DatabaseHelper dbhelper = new DatabaseHelper(this);
        myAlarms = dbhelper.getAllAlarms();
        ArrayList<String> listAlarms = new ArrayList<>();

        if (myAlarms.size() <= 0) {
            listAlarms.add("No alarm clock added yet" );
        }

        for(Alarm i: myAlarms) {
            listAlarms.add(i.getHour() + ":" + i.getMinute());
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listAlarms);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToCreateAlarmActivity(position);
            }
        });
    }

    private void goToCreateAlarmActivity(int position) {
        Intent i = new Intent(this, CreateAlarmActivity.class);
        i.putExtra("position", position);
        startActivity(i);
    }

    private void goToCreateAlarmActivity() {
        Intent i = new Intent(this, CreateAlarmActivity.class);
        startActivity(i);
    }
}
