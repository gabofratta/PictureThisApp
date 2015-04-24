package com.example.janrodriguez.picturethis.Helpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.janrodriguez.picturethis.R;

/**
 * Created by janrodriguez on 4/23/15.
 */
public class SettingsAdapter extends RecyclerView.Adapter <SettingsAdapter.ViewHolder> {

    private String[] mSettingsArray = {"Log Out", "Other Setting", "More stuff", "Foo", "Bar", "Baz"};
    private OnItemClickListener mListener;

    public SettingsAdapter (OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        public void onClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        View v = vi.inflate(R.layout.drawer_list_item, parent, false);
        TextView tv = (TextView)v.findViewById(android.R.id.text1);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mTextView.setText(mSettingsArray[position]);
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(view, position);
            }
        });
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


}
