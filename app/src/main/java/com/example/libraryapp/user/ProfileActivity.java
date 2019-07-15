package com.example.libraryapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.libraryapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {
    private String owner_email;
    private DatabaseReference libraryRef;
    private DatabaseReference ref;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref = database.getReference("server/saving-data/");
        libraryRef = ref.child("library");
        Intent intent = getIntent();
        owner_email = intent.getStringExtra("owner_email");
        TableRow profileRow = findViewById(R.id.profile_row);
        TableRow locationRow = findViewById(R.id.location_row);
        TableRow gotoRow = findViewById(R.id.profile_goto);
        TableRow logoutRow = findViewById(R.id.profile_signout);
        TextView profile_name = findViewById(R.id.profile_name);
        TextView profile_email = findViewById(R.id.profile_email);
        TextView profile_location = findViewById(R.id.profile_location);
        final TextView profile_added = findViewById(R.id.profile_added_number);
        final TextView profile_borrowed = findViewById(R.id.profile_borrowed_number);
        TextView profile_purchased = findViewById(R.id.profile_purchased_number);
        profile_email.setText(owner_email);

        libraryRef.orderByChild("owner").equalTo(owner_email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numChildren = dataSnapshot.getChildrenCount();
                if (numChildren > 0) {
                    profile_added.setText(String.valueOf(numChildren));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        libraryRef.orderByChild("borrower").equalTo(owner_email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long numChildren = dataSnapshot.getChildrenCount();
                if (numChildren > 0) {
                    profile_borrowed.setText(String.valueOf(numChildren));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        gotoRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, LibraryActivity.class);
                intent.putExtra("owner_email", owner_email);
                startActivity(intent);
                finish();
            }
        });

        logoutRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                ActivityCompat.finishAffinity(ProfileActivity.this);
                Intent restart = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(restart);
            }
        });
    }
}
