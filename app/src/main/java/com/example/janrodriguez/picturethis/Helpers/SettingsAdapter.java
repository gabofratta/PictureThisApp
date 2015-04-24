package com.example.janrodriguez.picturethis.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.janrodriguez.picturethis.R;

/**
 * Created by janrodriguez on 4/23/15.
 */
public class SettingsAdapter extends RecyclerView.Adapter <RecyclerView.ViewHolder> {

    private static final int LOG_OUT_TYPE = 0;
    private static final int REGULAR_TYPE = 1;

    private static final String[] mSettingsArray = {"Other Setting", "More stuff", "Foo", "Bar", "Baz", "Log Out"};
    public static final int LOG_OUT_POSITION = 5;

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
            View v = vi.inflate(R.layout.drawer_list_item, parent, false);
            TextView tv = (TextView)v.findViewById(android.R.id.text1);
            return new ViewHolder(tv);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (position) {
            case LOG_OUT_POSITION:
                LogOutViewHolder logOutHolder = (LogOutViewHolder)holder;
                logOutHolder.mButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        mListener.onClick(v, position);
                    }
                });
                break;
            default:
                ViewHolder viewHolder = (ViewHolder)holder;
                viewHolder.mTextView.setText(mSettingsArray[position]);
                viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
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

        public ViewHolder(TextView textView) {
            super(textView);
            mTextView = textView;
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
