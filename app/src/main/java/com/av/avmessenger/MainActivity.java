// app/src/main/java/com/av/avmessenger/MainActivity.java
package com.av.avmessenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.Toast;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    RecyclerView mainUserRecyclerView;
    FrameLayout mainUserFragment;
    ImageView btnRefresh;
    UserAdpter adapter;
    ArrayList<Users> usersArrayList = new ArrayList<>();
    ImageView imglogout, cumbut, setbut, chatBut;
    DatabaseHelper dbHelper;
    Executor executor = Executors.newSingleThreadExecutor();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().hide();

        dbHelper = new DatabaseHelper(this);
        cumbut = findViewById(R.id.camBut);
        setbut = findViewById(R.id.settingBut);
        chatBut = findViewById(R.id.chatBtn);

        mainUserFragment = findViewById(R.id.fragment_container);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new CommunityFragment())
                .commit();
//
////        mainUserRecyclerView = findViewById(R.id.mainUserRecyclerView);
//        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new UserAdpter(MainActivity.this, usersArrayList);
//        mainUserRecyclerView.setAdapter(adapter);

        chatBut.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ChatFragment())
                    .commit();
        });

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        if (isDarkMode) {
            setTheme(R.style.SCREEN_DARK);
        } else {
            setTheme(R.style.SCREEN);
        }



        // Load users in background
        executor.execute(() -> {
            btnRefresh = findViewById(R.id.btnRefresh);

            btnRefresh.setOnClickListener(v -> {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new CommunityFragment())
                        .commit();
            });

            ArrayList<Users> loadedUsers = dbHelper.getAllUsers();
            runOnUiThread(() -> {
                usersArrayList.clear();
                usersArrayList.addAll(loadedUsers);
//                adapter.notifyDataSetChanged();
            });
        });



        imglogout = findViewById(R.id.logoutimg);
        setbut.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, setting.class);
            startActivity(intent);
        });

        imglogout.setOnClickListener(v -> {
            Dialog dialog = new Dialog(MainActivity.this, R.style.dialoge);
            dialog.setContentView(R.layout.dialog_layout);
            Button no = dialog.findViewById(R.id.nobnt);
            Button yes = dialog.findViewById(R.id.yesbnt);
            yes.setOnClickListener(v1 -> {
                SessionManager.getInstance().logout();
                Intent intent = new Intent(MainActivity.this, login.class);
                startActivity(intent);
                finish();
            });
            no.setOnClickListener(v12 -> dialog.dismiss());
            dialog.show();
        });

        cumbut.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 10);
        });

        if (SessionManager.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
            finish();
        }
    }



}

