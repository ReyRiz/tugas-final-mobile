package com.av.avmessenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatwindo extends AppCompatActivity {
    String reciverimg, reciverUid, reciverName, SenderUID, reciverBio;
    CircleImageView profile;
    TextView reciverNName, reciverBioText;
    public static String senderImg;
    public static String reciverIImg;
    CardView sendbtn;
    EditText textmsg;
    ImageView btnBack;

    String senderRoom, reciverRoom;
    RecyclerView messageAdpter;
    ArrayList<msgModelclass> messagesArrayList;
    messagesAdpter mmessagesAdpter;
    DatabaseHelper dbHelper;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatwindo);
//        getSupportActionBar().hide();

        btnBack = findViewById(R.id.btnBack);



        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        reciverUid = getIntent().getStringExtra("uid");
        reciverBio = getIntent().getStringExtra("bioooo");


        SenderUID = SessionManager.getInstance().getCurrentUser().getUserId();

        senderImg = SessionManager.getInstance().getCurrentUser().getProfilepic();
        reciverIImg = reciverimg;

        senderRoom = SenderUID + reciverUid;
        reciverRoom = reciverUid + SenderUID;

        messagesArrayList = new ArrayList<>();
        dbHelper = new DatabaseHelper(this);

        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);
        reciverBioText = findViewById(R.id.receiverbio);
        reciverNName = findViewById(R.id.recivername);
        profile = findViewById(R.id.profileimgg);
        messageAdpter = findViewById(R.id.msgadpter);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdpter.setLayoutManager(linearLayoutManager);
        mmessagesAdpter = new messagesAdpter(chatwindo.this, messagesArrayList);
        messageAdpter.setAdapter(mmessagesAdpter);

        Picasso.get().load(reciverimg).into(profile);
        reciverNName.setText(reciverName);
        reciverBioText.setText(reciverBio);

        loadMessages();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = textmsg.getText().toString().trim();
                if (message.isEmpty()) {
                    Toast.makeText(chatwindo.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                    return;
                }
                textmsg.setText("");
                Date date = new Date();
                msgModelclass messagess = new msgModelclass(message, SenderUID, date.getTime());

                // Save to both rooms
                if (Objects.equals(senderRoom, reciverRoom)) {
                    Toast.makeText(chatwindo.this, "Your messsage only see by urself", Toast.LENGTH_SHORT).show();
                    dbHelper.addMessage(messagess, senderRoom);
                } else {
                    dbHelper.addMessage(messagess, senderRoom);
                    dbHelper.addMessage(messagess, reciverRoom);
                }

                loadMessages();
            }
        });
    }

    private void loadMessages() {
        messagesArrayList.clear();
        messagesArrayList.addAll(dbHelper.getMessages(senderRoom));
        mmessagesAdpter.notifyDataSetChanged();
        // Scroll to the latest message
        if (!messagesArrayList.isEmpty()) {
            messageAdpter.scrollToPosition(messagesArrayList.size() - 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMessages();
    }
}