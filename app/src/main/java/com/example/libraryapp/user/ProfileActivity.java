package com.example.libraryapp.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.libraryapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TableRow profileRow = findViewById(R.id.profile_row);
        TableRow locationRow = findViewById(R.id.location_row);
        TableRow gotoRow = findViewById(R.id.profile_goto);
        TableRow logoutRow = findViewById(R.id.profile_signout);

        gotoRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, LibraryActivity.class);
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
