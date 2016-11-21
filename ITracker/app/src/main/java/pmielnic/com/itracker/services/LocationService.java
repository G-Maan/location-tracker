package pmielnic.com.itracker.services;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pmielnic.com.itracker.globals.Globals;

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
    private Geocoder geocoder;


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

        String currentDateAndTime = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date());
        double dLatitude = mLastLocation.getLatitude();
        double dLongitude = mLastLocation.getLongitude();
        Globals globals = new Globals();
        Map<String, String> params = new HashMap<>();
        params.put("email", userEmail);
        params.put("latitude", String.valueOf(dLatitude));
        params.put("longitude", String.valueOf(dLongitude));
        params.put("date", currentDateAndTime);
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(dLatitude, dLongitude, 1);
            String country = addressList.get(0).getCountryName();
            String city = addressList.get(0).getLocality();
            String street = addressList.get(0).getAddressLine(0);
            params.put("country", country);
            params.put("city", city);
            params.put("street", street);
        } catch (IOException e) {
            e.printStackTrace();
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        Log.d("Network info", "isAvailable() " + networkInfo.isAvailable() + " isConnected() " + networkInfo.isConnected());
        if(networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected()) {
            this.stopSelf();
        }else {
            queue = Volley.newRequestQueue(this);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, globals.getUrlSaveLocation(), new JSONObject(params), new Response.Listener<JSONObject>() {

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