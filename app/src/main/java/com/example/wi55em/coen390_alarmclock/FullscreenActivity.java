package com.example.wi55em.coen390_alarmclock;

import android.app.ActionBar;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FullscreenActivity extends AppCompatActivity {

    protected Button btnOff;
    protected TextView alarmTime;
    protected Ringtone ringtone;
    protected String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ring_fullscreen);

        //intent getTime

        ActionBar actionBar = getActionBar();
        actionBar.hide();

        ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        ringtone.play();


        alarmTime = findViewById(R.id.alarmTime);
        alarmTime.setText("caca");

        btnOff = findViewById(R.id.btnOff);
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtone.stop();
                goToMainActivity();
            }
        });



    }

    private void goToMainActivity() {
        Intent i;
        i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
