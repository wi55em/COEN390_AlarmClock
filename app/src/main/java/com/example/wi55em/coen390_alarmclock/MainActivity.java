package com.example.wi55em.coen390_alarmclock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    protected FloatingActionButton fab;
    protected ListView listView;
    protected TextView noAlarm;
    protected ImageView imgSleep;
    protected ArrayList<Alarm> myAlarms = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupUI();
    }

    @SuppressLint("ResourceAsColor")
    private void setupUI() {

        listView = findViewById(R.id.editListView);
        noAlarm = findViewById(R.id.editNoAlarm);
        imgSleep = findViewById(R.id.editImageView);

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
            listView.setVisibility(View.INVISIBLE);
            noAlarm.setVisibility(View.VISIBLE);
            imgSleep.setVisibility(View.VISIBLE);
        }

        for(Alarm i: myAlarms) {
            noAlarm.setVisibility(View.INVISIBLE);
            imgSleep.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            listAlarms.add(i.getHour() + ":" + i.getMinute());
        }

        //This method is used to change the color of the ListView text
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, listAlarms){

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);

                TextView textView=(TextView) view.findViewById(android.R.id.text1);

                /*YOUR CHOICE OF COLOR*/
                textView.setTextColor(Color.WHITE);

                return view;
            }
        };
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
