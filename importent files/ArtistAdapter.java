package com.example.musicplayerexam.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicplayerexam.R;
import com.example.musicplayerexam.data.entities.ArtistEntity;
import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {
    private List<ArtistEntity> artists = new ArrayList<>();
    private OnArtistClickListener listener;

    public interface OnArtistClickListener {
        void onArtistClick(ArtistEntity artist);
    }

    public ArtistAdapter(OnArtistClickListener listener) {
        this.listener = listener;
    }

    public void setArtists(List<ArtistEntity> artists) {
        this.artists = artists;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        ArtistEntity artist = artists.get(position);
        holder.tvArtistName.setText(artist.artist);
        holder.tvTrackCount.setText(artist.trackCount + " треков");
        holder.itemView.setOnClickListener(v -> listener.onArtistClick(artist));
    }

    @Override
    public int getItemCount() { return artists.size(); }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {
        TextView tvArtistName, tvTrackCount;
        ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvArtistName = itemView.findViewById(R.id.tvArtistName);
            tvTrackCount = itemView.findViewById(R.id.tvTrackCount);
        }
    }
}