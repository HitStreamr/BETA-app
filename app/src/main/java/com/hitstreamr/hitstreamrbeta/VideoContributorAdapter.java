package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class VideoContributorAdapter extends ArrayAdapter<Contributor> {
    private Context mContext;
    private int mResource;
    private List<Contributor> mContributors;

    public VideoContributorAdapter(@NonNull Context context, int resource, @NonNull List<Contributor> objects) {
        super(context, resource, objects);
        this.mContext =  context;
        this.mResource = resource;
        this.mContributors = objects;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Contributor Contributors = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);

        convertView = inflater.inflate(mResource, parent, false);
        Contributor currentContributors = mContributors.get(position);

        Log.e("TAG", "object current values" + currentContributors.toString());
        TextView TextViewContributorName = convertView.findViewById(R.id.nameContributor);

        TextViewContributorName.setText(currentContributors.getContributorName());

        TextViewContributorName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Profile.class);
                intent.putExtra("TYPE", mContext.getString(R.string.type_artist));
                intent.putExtra("artistUsername", currentContributors.getContributorName());
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

}
