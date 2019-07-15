package com.example.libraryapp.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.BookItem;
import com.example.libraryapp.PreActivity;
import com.example.libraryapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    CheckAdapter checkAdapter;
    Context context;
    private ArrayList<BookItem> bookItemArrayList;
    private ImageButton ib_back, ib_save;
    //로그인 후 유저 정보  인텐트로 이메일만 가지고 옴, 레지스터할 때 유저 인스턴스 생성 -> 디비에 저장/ 로그인할 때 디비에 있는 유저 정보 가지고 오기
    //더미 유저 생성
    public static User user = new User("이름", "아이디","이메일",new HashMap<String, Double>());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        context = getApplicationContext();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        ib_back = (ImageButton) findViewById(R.id.ib_back); // 뒤로가기 버튼은 없애야?, 경고하는 토스트 생성해야? // 한 번 더 누르면 뒤로 가집니다
        ib_save = (ImageButton) findViewById(R.id.ib_save);
        ib_back.setOnClickListener(this);
        ib_save.setOnClickListener(this);

        //더미 북아이템 생성
        BookItem bookItem1 = new BookItem("썸네일", "타이틀", "작가", "출판사", "업데이트날짜");
        bookItem1.setBookId("아이디1");
        BookItem bookItem2 = new BookItem("썸네일2", "타이틀2", "작가2", "출판사2", "업데이트날짜2");
        bookItem2.setBookId("아이디2");
        bookItemArrayList =new ArrayList<BookItem>();
        bookItemArrayList.add(bookItem1);
        bookItemArrayList.add(bookItem2);

        checkAdapter = new CheckAdapter(bookItemArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(checkAdapter);
        //인텐트로 가지고 온 유저 이메일 등록
        Intent intent = getIntent();
        String userEmail = (String) intent.getStringExtra("owner_email");
        user.setUserMail(userEmail);
        System.out.println("Id is: "+user.getUserId());
        System.out.println("Email is: "+user.getUserMail());


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_save:
                Toast.makeText(CheckActivity.this,"성공적으로 저장.",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CheckActivity.this, PreActivity.class);
                intent.putExtra("code", 1);
                // 디비에 저장 안 했음
                startActivity(intent);
                break;
                //finish();

            case R.id.ib_back:// 뒤로 가기 하면 PreActivity로 가되, 레이팅은 저장 안 되게// 지금 알고리즘으로는 뒤로 가도 저장이 될텐데??
                // 백프레스를 해서 뒤로 가기로 하면 뭐가 바뀌나?/
                Toast.makeText(CheckActivity.this,"지금 뒤로 돌아가시면 추천 정보가 제공되지 않습니다. 한 번 더 누르면 종료됩니다.",Toast.LENGTH_LONG).show();
                Intent intent1 = new Intent(CheckActivity.this, PreActivity.class);
                intent1.putExtra("code", 0);
                startActivity(intent1);
                //finish();
                break;
        }

    }
}
