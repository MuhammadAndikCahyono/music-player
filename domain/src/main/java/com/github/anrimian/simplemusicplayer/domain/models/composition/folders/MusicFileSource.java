package com.github.anrimian.simplemusicplayer.domain.models.composition.folders;

import com.github.anrimian.simplemusicplayer.domain.models.composition.Composition;

import javax.annotation.Nonnull;

/**
 * Created on 31.10.2017.
 */

public class MusicFileSource implements FileSource {

    @Nonnull
    private Composition composition;

    public MusicFileSource(@Nonnull Composition composition) {
        this.composition = composition;
    }

    @Nonnull
    public Composition getComposition() {
        return composition;
    }

    @Override
    public String toString() {
        return "MusicFileSource{" +
                "composition=" + composition +
                '}';
    }
}