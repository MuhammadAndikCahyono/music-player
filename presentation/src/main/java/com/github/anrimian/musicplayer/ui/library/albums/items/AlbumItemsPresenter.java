package com.github.anrimian.musicplayer.ui.library.albums.items;

import com.github.anrimian.musicplayer.domain.interactors.library.LibraryAlbumsInteractor;
import com.github.anrimian.musicplayer.domain.interactors.player.LibraryPlayerInteractor;
import com.github.anrimian.musicplayer.domain.interactors.playlists.PlayListsInteractor;
import com.github.anrimian.musicplayer.domain.interactors.settings.DisplaySettingsInteractor;
import com.github.anrimian.musicplayer.domain.models.albums.Album;
import com.github.anrimian.musicplayer.domain.models.composition.Composition;
import com.github.anrimian.musicplayer.domain.models.utils.ListPosition;
import com.github.anrimian.musicplayer.ui.common.error.parser.ErrorParser;
import com.github.anrimian.musicplayer.ui.library.common.compositions.BaseLibraryCompositionsPresenter;

import java.util.List;

import javax.annotation.Nullable;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;


public class AlbumItemsPresenter extends BaseLibraryCompositionsPresenter<AlbumItemsView> {

    private final long albumId;
    private final LibraryAlbumsInteractor interactor;

    @Nullable
    private Album album;

    public AlbumItemsPresenter(long albumId,
                               LibraryAlbumsInteractor interactor,
                               PlayListsInteractor playListsInteractor,
                               LibraryPlayerInteractor playerInteractor,
                               DisplaySettingsInteractor displaySettingsInteractor,
                               ErrorParser errorParser,
                               Scheduler uiScheduler) {
        super(playerInteractor,
                playListsInteractor,
                displaySettingsInteractor,
                errorParser,
                uiScheduler);
        this.albumId = albumId;
        this.interactor = interactor;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        subscribeOnAlbumInfo();
    }

    @Override
    protected Observable<List<Composition>> getCompositionsObservable(String searchText) {
        return interactor.getAlbumItemsObservable(albumId);
    }

    @Override
    protected ListPosition getSavedListPosition() {
        return interactor.getSavedItemsListPosition(albumId);
    }

    @Override
    protected void saveListPosition(ListPosition listPosition) {
        interactor.saveItemsListPosition(albumId, listPosition);
    }

    void onFragmentMovedToTop() {
        //save selected screen. Wait a little for all screens
    }

    void onEditAlbumClicked() {
        if (album != null) {
            getViewState().showEditAlbumScreen(album);
        }
    }

    private void subscribeOnAlbumInfo() {
        presenterDisposable.add(interactor.getAlbumObservable(albumId)
                .observeOn(uiScheduler)
                .subscribe(this::onAlbumInfoReceived,
                        t -> getViewState().closeScreen(),
                        getViewState()::closeScreen));
    }

    private void onAlbumInfoReceived(Album album) {
        this.album = album;
        getViewState().showAlbumInfo(album);
    }
}
