package com.github.anrimian.musicplayer.domain.repositories;

import com.github.anrimian.musicplayer.domain.models.albums.Album;
import com.github.anrimian.musicplayer.domain.models.artist.Artist;
import com.github.anrimian.musicplayer.domain.models.composition.Composition;
import com.github.anrimian.musicplayer.domain.models.composition.CorruptionType;
import com.github.anrimian.musicplayer.domain.models.composition.FullComposition;
import com.github.anrimian.musicplayer.domain.models.composition.folders.FileSource;
import com.github.anrimian.musicplayer.domain.models.composition.folders.FileSource2;
import com.github.anrimian.musicplayer.domain.models.composition.folders.Folder;
import com.github.anrimian.musicplayer.domain.models.composition.folders.FolderFileSource;
import com.github.anrimian.musicplayer.domain.models.composition.folders.FolderFileSource2;
import com.github.anrimian.musicplayer.domain.models.composition.folders.IgnoredFolder;
import com.github.anrimian.musicplayer.domain.models.genres.Genre;
import com.github.anrimian.musicplayer.domain.models.genres.ShortGenre;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created on 24.10.2017.
 */

public interface LibraryRepository {

    Observable<List<Composition>> getAllCompositionsObservable(@Nullable String searchText);

    Observable<FullComposition> getCompositionObservable(long id);

    Observable<List<Artist>> getArtistsObservable(@Nullable String searchText);

    Observable<List<Album>> getAlbumsObservable(@Nullable String searchText);

    Observable<List<Genre>> getGenresObservable(@Nullable String searchText);

    Observable<List<ShortGenre>> getShortGenresInComposition(long compositionId);

    Observable<List<Composition>> getGenreItemsObservable(long genreId);

    Observable<List<Composition>> getAlbumItemsObservable(long albumId);

    Observable<Album> getAlbumObservable(long albumId);

    Observable<List<Composition>> getCompositionsByArtist(long artistId);

    Observable<Artist> getArtistObservable(long artistId);

    Observable<List<Album>> getAllAlbumsForArtist(long artistId);

    Single<String[]> getAuthorNames();

    Single<String[]> getAlbumNames();

    Single<String[]> getGenreNames();

    Single<Folder> getCompositionsInPath(@Nullable String path, @Nullable String searchText);

    Observable<List<FileSource2>> getFoldersInFolder(@Nullable Long folderId,
                                                     @Nullable String searchQuery);

    Observable<FolderFileSource2> getFolderObservable(long folderId);

    @Deprecated
    Single<List<Composition>> getAllCompositionsInPath(@Nullable String path);

    Single<List<Composition>> getAllCompositionsInFolder(@Nullable Long folderId);

    Single<List<Composition>> getAllCompositionsInFolders(Iterable<FileSource> fileSources);

    Single<List<String>> getAvailablePathsForPath(@Nullable String path);

    Completable writeErrorAboutComposition(CorruptionType errorType, Composition composition);

    Completable deleteComposition(Composition composition);

    Completable deleteCompositions(List<Composition> compositions);

    Single<List<Composition>> changeFolderName(String folderPath, String newPath);

    Single<List<Composition>> moveFileTo(String folderPath,
                                         String newSourcePath,
                                         FileSource fileSource);

    Observable<Genre> getGenreObservable(long genreId);

    Single<IgnoredFolder> addFolderToIgnore(FolderFileSource folder);

    Completable addFolderToIgnore(IgnoredFolder folder);

    Observable<List<IgnoredFolder>> getIgnoredFoldersObservable();

    Completable deleteIgnoredFolder(IgnoredFolder folder);

}
