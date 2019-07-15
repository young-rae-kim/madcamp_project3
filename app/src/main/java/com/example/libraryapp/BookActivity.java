package com.example.libraryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class BookActivity extends AppCompatActivity {
    private BookItem item = new BookItem(null, null, null, null, null, null, null);
    private BookItem.BookStatus status;
    private String isbn;
    private String owner_email;
    private String book_email;
    private String book_key;
    private TextView tv_title, tv_author, tv_publisher, tv_isbn, tv_owner, tv_status, tv_request;
    private ImageView iv_thumbnail;
    private ImageButton ib_back;
    private RatingBar ratingBar;
    private DatabaseReference libraryRef;
    private DatabaseReference bookRef;
    private DatabaseReference ref;
    private TableRow borrowedRow;
    private TableRow informationRow;
    private TableRow reserveRow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("server/saving-data/");
        libraryRef = ref.child("library");
        bookRef = ref.child("book");

        Intent intent = getIntent();
        book_key = intent.getStringExtra("key");
        owner_email = intent.getStringExtra("owner_email");
        iv_thumbnail = findViewById(R.id.iv_thumbnail);
        tv_title = findViewById(R.id.tv_title);
        tv_author = findViewById(R.id.tv_author);
        tv_publisher = findViewById(R.id.tv_publisher);
        tv_isbn = findViewById(R.id.tv_ISBN);
        tv_owner = findViewById(R.id.tv_owner);
        tv_status = findViewById(R.id.tv_status);
        tv_request = findViewById(R.id.tv_request);
        ib_back = findViewById(R.id.ib_back);
        ratingBar = findViewById(R.id.book_ratingBar);

        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        libraryRef.child(book_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Glide.with(BookActivity.this)
                            .load(dataSnapshot.child("thumbnail").getValue().toString())
                            .into(iv_thumbnail);
                    tv_title.setText(dataSnapshot.child("title").getValue().toString());
                    tv_author.setText(dataSnapshot.child("author").getValue().toString());
                    tv_publisher.setText(dataSnapshot.child("publisher").getValue().toString());
                    tv_isbn.setText(dataSnapshot.child("isbn").getValue().toString());
                    isbn = dataSnapshot.child("isbn").getValue().toString();
                    tv_owner.setText(dataSnapshot.child("owner").getValue().toString());
                    book_email = dataSnapshot.child("owner").getValue().toString();
                    tv_status.setText(dataSnapshot.child("status").getValue().toString());
                    status = item.parseStatus(dataSnapshot.child("status").getValue().toString());
                    Log.e("borrowed row", status + ", " + owner_email + ", " + book_email);
                    if (status == BookItem.BookStatus.Available && !book_email.equals(owner_email)) {
                        borrowedRow.setClickable(true);
                        if (Build.VERSION.SDK_INT >= 23) {
                            tv_request.setTextColor(getColor(R.color.black));
                        }
                    } else {
                        borrowedRow.setClickable(false);
                        if (Build.VERSION.SDK_INT >= 23) {
                            tv_request.setTextColor(getColor(R.color.grayWhite));
                        }
                    }

                    bookRef.orderByChild("isbn").equalTo(isbn).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot data, @Nullable String s) {
                            if (data.getValue() != null) {
                                Log.e("library activity", "rating : " + (float) Double.parseDouble(data.child("averageStar").getValue().toString()));
                                ratingBar.setRating((float) Double.parseDouble(data.child("averageStar").getValue().toString()));
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        borrowedRow = findViewById(R.id.tv_borrow);
        borrowedRow.setClickable(true);
        if (Build.VERSION.SDK_INT >= 23) {
            tv_request.setTextColor(getColor(R.color.gray));
        }
        borrowedRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> update = new HashMap<>();
                update.put(book_key + "/borrower", owner_email);
                update.put(book_key + "/status", "Borrowed");
                libraryRef.updateChildren(update);
                Toast.makeText(BookActivity.this,"대출 성공!", Toast.LENGTH_LONG).show();
                tv_status.setText("Borrowed");
                borrowedRow.setClickable(false);
                if (Build.VERSION.SDK_INT >= 23) {
                    tv_request.setTextColor(getColor(R.color.grayWhite));
                }
            }
        });

        informationRow = findViewById(R.id.tv_show);
        informationRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("http://www.kyobobook.co.kr/product/detailViewKor.laf?mallGb=KOR&ejkGb=KOR&barcode=" + isbn);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        reserveRow = findViewById(R.id.tv_reserve);
    }
}
