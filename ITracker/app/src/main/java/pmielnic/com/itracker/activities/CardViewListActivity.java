package pmielnic.com.itracker.activities;

import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapView;
        import com.google.android.gms.maps.MapsInitializer;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;

        import android.content.Context;
        import android.os.Bundle;
        import android.support.v4.app.ListFragment;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.AbsListView;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.HashSet;

import pmielnic.com.itracker.R;
import pmielnic.com.itracker.model.NamedLocation;

public class CardViewListActivity extends AppCompatActivity {

    private ListFragment mList;

    private MapAdapter mAdapter;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cardview_list);


        // Set a custom list adapter for a list of locations
        mAdapter = new MapAdapter(this, LIST_LOCATIONS);
        mList = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.list);
        mList.setListAdapter(mAdapter);

        // Set a RecyclerListener to clean up MapView from ListView
        AbsListView lv = mList.getListView();
        lv.setRecyclerListener(mRecycleListener);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            email = bundle.getString("email");
        }

    }


    private void setMapLocation(GoogleMap map, NamedLocation data) {
        // Add a marker for this item and set the camera

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(data.location, 13f));
        map.addMarker(new MarkerOptions().position(data.location));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }
    private class MapAdapter extends ArrayAdapter<NamedLocation>{

        private final HashSet<MapView> mMaps = new HashSet<MapView>();

        public MapAdapter(Context context, NamedLocation[] locations) {
            super(context, R.layout.cardview_list_row, R.id.cardview_user_name, locations);
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

                holder.mapView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NamedLocation namedLocation = getItem(position);
                        Toast.makeText(getApplicationContext(), namedLocation.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NamedLocation namedLocation = getItem(position);
                        Toast.makeText(getApplicationContext(), namedLocation.toString(), Toast.LENGTH_SHORT).show();
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

            // Get the NamedLocation for this item and attach it to the MapView
            NamedLocation item = getItem(position);
            holder.mapView.setTag(item);
            holder.mapView.setClickable(false);


            // Ensure the map has been initialised by the on map ready callback in ViewHolder.
            // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
            // when the callback is received.
            if (holder.map != null) {
                // The map is already ready to be used
                setMapLocation(holder.map, item);
            }
            // Set the text label for this item
            holder.userName.setText(item.name);
            holder.userLocation.setText(item.streetName);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int id = v.getId();
                    switch (id){
                        case R.id.lite_listrow_map:
                            NamedLocation namedLocation = getItem(v.getId());
                            Toast.makeText(getApplicationContext(), namedLocation.toString(), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
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
            NamedLocation data = (NamedLocation) mapView.getTag();
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



    /**
     * A list of locations to show in this ListView.
     */
    private static final NamedLocation[] LIST_LOCATIONS = new NamedLocation[]{
            new NamedLocation("Kamilek", "Dibuła 9", new LatLng(-33.920455, 18.466941)),
            new NamedLocation("Paweł", "Wrocławska 42/13", new LatLng(39.937795, 116.387224)),
            new NamedLocation("Adame", "Krakowska 1", new LatLng(46.948020, 7.448206))
    };

}
