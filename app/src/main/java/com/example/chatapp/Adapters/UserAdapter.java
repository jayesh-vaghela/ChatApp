package com.example.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapp.MessageActivity;
import com.example.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> implements Filterable {
    private Context context;
    private List<UserClass> users;
    private List<UserClass> usersList;
    private String last_msg;


    public UserAdapter(Context context, List<UserClass> users) {
        this.context = context;
        this.users = users;
        this.usersList=new ArrayList<>(users);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.contactview,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

  public Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<UserClass> searchuser=new ArrayList<>();
            if(constraint.toString().isEmpty()){
                searchuser.addAll(usersList);
            }
            else {
                for(UserClass user : usersList){
                   if(user.getPhoneNo().toString().contains(constraint.toString())){
                       searchuser.add(user);
                    }
                }
            }
            FilterResults filterResults=new FilterResults();
            filterResults.values=searchuser;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
                users.clear();
                users.addAll((Collection<? extends UserClass>) results.values);
                notifyDataSetChanged();
        }
    };

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final UserClass user=users.get(position);
        holder.username.setText(user.getPhoneNo());
        if(user.getProfileImage().toString().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context).load(user.getProfileImage()).into(holder.profile_image);
        }
        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("ChatBox");
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        databaseReference.child(firebaseUser.getUid()).child(user.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                last_msg="No Message";
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        last_msg=snapshot1.getValue(String.class);
                    }
                }
                holder.lastmsg.setText(last_msg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("UserId",user.getUserId());
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username,lastmsg;
        public CircleImageView profile_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.un);
            profile_image=itemView.findViewById(R.id.pi);
            lastmsg=itemView.findViewById(R.id.last_msg);
        }
    }


}
