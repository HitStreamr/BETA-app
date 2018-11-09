package com.hitstreamr.hitstreamrbeta;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class BookAdapter extends FirestoreRecyclerAdapter<Book, BookAdapter.BookViewHolder>{

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public BookAdapter(@NonNull FirestoreRecyclerOptions<Book> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BookViewHolder holder, int position, @NonNull Book model) {
        holder.title.setText(model.getTitle());
        holder.author.setText(model.getAuthor());
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watch_later_results, parent, false);

        return new BookViewHolder(itemView);
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView author;

        public BookViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.scrollingTitle);
            author = (TextView) view.findViewById(R.id.author);
        }
    }



   /*
   public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder>{

   private List<Book> bookList;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.watch_later_results, parent, false);

        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        holder.title.setText(bookList.get(position).getTitle());
        holder.author.setText(bookList.get(position).getAuthor());
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView author;

        public BookViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.scrollingTitle);
            author = (TextView) view.findViewById(R.id.author);
        }
    }*/
}