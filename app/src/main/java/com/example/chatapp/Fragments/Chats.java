package com.example.chatapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.Adapters.ChatList;
import com.example.chatapp.Adapters.UserAdapter;
import com.example.chatapp.Adapters.UserClass;
import com.example.chatapp.Notifications.Token;
import com.example.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chats extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserClass> users;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    public List<ChatList> chatLists;

    public Chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        //FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        View view=inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView=view.findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        chatLists=new ArrayList<ChatList>();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("ChatBox").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatLists.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String ID=snapshot.getKey().toString();
                    ChatList chatList=new ChatList(ID);
                    chatLists.add(chatList);
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(!task.isSuccessful()){
                    return;
                }updateToken(task.getResult().getToken());
            }
        });
        return view;
    }
    private void updateToken(String s) {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Tokens");
        Token token=new Token(s);
        databaseReference.child(firebaseUser.getUid()).setValue(token);
    }

    private void readChats(){
        users=new ArrayList<>();
        //Toast.makeText(getContext(),chatLists.toString(),Toast.LENGTH_LONG).show();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("UsersList");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String UserId=snapshot.child("UserId").getValue(String.class);
                    String PhoneNo=snapshot.child("PhoneNo").getValue(String.class);
                    String ProfileImage=snapshot.child("ProfileImage").getValue(String.class);
                    String Status=dataSnapshot.child("Status").getValue(String.class);
                    UserClass user=new UserClass(UserId,PhoneNo,ProfileImage,Status);
                    assert user!=null;
                   for(ChatList chatList : chatLists){
                        if(user.getUserId().equals(chatList.getId())){
                            users.add(user);
                        }
                    }

                    userAdapter=new UserAdapter(getContext(),users);
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem=menu.findItem(R.id.search);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setQueryHint("Search User....");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
