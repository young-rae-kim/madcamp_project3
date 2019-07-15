package com.example.libraryapp;

import android.content.Intent;
import android.os.Bundle;

import com.example.libraryapp.user.LibraryActivity;
import com.example.libraryapp.user.LoginActivity;
import com.example.libraryapp.user.ProfileActivity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String owner_email;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private FloatingActionButton fab;
    private BottomSheetBehavior bottomSheetBehavior;
    private NavigationView navigationView;
    private int[][] ratings = null;
    private ArrayList<String> isbnMap = new ArrayList<>();
    private ArrayList<String> userMap = new ArrayList<>();
    private int user_index = 0;
    private int book_index = 0;
    private DatabaseReference ref;
    private DatabaseReference userRef;
    private DatabaseReference bookRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomAppBar toolbar = findViewById(R.id.bar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        owner_email = intent.getStringExtra("owner_email");
        mViewPager = findViewById(R.id.pager_content);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), 3);
        mViewPager.setAdapter(mPagerAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int position = extras.getInt("fragment");
            mViewPager.setCurrentItem(position);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("server/saving-data/");
        userRef = ref.child("user");
        bookRef = ref.child("book");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final long userNum = dataSnapshot.getChildrenCount();
                bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final long bookNum = dataSnapshot.getChildrenCount();
                        ratings = new int[(int) userNum][(int) bookNum];
                        userRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                if (dataSnapshot.getValue() != null && dataSnapshot.hasChild("userRating")) {
                                    String data = dataSnapshot.child("userRating").getValue().toString().substring(1, dataSnapshot.child("userRating").getValue().toString().length() - 1);
                                    Log.e("main", data);
                                    String[] ratingData = data.split(",");
                                    for (int i = 0; i < ratingData.length; i++) {
                                        String[] split = ratingData[i].split("=");
                                        if (isbnMap.contains(split[0])) {
                                            ratings[user_index][isbnMap.indexOf(split[0])] = Integer.parseInt(split[1]);
                                        } else {
                                            ratings[user_index][book_index] = Integer.parseInt(split[1]);
                                            isbnMap.add(split[0]);
                                            book_index++;
                                        }
                                    }
                                    userMap.add(dataSnapshot.child("userMail").getValue().toString());
                                    user_index++;
                                } else {
                                    for (int j = 0; j < bookNum; j++) {
                                        ratings[user_index][j] = 0;
                                    }
                                    userMap.add(dataSnapshot.child("userMail").getValue().toString());
                                    user_index++;
                                }
                                for (int i = 0; i < ratings[user_index - 1].length; i++) {
                                        System.out.print(ratings[user_index - 1][i]);
                                }
                                System.out.print("\n");
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LinearLayout llBottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        fab.show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.hide();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Bundle bundle = new Bundle();
                bundle.putString("owner_email", owner_email);
                BottomSheetFragment fragment = new BottomSheetFragment();
                fragment.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.bottom_frame, fragment);
                fragmentTransaction.commit();
            }
        });
    }

    public String getOwner_email() { return owner_email; }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            fab.show();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("owner_email", owner_email);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_library) {
            Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
            intent.putExtra("owner_email", owner_email);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            ActivityCompat.finishAffinity(MainActivity.this);
            Intent restart = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(restart);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setCheckable(false);
        }
        return true;
    }
}
