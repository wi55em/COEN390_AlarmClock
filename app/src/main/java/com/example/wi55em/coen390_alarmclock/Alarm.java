package com.example.wi55em.coen390_alarmclock;

public class Alarm {

    private int hour;
    private int minute;
    private int days;
    private boolean on;

    public Alarm(int hour, int minute, int days, boolean on) {
        this.hour = hour;
        this.minute = minute;
        this.days = days;
        this.on = on;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setAlarmOnOff(boolean on) {
        this.on = on;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getDays() {
        return days;
    }

    public boolean getOnOff() {
        return on;
    }

}
