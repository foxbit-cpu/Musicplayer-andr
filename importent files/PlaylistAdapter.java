package com.example.musicplayerexam.ui.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerexam.R;
import com.example.musicplayerexam.data.entities.PlaylistEntity;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<PlaylistEntity> playlists = new ArrayList<>();
    private OnPlaylistClickListener listener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(PlaylistEntity playlist);
        void onPlaylistLongClick(PlaylistEntity playlist);
    }

    public PlaylistAdapter(OnPlaylistClickListener listener) {
        this.listener = listener;
    }

    public void setPlaylists(List<PlaylistEntity> playlists) {
        this.playlists = playlists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistEntity playlist = playlists.get(position);
        holder.tvName.setText(playlist.getName());

        // Жанр
        if (!TextUtils.isEmpty(playlist.getGenre())) {
            holder.tvGenre.setText(playlist.getGenre());
            holder.tvGenre.setVisibility(View.VISIBLE);
        } else {
            holder.tvGenre.setVisibility(View.GONE);
        }

        // Описание
        if (!TextUtils.isEmpty(playlist.getDescription())) {
            holder.tvDescription.setText(playlist.getDescription());
            holder.tvDescription.setVisibility(View.VISIBLE);
        } else {
            holder.tvDescription.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onPlaylistClick(playlist));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onPlaylistLongClick(playlist);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvGenre, tvDescription;
        PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPlaylistName);
            tvGenre = itemView.findViewById(R.id.tvPlaylistGenre);
            tvDescription = itemView.findViewById(R.id.tvPlaylistDescription);
        }
    }
}