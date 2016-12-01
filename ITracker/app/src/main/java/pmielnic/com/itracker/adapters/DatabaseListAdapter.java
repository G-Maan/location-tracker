package pmielnic.com.itracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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

import java.util.List;

import pmielnic.com.itracker.R;
import pmielnic.com.itracker.globals.Globals;
import pmielnic.com.itracker.model.User;
import pmielnic.com.itracker.utilities.Utils;

/**
 * Created by Pawel Mielniczuk on 2016-10-30.
 */
public class DatabaseListAdapter extends BaseAdapter {

    private Activity activity;
    private List<User> userList;
    private LayoutInflater inflater;
    private RequestQueue queue;
    private Globals globals;
    private SharedPreferences sharedPreferences;

    public DatabaseListAdapter(Activity activity, List<User> userList) {
        this.activity = activity;
        this.userList = userList;
        queue = Volley.newRequestQueue(activity.getApplicationContext());
        globals = (Globals) activity.getApplicationContext();
        sharedPreferences = activity.getSharedPreferences("credentials",activity.MODE_PRIVATE);;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(userList.isEmpty()){

        }
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView email = (TextView) convertView.findViewById(R.id.email);
        ImageButton imageButton = (ImageButton) convertView.findViewById(R.id.add_button);
        View row = inflater.inflate(R.layout.list_row, parent, false);
//        ImageButton imageButton = (ImageButton) row.findViewById(R.id.add_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User selectedUser = (User)getItem(position);
                Toast.makeText(parent.getContext(), selectedUser.getName() + " " + selectedUser.getEmail() + " clicked!", Toast.LENGTH_SHORT).show();
                addFriend(parent.getContext(), selectedUser);
            }
        });

        User user = userList.get(position);
        name.setText(user.getName());
        email.setText(user.getEmail());
        return convertView;
    }

    private void addFriend(final Context context, final User userToAdd){
        String userEmail = sharedPreferences.getString("user_email", "");
        String url = globals.getUrlAddUser() + userEmail + "/" + userToAdd.getEmail();
        JsonArrayRequest request = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                userList.remove(userToAdd);
                Toast.makeText(context, "User added successfully", Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
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
