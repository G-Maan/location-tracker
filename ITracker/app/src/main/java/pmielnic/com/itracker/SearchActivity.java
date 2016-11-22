package pmielnic.com.itracker;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pmielnic.com.itracker.adapters.DatabaseListAdapter;
import pmielnic.com.itracker.globals.Globals;
import pmielnic.com.itracker.model.Address;
import pmielnic.com.itracker.model.Location;
import pmielnic.com.itracker.model.User;

/**
 * Created by Pawel Mielniczuk on 2016-10-30.
 */
public class SearchActivity extends AppCompatActivity {

    private List<User> userList = new ArrayList<>();
    private ListView listView;
    private DatabaseListAdapter listAdapter;
    private ProgressDialog progressDialog;
    private RequestQueue queue;
    private SearchView searchView;
    private String userEmail;
    Globals globals;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        globals = ((Globals)getApplicationContext());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            userEmail = bundle.getString("email");
        }

        progressDialog = new ProgressDialog(this);
        listView = (ListView) findViewById(R.id.list);
        TextView empty = (TextView) findViewById(R.id.empty);
        listView.setEmptyView(empty);
        listAdapter = new DatabaseListAdapter(this, userList);
        searchView = (SearchView) findViewById(R.id.search);
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchFor(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                filterSearchFor();
                return false;
            }
        });

        listView.setAdapter(listAdapter);

        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideProgressDialog();
    }

    private void addFriend(){

    }



    private void searchFor(String text){
        System.out.println(userEmail);
        String url = globals.getUrlFindUser() + userEmail + "/" + text;
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                hideProgressDialog();
                userList.clear();
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
                listAdapter.notifyDataSetChanged();
                for (User user : userList) {
                    Log.d("USER", user.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                    Log.d("Error", error.getMessage());
            }
        });
        queue.add(request);
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
