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
import com.example.musicplayerexam.data.entities.AlbumEntity;
import com.example.musicplayerexam.ui.adapters.AlbumAdapter;
import com.example.musicplayerexam.viewmodel.MainViewModel;

public class AlbumsFragment extends Fragment implements AlbumAdapter.OnAlbumClickListener {

    private RecyclerView recyclerView;
    private AlbumAdapter adapter;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewAlbums);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AlbumAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.getAllAlbums().observe(getViewLifecycleOwner(), albums -> {
            if (albums != null) adapter.setAlbums(albums);
        });
    }

    @Override
    public void onAlbumClick(AlbumEntity album) {
        // Открываем фрагмент со списком треков этого альбома
        TracksByAlbumFragment fragment = TracksByAlbumFragment.newInstance(album.album, album.artist);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
        // Скрываем основной ViewPager и показываем контейнер для фрагментов
        requireActivity().findViewById(R.id.viewPager).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.tabLayout).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
    }
}