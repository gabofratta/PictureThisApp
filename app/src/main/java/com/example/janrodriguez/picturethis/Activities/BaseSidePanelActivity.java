package com.example.janrodriguez.picturethis.Activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.janrodriguez.picturethis.Helpers.SettingsAdapter;
import com.example.janrodriguez.picturethis.R;

public class BaseSidePanelActivity extends BaseGameActivity implements
        SettingsAdapter.OnItemClickListener{

    private static String TAG = "BaseSidePanelActvity";

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
//    private Toolbar mToolBar;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_side_panel);
        //Get side panel
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);
//        mToolBar = (Toolbar)findViewById(R.id.app_toolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        if(mDrawerList == null) {
            Log.e(TAG, "Side panel drawer not found. Make sure to set include leftDrawerLayout and setContentView before calling super.onCreate().");
            throw new Error("Side panel drawer not found. Make sure to set include leftDrawerLayout and setContentView before calling super.onCreate().");
        }

        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));

        mDrawerList.setAdapter(new SettingsAdapter(this));

//        setSupportActionBar(mToolBar);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
//                mToolBar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                Log.d(TAG, "Closed");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                Log.d(TAG, "Opened");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }


    @Override
    public void onClick(View view, int position) {
        Log.d(TAG, "Yes, touch me like that.");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
}
