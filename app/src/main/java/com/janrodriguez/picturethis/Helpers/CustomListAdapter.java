package com.janrodriguez.picturethis.Helpers;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
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
        super(context,R.layout.challenge_card_view, challenges);
        this.type = type;
        this.context = context;
        this.challenges = challenges;
        this.emptyText = (TextView) rootView.findViewById(R.id.empty_challenges);
        this.loadingPanel = (RelativeLayout) rootView.findViewById(R.id.loadingPanel);
    }

    public View getView(int position,View view,ViewGroup parent) {
        View v = view;
        ViewHolder holder;

        if(v == null){
            LayoutInflater inflater = context.getLayoutInflater();
            v = inflater.inflate(R.layout.challenge_card_view, null,true);

            holder = new ViewHolder();

            holder.baseView = (CardView) v;
            holder.imgageView = (ImageView) v.findViewById(R.id.icon);
            holder.titleTextView = (TextView) v.findViewById(R.id.item);
            holder.subTitleTextView = (TextView) v.findViewById(R.id.textView1);

            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        Challenge challenge = challenges.get(position);
        holder.titleTextView.setText(challenge.getTitle());

        Bitmap picture = challenge.getIconBitmap();
        if (picture != null) {
            holder.imgageView.setImageBitmap(picture);
        }

        Resources resources = this.context.getResources();

        if (type == TYPE_RECEIVED_CHALLENGE) {
            holder.subTitleTextView.setText("From: " + challenge.getChallenger().toString());

            if (challenge.isActive() && challenge.getChallengedStatus() == Challenge.Status.WAITING) {
                holder.baseView.setCardBackgroundColor(resources.getColor(R.color.no_action));
            } else if (challenge.isActive() && challenge.getChallengedStatus() == Challenge.Status.NEED_ACTION) {
                holder.baseView.setCardBackgroundColor(resources.getColor(R.color.action_needed));
            } else {
                holder.baseView.setCardBackgroundColor(resources.getColor(R.color.history_color));
            }
        } else {
            String challenged = challenge.getChallengedList().toString();
            holder.subTitleTextView.setText("To: " + challenged.substring(1, challenged.length() - 1));

            if (challenge.isActive() && challenge.getChallengerStatus() == Challenge.Status.WAITING) {
                holder.baseView.setCardBackgroundColor(resources.getColor(R.color.no_action));
            } else if (challenge.isActive() && challenge.getChallengerStatus() == Challenge.Status.NEED_ACTION) {
                holder.baseView.setCardBackgroundColor(resources.getColor(R.color.action_needed));
            } else {
                holder.baseView.setCardBackgroundColor(resources.getColor(R.color.history_color));
            }
        }


        return holder.baseView;
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

    static class ViewHolder {
        CardView baseView;
        ImageView imgageView;
        TextView titleTextView;
        TextView subTitleTextView;
    }

}