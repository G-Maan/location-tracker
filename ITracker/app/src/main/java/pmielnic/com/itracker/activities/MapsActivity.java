package pmielnic.com.itracker.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ogaclejapan.arclayout.ArcLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import java.util.List;

import pmielnic.com.itracker.R;
import pmielnic.com.itracker.utilities.AnimatorUtils;
import pmielnic.com.itracker.utilities.Utils;
import pmielnic.com.itracker.globals.Globals;
import pmielnic.com.itracker.model.User;
import pmielnic.com.itracker.receivers.AlarmReceiver;


public class MapsActivity extends BaseActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    private List<Marker> markers = new ArrayList();

    private GoogleMap map;
    private LocationRequest mLocationRequest;
    private Geocoder geocoder;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker marker;
    private RequestQueue queue;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private boolean permissionGranted = false;
    private PendingIntent pendingIntent;
    private SupportMapFragment supportMapFragment;

    private Toast toast = null;
    private View fab;
    private View menuLayout;
    private ArcLayout arcLayout;

    private Globals globals;
    private List<User> userList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_maps, null, false);
        mDrawerLayout.addView(contentView, 0);

        globals = ((Globals)getApplicationContext());

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(MapsActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(MapsActivity.this, 0, alarmIntent, 0);
        start();

        fab = findViewById(R.id.fab);
        menuLayout = findViewById(R.id.menu_layout);
        arcLayout = (ArcLayout) findViewById(R.id.arc_layout);

        queue = Volley.newRequestQueue(this);
        String url = globals.getUrlPrint() + userName;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            permissionGranted = true;
        }
    }

    private void setupArcLayout(List<User> userList){
        arcLayout.removeAllViews();
        ArcLayout.LayoutParams params = new ArcLayout.LayoutParams(150, 150);
        for(User u: userList){
            Button b = new AppCompatButton(this);
            b.setText(u.getName().substring(0,1));
            b.setBackgroundResource(R.drawable.path_white_oval);
            b.setLayoutParams(params);
            arcLayout.addView(b);
        }

        for (int i = 0, size = arcLayout.getChildCount(); i < size; i++) {
            arcLayout.getChildAt(i).setOnClickListener(this);
        }

        fab.setOnClickListener(this);
    }

    private void moveToLocation(pmielnic.com.itracker.model.Location location){
        if(map != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d("LATLNG", location.getLatitude() + " " + location.getLongitude());
            int index = 0;
            for(User u: userList){
                if(u.getLocation().getLatitude() == location.getLatitude() && u.getLocation().getLongitude() == location.getLongitude()){
                    index = userList.indexOf(u);
                }
            }
            if(markers.isEmpty()){
                markers = new ArrayList<>();
                markers = globals.getMarkerList();
            }
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
            markers.get(index).showInfoWindow();
        }
    }

    public void start() {
        AlarmManager am=(AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        final PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pendingIntent);
        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(permissionGranted) {
            if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {

                buildGoogleApiClient();
                mGoogleApiClient.connect();

            }else{
                requestLocationUpdates();
            }
            if(markers.isEmpty()){
            getMarkerInfo();
            }
        }
    }

    private List<Marker> setupMarkers(List<User> users){
        if (markers != null){
            markers = new ArrayList<>();
        }
        for(User u: users){
            Log.d("LATLNG", u.getName());
            LatLng latLng = new LatLng(u.getLocation().getLatitude(), u.getLocation().getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng).title(u.getName()).snippet(u.getLocation().getAddress().getStreetName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            Marker marker = map.addMarker(markerOptions);

            markers.add(marker);
        }
        globals.setMarkerList(markers);
        return markers;
    }

    private List<User> getMarkerInfo(){
        String url = globals.getUrlListFriends() + userEmail;
        userList = new ArrayList<>();
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("Response", response.toString());
                for(int i = 0; i < response.length(); i++) {
                    try{
                        JSONObject obj = response.getJSONObject(i);

                        User user = Utils.parseJsonToUser(obj);

                        userList.add(user);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                globals.setUserList(userList);
                setupMarkers(userList);
                setupArcLayout(userList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.getMessage());
            }
        });
        queue.add(request);
        return userList;
    }

    @Override
    public void onMapReady(GoogleMap retMap) {

        map = retMap;

        setUpMap();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                if(data.getParcelableExtra("location_parcel") != null){
                    pmielnic.com.itracker.model.Location locationParcel = data.getParcelableExtra("location_parcel");
                    moveToLocation(locationParcel);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void setUpMap() {

        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            else permissionGranted = true;
            return;
        }
        //Change to true if circle and arrow should be present for accurate navigation
        map.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        mLastLocation = locationManager.getLastKnownLocation(provider);
        if(mLastLocation != null) {
            double dLatitude = mLastLocation.getLatitude();
            double dLongitude = mLastLocation.getLongitude();
            Log.d("COORDS", String.valueOf(dLatitude));
            Log.d("COORDS", String.valueOf(dLongitude));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 15));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    permissionGranted = false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(permissionGranted) {
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 60);
        mLocationRequest.setFastestInterval(1000 * 30);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //mLocationRequest.setSmallestDisplacement(0.1F);
        requestLocationUpdates();

    }

    public void requestLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            else permissionGranted = true;
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        //remove previous current location Marker
        if (marker != null){
            marker.remove();
        }

        double dLatitude = mLastLocation.getLatitude();
        double dLongitude = mLastLocation.getLongitude();
//        Toast.makeText(MapsActivity.this, "Lat: " + dLatitude + " Long: " + dLongitude, Toast.LENGTH_SHORT).show();
//        marker = map.addMarker(new MarkerOptions().position(new LatLng(dLatitude, dLongitude))
//                .title("My Location").icon(BitmapDescriptorFactory
//                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(dLatitude, dLongitude), 15));

        geocoder = new Geocoder(this, Locale.getDefault());


        String currentDateAndTime = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date());
        Map<String, String> params = new HashMap<>();
        params.put("email", userEmail);
        params.put("latitude", String.valueOf(dLatitude));
        params.put("longitude", String.valueOf(dLongitude));
        params.put("date", currentDateAndTime);
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
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(objectRequest);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab) {
            onFabClick(v);
            return;
        }

        if (v instanceof Button) {
            int index = ((ViewGroup) v.getParent()).indexOfChild(v);
            pmielnic.com.itracker.model.Location selectedUserLocation = userList.get(index).getLocation();
            moveToLocation(selectedUserLocation);
            hideMenu();
        }
    }

    private void onFabClick(View v) {
        if (menuLayout.getVisibility() == View.VISIBLE) {
            hideMenu();
        } else {
            showMenu();
        }
    }

    private void showMenu() {
        menuLayout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();

    }

    private Animator createShowItemAnimator(View item) {

        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }
}