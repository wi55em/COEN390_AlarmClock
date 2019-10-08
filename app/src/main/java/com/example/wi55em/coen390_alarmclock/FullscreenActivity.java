package com.example.wi55em.coen390_alarmclock;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;

public class FullscreenActivity extends AppCompatActivity {

    MediaPlayer mMediaPlayer;

    protected Button btnOff;
    protected TextView alarmTime;
    protected Ringtone ringtone;
    protected String time;

    protected TextClock textClock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_ring_fullscreen);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mMediaPlayer = new MediaPlayer();

        try {
            Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mMediaPlayer.setDataSource(this, alert);
            final AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), AudioManager.FLAG_PLAY_SOUND);
            //if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            //Thread.sleep(2000);
            //}
        } catch (Exception e) {
        }

        textClock = findViewById(R.id.textClock);

        MainActivity.bleController.serialSend("O");

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(MainActivity.serialReceivedText.getText().toString().contains("F")) {
                    mMediaPlayer.stop();
                    finish();
                }
            }
        }, 0, 1000);


        btnOff = findViewById(R.id.btnOff);
        if (!MainActivity.bleController.mConnected) {
            btnOff.setVisibility(View.VISIBLE);
        } else {
            btnOff.setVisibility(View.INVISIBLE);
        }

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                finish();
            }
        });

    }

}
