package com.example.wi55em.coen390_alarmclock;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;

import static com.example.wi55em.coen390_alarmclock.R.color.colorAccent;
import static com.example.wi55em.coen390_alarmclock.R.color.pink;
import static com.example.wi55em.coen390_alarmclock.R.color.violet;

public class CreateAlarmActivity extends DialogFragment {

    DatabaseHelper databaseHelper;
    protected TimePicker timePicker;
    protected Button btnSave;
    protected Button btnCancel;

    protected FloatingActionButton mon;
    protected FloatingActionButton tue;
    protected FloatingActionButton thu;
    protected FloatingActionButton wed;
    protected FloatingActionButton fri;
    protected FloatingActionButton sat;
    protected FloatingActionButton sun;

    boolean m, tu, w, th, f, sa, su;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflator, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflator.inflate(R.layout.activity_create_alarm, container, false);

        setupUI(view);

        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = timePicker.getHour();
                int min = timePicker.getMinute();
                String days = "";
                if(m) days += 1;
                if(tu) days += 2;
                if(w) days += 3;
                if(th) days += 4;
                if(f) days += 5;
                if(sa) days += 6;
                if(su) days += 7;
                int day;
                if(days == "")
                    day = 0;
                else day = Integer.parseInt(days);
                databaseHelper = new DatabaseHelper(getActivity());
                databaseHelper.insertAlarm(new Alarm(-1, hour, min, day, true));
                ((MainActivity)getActivity()).loadListView();
                getDialog().dismiss();
            }
        });

        return view;
    }

    @SuppressLint("ResourceAsColor")
    private void setupUI(View view) {

        timePicker = view.findViewById(R.id.simpleTimePicker);
        btnSave = view.findViewById(R.id.btnSave);
        btnCancel = view.findViewById(R.id.btnCancel);

        mon = view.findViewById(R.id.monday);
        tue = view.findViewById(R.id.tuesday);
        wed = view.findViewById(R.id.wednesday);
        thu = view.findViewById(R.id.thursday);
        fri = view.findViewById(R.id.friday);
        sat = view.findViewById(R.id.saturday);
        sun = view.findViewById(R.id.sunday);

        mon.setOnClickListener(new View.OnClickListener() {

            int time = 0;

            @Override
            public void onClick(View view) {
                if(time%2== 0) {
                    mon.setBackgroundTintList(getResources().getColorStateList(violet));
                    m = true;
                } else {
                    mon.setBackgroundTintList(getResources().getColorStateList(pink));
                    m = false;
                }
                time++;
            }
        });

        tue.setOnClickListener(new View.OnClickListener() {

            int time = 0;

            @Override
            public void onClick(View view) {
                if(time%2== 0) {
                    tue.setBackgroundTintList(getResources().getColorStateList(violet));
                    tu = true;
                } else {
                    tue.setBackgroundTintList(getResources().getColorStateList(pink));
                    tu = false;
                }
                time++;
            }
        });

        wed.setOnClickListener(new View.OnClickListener() {

            int time = 0;

            @Override
            public void onClick(View view) {
                if(time%2== 0) {
                    wed.setBackgroundTintList(getResources().getColorStateList(violet));
                    w = true;
                    time++;
                } else {
                    wed.setBackgroundTintList(getResources().getColorStateList(pink));
                    w = false;
                    time++;
                }
            }
        });

        thu.setOnClickListener(new View.OnClickListener() {

            int time = 0;

            @Override
            public void onClick(View view) {
                if(time%2== 0) {
                    thu.setBackgroundTintList(getResources().getColorStateList(violet));
                    th = true;
                    time++;
                } else {
                    thu.setBackgroundTintList(getResources().getColorStateList(pink));
                    th = false;
                    time++;
                }
            }
        });

        fri.setOnClickListener(new View.OnClickListener() {

            int time = 0;

            @Override
            public void onClick(View view) {
                if(time%2== 0) {
                    fri.setBackgroundTintList(getResources().getColorStateList(violet));
                    f = true;
                    time++;
                } else {
                    fri.setBackgroundTintList(getResources().getColorStateList(pink));
                    f = false;
                    time++;
                }
            }
        });

        sat.setOnClickListener(new View.OnClickListener() {

            int time = 0;

            @Override
            public void onClick(View view) {
                if(time%2== 0) {
                    sat.setBackgroundTintList(getResources().getColorStateList(violet));
                    sa = true;
                    time++;
                } else {
                    sat.setBackgroundTintList(getResources().getColorStateList(pink));
                    sa = false;
                    time++;
                }
            }
        });

        sun.setOnClickListener(new View.OnClickListener() {

            int time = 0;

            @Override
            public void onClick(View view) {
                if(time%2== 0) {
                    sun.setBackgroundTintList(getResources().getColorStateList(violet));
                    su = true;
                    time++;
                } else {
                    sun.setBackgroundTintList(getResources().getColorStateList(pink));
                    su = false;
                    time++;
                }
            }
        });

        int hour_numberpicker_id = Resources.getSystem().getIdentifier("hour", "id", "android");
        int minute_numberpicker_id = Resources.getSystem().getIdentifier("minute", "id", "android");
        int ampm_numberpicker_id = Resources.getSystem().getIdentifier("amPm", "id", "android");

        NumberPicker hour_numberpicker = timePicker.findViewById(hour_numberpicker_id);
        NumberPicker minute_numberpicker = timePicker.findViewById(minute_numberpicker_id);
        NumberPicker ampm_numberpicker = timePicker.findViewById(ampm_numberpicker_id);

        set_numberpicker_text_colour(hour_numberpicker);
        set_numberpicker_text_colour(minute_numberpicker);
        set_numberpicker_text_colour(ampm_numberpicker);
    }

    private void set_numberpicker_text_colour(NumberPicker number_picker){
        final int count = number_picker.getChildCount();
        final int color = getResources().getColor(R.color.white);

        for(int i = 0; i < count; i++){
            View child = number_picker.getChildAt(i);

            try{
                Field wheelpaint_field = number_picker.getClass().getDeclaredField("mSelectorWheelPaint");
                wheelpaint_field.setAccessible(true);

                ((Paint)wheelpaint_field.get(number_picker)).setColor(color);
                ((EditText)child).setTextColor(color);
                number_picker.invalidate();
            }
            catch(NoSuchFieldException e){

            }
            catch(IllegalAccessException e){

            }
            catch(IllegalArgumentException e){

            }
        }
    }

}
