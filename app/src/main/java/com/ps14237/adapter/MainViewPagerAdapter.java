package com.ps14237.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.ps14237.fragment.FavoriteFragment;
import com.ps14237.fragment.LibraryFragment;
import com.ps14237.fragment.RecentFragment;
import com.ps14237.fragment.SettingFragment;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTab;
    LibraryFragment libraryFragment;
    RecentFragment recentFragment;
    FavoriteFragment favoriteFragment;
    SettingFragment settingFragment;

    public MainViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        numOfTab = 4;
        libraryFragment = new LibraryFragment();
        recentFragment = new RecentFragment();
        favoriteFragment = new FavoriteFragment();
        settingFragment = new SettingFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0: fragment = recentFragment; break;
            case 1: fragment = libraryFragment; break;
            case 2: fragment = favoriteFragment; break;
            case 3: fragment = settingFragment; break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return numOfTab;
    }
}
