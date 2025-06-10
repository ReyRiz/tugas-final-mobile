package com.av.avmessenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class setting extends AppCompatActivity {
    ImageView setprofile;
    EditText setname, setstatus;
    Button donebut;
    Uri setImageUri;
    ProgressDialog progressDialog;
    DatabaseHelper dbHelper;
    Users currentUser;
    SwitchCompat themeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        getSupportActionBar().hide();
        dbHelper = new DatabaseHelper(this);
        setprofile = findViewById(R.id.settingprofile);
        setname = findViewById(R.id.settingname);
        setstatus = findViewById(R.id.settingstatus);
        donebut = findViewById(R.id.donebutt);
        themeSwitch = findViewById(R.id.themeSwitch);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);

        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            setname.setText(currentUser.getUserName());
            setstatus.setText(currentUser.getStatus());
            Picasso.get().load(currentUser.getProfilepic()).into(setprofile);
        }

        setprofile.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
        });

        donebut.setOnClickListener(view -> {
            progressDialog.show();
            String name = setname.getText().toString();
            String Status = setstatus.getText().toString();
            String profileUri = (setImageUri != null) ? setImageUri.toString() : currentUser.getProfilepic();
            currentUser.setUserName(name);
            currentUser.setStatus(Status);
            currentUser.setProfilepic(profileUri);
            dbHelper.updateUser(currentUser);
            progressDialog.dismiss();
            Toast.makeText(setting.this, "Data is saved", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(setting.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Theme switch logic
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isNight = prefs.getBoolean("night", false);
        themeSwitch.setChecked(isNight);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                prefs.edit().putBoolean("night", true).apply();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                prefs.edit().putBoolean("night", false).apply();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && data != null) {
            setImageUri = data.getData();
            setprofile.setImageURI(setImageUri);

            // Copy the image to local storage and get the path
            String x = copyImageToLocalStorage(setImageUri);
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