package com.example.libraryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CustomViewHolder> {
    private ArrayList<BookItem> imageList;
    private final RequestManager glide;
    public CartAdapter(ArrayList<BookItem> list, RequestManager mGlide) {
        imageList = list;
        glide = mGlide;
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView result_image;
        public TextView result_title;
        public CustomViewHolder(View view) {
            super(view);
            result_image = view.findViewById(R.id.result_image);
            result_title = view.findViewById(R.id.result_title);
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.book_item, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder viewHolder, final int position) {
        glide.load(imageList.get(position).getThumbnail())
                .thumbnail(0.25f)
                .centerCrop()
                .into(viewHolder.result_image);
        viewHolder.result_title.setText(imageList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public ArrayList<BookItem> getItems() { return imageList; }
}
