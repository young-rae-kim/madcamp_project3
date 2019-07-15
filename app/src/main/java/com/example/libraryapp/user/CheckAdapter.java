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
    //private final RequestManager glide;

    //private User user; // 로그인했을 때 유저의 정보를 어떻게 가져오지??, 이 코드에서 컨스트럭트 안 했으니깐 무조건 에러

    public CheckAdapter(ArrayList<BookItem> bookItemArrayList){
        this.bookItemArrayList =bookItemArrayList;
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
        final String bookId = bookItem.getBookId();

        final HashMap<String,Double> userRating = CheckActivity.user.getUserRating();
        //holder.iv_thumbnail.set
        holder.tv_title.setText(bookItem.getTitle());
        holder.tv_author.setText(bookItem.getAuthor());

        holder.ratingBar1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                //유저에게 특정 책의 레이팅을 받아와서, 그 유저의 정보로 넣는 과정, 이렇게 해도 충분한가?
                userRating.put(bookId, (double)v);
                CheckActivity.user.setUserRating(userRating);
                // 디버깅을 위해, 언제 저장되는지 모르겠음
                System.out.println("rating is: "+Float.toString(v));
                for ( HashMap.Entry<String, Double> entry : CheckActivity.user.getUserRating().entrySet() ) {
                    System.out.println("방법2) key : " + entry.getKey() +" / value : " + entry.getValue());
                }
                System.out.println("CheckAdapter=======================");


            }
        });



    }

    @Override
    public int getItemCount() {
        return bookItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title, tv_author;
        private ImageView iv_thumbnail;
        private RatingBar ratingBar1;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_author = (TextView) itemView.findViewById(R.id.tv_author);
            iv_thumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
            ratingBar1 = (RatingBar) itemView.findViewById(R.id.ratingBar1);
        }
    }
}
