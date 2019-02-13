package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class PayoutAdapter extends RecyclerView.Adapter<PayoutAdapter.PayoutHolder> {

    private ArrayList<Pay> payoutdtls = new ArrayList<>();


    public PayoutAdapter(ArrayList<Pay> object1){
        this.payoutdtls = object1;

    }

    public PayoutHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_previous_earnings, parent, false);

        return new PayoutHolder(view);

    }

    public void onBindViewHolder(@NonNull PayoutHolder holder, int position) {
        holder.paymentDate.setText(payoutdtls.get(position).getPayDate());
        holder.PayoutAmount.setText(payoutdtls.get(position).getPaymentAmt());
    }

    @Override
    public int getItemCount() {
        return payoutdtls.size();
    }

    public void clear() {
        final int size = payoutdtls.size();
        payoutdtls.clear();
        notifyItemRangeRemoved(0, size);
    }
    class PayoutHolder extends RecyclerView.ViewHolder{
        TextView paymentDate;
        TextView PayoutAmount;

        public PayoutHolder(View itemView) {
            super(itemView);
            paymentDate = itemView.findViewById(R.id.finalQuaterDate);
            PayoutAmount = itemView.findViewById(R.id.estimatedPayout);
        }
    }
}