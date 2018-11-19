package com.github.anrimian.musicplayer.ui.library.compositions.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.anrimian.musicplayer.domain.models.composition.Composition;
import com.github.anrimian.musicplayer.ui.library.folders.adapter.MusicViewHolder;
import com.github.anrimian.musicplayer.ui.utils.OnItemClickListener;
import com.github.anrimian.musicplayer.ui.utils.OnViewItemClickListener;

import java.util.List;

/**
 * Created on 31.10.2017.
 */

public class CompositionsAdapter extends RecyclerView.Adapter<MusicViewHolder> {

    private List<Composition> musicList;
    private OnItemClickListener<Integer> onCompositionClickListener;
    private OnViewItemClickListener<Composition> onMenuItemClickListener;

    public CompositionsAdapter(List<Composition> musicList) {
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MusicViewHolder(LayoutInflater.from(parent.getContext()),
                parent,
                null,
                onCompositionClickListener,
                onMenuItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        Composition composition = musicList.get(position);
        holder.bind(composition);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public void setItems(List<Composition> list) {
        musicList = list;
    }

    public void setOnCompositionClickListener(OnItemClickListener<Integer> onCompositionClickListener) {
        this.onCompositionClickListener = onCompositionClickListener;
    }

    public void setOnMenuItemClickListener(OnViewItemClickListener<Composition> onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

}
