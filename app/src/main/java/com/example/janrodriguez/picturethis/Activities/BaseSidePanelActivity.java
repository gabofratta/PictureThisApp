package com.example.janrodriguez.picturethis.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.janrodriguez.picturethis.Helpers.Leaderboard;
import com.example.janrodriguez.picturethis.Helpers.SettingsAdapter;
import com.example.janrodriguez.picturethis.R;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

public class BaseSidePanelActivity extends BaseGameActivity implements
        SettingsAdapter.OnItemClickListener{

    private static String TAG = "BaseSidePanelActvity";

    private static final int REQUEST_ACHIEVEMENTS = 100;
    private static final int REQUEST_LEADERBOARD = 101;


    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
//    private Toolbar mToolBar;
    private ActionBarDrawerToggle mDrawerToggle;
    private SettingsAdapter mSettingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view, int position) {
        Log.d(TAG, "Clicked item at position: " + position);

        switch (position) {
            case SettingsAdapter.LOG_OUT_POSITION:
                //Remove user shared preferences
                SharedPreferences sharedPref = BaseSidePanelActivity.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);;
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.commit();

                Plus.AccountApi.clearDefaultAccount(getApiClient());
                getApiClient().disconnect();

                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(loginIntent);
                break;
            case SettingsAdapter.ACHIEVEMENTS_POSITION:
                if((mRequestedClients & CLIENT_GAMES )!= 0){
                    startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), REQUEST_ACHIEVEMENTS);
                }else {
                    Toast.makeText(this, "Not logged in to google games.", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Not connected to google games.");
                }
                break;
            case SettingsAdapter.HISTORY_POSITION:
                Intent intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                break;

            case SettingsAdapter.LEADERBOARD_POSITION:
                if(loggedIntoGoogleGames()){
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(getApiClient(), Leaderboard.ID), REQUEST_LEADERBOARD);
                }else {
                    Toast.makeText(this, "Not logged in to google games.", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Not connected to google games.");
                }
        }
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

    protected void setUpSidePanel() {
        //Get side panel
        mDrawerList = (RecyclerView) findViewById(R.id.left_drawer);
//        mToolBar = (Toolbar)findViewById(R.id.app_toolbar);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        if(mDrawerList == null || mDrawerLayout == null) {
            Log.e(TAG, "Side panel drawer not found. Make sure to set include leftDrawerLayout. Look at layout/activity_base_side_panel.xml as a reference.");
            throw new Error("Side panel drawer not found. Make sure to set include leftDrawerLayout. Look at layout/activity_base_side_panel.xml as a reference.");
        }

        mDrawerList.setHasFixedSize(true);
        mDrawerList.setLayoutManager(new LinearLayoutManager(this));

        mSettingsAdapter = new SettingsAdapter(this);

        mDrawerList.setAdapter(mSettingsAdapter);

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
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

    }
}
