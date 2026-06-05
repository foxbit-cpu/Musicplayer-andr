package com.example.musicplayerexam.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerexam.R;
import com.example.musicplayerexam.data.entities.AlbumEntity;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<AlbumEntity> albums = new ArrayList<>();
    private OnAlbumClickListener listener;

    public interface OnAlbumClickListener {
        void onAlbumClick(AlbumEntity album);
    }

    public AlbumAdapter(OnAlbumClickListener listener) {
        this.listener = listener;
    }

    public void setAlbums(List<AlbumEntity> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        AlbumEntity album = albums.get(position);
        holder.tvAlbumName.setText(album.album);
        holder.tvAlbumArtist.setText(album.artist);
        holder.tvAlbumCount.setText(album.trackCount + " треков");
        holder.itemView.setOnClickListener(v -> listener.onAlbumClick(album));
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        TextView tvAlbumName, tvAlbumArtist, tvAlbumCount;
        AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAlbumName = itemView.findViewById(R.id.tvAlbumName);
            tvAlbumArtist = itemView.findViewById(R.id.tvAlbumArtist);
            tvAlbumCount = itemView.findViewById(R.id.tvAlbumCount);
        }
    }
}