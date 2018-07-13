package com.github.anrimian.simplemusicplayer.data.repositories.music.comparators.composition;

import com.github.anrimian.simplemusicplayer.domain.models.composition.Composition;
import com.github.anrimian.simplemusicplayer.domain.models.composition.folders.FileSource;
import com.github.anrimian.simplemusicplayer.domain.models.composition.folders.MusicFileSource;

import java.text.Collator;
import java.util.Comparator;

public class AphabeticalCompositionComparator implements Comparator<Composition> {

    @Override
    public int compare(Composition first, Composition second) {
        return Collator.getInstance().compare(first.getFilePath(), second.getFilePath());
    }
}