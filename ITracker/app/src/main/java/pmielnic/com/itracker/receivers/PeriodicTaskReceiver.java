package pmielnic.com.itracker.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * Created by Pawel on 2016-10-23.
 */
public class PeriodicTaskReceiver extends BroadcastReceiver {

    private static final String TAG = "PeriodicTaskReceiver";
    private static final String INTENT_ACTION = "com.example.app.PERIODIC_TASK_HEART_BEAT";

    @Override
    public void onReceive(Context context, Intent intent) {
                doPeriodicTask(context);
    }

    private void doPeriodicTask(Context context) {
        Toast.makeText(context, "hi", Toast.LENGTH_SHORT).show();
        // Periodic task(s) go here ...
        restartPeriodicTaskHeartBeat(context);
    }

    public void restartPeriodicTaskHeartBeat(Context context) {
        Intent alarmIntent = new Intent(context, PeriodicTaskReceiver.class);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmIntent.setAction(INTENT_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), System.currentTimeMillis() + 5000, pendingIntent);
    }

    public void stopPeriodicTaskHeartBeat(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, PeriodicTaskReceiver.class);
        alarmIntent.setAction(INTENT_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        alarmManager.cancel(pendingIntent);
    }
}
