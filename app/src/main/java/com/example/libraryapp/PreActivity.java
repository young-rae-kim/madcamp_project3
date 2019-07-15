package com.example.libraryapp;

import android.Manifest;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryapp.user.CheckActivity;
import com.example.libraryapp.user.ProfileActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.HashMap;

public class PreActivity extends AppCompatActivity {
    private String owner_email;
    private int item_clicked = 0;
    private RecyclerView recyclerView;
    private MenuAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre);
        Intent intent = getIntent();
        int code = intent.getIntExtra("code",-1);
        System.out.println("code is: "+code);
//        if (code == 0) {// 뒤로 가기 버튼을 눌렀으면 받은
//            System.out.println("if condtition===========================================");
//            CheckActivity.user.getUserRating().clear();
//        }
        System.out.println("PreActivity==============================================================");
        for ( HashMap.Entry<String, Double> entry :  CheckActivity.user.getUserRating().entrySet() ) {
            System.out.println("방법2) key : " + entry.getKey() +" / value : " + entry.getValue());
        }


        출처: https://donggov.tistory.com/51 [동고랩]

        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().setStatusBarColor(getColor(R.color.colorPrimaryDark));
        }

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        }, 0);

        Intent intent = getIntent();
        owner_email = intent.getStringExtra("owner_email");
        recyclerView = findViewById(R.id.recycler_menu);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) layoutManager).setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setClickable(true);
        mAdapter = new MenuAdapter(getApplicationContext());
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final MenuAdapter.MenuViewHolder old_holder = (MenuAdapter.MenuViewHolder) recyclerView.findViewHolderForAdapterPosition(item_clicked);
                        final MenuAdapter.MenuViewHolder holder = (MenuAdapter.MenuViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                        if (item_clicked != position && holder != null && old_holder != null) {
                            old_holder.getNameView().setPaintFlags(old_holder.getNameView().getPaintFlags() & (~ Paint.UNDERLINE_TEXT_FLAG));
                            holder.getNameView().setPaintFlags(holder.getNameView().getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                            item_clicked = position;
                            return;
                        }

                        switch (position) {
                            case 0:
                                Intent intent0 = new Intent(PreActivity.this, MainActivity.class);
                                intent0.putExtra("fragment", position);
                                intent0.putExtra("owner_email", owner_email);
                                startActivity(intent0);
                                break;
                            case 1:
                                Intent intent1 = new Intent(PreActivity.this, MainActivity.class);
                                intent1.putExtra("fragment", position);
                                intent1.putExtra("owner_email", owner_email);
                                startActivity(intent1);
                                break;
                            case 2:
                                Intent intent2 = new Intent(PreActivity.this, MainActivity.class);
                                intent2.putExtra("fragment", position);
                                intent2.putExtra("owner_email", owner_email);
                                startActivity(intent2);
                                break;
                            case 3:
                                Intent intent = new Intent(PreActivity.this, ProfileActivity.class);
                                intent.putExtra("owner_email", owner_email);
                                startActivity(intent);
                                break;
                        }
                    }
                }));
        recyclerView.setAdapter(mAdapter);

        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        Bundle bundle = new Bundle();
        bundle.putString("owner_email", owner_email);
        BottomSheetFragment fragment = new BottomSheetFragment();
        fragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.bottom_frame, fragment);
        fragmentTransaction.commit();
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public String getOwner_email() { return owner_email; }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}
