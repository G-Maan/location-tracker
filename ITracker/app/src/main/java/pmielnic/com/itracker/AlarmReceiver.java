package pmielnic.com.itracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Pawel on 2016-10-23.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("info", "-----------------Worked-----------------------");
        Toast.makeText(context, "Alarm worked.", Toast.LENGTH_LONG).show();
    }
}
