package com.example.libraryapp.user;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.libraryapp.BookActivity;
import com.example.libraryapp.BookItem;
import com.example.libraryapp.R;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.CustomViewHolder> {
    private ArrayList<BookItem> imageList; // 아이템 어레이
    private final RequestManager glide;
    private Context mcontext;

    public BookAdapter(ArrayList<BookItem> list, RequestManager mGlide) {
        imageList = list;
        glide = mGlide;
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView result_image;
        public TextView result_title;
        public TextView result_author;
        public TextView result_publisher;
        public TextView result_status;
        RelativeLayout parent_layout;
        public CustomViewHolder(View view) {
            super(view);
            result_image = view.findViewById(R.id.library_thumbnail);
            result_title = view.findViewById(R.id.library_title);
            result_author = view.findViewById(R.id.library_author);
            result_publisher = view.findViewById(R.id.library_publisher);
            result_status = view.findViewById(R.id.library_status);
            parent_layout = view.findViewById(R.id.parent_layout);
        }
    }

    @Override
    public BookAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.book_item, viewGroup, false); // library_item이 아니라?
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
        viewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onClick: ");
                // 인텐트로 북아이템이나 각각의 텍스트나 이미지 넘겨서 BookActivity에서 띄울 수 있게// 근데 레지스터할 때 처럼 네이버 책의 url을 못 받아 오나?
                Intent intent = new Intent(mcontext, BookActivity.class);
                //intent.putExtra() position
                mcontext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public ArrayList<BookItem> getItems() { return imageList; }
}
