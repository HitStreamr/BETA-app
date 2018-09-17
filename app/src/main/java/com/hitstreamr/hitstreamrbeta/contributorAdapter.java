package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class contributorAdapter extends ArrayAdapter<Contributor> {
    private static final String TAG = "contributorAdapter";

    private Context mContext;
    private int mResource;
    private ArrayList<Contributor> objects1 =new ArrayList<>();
    deleteinterface deleteInter;

    public contributorAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Contributor> objects, deleteinterface deleteInter) {
        super(context, resource, objects);
        this.mContext =  context;
        this.mResource = resource;
        this.objects1 = objects;
        this.deleteInter = deleteInter;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Contributor Contributors = getItem(position);
        Log.e("TAG", "object values"+objects1.toString());
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        Contributor currentContributors = objects1.get(position);

        Log.e("TAG", "object current values"+currentContributors.toString());
        TextView TextViewContributorName = convertView.findViewById(R.id.firstLine);
        TextView TextViewContributorPercentage = convertView.findViewById(R.id.thirdLine);
        TextView TextViewContributorType = convertView.findViewById(R.id.secondLine);

        Button deleteContributorBtn = convertView.findViewById(R.id.deleteContributor);
        deleteContributorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "button is clicked" + position, Toast.LENGTH_SHORT).show();
                deleteInter.deleteposition(position);

            }
        });

        TextViewContributorName.setText(currentContributors.getContributorName());
        TextViewContributorPercentage.setText(currentContributors.getContributorPercentage());
        TextViewContributorType.setText(currentContributors.getContributorType());
        return convertView;
    }
    public interface deleteinterface{
        void deleteposition(int deletePosition);

    }

}
