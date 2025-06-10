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

public class CommunityFragment extends Fragment {


    RecyclerView mainUserRecyclerView;
    UserAdpter adapter;
    ArrayList<Users> usersArrayList = new ArrayList<>();

    DatabaseHelper dbHelper;

    public CommunityFragment() {
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

        View view = inflater.inflate(R.layout.fragment_community, container, false);

        mainUserRecyclerView = view.findViewById(R.id.mainUserRecyclerView);

        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdpter(getContext(), usersArrayList);
        mainUserRecyclerView.setAdapter(adapter);

        usersArrayList.clear();
        fetchUsersFromApi();


        return view;
    }

    private void fetchUsersFromApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://randomuser.me/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RandomUserApi api = retrofit.create(RandomUserApi.class);

        api.getUsers().enqueue(new Callback<RandomUserResponse>() {
            @Override
            public void onResponse(Call<RandomUserResponse> call, Response<RandomUserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usersArrayList.clear();
                    for (RandomUser ru : response.body().results) {
                        Users user = new Users(
                                String.valueOf(System.currentTimeMillis()),
                                ru.name.first + " " + ru.name.last,
                                ru.email,
                                "password", // placeholder
                                ru.picture.large,
                                "Online"
                        );
                        usersArrayList.add(user);
                    }
                    adapter.notifyDataSetChanged(); // Pastikan dipanggil
                } else {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RandomUserResponse> call, Throwable t) {
                Toast.makeText(getContext(), "No network. Tap refresh.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
