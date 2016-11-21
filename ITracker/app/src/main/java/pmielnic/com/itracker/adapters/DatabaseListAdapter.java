package pmielnic.com.itracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pmielnic.com.itracker.R;
import pmielnic.com.itracker.SearchActivity;
import pmielnic.com.itracker.model.User;

/**
 * Created by Pawel Mielniczuk on 2016-10-30.
 */
public class DatabaseListAdapter extends BaseAdapter {

    private Activity activity;
    private List<User> userList;
    private LayoutInflater inflater;

    public DatabaseListAdapter(Activity activity, List<User> userList) {
        this.activity = activity;
        this.userList = userList;
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
        View row = inflater.inflate(R.layout.list_row, parent, false);
        ImageButton imageButton = (ImageButton) row.findViewById(R.id.add_button);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User selectedUser = (User)getItem(position);
                Toast.makeText(parent.getContext(), selectedUser.getName() + " " + selectedUser.getEmail() + " clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        User user = userList.get(position);
        name.setText(user.getName());
        email.setText(user.getEmail());
        return convertView;
    }
}
