package com.example.musicplayerexam.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicplayerexam.R;
import com.example.musicplayerexam.data.entities.TrackEntity;
import com.example.musicplayerexam.ui.adapters.TrackAdapter;
import com.example.musicplayerexam.viewmodel.MainViewModel;
import java.util.ArrayList;
import java.util.List;

public class TracksByAlbumFragment extends Fragment {
    private static final String ARG_ALBUM_NAME = "album_name";
    private static final String ARG_ARTIST_NAME = "artist_name";
    private String albumName, artistName;
    private MainViewModel viewModel;
    private RecyclerView rvTracks;
    private TrackAdapter adapter;
    private TextView tvTitle;

    public static TracksByAlbumFragment newInstance(String albumName, String artistName) {
        TracksByAlbumFragment fragment = new TracksByAlbumFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ALBUM_NAME, albumName);
        args.putString(ARG_ARTIST_NAME, artistName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            albumName = getArguments().getString(ARG_ALBUM_NAME);
            artistName = getArguments().getString(ARG_ARTIST_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracks_by_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle = view.findViewById(R.id.tvAlbumTitle);
        tvTitle.setText(albumName + " — " + artistName);
        rvTracks = view.findViewById(R.id.recyclerViewAlbumTracks);
        rvTracks.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TrackAdapter(new ArrayList<>(),
                (track, position) -> {
                    List<TrackEntity> allTracks = adapter.getCurrentList();
                    viewModel.setPlaylistAndPlay(allTracks, position);
                },
                null);
        rvTracks.setAdapter(adapter);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.getTracksForAlbum(albumName).observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null) adapter.updateList(tracks);
        });
    }
}