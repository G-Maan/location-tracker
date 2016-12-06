package pmielnic.com.itracker.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ramotion.foldingcell.FoldingCell;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pmielnic.com.itracker.R;
import pmielnic.com.itracker.utilities.Utils;
import pmielnic.com.itracker.adapters.FoldingCellListAdapter;
import pmielnic.com.itracker.globals.Globals;
import pmielnic.com.itracker.model.User;

/**
 * Example of using Folding Cell with ListView and ListAdapter
 */
public class FriendListActivity extends BaseActivity {

    private RequestQueue queue;
    private List<User> userList = new ArrayList<>();
    Globals globals;
    FoldingCellListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);

//        setContentView(R.layout.activity_main);

        globals = ((Globals)getApplicationContext());

        queue = Volley.newRequestQueue(this);


        // get our list view
        ListView theListView = (ListView) findViewById(R.id.mainListView);

        // prepare elements to display
//        final ArrayList<Item> items = Item.getTestingList();

        // add custom btn handler to first list item
//        items.get(0).setRequestBtnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "CUSTOM HANDLER FOR FIRST BUTTON", Toast.LENGTH_SHORT).show();
//            }
//        });

        // create custom adapter that holds elements and their state (we need hold a id's of unfolded elements for reusable elements)
        adapter = new FoldingCellListAdapter(this, userList);

        // add default btn handler for each request btn on each item if custom handler not found
        adapter.setDefaultRequestBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "DEFAULT HANDLER FOR ALL BUTTONS", Toast.LENGTH_SHORT).show();
            }
        });

        // set elements to adapter
        theListView.setAdapter(adapter);

        // set on click event listener to list view
        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                // toggle clicked cell state
                ((FoldingCell) view).toggle(false);
                // register in adapter that state for selected cell is toggled
                adapter.registerToggle(pos);
            }
        });
        listFriends();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void listFriends(){
        String url = globals.getUrlListFriends() + userEmail;

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
                for (User u: userList){
                    Log.d("USER", u.toString());
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", error.getMessage());
            }
        });
        queue.add(request);
    }

}