package com.example.libraryapp.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.libraryapp.BookItem;
import com.example.libraryapp.R;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.CustomViewHolder> {
    private ArrayList<BookItem> imageList;
    private ArrayList<BookItem> arrayList = new ArrayList<>();
    private final RequestManager glide;

    public BookAdapter(ArrayList<BookItem> list, RequestManager mGlide) {
        imageList = list;
        glide = mGlide;
        arrayList.addAll(imageList);
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView result_image;
        public TextView result_title;
        public TextView result_author;
        public TextView result_publisher;
        public TextView result_status;
        public CustomViewHolder(View view) {
            super(view);
            result_image = view.findViewById(R.id.library_thumbnail);
            result_title = view.findViewById(R.id.library_title);
            result_author = view.findViewById(R.id.library_author);
            result_publisher = view.findViewById(R.id.library_publisher);
            result_status = view.findViewById(R.id.library_status);
        }
    }

    @Override
    public BookAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.library_item, viewGroup, false);
        BookAdapter.CustomViewHolder viewHolder = new BookAdapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BookAdapter.CustomViewHolder viewHolder, final int position) {
        glide.load(imageList.get(position).getThumbnail())
                .thumbnail(0.25f)
                .centerCrop()
                .into(viewHolder.result_image);
        viewHolder.result_title.setText(imageList.get(position).getTitle());
        viewHolder.result_author.setText(imageList.get(position).getAuthor());
        viewHolder.result_publisher.setText(imageList.get(position).getPublisher());
        viewHolder.result_status.setText(imageList.get(position).getStatus());
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void filter(String charText) {
        imageList.clear();
        if (charText.length() == 0) {
            imageList.addAll(arrayList);
        } else {
            for (BookItem recent : arrayList) {
                String name = recent.getTitle();
                String author = recent.getAuthor();
                String publisher = recent.getPublisher();
                if (name.contains(charText) || author.contains(charText) || publisher.contains(charText)) {
                    imageList.add(recent);
                }
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<BookItem> getItems() { return imageList; }
}
