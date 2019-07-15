package com.example.libraryapp.Fragment2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.libraryapp.BookItem;
import com.example.libraryapp.PreActivity;
import com.example.libraryapp.R;
import com.example.libraryapp.user.BookAdapter;

import java.util.ArrayList;

public class Fragment2 extends Fragment {
    private TextView mTextView;
    private EditText editText;
    private static Context context;
    static ArrayList<BookItem> copyArraylList= new ArrayList<>();
    ArrayList<BookItem> bookItemArrayList;
    BookAdapter bookAdapter;
    RecyclerView recyclerView;
    private ImageButton ib_back;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookItemArrayList=new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_2, null);
        editText = (EditText) view.findViewById(R.id.et_search);
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
        //bookItemArrayList ;
        bookItemArrayList=new ArrayList<>();
        bookItemArrayList.add(bookItem1);
        bookItemArrayList.add(bookItem2);

        copyArraylList.addAll(bookItemArrayList);

        bookAdapter = new BookAdapter(bookItemArrayList, Glide.with(view));
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setLayoutManager(layoutManager);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editText.getText().toString();
                search(text);

            }
        });

        return view;
    }

    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        System.out.println(copyArraylList.size());
        bookItemArrayList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            bookItemArrayList.addAll(copyArraylList);
        }
        // 문자 입력을 할때..
        else
        {

            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < copyArraylList.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (copyArraylList.get(i).getTitle().contains(charText)
                        ||copyArraylList.get(i).getAuthor().contains(charText)
                        ||copyArraylList.get(i).getPublisher().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    bookItemArrayList.add(copyArraylList.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        bookAdapter.notifyDataSetChanged();
    }
}

