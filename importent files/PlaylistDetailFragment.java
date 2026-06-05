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
import com.example.musicplayerexam.data.entities.PlaylistEntity;
import com.example.musicplayerexam.data.entities.TrackEntity;
import com.example.musicplayerexam.ui.adapters.TrackAdapter;
import com.example.musicplayerexam.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailFragment extends Fragment {

    private static final String ARG_PLAYLIST = "playlist";
    private PlaylistEntity playlist;
    private MainViewModel viewModel;
    private RecyclerView rvTracks;
    private TrackAdapter adapter;
    private TextView tvPlaylistName, tvPlaylistGenre, tvPlaylistDescription;

    public static PlaylistDetailFragment newInstance(PlaylistEntity playlist) {
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAYLIST, playlist);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlist = (PlaylistEntity) getArguments().getSerializable(ARG_PLAYLIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvPlaylistName = view.findViewById(R.id.tvPlaylistDetailName);
        tvPlaylistGenre = view.findViewById(R.id.tvPlaylistDetailGenre);
        tvPlaylistDescription = view.findViewById(R.id.tvPlaylistDetailDescription);
        rvTracks = view.findViewById(R.id.recyclerViewPlaylistTracks);
        rvTracks.setLayoutManager(new LinearLayoutManager(getContext()));

        if (playlist != null) {
            tvPlaylistName.setText(playlist.getName());
            if (playlist.getGenre() != null && !playlist.getGenre().isEmpty()) {
                tvPlaylistGenre.setText("Жанр: " + playlist.getGenre());
                tvPlaylistGenre.setVisibility(View.VISIBLE);
            } else {
                tvPlaylistGenre.setVisibility(View.GONE);
            }
            if (playlist.getDescription() != null && !playlist.getDescription().isEmpty()) {
                tvPlaylistDescription.setText("Описание: " + playlist.getDescription());
                tvPlaylistDescription.setVisibility(View.VISIBLE);
            } else {
                tvPlaylistDescription.setVisibility(View.GONE);
            }
        }

        // Создаём адаптер с обработчиком клика (воспроизведение)
        adapter = new TrackAdapter(new ArrayList<>(),
                (track, position) -> {
                    // Получаем все треки этого плейлиста и начинаем воспроизведение
                    List<TrackEntity> allTracks = adapter.getCurrentList();
                    viewModel.setPlaylistAndPlay(allTracks, position);
                },
                null  // long click не нужен
        );
        rvTracks.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        if (playlist != null) {
            viewModel.getTracksForPlaylist(playlist.getId()).observe(getViewLifecycleOwner(), tracks -> {
                if (tracks != null) adapter.updateList(tracks);
            });
        }
    }
}