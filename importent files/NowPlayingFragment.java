package com.example.musicplayerexam.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.musicplayerexam.R;
import com.example.musicplayerexam.viewmodel.MainViewModel;
import com.example.musicplayerexam.visualizer.MusicVisualizerView;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class NowPlayingFragment extends Fragment {

    private MainViewModel viewModel;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private ImageView ivAlbumArtLarge, ivAlbumArtBackground;
    private MusicVisualizerView visualizerView;
    private SeekBar seekBar;
    private ImageButton btnPlayPause, btnPrev, btnNext, btnShuffle, btnRepeat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now_playing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        tvTitle = view.findViewById(R.id.tvNowPlayingTitle);
        tvArtist = view.findViewById(R.id.tvNowPlayingArtist);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvTotalTime = view.findViewById(R.id.tvTotalTime);
        ivAlbumArtLarge = view.findViewById(R.id.ivAlbumArtLarge);
        ivAlbumArtBackground = view.findViewById(R.id.ivAlbumArtBackground);
        visualizerView = view.findViewById(R.id.visualizerView);
        seekBar = view.findViewById(R.id.seekBar);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        btnPrev = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        btnShuffle = view.findViewById(R.id.btnShuffle);
        btnRepeat = view.findViewById(R.id.btnRepeat);

        viewModel.getCurrentTrack().observe(getViewLifecycleOwner(), track -> {
            if (track != null) {
                tvTitle.setText(track.getTitle());
                tvArtist.setText(track.getArtist());
                Glide.with(this)
                        .load(track.getData())
                        .placeholder(R.drawable.ic_music_note)
                        .error(R.drawable.ic_music_note)
                        .into(ivAlbumArtLarge);
                Glide.with(this)
                        .load(track.getData())
                        .transform(new BlurTransformation(25, 3))
                        .into(ivAlbumArtBackground);
            }
        });

        viewModel.isPlaying().observe(getViewLifecycleOwner(), isPlaying -> {
            btnPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        });

        viewModel.getCurrentPosition().observe(getViewLifecycleOwner(), position -> {
            if (!seekBar.isPressed()) {
                seekBar.setProgress(position);
                tvCurrentTime.setText(formatTime(position));
            }
        });

        viewModel.getDuration().observe(getViewLifecycleOwner(), duration -> {
            seekBar.setMax(duration);
            tvTotalTime.setText(formatTime(duration));
        });

        viewModel.getShuffle().observe(getViewLifecycleOwner(), shuffle -> {
            btnShuffle.setColorFilter(shuffle ? getResources().getColor(R.color.accent_purple) : getResources().getColor(R.color.control_inactive));
        });

        viewModel.getRepeat().observe(getViewLifecycleOwner(), repeat -> {
            btnRepeat.setColorFilter(repeat ? getResources().getColor(R.color.accent_purple) : getResources().getColor(R.color.control_inactive));
        });

        viewModel.getAudioSessionId().observe(getViewLifecycleOwner(), sessionId -> {
            if (sessionId != null && sessionId != 0 && visualizerView != null) {
                visualizerView.linkToMediaPlayer(sessionId);
            }
        });

        btnPlayPause.setOnClickListener(v -> viewModel.playPause());
        btnPrev.setOnClickListener(v -> viewModel.previous());
        btnNext.setOnClickListener(v -> viewModel.next());
        btnShuffle.setOnClickListener(v -> viewModel.toggleShuffle());
        btnRepeat.setOnClickListener(v -> viewModel.toggleRepeat());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    viewModel.seekTo(progress);
                    tvCurrentTime.setText(formatTime(progress));
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (visualizerView != null) visualizerView.release();
    }
}