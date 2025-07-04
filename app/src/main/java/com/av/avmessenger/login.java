// app/src/main/java/com/av/avmessenger/login.java
package com.av.avmessenger;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class login extends AppCompatActivity {
    TextView logsignup;
    Button button;
    EditText email, password;
    ProgressDialog progressDialog;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
//        getSupportActionBar().hide();
        dbHelper = new DatabaseHelper(this);
        button = findViewById(R.id.logbutton);
        email = findViewById(R.id.editTexLogEmail);
        password = findViewById(R.id.editTextLogPassword);
        logsignup = findViewById(R.id.logsignup);

        logsignup.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, registration.class);
            startActivity(intent);
            finish();
        });

        button.setOnClickListener(v -> {
            String Email = email.getText().toString();
            String pass = password.getText().toString();

            if (TextUtils.isEmpty(Email)) {
                progressDialog.dismiss();
                Toast.makeText(login.this, "Enter The Email", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(pass)) {
                progressDialog.dismiss();
                Toast.makeText(login.this, "Enter The Password", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                progressDialog.dismiss();
                password.setError("More Than Six Characters");
                Toast.makeText(login.this, "Password Needs To Be Longer Than Six Characters", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show();
                Users user = dbHelper.authenticateUser(Email, pass);
                if (user != null) {
                    SessionManager.getInstance().setCurrentUser(user);
                    Intent intent = new Intent(login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(login.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}