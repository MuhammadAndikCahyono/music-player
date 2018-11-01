package com.github.anrimian.musicplayer.domain.repositories;

import com.github.anrimian.musicplayer.domain.models.composition.Composition;
import com.github.anrimian.musicplayer.domain.models.playlist.PlayList;
import com.github.anrimian.musicplayer.domain.models.playlist.PlayListItem;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface PlayListsRepository {

    Observable<List<PlayList>> getPlayListsObservable();

    Observable<PlayList> getPlayListObservable(long playlistId);

    Observable<List<PlayListItem>> getCompositionsObservable(long playlistId);

    Single<PlayList> createPlayList(String name);

    Completable addCompositionsToPlayList(List<Composition> compositions, PlayList playList);
}