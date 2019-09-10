package com.hitstreamr.hitstreamrbeta;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GenreRecyclerViewAdapter extends RecyclerView.Adapter<GenreRecyclerViewAdapter.GenreHolder> {

    String[] mImageNames;
    ItemClickListener itemClickListener;

    //store the selected items
    private final SparseBooleanArray selItems = new SparseBooleanArray();

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onAddItemClick(CardView view, ImageView img, int position);
        void onRemoveItemClick(CardView view,ImageView img, int position);
    }

    // provides reference to the views for each data item
    public class GenreHolder extends RecyclerView.ViewHolder {
        ImageView genre;
        CardView card;

        public GenreHolder(View itemV) {
            super(itemV);
            genre = itemV.findViewById(R.id.genreImage);
            card = itemV.findViewById(R.id.cardView);
            //when clicked notify add to the sparse array and update the view so that the color changes
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // in the sparse array
                    if(selItems.get(getAdapterPosition())){
                        selItems.delete(getAdapterPosition());
                        boolean test =  itemClickListener != null;
                        if (itemClickListener != null) {
                            itemClickListener.onRemoveItemClick(card, genre, getAdapterPosition());
                        }
                    }else{
                        selItems.put(getAdapterPosition(),true);
                        if (itemClickListener != null) {
                            itemClickListener.onAddItemClick(card, genre,  getAdapterPosition());
                        }
                    }

                    notifyDataSetChanged();


                }
            });
        }

    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public GenreRecyclerViewAdapter(String[] myDataset, ItemClickListener mListener) {
        mImageNames = myDataset;
        itemClickListener = mListener;
    }

    @NonNull
    @Override
    public GenreRecyclerViewAdapter.GenreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // created container for my view
        CardView v = (CardView ) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.genre_view, parent, false);

        GenreHolder vh = new GenreHolder(v);

        return vh;
    }

    @Override
    // Replace the contents of a view (invoked by the layout manager)
    public void onBindViewHolder(@NonNull GenreHolder holder, int position) {
        if(selItems.get(position)){
            holder.card.setCardBackgroundColor(holder.card.getContext().getColorStateList(R.color.colorPrimary));
        }else{
            holder.card.setCardBackgroundColor(holder.card.getContext().getColorStateList(R.color.cardview_light_background));
        }
        //set the content description and image
        holder.genre.setContentDescription(mImageNames[position]);
        holder.genre.setImageResource(holder.itemView.getResources().getIdentifier(mImageNames[position], "drawable", holder.genre.getContext().getPackageName()));
    }

    @Override
    public int getItemCount() {
        return mImageNames.length;
    }

}
