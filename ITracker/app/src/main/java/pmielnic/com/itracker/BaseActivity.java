package pmielnic.com.itracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import pmielnic.com.itracker.activities.ArcPath;
import pmielnic.com.itracker.activities.CardViewListActivity;
import pmielnic.com.itracker.activities.FriendListActivity;
import pmielnic.com.itracker.activities.SearchActivity;
import pmielnic.com.itracker.activities.SignInActivity;

public class BaseActivity extends AppCompatActivity{ //changed from depricated ActionBarActivity
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    protected DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private String[] mDrawerListItems = { "Find users", "Friend list", "List New", "ARC"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout);

        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        setupDrawer();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectItem(int position) {
        // update selected item and title, then close the drawer
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", null);
        mDrawerList.setItemChecked(position, true);
        Intent intent;
        switch(position){
            case 0:
                intent = new Intent(this, SearchActivity.class);
                intent.putExtra("email", userEmail);
                mDrawerLayout.closeDrawer(mDrawerList);
                startActivity(intent);
                break;
            case 1:
                intent = new Intent(this, FriendListActivity.class);
                intent.putExtra("email", userEmail);
                mDrawerLayout.closeDrawer(mDrawerList);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(this, CardViewListActivity.class);
                intent.putExtra("email", userEmail);
                mDrawerLayout.closeDrawer(mDrawerList);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(this, ArcPath.class);
                intent.putExtra("email", userEmail);
                mDrawerLayout.closeDrawer(mDrawerList);
                startActivity(intent);
                break;
            default:
                intent = new Intent(this, SignInActivity.class);
                mDrawerLayout.closeDrawer(mDrawerList);
                finish();
                startActivity(intent);
        }
    }

    private void addDrawerItems() {
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mDrawerListItems);
        mDrawerList.setAdapter(mAdapter);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigate to:");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}