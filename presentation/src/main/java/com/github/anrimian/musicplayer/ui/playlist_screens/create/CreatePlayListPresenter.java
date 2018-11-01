package com.github.anrimian.musicplayer.ui.playlist_screens.create;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.github.anrimian.musicplayer.domain.business.playlists.PlayListsInteractor;
import com.github.anrimian.musicplayer.domain.models.playlist.PlayList;
import com.github.anrimian.musicplayer.ui.common.error.ErrorCommand;
import com.github.anrimian.musicplayer.ui.common.error.parser.ErrorParser;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

@InjectViewState
public class CreatePlayListPresenter extends MvpPresenter<CreatePlayListView> {

    private final PlayListsInteractor playListsInteractor;
    private final Scheduler uiScheduler;
    private final ErrorParser errorParser;

    private final CompositeDisposable presenterDisposable = new CompositeDisposable();

    public CreatePlayListPresenter(PlayListsInteractor playListsInteractor,
                                   Scheduler uiScheduler,
                                   ErrorParser errorParser) {
        this.playListsInteractor = playListsInteractor;
        this.uiScheduler = uiScheduler;
        this.errorParser = errorParser;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().showInputState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenterDisposable.dispose();
    }

    void onCompleteInputButtonClicked(String playListName) {
        presenterDisposable.add(playListsInteractor.createPlayList(playListName)
                .observeOn(uiScheduler)
                .doOnSubscribe(d -> getViewState().showProgress())
                .subscribe(this::onPlayListCreated, this::onPlayListCreatingError));
    }

    private void onPlayListCreated(PlayList playList) {
        getViewState().onPlayListCreated(playList);
    }

    private void onPlayListCreatingError(Throwable throwable) {
        ErrorCommand errorCommand = errorParser.parseError(throwable);
        getViewState().showError(errorCommand);
    }
}