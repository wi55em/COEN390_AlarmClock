package com.example.wi55em.coen390_alarmclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.wi55em.coen390_alarmclock.Bluetooth.BluetoothController;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{

    protected Context context = this;
    protected BluetoothController bleController;
    protected AlarmManager alarmManager;

    /**
     * These variable are used for menu
     */
    protected ViewFlipper viewFlipper;
    protected Button alarmPage;
    protected Button timerPage;
    protected Button clockwatchPage;
    protected Button connectPage;

    /**
     * These variables are used for alarm page
     */
    protected FloatingActionButton fab;
    protected ListView listView;
    protected TextView noAlarm;
    protected ImageView imgSleep;
    protected ArrayList<Alarm> myAlarms = new ArrayList<>();
    protected ArrayList<String> listAlarms = new ArrayList<>();

    /**
     * These variable are used for timer page
     */
    private EditText mEditTextInput;
    private TextView mTextViewCountDown;
    private Button mButtonSet;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;// See the timer is running or not
    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;



    private float initialX;

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

        viewFlipper = findViewById(R.id.flipper);
        viewFlipper.setInAnimation(this, android.R.anim.fade_in);
        viewFlipper.setOutAnimation(this, android.R.anim.fade_out);

        alarmPage = findViewById(R.id.AlarmPage);
        alarmPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmPage.setBackgroundColor(getResources().getColor(R.color.violet));
                timerPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                clockwatchPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                viewFlipper.setDisplayedChild(0);
            }
        });

        timerPage = findViewById(R.id.TimerPage);
        timerPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                timerPage.setBackgroundColor(getResources().getColor(R.color.violet));
                clockwatchPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                viewFlipper.setDisplayedChild(1);
            }
        });

        clockwatchPage = findViewById(R.id.ClockwatchPage);
        clockwatchPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                timerPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                clockwatchPage.setBackgroundColor(getResources().getColor(R.color.violet));
                viewFlipper.setDisplayedChild(2);
            }
        });


        connectPage = findViewById(R.id.ConnectPage);
        connectPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBluetoothControllerActivity();
            }
        });

        mEditTextInput = findViewById(R.id.edit_text_input);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);

        mButtonSet = findViewById(R.id.button_set);
        mButtonStartPause = findViewById(R.id.button_start_pause);
        mButtonReset = findViewById(R.id.button_reset);

        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEditTextInput.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(MainActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(MainActivity.this, "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }

                setTime(millisInput);
                mEditTextInput.setText("");
            }
        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        loadListView();
    }

    protected void loadListView() {

        DatabaseHelper dbhelper = new DatabaseHelper(this);
        myAlarms = dbhelper.getAllAlarms();

        Alarm temp;
        for(int i = 0; i < myAlarms.size(); i++) {
            int h1 = myAlarms.get(i).getHour();
            int m1 = myAlarms.get(i).getMinute();
            for(int j = i; j < myAlarms.size(); j++) {
                int h2 = myAlarms.get(j).getHour();
                int m2 = myAlarms.get(j).getMinute();
                if (h2 < h1) {

                } else if (h2 == h1) {

                } else {

                }
            }
        }

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
            listAlarms.add(i.getHour() + ":" + i.getMinute() + ":" + i.getDays() + ":" + i.getOnOff());
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

    @Override
    public boolean onTouchEvent(MotionEvent touchevent) {
        switch (touchevent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                initialX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float finalX = touchevent.getX();
                if (initialX > finalX) {
                    if (viewFlipper.getDisplayedChild() == 2) {
                        viewFlipper.setDisplayedChild(0);
                        break;
                    }

                    viewFlipper.showNext();
                } else {
                    if (viewFlipper.getDisplayedChild() == 0) {
                        viewFlipper.setDisplayedChild(2);
                        break;
                    }

                    viewFlipper.showPrevious();
                }
                break;
        }

        switch(viewFlipper.getDisplayedChild()) {
            case 0:
                alarmPage.setBackgroundColor(getResources().getColor(R.color.violet));
                timerPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                clockwatchPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                break;
            case 1:
                alarmPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                timerPage.setBackgroundColor(getResources().getColor(R.color.violet));
                clockwatchPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                break;
            case 2:
                alarmPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                timerPage.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                clockwatchPage.setBackgroundColor(getResources().getColor(R.color.violet));
                break;
        }

        return false;
    }

    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateWatchInterface();
            }
        }.start();//After press the startTimer, it can be started immediatly

        mTimerRunning = true;
        updateWatchInterface();//The start button change to Pause button
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateWatchInterface();
    }

    private void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
        updateWatchInterface();
    }

    private void updateCountDownText() {
        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted;
        if (hours > 0) {                       //If we got the timer bigger than 1 hour using the format
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }
        mTextViewCountDown.setText(timeLeftFormatted);
    }

    private void updateWatchInterface() {
        if (mTimerRunning) {                       //You can hide the button while running or not hide them
            //mEditTextInput.setVisibility(View.INVISIBLE);
            //mButtonSet.setVisibility(View.INVISIBLE);
            //mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Pause");
        } else {
            mEditTextInput.setVisibility(View.VISIBLE);
            mButtonSet.setVisibility(View.VISIBLE);
            mButtonStartPause.setText("Start");

            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }

            if (mTimeLeftInMillis < mStartTimeInMillis) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        updateCountDownText();
        updateWatchInterface();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateWatchInterface();
            } else {
                startTimer();
            }
        }
    }

    @Override//This part used for background running
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("millisLeft", mTimeLeftInMillis);
        outState.putBoolean("timerRunning", mTimerRunning);
        outState.putLong("endTime", mEndTime);
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
            String onOff = split[3];

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

            days.setText(onOff);

            if(Boolean.parseBoolean(onOff))
                toggleButton.setChecked(true);
            else
                toggleButton.setChecked(false);

            return rowView;
        }
    }

}
