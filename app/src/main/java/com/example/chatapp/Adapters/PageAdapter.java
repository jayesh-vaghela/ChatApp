package com.example.chatapp.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.chatapp.Fragments.Chats;
import com.example.chatapp.Fragments.Contacts;

public class PageAdapter extends FragmentPagerAdapter {
    public PageAdapter(@NonNull FragmentManager fm) {
        //super(fm);
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return (new Chats());
            case 1:
                return (new Contacts());
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Chats";
            case 1:
                return "Contacts";
            default:
                return null;
        }
    }
}
