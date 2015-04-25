package com.example.janrodriguez.picturethis.Activities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.janrodriguez.picturethis.Helpers.Challenge;
import com.example.janrodriguez.picturethis.R;

import java.util.ArrayList;

/**
 * Created by Emily on 4/24/15.
 */
public class CustomListAdapter extends ArrayAdapter<Challenge> {

    private Activity context;
//    static String[] itemname ={
//            "Safari",
//            "Camera",
//            "Chrome",
//            "FireFox",
//            "UC Browser",
//            "Android Folder",
//            "VLC Player",
//            "Cold War",
//            "VLC Player",
//            "Cold War"
//    };
//
//    static Integer[] imgid={
//            R.drawable.camera1,
//            R.drawable.gameroom2,
//            R.drawable.picturethis,
//            R.drawable.camera1,
//            R.drawable.gameroom2,
//            R.drawable.picturethis,
//            R.drawable.camera1,
//            R.drawable.gameroom2,
//            R.drawable.camera1,
//            R.drawable.gameroom2
//    };

    private ArrayList<Challenge> challenges;
//    private final String[] itemname;
//    private final Integer[] imgid;

//    public CustomListAdapter(Activity context, String[] itemname, Integer[] imgid) {
//        super(context, R.layout.my_list, itemname);
//
//        this.context = context;
//        this.itemname=itemname;
//        this.imgid=imgid;
//    }

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

//        txtTitle.setText(itemname[position]);
//        imageView.setImageResource(imgid[position]);
//        extratxt.setText("Description "+itemname[position]);

        txtTitle.setText(challenges.get(position).getTitle());
        imageView.setImageResource(R.drawable.picturethis);
        extratxt.setText("Challenger: " + challenges.get(position).getChallenger().toString());
        return rowView;

    };
}