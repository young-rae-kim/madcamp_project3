package com.example.libraryapp.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.libraryapp.BookItem;
import com.example.libraryapp.PreActivity;
import com.example.libraryapp.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class CheckActivity extends AppCompatActivity implements View.OnClickListener {
    private boolean backPressed = false;
    private String owner_email;
    private String name;
    private RecyclerView recyclerView;
    private CheckAdapter checkAdapter;
    private Context context;
    private ArrayList<BookItem> bookItemArrayList = new ArrayList<>();
    private ImageButton ib_back, ib_save;
    public static User user;
    private DatabaseReference bookRef;
    private DatabaseReference userRef;
    private DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        final Intent intent = getIntent();
        owner_email = intent.getStringExtra("owner_email");
        name = intent.getStringExtra("name");
        context = getApplicationContext();
        recyclerView = findViewById(R.id.recycler_view);
        ib_back = findViewById(R.id.ib_back);
        ib_save = findViewById(R.id.ib_save);
        ib_back.setOnClickListener(this);
        ib_save.setOnClickListener(this);
        User user = new User(name, owner_email, new HashMap<String, Double>());
        checkAdapter = new CheckAdapter(bookItemArrayList, Glide.with(CheckActivity.this));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(checkAdapter);
        this.user = user;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("server/saving-data/");
        bookRef = ref.child("book");
        userRef = ref.child("user");

        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                if (count < 10) {
                    bookRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.getValue() != null) {
                                BookItem item = new BookItem(dataSnapshot.child("thumbnail").getValue().toString(),
                                        dataSnapshot.child("title").getValue().toString(),
                                        dataSnapshot.child("author").getValue().toString(),
                                        dataSnapshot.child("publisher").getValue().toString(),
                                        dataSnapshot.child("pubdate").getValue().toString(),
                                        dataSnapshot.child("isbn").getValue().toString(),
                                        owner_email);
                                item.setValue(Integer.parseInt(dataSnapshot.child("value").getValue().toString()));
                                item.setAverageStar(Double.parseDouble(dataSnapshot.child("averageStar").getValue().toString()));
                                item.setBorrower(dataSnapshot.child("borrower").getValue().toString());
                                item.setStatus(item.parseStatus(dataSnapshot.child("status").getValue().toString()));
                                checkAdapter.getBookItemArrayList().add(item);
                                checkAdapter.notifyItemInserted(checkAdapter.getItemCount() - 1);
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
                } else {
                    bookRef.orderByChild("averageStar").limitToLast(10).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.getValue() != null) {
                                BookItem item = new BookItem(dataSnapshot.child("thumbnail").getValue().toString(),
                                        dataSnapshot.child("title").getValue().toString(),
                                        dataSnapshot.child("author").getValue().toString(),
                                        dataSnapshot.child("publisher").getValue().toString(),
                                        dataSnapshot.child("pubdate").getValue().toString(),
                                        dataSnapshot.child("isbn").getValue().toString(),
                                        owner_email);
                                item.setValue(Integer.parseInt(dataSnapshot.child("value").getValue().toString()));
                                item.setAverageStar(Double.parseDouble(dataSnapshot.child("averageStar").getValue().toString()));
                                item.setBorrower(dataSnapshot.child("borrower").getValue().toString());
                                item.setStatus(item.parseStatus(dataSnapshot.child("status").getValue().toString()));
                                checkAdapter.getBookItemArrayList().add(item);
                                checkAdapter.notifyItemInserted(checkAdapter.getItemCount() - 1);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_save:
                backPressed = false;
                userRef.push().setValue(user);
                Iterator it = user.getUserRating().entrySet().iterator();
                while (it.hasNext()) {
                    final HashMap.Entry pair = (HashMap.Entry) it.next();
                    bookRef.orderByChild("isbn").equalTo(pair.getKey().toString()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot != null) {
                                int ratedPerson = Integer.parseInt(dataSnapshot.child("ratedPerson").getValue().toString());
                                Log.e("update rating", "person : " + ratedPerson);
                                int newPerson = ratedPerson + 1;
                                double newRating = Double.parseDouble(dataSnapshot.child("averageStar").getValue().toString());
                                if (ratedPerson > 0) {
                                    newRating = (newRating * ratedPerson + Double.parseDouble(pair.getValue().toString())) / newPerson;
                                }
                                Log.e("update rating", "rating : " + newRating);
                                Map<String, Object> update = new HashMap<>();
                                update.put(dataSnapshot.getKey() + "/ratedPerson", newPerson);
                                update.put(dataSnapshot.getKey() + "/averageStar", newRating);
                                bookRef.updateChildren(update);
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
                    it.remove();
                }

                Toast.makeText(CheckActivity.this,"성공적으로 저장되었습니다.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CheckActivity.this, PreActivity.class);
                intent.putExtra("code", 1);
                intent.putExtra("owner_email", owner_email);
                startActivity(intent);
                break;
            case R.id.ib_back:
                if (!backPressed) {
                    Toast.makeText(CheckActivity.this, "지금 뒤로 돌아가시면 추천 정보가 제공되지 않습니다. 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG).show();
                    backPressed = true;
                } else {
                    user.getUserRating().clear();
                    userRef.push().setValue(user);
                    Intent intent1 = new Intent(CheckActivity.this, PreActivity.class);
                    intent1.putExtra("code", 0);
                    intent1.putExtra("owner_email", owner_email);
                    startActivity(intent1);
                    break;
                }
        }
    }
}
