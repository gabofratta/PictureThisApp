package com.example.janrodriguez.picturethis.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.Helpers.CustomListAdapter;
import com.example.janrodriguez.picturethis.Helpers.ParseHelper;
import com.example.janrodriguez.picturethis.Helpers.ParseTableConstants;
import com.example.janrodriguez.picturethis.Layouts.SlidingTabLayout;
import com.example.janrodriguez.picturethis.R;
import com.gc.materialdesign.views.ButtonFloat;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChallengeFeedActivity extends BaseSidePanelActivity implements ActionBar.TabListener {

    static private final String TAG = "ChallengeFeedActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    SlidingTabLayout mSlidingTabLayout;

    private static SwipeRefreshLayout receivedRefreshLayout;
    private static SwipeRefreshLayout sentRefreshLayout;

    static ArrayList<Challenge> listOfReceivedChallenges = new ArrayList<>();
    static ArrayList<Challenge> listOfSentChallenges = new ArrayList<>();
    static CustomListAdapter adapter1;
    static CustomListAdapter adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_feed);

        setUpSidePanel();

        ButtonFloat buttonFloat = (ButtonFloat)findViewById(R.id.buttonFloat);
        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChallengeFeedActivity.this, CreateChallengeActivity.class);
                startActivity(intent);
            }
        });

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(false);

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

    @Override
    public void onSignInSucceeded () {
        fetchData();
    }

    @Override
    public void onStop () {
//        refreshHandler.removeCallbacks(runnable);
        super.onStop();
    }

    protected static void fetchData(){
        ParseHelper.GetActiveChallengesReceivedByUser(BaseGameActivity.currentUser, getFindCallbackReceived());
        ParseHelper.GetActiveChallengesInitiatedByUser(BaseGameActivity.currentUser, getFindCallbackSent());
    }

    private static FindCallback<ParseObject> getFindCallbackReceived() {
        return new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    listOfReceivedChallenges.clear();

                    for (ParseObject parseObject : parseObjects) {

                        final Challenge challenge = new Challenge(parseObject);
                        listOfReceivedChallenges.add(challenge);


                        ParseHelper.GetChallengeImage(challenge, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {

                                    ParseFile parseFile = parseObject.getParseFile(ParseTableConstants.CHALLENGE_ICON);

                                    if (parseFile == null) {
                                        return;
                                    }

                                    try {
                                        byte[] bytes = parseFile.getData();
                                        ImageProcess process = new ImageProcess(challenge, adapter1);
                                        process.execute(bytes);
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        });
                    }

                    adapter1.notifyDataSetChanged();
//                    Log.e(TAG, listOfReceivedChallenges.size()+"");
                    receivedRefreshLayout.setRefreshing(false);

                } else {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        };
    }

    private static FindCallback<ParseObject> getFindCallbackSent() {
        return new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    listOfSentChallenges.clear();

                    for (ParseObject parseObject : parseObjects) {

                        final Challenge challenge = new Challenge(parseObject);
                        listOfSentChallenges.add(challenge);


                        ParseHelper.GetChallengeImage(challenge, new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {

                                    ParseFile parseFile = parseObject.getParseFile(ParseTableConstants.CHALLENGE_ICON);

                                    if (parseFile == null) {
                                        return;
                                    }

                                    try {
                                        byte[] bytes = parseFile.getData();
                                        ImageProcess process = new ImageProcess(challenge, adapter2);
                                        process.execute(bytes);
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        });
                    }

                    adapter2.notifyDataSetChanged();
//                    Log.e(TAG, listOfSentChallenges.size()+"");
                    sentRefreshLayout.setRefreshing(false);

                } else {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        };
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

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

        static ListView listView = null;


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

            receivedRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_received);
            receivedRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    fetchData();
                }
            });

            adapter1 = new CustomListAdapter(CustomListAdapter.TYPE_RECEIVED_CHALLENGE,
                    getActivity(), listOfReceivedChallenges, BaseGameActivity.currentUser);
            listView.setAdapter(adapter1);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub

                    Intent intent = new Intent(getActivity(), ViewChallengeActivity.class);
                    intent.putExtra(Challenge.INTENT_TAG, listOfReceivedChallenges.get(position));
                    startActivity(intent);


                }
            });


            return rootView;
        }
    }

    public static class SentChallengeFeedFragment extends Fragment {

        static ListView listView = null;


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

            sentRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_sent);
            sentRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    fetchData();
                }
            });

            adapter2 = new CustomListAdapter(CustomListAdapter.TYPE_SENT_CHALLENGE,
                    getActivity(), listOfSentChallenges, BaseGameActivity.currentUser);
            listView.setAdapter(adapter2);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub

                    Intent intent = new Intent(getActivity(), ViewResponseActivity.class);
                    intent.putExtra(Challenge.INTENT_TAG, listOfSentChallenges.get(position));
                    startActivity(intent);

                }
            });



            return rootView;
        }
    }
}

class ImageProcess extends AsyncTask<byte[], Void, Void> {
    Challenge challenge;
    CustomListAdapter adapter;

    public ImageProcess(Challenge challenge, CustomListAdapter adapter){
        this.challenge = challenge;
        this.adapter = adapter;
    }

    @Override
    protected Void doInBackground(byte[]... params) {
        Bitmap iconBitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);
        challenge.setBitmap(iconBitmap);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (challenge.getPictureBitmap()!=null){
            adapter.notifyDataSetChanged();
        }
    }
}
