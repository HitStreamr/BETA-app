package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class CreditsRCVAdapter extends RecyclerView.Adapter<CreditsRCVAdapter.ViewHolder> {

    private static final String TAG = "CreditsRCVAdapter";

    private ArrayList<String> mCreditVal = new ArrayList<>();
    private ArrayList<String> mCreditPrice = new ArrayList<>();
    private ArrayList<String> mDescription = new ArrayList<>();
//    private Context mContext;

    public CreditsRCVAdapter(CreditsPurchase creditsPurchase, ArrayList<String> mCreditVal, ArrayList<String> mCreditPrice, ArrayList<String> mDescription) {
        this.mCreditVal = mCreditVal;
        this.mCreditPrice = mCreditPrice;
        this.mDescription = mDescription;
//        this.mContext = mContext;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_creditvalueitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d(TAG, "onBindVIewHolder: called.");

        holder.CreditVal.setText(mCreditVal.get(position));
        holder.CreditPrice.setText(mCreditPrice.get(position));
        holder.PriceDescript.setText(mDescription.get(position));

//        holder.CreditPrice.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        holder.CreditVal.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        holder.PriceDescript.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


        //TODO set up onBindViewHolder

    }

    @Override
    public int getItemCount() {
        return mCreditVal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView CreditVal, CreditPrice, PriceDescript;


        public ViewHolder(View itemView) {
            super(itemView);

            CreditVal = itemView.findViewById(R.id.creditprice);
            CreditPrice = itemView.findViewById(R.id.creditvalue);
            PriceDescript = itemView.findViewById(R.id.priceDescription);
        }
    }
}
