package pmielnic.com.itracker.activities;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapView;
        import com.google.android.gms.maps.MapsInitializer;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
        import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AbsListView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import pmielnic.com.itracker.R;
import pmielnic.com.itracker.globals.Globals;
import pmielnic.com.itracker.model.User;
import pmielnic.com.itracker.utilities.Utils;

public class CardViewListActivity extends BaseActivity {

    private ListFragment mList;

    private MapAdapter mAdapter;

    private List<User> userList = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    private RequestQueue queue;

    Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.cardview_list, null, false);
        mDrawerLayout.addView(contentView, 0);

        globals = ((Globals)getApplicationContext());

        queue = Volley.newRequestQueue(this);

        listFriends();
    }

    private void listFriends(){
//        String url = globals.getUrlListFriends() + userEmail;
//        showProgressDialog();
//        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                Log.d("Response", response.toString());
//                for(int i = 0; i < response.length(); i++) {
//                    try{
//                        JSONObject obj = response.getJSONObject(i);
//
//                        User user = Utils.parseJsonToUser(obj);
//
//                        userList.add(user);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
                // Set a custom list adapter for a list of locations
                userList = globals.getUserList();
                mAdapter = new MapAdapter(CardViewListActivity.this, userList);
                mList = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list);
                mList.setListAdapter(mAdapter);

                // Set a RecyclerListener to clean up MapView from ListView
                AbsListView lv = mList.getListView();
                lv.setRecyclerListener(mRecycleListener);

//                hideProgressDialog();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("Error", error.getMessage());
//            }
//        });
//        queue.add(request);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void setMapLocation(GoogleMap map, User data) {
        // Add a marker for this item and set the camera
        LatLng latLng = new LatLng(data.getLocation().getLatitude(), data.getLocation().getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f));
        map.addMarker(new MarkerOptions().position(latLng));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
    private class MapAdapter extends ArrayAdapter<User>{

        private final HashSet<MapView> mMaps = new HashSet<MapView>();

        public MapAdapter(Context context, List<User> users) {
            super(context, R.layout.cardview_list_row, R.id.cardview_user_name, users);
        }


        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;

            // Check if a view can be reused, otherwise inflate a layout and set up the view holder
            if (row == null) {
                // Inflate view from layout file
                row = getLayoutInflater().inflate(R.layout.cardview_list_row, null);

                // Set up holder and assign it to the View
                holder = new ViewHolder();
                holder.mapView = (MapView) row.findViewById(R.id.lite_listrow_map);
                holder.userName = (TextView) row.findViewById(R.id.cardview_user_name);
                holder.userLocation = (TextView) row.findViewById(R.id.cardview_location);
                holder.button = (Button) row.findViewById(R.id.show_on_map_button);

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User user = getItem(position);
                        Intent intent = new Intent(CardViewListActivity.this, MapsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        setResult(Activity.RESULT_OK, intent);
                        intent.putExtra("location_parcel", user.getLocation());
//                        startActivity(intent);
                        finish();
                    }
                });
                // Set holder as tag for row for more efficient access.
                row.setTag(holder);

                // Initialise the MapView
                holder.initializeMapView();

                // Keep track of MapView
                mMaps.add(holder.mapView);
            } else {
                // View has already been initialised, get its holder
                holder = (ViewHolder) row.getTag();
            }

            // Get the User for this item and attach it to the MapView
            User item = getItem(position);
            holder.mapView.setTag(item);
            holder.mapView.setClickable(false);


            // Ensure the map has been initialised by the on map ready callback in ViewHolder.
            // If it is not ready yet, it will be initialised with the User set as its tag
            // when the callback is received.
            if (holder.map != null) {
                // The map is already ready to be used
                setMapLocation(holder.map, item);
            }
            // Set the text label for this item
            holder.userName.setText(item.getName());
            holder.userLocation.setText(item.getLocation().getAddress().getStreetName());
            return row;
        }

        public HashSet<MapView> getMaps() {
            return mMaps;
        }
    }
    class ViewHolder implements OnMapReadyCallback{

        MapView mapView;
        TextView userName;
        TextView userLocation;
        GoogleMap map;
        Button button;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getApplicationContext());
            map = googleMap;
            User data = (User) mapView.getTag();
            if (data != null) {
                setMapLocation(map, data);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }

    }

    /**
     * RecycleListener that completely clears the {@link com.google.android.gms.maps.GoogleMap}
     * attached to a row in the ListView.
     * Sets the map type to {@link com.google.android.gms.maps.GoogleMap#MAP_TYPE_NONE} and clears
     * the map.
     */
    private AbsListView.RecyclerListener mRecycleListener = new AbsListView.RecyclerListener() {

        @Override
        public void onMovedToScrapHeap(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder != null && holder.map != null) {
                // Clear the map and free up resources by changing the map type to none
                holder.map.clear();
                holder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
            }

        }
    };
}
