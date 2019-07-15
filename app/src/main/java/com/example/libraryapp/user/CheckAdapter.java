package com.example.libraryapp.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.libraryapp.BookItem;
import com.example.libraryapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckAdapter extends RecyclerView.Adapter<CheckAdapter.ViewHolder> {
    private ArrayList<BookItem> bookItemArrayList;
    private BookItem bookItem;
    private final RequestManager glide;

    public CheckAdapter(ArrayList<BookItem> bookItemArrayList, RequestManager manager){
        this.bookItemArrayList =bookItemArrayList;
        this.glide = manager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.check_holder_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        bookItem = bookItemArrayList.get(position);
        final String bookId = bookItem.getIsbn();

        final HashMap<String,Double> userRating = CheckActivity.user.getUserRating();
        holder.tv_title.setText(bookItem.getTitle());
        holder.tv_author.setText(bookItem.getAuthor());
        glide.load(bookItem.getThumbnail()).into(holder.iv_thumbnail);

        holder.ratingBar1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                userRating.put(bookId, (double) v);
                CheckActivity.user.setUserRating(userRating);
                System.out.println("rating is: " + v);
                for ( HashMap.Entry<String, Double> entry : CheckActivity.user.getUserRating().entrySet() ) {
                    System.out.println("방법2) key : " + entry.getKey() +" / value : " + entry.getValue());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookItemArrayList.size();
    }

    public ArrayList<BookItem> getBookItemArrayList() { return  bookItemArrayList; }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title, tv_author;
        private ImageView iv_thumbnail;
        private RatingBar ratingBar1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_author = itemView.findViewById(R.id.tv_author);
            iv_thumbnail = itemView.findViewById(R.id.iv_thumbnail);
            ratingBar1 = itemView.findViewById(R.id.ratingBar1);
        }
    }
}
