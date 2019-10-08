package com.example.wi55em.coen390_alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
            Intent i = new Intent(context, FullscreenActivity.class);
            context.startActivity(i);
        }


    }


