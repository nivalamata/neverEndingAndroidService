package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WAKE_LOCK;

public class MyTestReceiver extends WakefulBroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";
    private PowerManager.WakeLock screenWakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (screenWakeLock == null) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            screenWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "ScreenLock tag: from AlarmListener");
            screenWakeLock.acquire();
        }
        if (!isMyServiceRunning(context, SensorService.class)) {
            Intent service = new Intent(context, SensorService.class);

            service.putExtra("qude", "kapita");
            startWakefulService(context, service);

            // context.startService(new Intent(context, SensorService.class));

            Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service starts! Oooooooooooooppppssssss!!!!");

        }
        if (screenWakeLock != null)
            screenWakeLock.release();
        //reschedule alarm
        Intent alarmIntent = new Intent(context, MyTestReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, MyTestReceiver.REQUEST_CODE,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long firstMillis = System.currentTimeMillis(); // alarm is set right away

        alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firstMillis + 1000, pIntent);


        Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service  al ready starts! Oooooooooooooppppssssss!!!!");


        //  Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
    /*    if (!isMyServiceRunning(context,SensorService.class)) {
            context.startService(new Intent(context, SensorService.class));
        }*/
        //launchIntentService(context, intent);
    }

    private void launchIntentService(Context context, Intent intent) {
        Toast.makeText(context, "launching intent service  from broadcast", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context, MyTestIntentService.class);
        i.putExtra("qude broadrec", "kapita receiver");
        context.startService(i);

    }


    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }
}
