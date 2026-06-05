package com.example.musicplayerexam.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayerexam.R;
import com.example.musicplayerexam.data.entities.TrackEntity;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private List<TrackEntity> trackList;
    private OnTrackClickListener clickListener;
    private OnTrackLongClickListener longClickListener;

    public interface OnTrackClickListener {
        void onTrackClick(TrackEntity track, int position);
    }

    public interface OnTrackLongClickListener {
        void onTrackLongClick(TrackEntity track, int position);
    }

    public TrackAdapter(List<TrackEntity> trackList,
                        OnTrackClickListener clickListener,
                        OnTrackLongClickListener longClickListener) {
        this.trackList = trackList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_track, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        TrackEntity track = trackList.get(position);
        holder.title.setText(track.getTitle());
        holder.artist.setText(track.getArtist());
        holder.duration.setText(formatDuration(track.getDuration()));

        if (clickListener != null) {
            holder.itemView.setOnClickListener(v -> clickListener.onTrackClick(track, position));
        }
        if (longClickListener != null) {
            holder.itemView.setOnLongClickListener(v -> {
                longClickListener.onTrackLongClick(track, position);
                return true;
            });
        }
    }

    public void updateList(List<TrackEntity> newList) {
        this.trackList = newList;
        notifyDataSetChanged();
    }

    public List<TrackEntity> getCurrentList() {
        return trackList;
    }

    private String formatDuration(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public int getItemCount() {
        return trackList == null ? 0 : trackList.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {
        TextView title, artist, duration;
        TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTrackTitle);
            artist = itemView.findViewById(R.id.tvTrackArtist);
            duration = itemView.findViewById(R.id.tvTrackDuration);
        }
    }
}