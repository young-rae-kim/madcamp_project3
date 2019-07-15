package com.example.libraryapp.Fragment2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.libraryapp.BookActivity;
import com.example.libraryapp.BookItem;
import com.example.libraryapp.MainActivity;
import com.example.libraryapp.PreActivity;
import com.example.libraryapp.R;
import com.example.libraryapp.RecyclerItemClickListener;
import com.example.libraryapp.user.BookAdapter;
import com.example.libraryapp.user.LibraryActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Fragment2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private TextView mTextView;
    private EditText editText;
    private static Context context;
    static ArrayList<BookItem> copyArraylList= new ArrayList<>();
    static ArrayList<BookItem> bookItemArrayList = new ArrayList<>();
    ArrayList<String> bookIDList = new ArrayList<>();
    BookAdapter bookAdapter;
    RecyclerView recyclerView;
    private ImageButton ib_back;
    private DatabaseReference ref;
    private DatabaseReference libraryRef;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int index = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookItemArrayList=new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("server/saving-data/");
        libraryRef = ref.child("library");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(R.layout.fragment_2, null);
        editText = (EditText) view.findViewById(R.id.et_search);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        bookAdapter = new BookAdapter(bookItemArrayList, Glide.with(view));
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setAdapter(bookAdapter);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        ib_back = (ImageButton) view.findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PreActivity.class);
                startActivity(intent);
            }
        });

        libraryRef.addChildEventListener(new ChildEventListener() {
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
                                                     bookIDList.add(dataSnapshot.getKey());
                                                     bookAdapter.getItems().add(item);
                                                     bookAdapter.notifyItemInserted(bookAdapter.getItemCount() - 1);
                                                 }
                                             }

                                             @Override
                                             public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                             }

                                             @Override
                                             public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                             }

                                             @Override
                                             public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                             }

                                             @Override
                                             public void onCancelled(@NonNull DatabaseError databaseError) {

                                             }
                                         });

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

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getContext(), BookActivity.class);
                        intent.putExtra("key", bookIDList.get(position));
                        intent.putExtra("owner_email", ((MainActivity) getActivity()).getOwner_email());
                        startActivity(intent);
                        getActivity().finish();
                        index = position;
                    }
                }));

        return view;
    }

    public void search(String charText) {

        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        copyArraylList.addAll(bookAdapter.getItems());
        System.out.println(copyArraylList.size());
        bookAdapter.getItems().clear();
        bookAdapter.notifyDataSetChanged();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            bookAdapter.getItems().addAll(copyArraylList);
            bookAdapter.notifyDataSetChanged();
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
                    bookAdapter.getItems().add(copyArraylList.get(i));
                    bookAdapter.notifyItemInserted(bookAdapter.getItemCount() - 1);
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        bookAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bookAdapter.notifyItemChanged(index);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }
}

