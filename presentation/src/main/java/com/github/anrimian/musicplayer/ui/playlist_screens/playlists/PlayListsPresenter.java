package com.github.anrimian.musicplayer.ui.playlist_screens.playlists;

import com.github.anrimian.musicplayer.domain.interactors.playlists.PlayListsInteractor;
import com.github.anrimian.musicplayer.domain.models.playlist.PlayList;
import com.github.anrimian.musicplayer.domain.models.utils.ListPosition;
import com.github.anrimian.musicplayer.ui.common.error.ErrorCommand;
import com.github.anrimian.musicplayer.ui.common.error.parser.ErrorParser;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import moxy.MvpPresenter;


public class PlayListsPresenter extends MvpPresenter<PlayListsView> {

    private final PlayListsInteractor playListsInteractor;
    private final Scheduler uiScheduler;
    private final ErrorParser errorParser;

    private final CompositeDisposable presenterDisposable = new CompositeDisposable();

    private List<PlayList> playLists = new ArrayList<>();

    public PlayListsPresenter(PlayListsInteractor playListsInteractor,
                              Scheduler uiScheduler,
                              ErrorParser errorParser) {
        this.playListsInteractor = playListsInteractor;
        this.uiScheduler = uiScheduler;
        this.errorParser = errorParser;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        subscribeOnPlayLists();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenterDisposable.dispose();
    }

    public void onStop(ListPosition listPosition) {
        playListsInteractor.saveListPosition(listPosition);
    }

    void onPlayListLongClick(PlayList playList) {
        getViewState().showPlayListMenu(playList);
    }

    void onDeletePlayListButtonClicked(PlayList playList) {
        getViewState().showConfirmDeletePlayListDialog(playList);
    }

    void onDeletePlayListDialogConfirmed(PlayList playList) {
        playListsInteractor.deletePlayList(playList.getId())
                .observeOn(uiScheduler)
                .subscribe(() -> onPlayListDeleted(playList), this::onPlayListDeletingError);
    }

    void onFragmentMovedToTop() {
        playListsInteractor.setSelectedPlayListScreen(0);
    }

    void onChangePlayListNameButtonClicked(PlayList playList) {
        getViewState().showEditPlayListNameDialog(playList);
    }

    private void onPlayListDeletingError(Throwable throwable) {
        ErrorCommand errorCommand = errorParser.parseError(throwable);
        getViewState().showDeletePlayListError(errorCommand);
    }

    private void onPlayListDeleted(PlayList playList) {
        getViewState().showPlayListDeleteSuccess(playList);
    }

    private void subscribeOnPlayLists() {
        getViewState().showLoading();
        presenterDisposable.add(playListsInteractor.getPlayListsObservable()
                .observeOn(uiScheduler)
                .subscribe(this::onPlayListsReceived));
    }

    private void onPlayListsReceived(List<PlayList> list) {
        boolean firstReceive = this.playLists.isEmpty();

        this.playLists = list;
        getViewState().updateList(list);
        if (list.isEmpty()) {
            getViewState().showEmptyList();
        } else {
            getViewState().showList();
            if (firstReceive) {
                ListPosition listPosition = playListsInteractor.getSavedListPosition();
                if (listPosition != null) {
                    getViewState().restoreListPosition(listPosition);
                }
            }
        }
    }
}
