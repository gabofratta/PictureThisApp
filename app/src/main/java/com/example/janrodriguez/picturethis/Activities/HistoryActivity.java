package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.Helpers.ParseHelper;
import com.example.janrodriguez.picturethis.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseGameActivity {

    static private final String TAG = "HistoryActivity";

    private ArrayList<Challenge> sentChallenges;
    private ArrayAdapter<Challenge> sentChallengeAdapter;
    private ListView sentChallengeListView;

    private ArrayList<Challenge> receivedChallenges;
    private ArrayAdapter<Challenge> receivedChallengeAdapter;
    private ListView receivedChallengeListView;

    private Menu optionsMenu;
    private int refreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        refreshing = 0;
        initializeUiComponents();
        populateChallengeListViews();
    }

    private void initializeUiComponents() {
        // Sent Challenges
        sentChallenges = new ArrayList<Challenge>();
        sentChallengeAdapter = new ArrayAdapter<Challenge>(this, android.R.layout.simple_list_item_1, sentChallenges);

        sentChallengeListView = (ListView) findViewById(R.id.sentListView);
        sentChallengeListView.setAdapter(sentChallengeAdapter);
        sentChallengeListView.setOnItemClickListener(getOnClickListener(sentChallenges, SentChallengeActivity.class));

        // Received Challenges
        receivedChallenges = new ArrayList<Challenge>();
        receivedChallengeAdapter = new ArrayAdapter<Challenge>(this, android.R.layout.simple_list_item_1, receivedChallenges);

        receivedChallengeListView = (ListView) findViewById(R.id.receivedListView);
        receivedChallengeListView.setAdapter(receivedChallengeAdapter);
        receivedChallengeListView.setOnItemClickListener(getOnClickListener(receivedChallenges, ReceivedChallengeActivity.class));
    }

    private AdapterView.OnItemClickListener getOnClickListener(final ArrayList<Challenge> challenges, final Class targetClass) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(HistoryActivity.this, targetClass);
                intent.putExtra(Challenge.INTENT_TAG, challenges.get(position));
                startActivity(intent);
            }
        };
    }

    private void populateChallengeListViews() {
        ParseHelper.GetInactiveChallengesInitiatedByUser(currentUser, getFindCallback(sentChallenges, sentChallengeAdapter));
        ParseHelper.GetInactiveChallengesReceivedByUser(currentUser, getFindCallback(receivedChallenges, receivedChallengeAdapter));
    }

    private FindCallback<ParseObject> getFindCallback(final ArrayList<Challenge> challengeList, final ArrayAdapter<Challenge> challengeAdapter) {
        return new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    challengeList.clear();

                    for (ParseObject parseObject : parseObjects) {
                        Challenge challenge = new Challenge(parseObject);
                        challengeList.add(challenge);
                    }

                    challengeAdapter.notifyDataSetChanged();

                    if (--refreshing == 0) {
                        setRefreshActionButtonState(false);
                    }
                } else {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        };
    }

    private void setRefreshActionButtonState(boolean refreshing) {
        if (optionsMenu != null) {
            MenuItem refreshItem = optionsMenu.findItem(R.id.history_menu_refresh);

            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
                } else {
                    refreshItem.setActionView(null);
                }
            } else {
                Log.e(TAG, "Could not find refresh item in menu");
            }
        } else {
            Log.e(TAG, "Could not find menu");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        optionsMenu = menu;

        getMenuInflater().inflate(R.menu.menu_history, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.history_menu_refresh:
                refreshing = 2;
                setRefreshActionButtonState(true);
                populateChallengeListViews();
                return true;
            case R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
