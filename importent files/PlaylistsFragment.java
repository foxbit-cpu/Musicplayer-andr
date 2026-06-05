package com.example.musicplayerexam.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerexam.R;
import com.example.musicplayerexam.data.entities.PlaylistEntity;
import com.example.musicplayerexam.ui.adapters.PlaylistAdapter;
import com.example.musicplayerexam.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class PlaylistsFragment extends Fragment implements PlaylistAdapter.OnPlaylistClickListener {

    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private MainViewModel viewModel;
    private FloatingActionButton fabAddPlaylist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewPlaylists);
        fabAddPlaylist = view.findViewById(R.id.fabAddPlaylist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PlaylistAdapter(this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        viewModel.getAllPlaylists().observe(getViewLifecycleOwner(), playlists -> {
            if (playlists != null) adapter.setPlaylists(playlists);
        });

        fabAddPlaylist.setOnClickListener(v -> showCreatePlaylistDialog());
    }

    private void showCreatePlaylistDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_create_playlist, null);

        TextInputEditText etName = dialogView.findViewById(R.id.etName);
        TextInputEditText etDescription = dialogView.findViewById(R.id.etDescription);
        AutoCompleteTextView etGenre = dialogView.findViewById(R.id.etGenre);
        CheckBox cbPublic = dialogView.findViewById(R.id.cbPublic);
        TextView tvNameError = dialogView.findViewById(R.id.tvNameError);
        TextView tvDescError = dialogView.findViewById(R.id.tvDescError);

        // Подсказки для жанра (необязательное поле)
        String[] genreSuggestions = {"Рок", "Поп", "Джаз", "Классика", "Электроника", "Хип-хоп", "Другое"};
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, genreSuggestions);
        etGenre.setAdapter(genreAdapter);
        etGenre.setThreshold(1);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Создание плейлиста")
                .setView(dialogView)
                .setPositiveButton("Создать", null)
                .setNegativeButton("Отмена", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                tvNameError.setVisibility(View.GONE);
                tvDescError.setVisibility(View.GONE);

                String name = etName.getText().toString().trim();
                String description = etDescription.getText().toString().trim();
                String genre = etGenre.getText().toString().trim();
                boolean isPublic = cbPublic.isChecked();

                boolean isValid = true;

                if (TextUtils.isEmpty(name)) {
                    tvNameError.setText("Введите название плейлиста");
                    tvNameError.setVisibility(View.VISIBLE);
                    isValid = false;
                }

                if (description.length() > 100) {
                    tvDescError.setText("Описание не должно превышать 100 символов");
                    tvDescError.setVisibility(View.VISIBLE);
                    isValid = false;
                }

                // Жанр не проверяем на соответствие списку – можно вводить любой текст
                if (isValid) {
                    viewModel.createPlaylist(name, description, genre, isPublic);
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Плейлист создан", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    public void onPlaylistClick(PlaylistEntity playlist) {
        // Передаём ВЕСЬ объект плейлиста, а не только id
        PlaylistDetailFragment fragment = PlaylistDetailFragment.newInstance(playlist);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
        requireActivity().findViewById(R.id.viewPager).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.tabLayout).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlaylistLongClick(PlaylistEntity playlist) {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить плейлист")
                .setMessage("Удалить \"" + playlist.getName() + "\"?")
                .setPositiveButton("Удалить", (d, w) -> viewModel.deletePlaylist(playlist))
                .setNegativeButton("Отмена", null)
                .show();
    }
}