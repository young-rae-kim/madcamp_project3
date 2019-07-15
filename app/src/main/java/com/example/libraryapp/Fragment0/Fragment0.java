package com.example.libraryapp.Fragment0;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.libraryapp.BookActivity;
import com.example.libraryapp.BookItem;
import com.example.libraryapp.CartAdapter;
import com.example.libraryapp.MainActivity;
import com.example.libraryapp.PreActivity;
import com.example.libraryapp.R;
import com.example.libraryapp.RecyclerItemClickListener;
import com.example.libraryapp.user.BookAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Fragment0 extends Fragment {

    private TextView mTextView;
    private static Context context;
    static ArrayList<BookItem> bookItemArrayList1 = new ArrayList<>();
    static ArrayList<BookItem> bookItemArrayList2 = new ArrayList<>();
    CartAdapter adapter1, adapter2;
    //BookAdapter bookAdapter;
    RecyclerView recyclerView, recyclerView2;
    private ImageButton ib_back;
    private DatabaseReference ref;
    private DatabaseReference bookRef;
    private DatabaseReference libraryRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("server/saving-data/");
        bookRef = ref.child("book");
        libraryRef = ref.child("library");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_0, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView2 = (RecyclerView) view.findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(true);
        recyclerView2.setHasFixedSize(true);
        ib_back = (ImageButton) view.findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        bookItemArrayList1 = new ArrayList<>();
        bookItemArrayList2 = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        adapter1 = new CartAdapter(bookItemArrayList1, Glide.with(view));
        recyclerView.setAdapter(adapter1);
        recyclerView.setLayoutManager(layoutManager);
        adapter2 = new CartAdapter(bookItemArrayList2, Glide.with(view));
        recyclerView2.setAdapter(adapter2);
        recyclerView2.setLayoutManager(layoutManager2);

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
                            ((MainActivity) getActivity()).getOwner_email());
                    item.setValue(Integer.parseInt(dataSnapshot.child("value").getValue().toString()));
                    item.setAverageStar(Double.parseDouble(dataSnapshot.child("averageStar").getValue().toString()));
                    item.setBorrower(dataSnapshot.child("borrower").getValue().toString());
                    item.setStatus(item.parseStatus(dataSnapshot.child("status").getValue().toString()));
                    adapter1.getItems().add(item);
                    adapter1.notifyItemInserted(adapter1.getItemCount() - 1);
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

        bookRef.orderByChild("pubdate").limitToLast(10).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                BookItem item = new BookItem(dataSnapshot.child("thumbnail").getValue().toString(),
                        dataSnapshot.child("title").getValue().toString(),
                        dataSnapshot.child("author").getValue().toString(),
                        dataSnapshot.child("publisher").getValue().toString(),
                        dataSnapshot.child("pubdate").getValue().toString(),
                        dataSnapshot.child("isbn").getValue().toString(),
                        ((MainActivity) getActivity()).getOwner_email());
                item.setValue(Integer.parseInt(dataSnapshot.child("value").getValue().toString()));
                item.setAverageStar(Double.parseDouble(dataSnapshot.child("averageStar").getValue().toString()));
                item.setBorrower(dataSnapshot.child("borrower").getValue().toString());
                item.setStatus(item.parseStatus(dataSnapshot.child("status").getValue().toString()));
                adapter2.getItems().add(item);
                adapter2.notifyItemInserted(adapter2.getItemCount() - 1);
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

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String isbn1 = adapter1.getItems().get(position).getIsbn();
                        libraryRef.orderByChild("isbn").equalTo(isbn1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.e("fragment 0", dataSnapshot.getValue().toString());
                                if (dataSnapshot.getValue() != null) {
                                    String[] datas = dataSnapshot.getValue().toString().substring(1).split("=");
                                    Intent intent = new Intent(getContext(), BookActivity.class);
                                    intent.putExtra("key", datas[0]);
                                    intent.putExtra("owner_email", ((MainActivity) getActivity()).getOwner_email());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }));

        recyclerView2.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String isbn1 = adapter2.getItems().get(position).getIsbn();
                        libraryRef.orderByChild("isbn").equalTo(isbn1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.e("fragment 0", dataSnapshot.getValue().toString());
                                if (dataSnapshot.getValue() != null) {
                                    String[] datas = dataSnapshot.getValue().toString().substring(1).split("=");
                                    Intent intent = new Intent(getContext(), BookActivity.class);
                                    intent.putExtra("key", datas[0]);
                                    intent.putExtra("owner_email", ((MainActivity) getActivity()).getOwner_email());
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }));

        return view;
    }
}
