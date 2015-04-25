package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.Helpers.ParseHelper;
import com.example.janrodriguez.picturethis.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity implements ActionBar.TabListener {

    static private final String TAG = "HistoryActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

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
        initializeTabs();
        initializeUiComponents();
        populateChallengeListViews();
    }

    private void initializeTabs() {
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
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
        ParseHelper.GetInactiveChallengesInitiatedByUser(BaseGameActivity.currentUser, getFindCallback(sentChallenges, sentChallengeAdapter));
        ParseHelper.GetInactiveChallengesReceivedByUser(BaseGameActivity.currentUser, getFindCallback(receivedChallenges, receivedChallengeAdapter));
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
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    return ReceivedChallengeFeedFragment.newInstance();
                case 1:
                    return SentChallengeFeedFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    public static class ReceivedChallengeFeedFragment extends Fragment {


        static ListView listView;



        public static ReceivedChallengeFeedFragment newInstance() {
            ReceivedChallengeFeedFragment fragment = new ReceivedChallengeFeedFragment();
            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        public ReceivedChallengeFeedFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_received_challenge_feed, container, false);

            listView = (ListView)rootView.findViewById(R.id.listView2);
            adapter1 = new CustomListAdapter(getActivity(), listOfReceivedChallenges);
            listView.setAdapter(adapter1);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    String Slecteditem= listOfReceivedChallenges.get(position).getTitle();
                    Toast.makeText(getActivity().getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

                }
            });

            return rootView;
        }
    }

    public static class SentChallengeFeedFragment extends Fragment {

        static ListView listView;


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SentChallengeFeedFragment newInstance() {
            SentChallengeFeedFragment fragment = new SentChallengeFeedFragment();
            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public SentChallengeFeedFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sent_challenge_feed, container, false);

            listView = (ListView)rootView.findViewById(R.id.listView3);
            adapter2 = new CustomListAdapter(getActivity(), listOfSentChallenges);
            listView.setAdapter(adapter2);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    String Slecteditem= listOfSentChallenges.get(position).getTitle();
                    Toast.makeText(getActivity().getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();
                }
            });

            return rootView;
        }
    }
}
