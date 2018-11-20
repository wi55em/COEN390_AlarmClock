package com.example.wi55em.coen390_alarmclock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // All Static variables
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = Config.DATABASE_NAME;

    private static final String CREATE_ALARM_TABLE = "CREATE TABLE " + Config.TABLE_ALARM + "("
            + Config.COLUMN_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Config.COLUMN_ALARM_HOUR + " INTEGER,"
            + Config.COLUMN_ALARM_MINUTE + " INTEGER,"
            + Config.COLUMN_ALARM_DAYS + " INTEGER,"
            + Config.COLUMN_ALARM_ONOFF + " INTEGER"
            + ")";


    private Context context;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables SQL execution
        db.execSQL(CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_ALARM);

        // Create tables again
        onCreate(db);
    }


    public long insertAlarm(Alarm alarm){

        long id = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_ALARM_HOUR, alarm.getHour());
        contentValues.put(Config.COLUMN_ALARM_MINUTE, alarm.getMinute());
        contentValues.put(Config.COLUMN_ALARM_DAYS, alarm.getDays());
        if(alarm.getOnOff())
            contentValues.put(Config.COLUMN_ALARM_ONOFF, 1);
        else
            contentValues.put(Config.COLUMN_ALARM_ONOFF, 0);

        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_ALARM, null, contentValues);
        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }

    public ArrayList<Alarm> getAllAlarms(){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(Config.TABLE_ALARM, null, null, null, null, null, null, null);
            if(cursor!=null)
                if(cursor.moveToFirst()){
                    ArrayList<Alarm> alarmList = new ArrayList<>();
                    do {
                        int hour = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ALARM_HOUR));
                        int minute = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ALARM_MINUTE));
                        int days = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ALARM_DAYS));
                        boolean on;
                        if(cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ALARM_ONOFF)) == 0)
                            on = false;
                        else
                            on = true;
                        alarmList.add(new Alarm(hour, minute, days, on));
                    }   while (cursor.moveToNext());

                    return alarmList;
                }
        } catch (Exception e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        ArrayList<Alarm> empty = new ArrayList<>();
        return empty;
    }

    /*public long deleteCourseByCode(String codeCourse) {
        long deletedRowCount = -1;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            deletedRowCount = sqLiteDatabase.delete(Config.TABLE_COURSE,
                    Config.COLUMN_COURSE_CODE + " = ? ",
                    new String[]{ codeCourse });
        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deletedRowCount;
    }

    public boolean deleteAllCourses(){
        boolean deleteStatus = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        try {
            //for "1" delete() method returns number of deleted rows
            //if you don't want row count just use delete(TABLE_NAME, null, null)
            sqLiteDatabase.delete(Config.TABLE_COURSE, null, null);

            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_COURSE);

            if(count==0)
                deleteStatus = true;

        } catch (SQLiteException e){
            Log.d(TAG,"Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return deleteStatus;
    }*/

}
