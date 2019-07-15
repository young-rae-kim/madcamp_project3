package com.example.libraryapp.Fragment0;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.libraryapp.BookItem;
import com.example.libraryapp.CartAdapter;
import com.example.libraryapp.PreActivity;
import com.example.libraryapp.R;
import com.example.libraryapp.user.BookAdapter;

import java.util.ArrayList;

public class Fragment0 extends Fragment {

    private TextView mTextView;
    private static Context context;
    static ArrayList<BookItem> bookItemArrayList;
    CartAdapter adapter, adapter2;
    //BookAdapter bookAdapter;
    RecyclerView recyclerView, recyclerView2;
    private ImageButton ib_back;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context =getActivity();
        View view = inflater.inflate(R.layout.fragment_0, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView2 = (RecyclerView) view.findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        ib_back = (ImageButton) view.findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PreActivity.class);
                startActivity(intent);
            }
        });

        bookItemArrayList =new ArrayList<>();
        BookItem bookItem1 = new BookItem("썸네일", "타이틀", "작가", "출판사", "업데이트날짜", "아이디", "이메일");
        BookItem bookItem2 = new BookItem("썸네일2", "타이틀2", "작가2", "출판사2", "업데이트날짜2", "아이디2", "이메일2");
        BookItem bookItem3 = new BookItem("썸네일3", "타이틀3", "작가3", "출판사3", "업데이트날짜3", "아이디3", "이메일3");
        BookItem bookItem4 = new BookItem("썸네일4", "타이틀4", "작가4", "출판사4", "업데이트날짜4", "아이디4", "이메일4");
        bookItemArrayList.add(bookItem1);
        bookItemArrayList.add(bookItem2);
        bookItemArrayList.add(bookItem3);
        bookItemArrayList.add(bookItem4);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        adapter = new CartAdapter(bookItemArrayList, Glide.with(view));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        adapter2 = new CartAdapter(bookItemArrayList, Glide.with(view));
        recyclerView2.setAdapter(adapter2);
        recyclerView2.setLayoutManager(layoutManager2);
        return view;
    }
}
