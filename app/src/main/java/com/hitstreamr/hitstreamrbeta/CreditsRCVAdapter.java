package com.hitstreamr.hitstreamrbeta;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hitstreamr.hitstreamrbeta.CreditsPurchase;
import java.util.ArrayList;

public class CreditsRCVAdapter extends RecyclerView.Adapter<CreditsRCVAdapter.CreditViewHolder> {

    private ArrayList<String> mCreditVal;
    private ArrayList<String> mCreditPrice;
    private ArrayList<String> mDescription;
    private ArrayList<String> mProductIds;
    CreditsPurchase.MyItemClickListener mListener;
    private Context mContext;
    private int selected_position=-1;


    public CreditsRCVAdapter(Context context, ArrayList<String> mCreditValue, ArrayList<String> mCreditPrices, ArrayList<String> mDescriptions, ArrayList<String> mProductId, CreditsPurchase.MyItemClickListener mListener) {
        this.mContext = context;
        this.mCreditVal = mCreditValue;
        this.mCreditPrice = mCreditPrices;
        this.mDescription = mDescriptions;
        this.mProductIds = mProductId;
        this.mListener = mListener;
    }


    @NonNull
    @Override
    public CreditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_creditvalueitem, parent, false);

        return new CreditViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(CreditViewHolder holder, int position) {

        holder.CreditVal.setText(mCreditVal.get(position));
        holder.CreditPrice.setText(mCreditPrice.get(position));
        holder.PriceDescript.setText(mDescription.get(position));
        if(selected_position == position) {
            holder.mCreditDtls.setCardBackgroundColor(Color.RED);
            mListener.onCreditsSelected(mProductIds.get(position).toString(), mCreditVal.get(position).toString());
        }
        else{
            holder.mCreditDtls.setCardBackgroundColor(Color.GRAY);
        }

    }

    @Override
    public int getItemCount() {
        return mCreditVal.size();
    }

    class CreditViewHolder extends RecyclerView.ViewHolder {

        TextView CreditVal, CreditPrice, PriceDescript;
        CreditsPurchase.MyItemClickListener mListener;
        CardView mCreditDtls;


        public CreditViewHolder(View itemView, final CreditsPurchase.MyItemClickListener mListener) {
            super(itemView);

            CreditVal = itemView.findViewById(R.id.creditvalue);
            CreditPrice = itemView.findViewById(R.id.creditprice);
            PriceDescript = itemView.findViewById(R.id.priceDescription);
            mCreditDtls = itemView.findViewById(R.id.selected);
            this.mListener = mListener;
            // selected_position = getAdapterPosition();
            //Log.e("credit ","selected position  adapter"+selected_position);
            mCreditDtls.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_position = getAdapterPosition();;
                    notifyDataSetChanged();
                }

            });
        }
    }
}
