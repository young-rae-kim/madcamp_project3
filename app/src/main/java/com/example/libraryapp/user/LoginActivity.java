package com.example.libraryapp.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.libraryapp.PreActivity;
import com.example.libraryapp.R;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private final int RC_SIGN_IN = 0;
    private ImageView createAccount;
    private TextInputEditText loginEmail;
    private TextInputEditText loginPassword;
    private MaterialButton loginButton;
    private SignInButton signInButton;
    private Boolean saveLogin;
    private CheckBox saveLoginCheckbox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private View loginView;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent resultIntent = new Intent(LoginActivity.this, PreActivity.class);
            resultIntent.putExtra("owner_email", currentUser.getEmail());
            startActivity(resultIntent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginEmail = findViewById(R.id.emailText);
        loginPassword = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginView = view;
                loginUser(loginEmail.getText().toString(),
                        loginPassword.getText().toString());
            }
        });

        saveLoginCheckbox = findViewById(R.id.loginCheckbox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            loginEmail.setText(loginPreferences.getString("email", ""));
            loginPassword.setText(loginPreferences.getString("password", ""));
            saveLoginCheckbox.setChecked(true);
        }

        signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        createAccount = findViewById(R.id.registerImage);
        createAccount.setImageResource(R.drawable.library);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View registerView = LayoutInflater.from(LoginActivity.this)
                        .inflate(R.layout.register_layout, null);

                new MaterialStyledDialog.Builder(LoginActivity.this)
                        .setIcon(R.drawable.ic_register)
                        .setTitle("Registration")
                        .setDescription("Please fill all fields.")
                        .setCustomView(registerView)
                        .setNegativeText("Cancel")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveText("Register")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                TextInputEditText editRegisterEmail = registerView.findViewById(R.id.registerEmailText);
                                TextInputEditText editRegisterName = registerView.findViewById(R.id.registerNameText);
                                TextInputEditText editRegisterPassword = registerView.findViewById(R.id.registerPasswordText);

                                if (TextUtils.isEmpty(editRegisterEmail.getText().toString())) {
                                    Toast.makeText(LoginActivity.this, "Email cannot be null or empty.", Toast.LENGTH_LONG)
                                            .show();
                                    dialog.dismiss();
                                    return;
                                } else if (TextUtils.isEmpty(editRegisterName.getText().toString())) {
                                    Toast.makeText(LoginActivity.this, "Name cannot be null or empty.", Toast.LENGTH_LONG)
                                            .show();
                                    dialog.dismiss();
                                    return;
                                } else if (TextUtils.isEmpty(editRegisterPassword.getText().toString())) {
                                    Toast.makeText(LoginActivity.this, "Password cannot be null or empty.", Toast.LENGTH_LONG)
                                            .show();
                                    dialog.dismiss();
                                    return;
                                }

                                registerUser(editRegisterEmail.getText().toString(),
                                        editRegisterName.getText().toString(),
                                        editRegisterPassword.getText().toString());
                            }
                        }).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Login", "signInWithGoogle:failure", task.getException());
                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginUser(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Snackbar.make(loginView, "Email cannot be null or empty.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Snackbar.make(loginView, "Password cannot be null or empty.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        // Connect
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Login", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(loginEmail.getWindowToken(), 0);

                            String savedEmail = loginEmail.getText().toString();
                            String savedPassword = loginPassword.getText().toString();

                            if (saveLoginCheckbox.isChecked()) {
                                loginPrefsEditor.putBoolean("saveLogin", true);
                                loginPrefsEditor.putString("email", savedEmail);
                                loginPrefsEditor.putString("password", savedPassword);
                                loginPrefsEditor.commit();
                            } else {
                                loginPrefsEditor.clear();
                                loginPrefsEditor.commit();
                            }
                            //Intent resultIntent = new Intent(LoginActivity.this, PreActivity.class);
                            Intent resultIntent = new Intent(LoginActivity.this, CheckActivity.class);
                            resultIntent.putExtra("owner_email", savedEmail);
                            startActivity(resultIntent);
                            finish();
                        } else {
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }

    private void registerUser(String email, String name, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Register", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Authentication & Login succeeded.",
                                    Toast.LENGTH_SHORT).show();
                            //Intent resultIntent = new Intent(LoginActivity.this, CheckActivity.class);
                            Intent resultIntent = new Intent(LoginActivity.this, PreActivity.class);
                            resultIntent.putExtra("owner_email", user.getEmail());
                            startActivity(resultIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Register", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Login", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Login", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent resultIntent = new Intent(LoginActivity.this, PreActivity.class);
                            resultIntent.putExtra("owner_email", user.getEmail());
                            startActivity(resultIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Login", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }
}
