package com.example.janrodriguez.picturethis.Helpers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.janrodriguez.picturethis.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emily on 4/24/15.
 */
public class CustomListAdapter extends ArrayAdapter<Challenge> {

    private Activity context;
    private final int type;
    public static int TYPE_RECEIVED_CHALLENGE = 1;
    public static int TYPE_SENT_CHALLENGE = 2;
    private User user;
    private static int COLOR_NEED_ACTION = Color.MAGENTA;
    private static int COLOR_NO_NEED_ACTION = Color.WHITE;

    private ArrayList<Challenge> challenges;

    public CustomListAdapter(int type, Activity context, ArrayList<Challenge> challenges, User user) {
        super(context, R.layout.my_list, challenges);
        this.type = type;
        Log.e("CustomListAdapter:", "Constructor");
        this.context = context;
        this.challenges = challenges;
        this.user = user;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView=inflater.inflate(R.layout.my_list, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
        final ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView extratxt = (TextView) rowView.findViewById(R.id.textView1);

        txtTitle.setText(challenges.get(position).getTitle());
//        imageView.setImageResource(R.drawable.picturethis);
        extratxt.setText("Challenger: " + challenges.get(position).getChallenger().toString());
        Bitmap bitmap = challenges.get(position).getPictureBitmap();
        if (bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }
//        rowView.setBackgroundColor(Color.RED);

        if (type == TYPE_RECEIVED_CHALLENGE){
            ParseHelper.GetPendingResponsesOfCurrentUserToChallenge(challenges.get(position), user, new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null){
                        if (parseObjects.size()>0){
                            rowView.setBackgroundColor(COLOR_NO_NEED_ACTION);
                        }else{
                            rowView.setBackgroundColor(COLOR_NEED_ACTION);
                        }
                    }else{
                        Log.i("CustomListAdapter1:", e.toString() );
                    }
                }
            });

        }else {
            ParseHelper.GetPendingResponsesToChallenge(challenges.get(position), new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null){
                        if (parseObjects.size()>0){
                            rowView.setBackgroundColor(COLOR_NEED_ACTION);
                        }else{
                            rowView.setBackgroundColor(COLOR_NO_NEED_ACTION);
                        }
                    }else{
                        Log.i("CustomListAdapter2:", e.toString() );
                    }
                }
            });
        }


        return rowView;

    };
}