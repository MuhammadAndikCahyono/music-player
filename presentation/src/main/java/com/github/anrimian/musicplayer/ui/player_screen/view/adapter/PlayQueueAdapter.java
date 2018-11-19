package com.github.anrimian.musicplayer.ui.player_screen.view.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.anrimian.musicplayer.domain.models.composition.Composition;
import com.github.anrimian.musicplayer.domain.models.composition.PlayQueueItem;
import com.github.anrimian.musicplayer.ui.utils.OnItemClickListener;
import com.github.anrimian.musicplayer.ui.utils.OnPositionItemClickListener;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Created on 31.10.2017.
 */

public class PlayQueueAdapter extends RecyclerView.Adapter<PlayQueueViewHolder> {

    private static final String CURRENT_COMPOSITION_CHANGED = "current_composition_changed";

    private List<PlayQueueItem> musicList;
    private OnPositionItemClickListener<PlayQueueItem> onCompositionClickListener;
    private OnItemClickListener<Composition> onDeleteCompositionClickListener;
    private OnItemClickListener<Composition> onAddToPlaylistClickListener;
    private OnItemClickListener<PlayQueueItem> onDeleteItemClickListener;

    @Nullable
    private PlayQueueItem currentItem;

    private int oldPosition = RecyclerView.NO_POSITION;

    public PlayQueueAdapter(List<PlayQueueItem> musicList) {
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public PlayQueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlayQueueViewHolder(LayoutInflater.from(parent.getContext()),
                parent,
                onCompositionClickListener,
                onDeleteCompositionClickListener,
                onAddToPlaylistClickListener,
                onDeleteItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayQueueViewHolder holder, int position) {
        PlayQueueItem composition = musicList.get(position);
        holder.bind(composition);
        holder.showAsPlayingComposition(composition.equals(currentItem));
    }

    @Override
    public void onBindViewHolder(@NonNull PlayQueueViewHolder holder,
                                 int position,
                                 @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        }
        for (Object payload: payloads) {
            if (payload == CURRENT_COMPOSITION_CHANGED) {
                PlayQueueItem item = musicList.get(position);
                holder.showAsPlayingComposition(item.equals(currentItem));
            }
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public void onCurrentItemChanged(PlayQueueItem currentItem, int position) {
        int oldPosition = getOldPosition();

        this.currentItem = currentItem;
        if (oldPosition != -1) {
            notifyItemChanged(oldPosition, CURRENT_COMPOSITION_CHANGED);
        }
        this.oldPosition = position;
        notifyItemChanged(position, CURRENT_COMPOSITION_CHANGED);
    }

    private int getOldPosition() {
        if (oldPosition != -1
                && oldPosition < musicList.size()
                && currentItem != null
                && currentItem.equals(musicList.get(oldPosition))) {
            return oldPosition;
        }
        return musicList.indexOf(this.currentItem);
    }

    public void setItems(List<PlayQueueItem> list) {
        musicList = list;
    }

    public void setOnCompositionClickListener(OnPositionItemClickListener<PlayQueueItem> onCompositionClickListener) {
        this.onCompositionClickListener = onCompositionClickListener;
    }

    public void setOnDeleteCompositionClickListener(OnItemClickListener<Composition> onDeleteCompositionClickListener) {
        this.onDeleteCompositionClickListener = onDeleteCompositionClickListener;
    }

    public void setOnAddToPlaylistClickListener(OnItemClickListener<Composition> onAddToPlaylistClickListener) {
        this.onAddToPlaylistClickListener = onAddToPlaylistClickListener;
    }

    public void setOnDeleteItemClickListener(OnItemClickListener<PlayQueueItem> onDeleteItemClickListener) {
        this.onDeleteItemClickListener = onDeleteItemClickListener;
    }
}
