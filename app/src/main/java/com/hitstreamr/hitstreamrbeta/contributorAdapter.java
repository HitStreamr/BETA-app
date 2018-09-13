package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class contributorAdapter extends ArrayAdapter<Contributor> {
    private static final String TAG = "contributorAdapter";

    private Context mContext;
    private int mResource;
    private ArrayList<Contributor> objects1 =new ArrayList<>();

    public contributorAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Contributor> objects) {
        super(context, resource, objects);
        this.mContext =  context;
        this.mResource = resource;
        this.objects1 = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Contributor Contributors = getItem(position);
        Log.e("TAG", "object values"+objects1.toString());
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        Contributor currentContributors = objects1.get(position);

        Log.e("TAG", "object current values"+currentContributors.toString());
        TextView TextViewContributorName = convertView.findViewById(R.id.firstLine);
        TextView TextViewContributorPercentage = convertView.findViewById(R.id.thirdLine);
        TextView TextViewContributorType = convertView.findViewById(R.id.secondLine);

        TextViewContributorName.setText(currentContributors.getContributorName());
        TextViewContributorPercentage.setText(currentContributors.getContributorPercentage());
        TextViewContributorType.setText(currentContributors.getContributorType());
        return convertView;
    }
}
