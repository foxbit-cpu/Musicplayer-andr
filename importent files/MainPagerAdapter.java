package com.example.musicplayerexam.ui.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicplayerexam.R;
import com.example.musicplayerexam.ui.fragments.AlbumsFragment;
import com.example.musicplayerexam.ui.fragments.ArtistsFragment;
import com.example.musicplayerexam.ui.fragments.PlaylistsFragment;
import com.example.musicplayerexam.ui.fragments.TracksFragment;

public class MainPagerAdapter extends FragmentStateAdapter {

    private final String[] titles;

    public MainPagerAdapter(@NonNull Context context) {
        super(((AppCompatActivity) context).getSupportFragmentManager(),
                ((AppCompatActivity) context).getLifecycle());
        titles = context.getResources().getStringArray(R.array.tab_titles);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TracksFragment();
            case 1:
                return new AlbumsFragment();
            case 2:
                return new ArtistsFragment();
            case 3:
                return new PlaylistsFragment();
            default:
                return new TracksFragment();
        }
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    public String getPageTitle(int position) {
        return titles[position];
    }
}