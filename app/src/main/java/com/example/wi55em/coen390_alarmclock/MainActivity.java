package com.example.wi55em.coen390_alarmclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wi55em.coen390_alarmclock.Bluetooth.BluetoothController;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    protected BluetoothController bleController;
    protected AlarmManager alarmManager;

    protected FloatingActionButton fab;
    protected ListView listView;
    protected TextView noAlarm;
    protected ImageView imgSleep;

    protected Button alarmPage;
    protected Button timerPage;
    protected Button clockwatchPage;
    protected Button connectPage;

    protected ArrayList<Alarm> myAlarms = new ArrayList<>();
    protected ArrayList<String> listAlarms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //bleController.onConectionStateChange(BlunoLibrary.connectionStateEnum.isScanning);



    }

    @SuppressLint("ResourceAsColor")
    private void setupUI() {

        fab = findViewById(R.id.fab_plus);
        fab.setImageResource(R.drawable.ic_action_add);
        fab.setBackgroundColor(R.color.violet);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAlarmActivity dialog = new CreateAlarmActivity();
                dialog.show(getSupportFragmentManager(), "Create Alarm");
            }
        });

        listView = findViewById(R.id.editListView);
        noAlarm = findViewById(R.id.editNoAlarm);
        imgSleep = findViewById(R.id.editImageView);

        alarmPage = findViewById(R.id.AlarmPage);
        timerPage = findViewById(R.id.TimerPage);
        clockwatchPage = findViewById(R.id.ClockwatchPage);
        connectPage = findViewById(R.id.ConnectPage);
        connectPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBluetoothControllerActivity();
            }
        });


        loadListView();
    }

    protected void loadListView() {

        DatabaseHelper dbhelper = new DatabaseHelper(this);
        myAlarms = dbhelper.getAllAlarms();
        listAlarms = new ArrayList<>();

        if (myAlarms.size() <= 0) {
            listView.setVisibility(View.INVISIBLE);
            noAlarm.setVisibility(View.VISIBLE);
            imgSleep.setVisibility(View.VISIBLE);
        }

        for(Alarm i: myAlarms) {
            noAlarm.setVisibility(View.INVISIBLE);
            imgSleep.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            listAlarms.add(i.getHour() + ":" + i.getMinute() + ":" + i.getDays());
        }

        SwitchListAdapter adap = new SwitchListAdapter(this, listAlarms);
        listView.setAdapter(adap);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), listAlarms.get(position) + " selected", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToBluetoothControllerActivity() {
        Intent i;
        i = new Intent(this, BluetoothController.class);
        startActivity(i);
    }

    public class SwitchListAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> values;

        public SwitchListAdapter(Context context, ArrayList<String> values) {
            super(context, R.layout.activity_main, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.row_layout, parent, false);

            if(position%2 == 0) rowView.setBackgroundColor(getResources().getColor(R.color.pink));
            else rowView.setBackgroundColor(getResources().getColor(R.color.pink2));

            TextView time = rowView.findViewById(R.id.time);
            TextView am = rowView.findViewById(R.id.am);
            TextView pm = rowView.findViewById(R.id.pm);
            TextView days = rowView.findViewById(R.id.days);
            Switch toggleButton = rowView.findViewById(R.id.switch1);

            toggleButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), values.get(position) + " checked", Toast.LENGTH_LONG).show();
                }
            });

            am.setTypeface(null, Typeface.NORMAL);
            pm.setTypeface(null, Typeface.NORMAL);
            am.setText("AM");
            pm.setText("PM");

            String[] split = values.get(position).split(":");
            String sTime = split[0];
            String sMin = split[1];
            String sDays = split[2];

            int t = Integer.parseInt(sTime);

            if(t > 12) {
                t = t - 12;
                sTime = "" + t;
                pm.setTypeface(null, Typeface.BOLD);
            } else {
                am.setTypeface(null, Typeface.BOLD);
            }

            if(t == 0)
                sTime = "0" + sTime;

            if(sMin.length() < 2)
                sMin = "0" + sMin;

            time.setText(sTime + ":" + sMin);

            String d = "";

            if(sDays.contains("1"))  d += "Mo ";
            if(sDays.contains("2"))  d += "Tu ";
            if(sDays.contains("3"))  d += "We ";
            if(sDays.contains("4"))  d += "Th ";
            if(sDays.contains("5"))  d += "Fr ";
            if(sDays.contains("6"))  d += "Sa ";
            if(sDays.contains("7"))  d += "Su ";

            days.setText(d);

            return rowView;
        }
    }

}
