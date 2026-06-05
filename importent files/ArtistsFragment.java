package com.example.musicplayerexam.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerexam.R;
import com.example.musicplayerexam.data.entities.ArtistEntity;
import com.example.musicplayerexam.ui.adapters.ArtistAdapter;
import com.example.musicplayerexam.viewmodel.MainViewModel;

public class ArtistsFragment extends Fragment implements ArtistAdapter.OnArtistClickListener {

    private RecyclerView recyclerView;
    private ArtistAdapter adapter;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewArtists);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ArtistAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.getAllArtists().observe(getViewLifecycleOwner(), artists -> {
            if (artists != null) adapter.setArtists(artists);
        });
    }

    @Override
    public void onArtistClick(ArtistEntity artist) {
        // Открываем фрагмент со списком треков этого исполнителя
        TracksByArtistFragment fragment = TracksByArtistFragment.newInstance(artist.artist);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
        requireActivity().findViewById(R.id.viewPager).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.tabLayout).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
    }
}