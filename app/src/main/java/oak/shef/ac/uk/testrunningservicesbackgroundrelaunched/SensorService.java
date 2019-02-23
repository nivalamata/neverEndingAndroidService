package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fabio on 30/01/2016.
 */
public class SensorService extends Service {
    public int counter = 0;
    private volatile HandlerThread mHandleThread;
    private ServiceHandler mServiceHandler;

    public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }


    //fires up when service is first initialised
    @Override
    public void onCreate() {
        super.onCreate();
        //an android handler thread internally operates on a looper
        mHandleThread = new HandlerThread("SensorService.HandlerThread");
        mHandleThread.start();
        //an android service handler is a handler running on  a specific background thread
        mServiceHandler = new ServiceHandler(mHandleThread.getLooper());

    }

    //fires up when the service is started up
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // super.onStartCommand(intent, flags, startId);
        //send empty message to background thread
       // mServiceHandler.sendEmptyMessageDelayed(0,500);
        Message message = mServiceHandler.obtainMessage();
        message.setData(intent.getExtras());
        mServiceHandler.sendMessage(message);
       // startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);

        mHandleThread.quit(); // todo u may implement quitSaifely depending on API
       // stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //define how the handler will process the message
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        //define how to handle any incoming message here
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            Bundle data = msg.getData();
            Log.i("Thread",data.getString("qude"));
            startTimer();
        }
    }
}