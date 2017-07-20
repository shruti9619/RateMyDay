package com.learn.shruti.ratemyday;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * @author Nilanchala
 *         <p/>
 *         Broadcast reciever, starts when the device gets starts.
 *         Start your repeating alarm here.
 */
public class DeviceOnReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */

                PendingIntent pendingIntent;
             /* Retrieve a PendingIntent that will perform a broadcast */
                Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
                AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);


        /* Set the alarm to start at 8.00 PM */

                Calendar alarmStartTime = Calendar.getInstance();
                Calendar now = Calendar.getInstance();
                alarmStartTime.set(Calendar.HOUR_OF_DAY, 16);
                alarmStartTime.set(Calendar.MINUTE, 00);
                alarmStartTime.set(Calendar.SECOND, 0);
                if (now.after(alarmStartTime)) {
                    alarmStartTime.add(Calendar.DATE, 1);
                }
        /* Repeating every day at 8 pm */
                manager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
            Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();
            }


    }
}