package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.chatapp.Adapters.ChatAdapter;
import com.example.chatapp.Adapters.ChatClass;
import com.example.chatapp.Adapters.UserClass;
import com.example.chatapp.Fragments.APIService;
import com.example.chatapp.Notifications.Client;
import com.example.chatapp.Notifications.Data;
import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;
import com.example.chatapp.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView imageView;
    private TextView username,status;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private ImageButton btn_send;
    private EditText text_send;
    private Intent intent;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<ChatClass> chats;
    private ChatAdapter chatAdapter;
    private APIService apiService;
    private boolean notify=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        imageView=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        status=findViewById(R.id.status);
        text_send=findViewById(R.id.text_send);
        btn_send=findViewById(R.id.btn_send);
        recyclerView=findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        intent=getIntent();
        final String UserId=intent.getStringExtra("UserId");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("UsersList").child(UserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String UserId=dataSnapshot.child("UserId").getValue(String.class);
                String PhoneNo=dataSnapshot.child("PhoneNo").getValue(String.class);
                String ProfileImage=dataSnapshot.child("ProfileImage").getValue(String.class);
                String Status=dataSnapshot.child("Status").getValue(String.class);
                UserClass user=new UserClass(UserId,PhoneNo,ProfileImage,Status);
                assert user!=null;
                username.setText(user.getPhoneNo());
                if(user.getProfileImage().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(MessageActivity.this).load(user.getProfileImage()).into(imageView);
                }
                if(user.getStatus().toString().equals("online")){

                    status.setText(user.getStatus());
                    status.setVisibility(View.VISIBLE);
                }
                else {
                    status.setVisibility(View.GONE);
                }
                readMessages(firebaseUser.getUid(),UserId,user.getProfileImage());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("Json"," : Send ");
                String msg=text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),UserId,msg);
                }else{
                    Toast.makeText(MessageActivity.this,"Message is Empty",Toast.LENGTH_LONG).show();
                }
                text_send.setText("");
            }
        });
    }
    public void sendMessage(String Sender, final String Receiver, String Message){
        notify=true;
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("ChatBox");
        databaseReference.child(Receiver).child(Sender).child(DateFormat.getDateInstance(DateFormat.FULL).format(Calendar.getInstance().getTime())).child(DateFormat.getTimeInstance().format(new Date())+":R").setValue(Message);
        databaseReference.child(Sender).child(Receiver).child(DateFormat.getDateInstance(DateFormat.FULL).format(Calendar.getInstance().getTime())).child(DateFormat.getTimeInstance().format(new Date())+":S").setValue(Message);
        final String msg=Message;
       DatabaseReference reference= FirebaseDatabase.getInstance().getReference("UsersList").child(Sender);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String UserId=dataSnapshot.child("UserId").getValue(String.class);
                String PhoneNo=dataSnapshot.child("PhoneNo").getValue(String.class);
                String ProfileImage=dataSnapshot.child("ProfileImage").getValue(String.class);
                String Status=dataSnapshot.child("Status").getValue(String.class);
                UserClass user=new UserClass(UserId,PhoneNo,ProfileImage,Status);
                assert user!=null;
                if(notify){
                    sendNotification(Receiver,user.getPhoneNo(),msg);
                }notify=false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    private void sendNotification(final String receiver, final String username, final String message){
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("Tokens");
        Query query=databaseReference.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + " : " + message, "New Message", intent.getStringExtra("UserId").toString());
                    Sender sender = new Sender(data, token.getToken());

                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                        if(response.code()==200){
                            if(response.body().success==1){
                                Log.d("Json","Success == 1");
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Log.d("Json","Failure == 1");
                    }
                });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Status("offline");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Status("online");
    }

    private void Status(String online) {
        DatabaseReference  databaseReference= FirebaseDatabase.getInstance().getReference().child("UsersList").child(firebaseUser.getUid());
        databaseReference.child("Status").setValue(online);

    }


    public void readMessages(final String MyId, final String UserId, final String ProfileImage){
        chats=new ArrayList<>();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("ChatBox").child(MyId).child(UserId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ChatClass chat;
                        if (snapshot1.getKey().toString().endsWith("S")) {
                            chat = new ChatClass(UserId, MyId, snapshot1.getValue(String.class), snapshot1.getKey().toString().substring(0, 4) + " " + snapshot1.getKey().toString().substring(8, 10));
                        } else {
                            chat = new ChatClass(MyId, UserId, snapshot1.getValue(String.class), snapshot1.getKey().toString().substring(0, 4) + " " + snapshot1.getKey().toString().substring(8, 10));
                        }
                        assert chat != null;
                        chats.add(chat);
                        chatAdapter = new ChatAdapter(MessageActivity.this, chats, ProfileImage);
                        recyclerView.setAdapter(chatAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }
}
