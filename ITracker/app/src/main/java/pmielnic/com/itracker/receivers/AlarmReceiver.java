package pmielnic.com.itracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import pmielnic.com.itracker.services.LocationService;

/**
 * Created by Pawel on 2016-10-23.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("info", "-----------------Worked-----------------------");
        String email = intent.getStringExtra("email");
        Intent i = new Intent(context, LocationService.class);
        i.putExtra("email", email);
        context.startService(i);
    }
}
