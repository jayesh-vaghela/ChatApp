package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.chatapp.Adapters.PageAdapter;
import com.example.chatapp.Settings.SettingsActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private PageAdapter adapter;
    private SharedPreferences sharedPreferences;
    private FirebaseUser firebaseUser;
    private boolean darktheme=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK){
            case Configuration.UI_MODE_NIGHT_YES:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                //Log.d("Theme","Dark");
                darktheme=true;
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                //Log.d("Theme","Light");
                break;
        }*/
       /*sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(!darktheme) {
            if (sharedPreferences.getBoolean("mkey", false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }*/
        super.onCreate(savedInstanceState);
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK){
            case Configuration.UI_MODE_NIGHT_YES:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                //Log.d("Theme","Dark");
                darktheme=true;
                break;
        }
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(!darktheme) {
            if (sharedPreferences.getBoolean("mkey", false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }
        setContentView(R.layout.activity_main);
        /*switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK){
            case Configuration.UI_MODE_NIGHT_YES:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                //Log.d("Theme","Dark");
                darktheme=true;
                break;
        }
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(!darktheme) {
            if (sharedPreferences.getBoolean("mkey", false)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }*/
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        adapter=new PageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout=(TabLayout)findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);

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

    @Override
    protected void onStart() {
        super.onStart();
        //SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        //if (sharedPreferences.getBoolean("mkey", false)) {
        //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        //}

    }

    private void Status(String online) {
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("UsersList").child(firebaseUser.getUid());
        databaseReference.child("Status").setValue(online);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menus,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
