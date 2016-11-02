package pmielnic.com.itracker;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pawel on 2016-11-02.
 */
public class LocationService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Location mLastLocation;
    private LocationManager locationManager;
    private RequestQueue queue;
    private String userEmail;
    private ConnectivityManager connectivityManager;
    private LoggerLoadTask mTask;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

//    @Override
//    public void onCreate() {
//        Log.d("onCreate", "Called onCreate()");
//        initializeLocationManager();
//    }
//
//    private void initializeLocationManager() {
//        Log.e("location", "initializeLocationManager");
//        if (locationManager == null) {
//            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//        }
//    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).
                        addConnectionCallbacks(this).
                        addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", "Called onStartCommand()");
        Toast.makeText(getApplicationContext(), "Called onStartCommand()", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();

        if(intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                userEmail = extras.getString("email");
            }
        }
        Toast.makeText(getApplicationContext(), "Passed email to LocationService: " + userEmail, Toast.LENGTH_SHORT).show();

        connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        executeLogger();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void executeLogger() {
        if (mTask != null
                && mTask.getStatus() != LoggerLoadTask.Status.FINISHED) {
            return;
        }
        mTask = (LoggerLoadTask) new LoggerLoadTask().execute();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Location services stopped", Toast.LENGTH_LONG).show();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    private class LoggerLoadTask extends AsyncTask<Void, Void, Void> {

        // TODO: create two base service urls, one for debugging and one for live.
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                // if we have no data connection, no point in proceeding.
                NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
                if (ni == null || !ni.isAvailable() || !ni.isConnected()) {
                    return null;
                }else{

                }
                // / grab and log data
                Log.i("info", "doinBackground worked!");
            } catch (Exception e) {
                        Log.e("Exception", e.toString());
            }
            return null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "Called onLocationChanged()");
        mLastLocation = location;

        double dLatitude = mLastLocation.getLatitude();
        double dLongitude = mLastLocation.getLongitude();
        String url = "https://localization-tracker.herokuapp.com/save/location";
        Map<String, String> params = new HashMap<>();
        params.put("email", userEmail);
        params.put("latitude", String.valueOf(dLatitude));
        params.put("longitude", String.valueOf(dLongitude));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        Log.d("Network info", "isAvailable() " + networkInfo.isAvailable() + " isConnected() " + networkInfo.isConnected());
        if(networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected()) {
            this.stopSelf();
        }else {
            queue = Volley.newRequestQueue(this);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d("response", response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error", error.toString());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };
            queue.add(objectRequest);
            this.stopSelf();
        }
    }
}
