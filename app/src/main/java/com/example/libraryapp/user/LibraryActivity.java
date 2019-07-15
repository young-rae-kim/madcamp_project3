package com.example.libraryapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.libraryapp.BookItem;
import com.example.libraryapp.BottomSheetFragment;
import com.example.libraryapp.MainActivity;
import com.example.libraryapp.R;
import com.example.libraryapp.RecyclerItemClickListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {
    private String owner_email;
    private RecyclerView libraryRecycler;
    private RecyclerView borrowedRecycler;
    private LinearLayoutManager libraryManager;
    private LinearLayoutManager borrowedManager;
    private BookAdapter libraryAdapter;
    private BookAdapter borrowedAdapter;
    private ArrayList<BookItem> libraryList = new ArrayList<>();
    private ArrayList<BookItem> borrowedList = new ArrayList<>();
    private BottomSheetBehavior bottomSheetBehavior;
    private DatabaseReference libraryRef;
    private DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("server/saving-data/");
        libraryRef = ref.child("library");
        Intent intent = getIntent();
        owner_email = intent.getStringExtra("owner_email");
        libraryManager = new LinearLayoutManager(this);
        borrowedManager = new LinearLayoutManager(this);
        libraryRecycler = findViewById(R.id.recycler_library);
        borrowedRecycler = findViewById(R.id.recycler_borrowed);
        libraryRecycler.setLayoutManager(libraryManager);
        borrowedRecycler.setLayoutManager(borrowedManager);
        libraryAdapter = new BookAdapter(libraryList, Glide.with(this));
        libraryRecycler.setAdapter(libraryAdapter);
        borrowedAdapter = new BookAdapter(borrowedList, Glide.with(this));
        borrowedRecycler.setAdapter(borrowedAdapter);

        libraryRef.orderByChild("owner").equalTo(owner_email).addChildEventListener(new ChildEventListener() {
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
                    libraryAdapter.getItems().add(item);
                    libraryAdapter.notifyItemInserted(libraryAdapter.getItemCount() - 1);
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

        libraryRef.orderByChild("borrower").equalTo(owner_email).addChildEventListener(new ChildEventListener() {
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
                    borrowedAdapter.getItems().add(item);
                    borrowedAdapter.notifyItemInserted(borrowedAdapter.getItemCount() - 1);
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

        TableRow addBook = findViewById(R.id.library_add);
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                BottomSheetFragment fragment = new BottomSheetFragment();
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.bottom_frame, fragment);
                fragmentTransaction.commit();
            }
        });

        TableRow borrowBook = findViewById(R.id.library_borrow);
        borrowBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LibraryActivity.this, MainActivity.class);
                intent.putExtra("fragment", 1);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        libraryRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this, libraryRecycler,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }
                }));

        borrowedRecycler.addOnItemTouchListener(new RecyclerItemClickListener(this, libraryRecycler,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }
                }));
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }
}
