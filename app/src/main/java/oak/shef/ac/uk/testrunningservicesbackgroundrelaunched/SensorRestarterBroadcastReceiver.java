package oak.shef.ac.uk.testrunningservicesbackgroundrelaunched;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by fabio on 24/01/2016.
 */
public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       // Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        //context.startService(new Intent(context, SensorService.class));
        //Toast.makeText(context,"service is re-started broadcast",Toast.LENGTH_SHORT).show();

    }



}
