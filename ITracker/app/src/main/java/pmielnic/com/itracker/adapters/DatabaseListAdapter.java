package pmielnic.com.itracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pmielnic.com.itracker.R;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView email = (TextView) convertView.findViewById(R.id.email);

        User user = userList.get(position);
        name.setText(user.getName());
        email.setText(user.getEmail());
        return convertView;
    }
}
