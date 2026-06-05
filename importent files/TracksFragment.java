package com.example.musicplayerexam.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicplayerexam.R;
import com.example.musicplayerexam.data.AppDatabase;
import com.example.musicplayerexam.data.entities.PlaylistEntity;
import com.example.musicplayerexam.data.entities.TrackEntity;
import com.example.musicplayerexam.ui.adapters.TrackAdapter;
import com.example.musicplayerexam.viewmodel.MainViewModel;
import java.util.ArrayList;
import java.util.List;

public class TracksFragment extends Fragment implements TrackAdapter.OnTrackClickListener, TrackAdapter.OnTrackLongClickListener {

    private RecyclerView recyclerView;
    private TrackAdapter adapter;
    private MainViewModel viewModel;
    private Button btnLoadMusic;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tracks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewTracks);
        btnLoadMusic = view.findViewById(R.id.btnLoadMusic);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TrackAdapter(new ArrayList<>(), this, this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.getAllTracks().observe(getViewLifecycleOwner(), tracks -> {
            if (tracks != null) adapter.updateList(tracks);
        });

        btnLoadMusic.setOnClickListener(v -> {
            if (checkPermission()) loadMusicFromDevice();
            else requestPermission();
        });
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 100);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    private void loadMusicFromDevice() {
        List<TrackEntity> trackList = new ArrayList<>();
        Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TRACK
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        try (Cursor cursor = requireContext().getContentResolver().query(collection, projection, selection, null, null)) {
            if (cursor != null) {
                int titleIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int artistIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int albumIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                int durationIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                int dataIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int trackIdx = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK);
                while (cursor.moveToNext()) {
                    String title = cursor.getString(titleIdx);
                    if (title == null || title.trim().isEmpty()) title = "Неизвестно";
                    String artist = cursor.getString(artistIdx);
                    if (artist == null || artist.trim().isEmpty()) artist = "Неизвестный исполнитель";
                    String album = cursor.getString(albumIdx);
                    if (album == null || album.trim().isEmpty()) album = "Неизвестный альбом";
                    String data = cursor.getString(dataIdx);
                    long duration = cursor.getLong(durationIdx);
                    int trackNumber = cursor.getInt(trackIdx);
                    trackList.add(new TrackEntity(title, artist, album, data, duration, trackNumber));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (!trackList.isEmpty()) {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(getContext());
                db.trackDao().deleteAll();
                db.trackDao().insertAll(trackList);
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Загружено " + trackList.size() + " треков", Toast.LENGTH_SHORT).show());
            }).start();
        } else {
            Toast.makeText(getContext(), "Не найдено аудиофайлов на устройстве", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadMusicFromDevice();
        } else {
            Toast.makeText(getContext(), "Нет разрешения на чтение музыки", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTrackClick(TrackEntity track, int position) {
        List<TrackEntity> allTracks = adapter.getCurrentList();

        // 1. Запускаем воспроизведение (как и раньше)
        viewModel.setPlaylistAndPlay(allTracks, position);

        // 2. ПРИНУДИТЕЛЬНО открываем NowPlayingFragment
        NowPlayingFragment fragment = new NowPlayingFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();

        // 3. Скрываем вкладки и показываем контейнер для фрагмента
        requireActivity().findViewById(R.id.viewPager).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.tabLayout).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
    }

    @Override
    public void onTrackLongClick(TrackEntity track, int position) {
        viewModel.getAllPlaylists().observe(getViewLifecycleOwner(), new androidx.lifecycle.Observer<List<PlaylistEntity>>() {
            @Override
            public void onChanged(List<PlaylistEntity> playlists) {
                viewModel.getAllPlaylists().removeObserver(this);
                if (playlists == null || playlists.isEmpty()) {
                    Toast.makeText(getContext(), "Нет плейлистов. Сначала создайте плейлист.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] names = new String[playlists.size()];
                for (int i = 0; i < playlists.size(); i++) names[i] = playlists.get(i).getName();
                new AlertDialog.Builder(requireContext())
                        .setTitle("Добавить в плейлист")
                        .setItems(names, (dialog, which) -> {
                            PlaylistEntity selected = playlists.get(which);
                            viewModel.addTrackToPlaylist(selected.getId(), track.getId());
                            Toast.makeText(getContext(), "Добавлено в \"" + selected.getName() + "\"", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            }
        });
    }
}