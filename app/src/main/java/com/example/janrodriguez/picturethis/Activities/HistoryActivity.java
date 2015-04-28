package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.Helpers.CustomListAdapter;
import com.example.janrodriguez.picturethis.Helpers.ParseHelper;
import com.example.janrodriguez.picturethis.Layouts.SlidingTabLayout;
import com.example.janrodriguez.picturethis.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends BaseGameActivity implements ActionBar.TabListener {

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

    SlidingTabLayout mSlidingTabLayout;

    private static SwipeRefreshLayout receivedRefreshLayout;
    private static SwipeRefreshLayout sentRefreshLayout;

    private static ArrayList<Challenge> sentChallenges = new ArrayList<Challenge>();
    private static CustomListAdapter sentChallengeAdapter;

    private static ArrayList<Challenge> receivedChallenges = new ArrayList<Challenge>();
    private static CustomListAdapter receivedChallengeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initializeTabs();
    }

    private void initializeTabs() {
        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();


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

        mSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    private static void populateChallengeListViews() {
        ParseHelper.GetInactiveChallengesInitiatedByUser(BaseGameActivity.currentUser, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    sentChallenges.clear();

                    for (ParseObject parseObject : parseObjects) {
                        Challenge challenge = new Challenge(parseObject);
                        sentChallenges.add(challenge);
                    }

                    sentChallengeAdapter.notifyDataSetChanged();
                    sentRefreshLayout.setRefreshing(false);
                } else {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        });

        ParseHelper.GetInactiveChallengesReceivedByUser(BaseGameActivity.currentUser, new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    receivedChallenges.clear();

                    for (ParseObject parseObject : parseObjects) {
                        Challenge challenge = new Challenge(parseObject);
                        receivedChallenges.add(challenge);
                    }

                    receivedChallengeAdapter.notifyDataSetChanged();
                    receivedRefreshLayout.setRefreshing(false);
                } else {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onSignInSucceeded () {
        populateChallengeListViews();
    }

    @Override
    public void onStop () {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
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

        public ReceivedChallengeFeedFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_received_challenge_feed, container, false);

            listView = (ListView)rootView.findViewById(R.id.listView2);

            receivedRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_received);
            receivedRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    populateChallengeListViews();
                }
            });
            receivedChallengeAdapter = new CustomListAdapter(getActivity(), receivedChallenges);
            listView.setAdapter(receivedChallengeAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent intent = new Intent(getActivity(), ViewChallengeActivity.class);
                    intent.putExtra(Challenge.INTENT_TAG, receivedChallenges.get(position));
                    startActivity(intent);
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

        public SentChallengeFeedFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sent_challenge_feed, container, false);

            listView = (ListView)rootView.findViewById(R.id.listView3);
            sentRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_sent);
            sentRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    populateChallengeListViews();
                }
            });
            sentChallengeAdapter = new CustomListAdapter(getActivity(), sentChallenges);
            listView.setAdapter(sentChallengeAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ViewResponseActivity.class);
                    intent.putExtra(Challenge.INTENT_TAG, sentChallenges.get(position));
                    startActivity(intent);
                }
            });

            return rootView;
        }
    }
}
