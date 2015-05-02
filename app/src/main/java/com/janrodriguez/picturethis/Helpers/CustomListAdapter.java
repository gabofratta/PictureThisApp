package com.janrodriguez.picturethis.Helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.janrodriguez.picturethis.R;

import java.util.ArrayList;

/**
 * Created by Emily on 4/24/15.
 */
public class CustomListAdapter extends ArrayAdapter<Challenge> {

    private Activity context;
    private final int type;
    public static int TYPE_RECEIVED_CHALLENGE = 1;
    public static int TYPE_SENT_CHALLENGE = 2;
    private TextView emptyText;
    private RelativeLayout loadingPanel;
    private ArrayList<Challenge> challenges;

    public CustomListAdapter(int type, Activity context, ArrayList<Challenge> challenges, View rootView) {
        super(context, R.layout.my_list, challenges);
        this.type = type;
        this.context = context;
        this.challenges = challenges;
        this.emptyText = (TextView) rootView.findViewById(R.id.empty_challenges);
        this.loadingPanel = (RelativeLayout) rootView.findViewById(R.id.loadingPanel);
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView=inflater.inflate(R.layout.my_list, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        Challenge challenge = challenges.get(position);
        txtTitle.setText(challenge.getTitle());
//        imageView.setImageResource(R.drawable.picturethis);

        Bitmap bitmap = challenge.getPictureBitmap();
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
//        rowView.setBackgroundColor(Color.RED);

        if (type == TYPE_RECEIVED_CHALLENGE) {
            extratxt.setText("Challenger: " + challenge.getChallenger().toString());

            if (challenge.isActive() && challenge.getChallengedStatus() == Challenge.Status.WAITING) {
                rowView.setBackgroundResource(R.drawable.rounded_corners_no_action);
            } else if (challenge.isActive() && challenge.getChallengedStatus() == Challenge.Status.NEED_ACTION) {
                rowView.setBackgroundResource(R.drawable.rounded_corners_action_needed);
            } else {
                rowView.setBackgroundResource(R.drawable.rounded_corners_history);
            }
        } else {
            String challenged = challenge.getChallengedList().toString();
            extratxt.setText("Challenged: " + challenged.substring(1, challenged.length() - 1));

            if (challenge.isActive() && challenge.getChallengerStatus() == Challenge.Status.WAITING) {
                rowView.setBackgroundResource(R.drawable.rounded_corners_no_action);
            } else if (challenge.isActive() && challenge.getChallengerStatus() == Challenge.Status.NEED_ACTION) {
                rowView.setBackgroundResource(R.drawable.rounded_corners_action_needed);
            } else {
                rowView.setBackgroundResource(R.drawable.rounded_corners_history);
            }
        }

        return rowView;
    }

    @Override
    public void notifyDataSetChanged() {
        loadingPanel.setVisibility(View.GONE);

        if (getCount() == 0) {
            emptyText.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.GONE);
        }

        super.notifyDataSetChanged();
    }

}