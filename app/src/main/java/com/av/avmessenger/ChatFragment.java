package com.av.avmessenger;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatFragment extends Fragment {


    RecyclerView mainUserRecyclerView2;
    UserAdpter adapter;
    ArrayList<Users> usersArrayList = new ArrayList<>();

    DatabaseHelper dbHelper;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mainUserRecyclerView2 = view.findViewById(R.id.mainUserRecyclerViewChat);

        mainUserRecyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdpter(getContext(), usersArrayList);
        mainUserRecyclerView2.setAdapter(adapter);

        usersArrayList.clear();
        usersArrayList.addAll(dbHelper.getAllUsers());
        adapter.notifyDataSetChanged();


        return view;
    }


}
