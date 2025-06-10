// app/src/main/java/com/av/avmessenger/registration.java
package com.av.avmessenger;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class registration extends AppCompatActivity {
    TextView loginbut;
    EditText rg_username, rg_email, rg_password, rg_repassword;
    Button rg_signup;
    CircleImageView rg_profileImg;
    Uri imageURI;
    String imageuri;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Establishing The Account");
        progressDialog.setCancelable(false);
//        getSupportActionBar().hide();
        dbHelper = new DatabaseHelper(this);
        loginbut = findViewById(R.id.loginbut);
        rg_username = findViewById(R.id.rgusername);
        rg_email = findViewById(R.id.rgemail);
        rg_password = findViewById(R.id.rgpassword);
        rg_repassword = findViewById(R.id.rgrepassword);
        rg_profileImg = findViewById(R.id.profilerg0);
        rg_signup = findViewById(R.id.signupbutton);

        loginbut.setOnClickListener(v -> {
            Intent intent = new Intent(registration.this, login.class);
            startActivity(intent);
            finish();
        });

        rg_signup.setOnClickListener(v -> {
            String namee = rg_username.getText().toString();
            String emaill = rg_email.getText().toString();
            String Password = rg_password.getText().toString();
            String cPassword = rg_repassword.getText().toString();
            String status = "Hey I'm Using This Application";

            if (TextUtils.isEmpty(namee) || TextUtils.isEmpty(emaill) ||
                    TextUtils.isEmpty(Password) || TextUtils.isEmpty(cPassword)) {
                progressDialog.dismiss();
                Toast.makeText(registration.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
            } else if (!emaill.matches(emailPattern)) {
                progressDialog.dismiss();
                rg_email.setError("Type A Valid Email Here");
            } else if (Password.length() < 6) {
                progressDialog.dismiss();
                rg_password.setError("Password Must Be 6 Characters Or More");
            } else if (!Password.equals(cPassword)) {
                progressDialog.dismiss();
                rg_password.setError("The Password Doesn't Match");
            } else {
                String id = String.valueOf(System.currentTimeMillis());
                imageuri = (imageURI != null) ? imageURI.toString() :
                        "android.resource://com.av.avmessenger/drawable/whatsapp"; // fallback local image
                Users user = new Users(id, namee, emaill, Password, imageuri, status);
                boolean success = dbHelper.registerUser(user);
                if (success) {
                    progressDialog.show();
                    SessionManager.getInstance().setCurrentUser(user);
                    Intent intent = new Intent(registration.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(registration.this, "User already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rg_profileImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK && data != null) {
            imageURI = data.getData();
            rg_profileImg.setImageURI(imageURI);

            // Salin ke penyimpanan lokal dan simpan path-nya
            imageuri = copyImageToLocalStorage(imageURI);
        }
    }


    private String copyImageToLocalStorage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File file = new File(getFilesDir(), "profile_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath(); // Simpan path-nya
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}