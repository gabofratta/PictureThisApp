package com.janrodriguez.picturethis.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.janrodriguez.picturethis.R;

/**
 * Created by janrodriguez on 4/23/15.
 */
public class SettingsAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

    private static final int LOG_OUT_TYPE = 0;
    private static final int REGULAR_TYPE = 1;

    private static final String[] mSettingsArray = {"History", "Achievements", "Leaderboard", "Log Out"};
    public static final int HISTORY_POSITION = 0;
    public static final int ACHIEVEMENTS_POSITION = 1;
    public static final int LEADERBOARD_POSITION = 2;
    public static final int LOG_OUT_POSITION = 3;

    private OnItemClickListener mListener;

    public SettingsAdapter (OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        public void onClick(View view, int position);
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if (LOG_OUT_POSITION == position) {
            return LOG_OUT_TYPE;
        }
        return REGULAR_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());

        if(i == LOG_OUT_TYPE) {
            View v = vi.inflate(R.layout.logout_button, parent, false);
            Button btn = (Button)v.findViewById(android.R.id.button1);
            return new LogOutViewHolder(btn);
        }else{
            LinearLayout v = (LinearLayout)vi.inflate(R.layout.drawer_list_item, parent, false);
            TextView tv = (TextView)v.findViewById(android.R.id.text1);
            ImageView iv = (ImageView)v.findViewById(R.id.drawerImage);
            return new ViewHolder(v, tv, iv);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ViewHolder viewHolder = null;

        if(position != LOG_OUT_POSITION) {
            viewHolder = (ViewHolder)holder;
        }
        switch (position) {
            case LOG_OUT_POSITION:
                LogOutViewHolder logOutHolder = (LogOutViewHolder) holder;
                logOutHolder.mButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mListener.onClick(v, position);
                    }
                });
                break;
            case ACHIEVEMENTS_POSITION:
                viewHolder = (ViewHolder) holder;
                viewHolder.mImageView.setImageResource(R.drawable.games_achievements);
                viewHolder.mTextView.setText(mSettingsArray[position]);
                viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClick(v, position);
                    }
                });
                break;
            case HISTORY_POSITION:
                viewHolder.mImageView.setImageResource(R.drawable.ic_action_view_as_list);
                viewHolder.mTextView.setText(mSettingsArray[position]);
                viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClick(v, position);
                    }
                });
                break;
            case LEADERBOARD_POSITION:
                viewHolder.mImageView.setImageResource(R.drawable.games_leaderboards);
                viewHolder.mTextView.setText(mSettingsArray[position]);
                viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onClick(v, position);
                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mSettingsArray.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTextView;
        public final ImageView mImageView;
        public final LinearLayout mLayout;

        public ViewHolder(LinearLayout v, TextView textView, ImageView imageView) {
            super(v);
            mLayout = v;
            mTextView = textView;
            mImageView = imageView;
        }

    }

    public static class LogOutViewHolder extends RecyclerView.ViewHolder {
        public final Button mButton;

        public LogOutViewHolder(Button button) {
            super(button);
            mButton = button;
        }
    }


}
