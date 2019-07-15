package com.example.libraryapp.Fragment1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.BookItem;
import com.example.libraryapp.PreActivity;
import com.example.libraryapp.R;
import com.example.libraryapp.RecyclerItemClickListener;
import com.example.libraryapp.user.BookAdapter;
import com.example.libraryapp.user.CheckAdapter;


import java.util.ArrayList;

public class Fragment1 extends Fragment {
    private TextView mTextView;
    private static Context context;
    static ArrayList<BookItem> bookItemArrayList;
    BookAdapter bookAdapter;
    RecyclerView recyclerView;
    private ImageButton ib_back;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        //context = getActivity();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context =getActivity();
        View view = inflater.inflate(R.layout.fragment_1, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        ib_back = (ImageButton) view.findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PreActivity.class);
                    startActivity(intent);
                }
        });

        //더미 북아이템 생성
        BookItem bookItem1 = new BookItem("썸네일", "타이틀", "작가", "출판사", "업데이트날짜");
        bookItem1.setBookId("아이디1");
        BookItem bookItem2 = new BookItem("썸네일2", "타이틀2", "작가2", "출판사2", "업데이트날짜2");
        bookItem2.setBookId("아이디2");
        bookItemArrayList =new ArrayList<BookItem>();
        bookItemArrayList.add(bookItem1);
        bookItemArrayList.add(bookItem2);


        bookAdapter = new BookAdapter(bookItemArrayList,Glide.with(view));
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setLayoutManager(layoutManager);

//        recyclerView.addOnItemTouchListener(context.getApplicationContext(),recyclerView, new RecyclerItemClickListener.OnItemClickListener(){
//            @Override
//            public void onItemClick(View view, int position) {
//
//            }
//        });

        return view;
    }
}
