/**
 * created by Fabio Ciravegna, The University of Sheffield, f.ciravegna@shef.ac.uk
 * LIcence: MIT
 * Copyright (c) 2016 (c) Fabio Ciravegna

 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WAKE_LOCK;

public class MainActivity extends AppCompatActivity {
    private static final int WAKE_LOCK_CODE = 0;
    Intent mServiceIntent;
    private SensorService mSensorService;

    Context ctx;

    public Context getCtx() {
        return ctx;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_main);
      //  mSensorService = new SensorService(getCtx());
       // mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
       // if (!isMyServiceRunning(mSensorService.getClass())) {
           // startService(mServiceIntent);
          //  Toast.makeText(ctx,"service is started",Toast.LENGTH_SHORT).show();
      //  }

        //launchIntentService();
        mayRequestPhoneStatePermission();
        scheduleAlarm();
    }

    private void launchIntentService() {
        Toast.makeText(ctx,"launching intent service",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,MyTestIntentService.class);
        intent.putExtra("qude","kapita");
        startService(intent);

    }

    // Setup a recurring alarm every half hour
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MyTestReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyTestReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        if (Build.VERSION.SDK_INT >= 23)
        {
            //alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,firstMillis,10000,pIntent);
            alarm.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firstMillis+ 1000, pIntent);
        }
        else if (Build.VERSION.SDK_INT >= 19)
        {
            alarm.setExact(AlarmManager.RTC_WAKEUP, 1000, pIntent);
        }
        else
        {
            alarm.set(AlarmManager.RTC_WAKEUP, 1000, pIntent);
        }
    }


    private boolean mayRequestPhoneStatePermission() {
        //permission is always granted once during install on android older version <=lolipop
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return  true;
        }
        //check if permission is granted
        if (checkSelfPermission(WAKE_LOCK) == PackageManager.PERMISSION_GRANTED) {
            return  true;
        }
        // explain reason why permission(s) is important if permssion wasnt granted before
        //dont block use async to request permission again if user chooses yes in  dialogue // todo
        if (shouldShowRequestPermissionRationale(WAKE_LOCK)) {
            DialogFactory.createSimpleOkDialog(this,"Read Phone State Permission",
                    "This permission helps  user to use the app better");
        }else {
            requestPermissions(new String[]{WAKE_LOCK},WAKE_LOCK_CODE);
        }
        return false;
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //todo handler for permissions
        if (requestCode == WAKE_LOCK_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i ("wake:lock","Wake lock,  permission granted");
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    @Override
    protected void onDestroy() {
      //  Toast.makeText(ctx,"service is stoped, on destroy",Toast.LENGTH_SHORT).show();
       // stopService(mServiceIntent);
      //  Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }
}


