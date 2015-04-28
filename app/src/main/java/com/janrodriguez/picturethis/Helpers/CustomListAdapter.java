package com.janrodriguez.picturethis.Helpers;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.janrodriguez.picturethis.R;

import java.util.ArrayList;

/**
 * Created by Emily on 4/24/15.
 */
public class CustomListAdapter extends ArrayAdapter<Challenge> {

    private Activity context;


    private ArrayList<Challenge> challenges;

    public CustomListAdapter(Activity context, ArrayList<Challenge> challenges) {
        super(context, R.layout.my_list, challenges);

        this.context = context;
        this.challenges = challenges;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.my_list, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(challenges.get(position).getTitle());
        imageView.setImageResource(R.drawable.picturethis);
        extratxt.setText("Challenger: " + challenges.get(position).getChallenger().toString());
        return rowView;

    };
}