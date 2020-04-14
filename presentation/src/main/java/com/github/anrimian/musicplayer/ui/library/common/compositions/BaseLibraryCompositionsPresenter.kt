package com.github.anrimian.musicplayer.ui.library.common.compositions

import com.github.anrimian.musicplayer.data.utils.rx.RxUtils
import com.github.anrimian.musicplayer.domain.business.player.MusicPlayerInteractor
import com.github.anrimian.musicplayer.domain.business.playlists.PlayListsInteractor
import com.github.anrimian.musicplayer.domain.business.settings.DisplaySettingsInteractor
import com.github.anrimian.musicplayer.domain.models.composition.Composition
import com.github.anrimian.musicplayer.domain.models.composition.CurrentComposition
import com.github.anrimian.musicplayer.domain.models.playlist.PlayList
import com.github.anrimian.musicplayer.domain.utils.ListUtils
import com.github.anrimian.musicplayer.domain.utils.TextUtils
import com.github.anrimian.musicplayer.ui.common.error.parser.ErrorParser
import com.github.anrimian.musicplayer.ui.common.mvp.AppPresenter
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

abstract class BaseLibraryCompositionsPresenter<T : BaseLibraryCompositionsView>(
        private val playerInteractor: MusicPlayerInteractor,
        private val playListsInteractor: PlayListsInteractor,
        private val displaySettingsInteractor: DisplaySettingsInteractor,
        errorParser: ErrorParser,
        uiScheduler: Scheduler)
    : AppPresenter<T>(uiScheduler, errorParser) {
    
    private val presenterBatterySafeDisposable = CompositeDisposable()
    
    private var currentCompositionDisposable: Disposable? = null
    private var compositionsDisposable: Disposable? = null
    
    private var compositions: List<Composition> = ArrayList()
    private val selectedCompositions = LinkedHashSet<Composition>()
    private val compositionsForPlayList: MutableList<Composition> = LinkedList()
    private val compositionsToDelete: MutableList<Composition> = LinkedList()
    
    private var currentComposition: Composition? = null
    
    private var searchText: String? = null
    
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        subscribeOnUiSettings()
        subscribeOnCompositions()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenterDisposable.dispose()
    }

    fun onStart() {
        if (compositions.isNotEmpty()) {
            subscribeOnCurrentComposition()
        }
    }

    fun onStop() {
        presenterBatterySafeDisposable.clear()
    }

    fun onTryAgainLoadCompositionsClicked() {
        subscribeOnCompositions()
    }

    fun onCompositionClicked(position: Int, composition: Composition) {
        if (selectedCompositions.isEmpty()) {
            viewState.showCompositionActionDialog(composition, position)
        } else {
            if (selectedCompositions.contains(composition)) {
                selectedCompositions.remove(composition)
                viewState.onCompositionUnselected(composition, position)
            } else {
                selectedCompositions.add(composition)
                viewState.onCompositionSelected(composition, position)
            }
            viewState.showSelectionMode(selectedCompositions.size)
        }
    }

    fun onCompositionIconClicked(position: Int, composition: Composition) {
        if (composition == currentComposition) {
            playerInteractor.playOrPause()
        } else {
            playerInteractor.startPlaying(compositions, position)
            viewState.showCurrentComposition(CurrentComposition(composition, true))
        }
    }

    fun onCompositionLongClick(position: Int, composition: Composition) {
        selectedCompositions.add(composition)
        viewState.showSelectionMode(selectedCompositions.size)
        viewState.onCompositionSelected(composition, position)
    }

    fun onPlayAllButtonClicked() {
        if (selectedCompositions.isEmpty()) {
            playerInteractor.startPlaying(compositions)
        } else {
            playSelectedCompositions()
        }
    }

    fun onDeleteCompositionButtonClicked(composition: Composition) {
        compositionsToDelete.clear()
        compositionsToDelete.add(composition)
        viewState.showConfirmDeleteDialog(compositionsToDelete)
    }

    fun onDeleteSelectedCompositionButtonClicked() {
        compositionsToDelete.clear()
        compositionsToDelete.addAll(selectedCompositions)
        viewState.showConfirmDeleteDialog(compositionsToDelete)
    }

    fun onDeleteCompositionsDialogConfirmed() {
        deletePreparedCompositions()
    }

    fun onPlayNextCompositionClicked(composition: Composition) {
        addCompositionsToPlayNext(ListUtils.asList(composition))
    }

    fun onAddToQueueCompositionClicked(composition: Composition) {
        addCompositionsToEnd(ListUtils.asList(composition))
    }

    fun onAddToPlayListButtonClicked(composition: Composition) {
        compositionsForPlayList.clear()
        compositionsForPlayList.add(composition)
        viewState.showSelectPlayListDialog()
    }

    fun onAddSelectedCompositionToPlayListClicked() {
        compositionsForPlayList.clear()
        compositionsForPlayList.addAll(selectedCompositions)
        viewState.showSelectPlayListDialog()
    }

    fun onPlayListToAddingSelected(playList: PlayList) {
        playListsInteractor.addCompositionsToPlayList(compositionsForPlayList, playList)
                .subscribeOnUi(
                        { onAddingToPlayListCompleted(playList) },
                        this::onAddingToPlayListError
                )
    }

    fun onSelectionModeBackPressed() {
        closeSelectionMode()
    }

    fun onShareSelectedCompositionsClicked() {
        viewState.shareCompositions(selectedCompositions)
    }

    fun onPlayAllSelectedClicked() {
        playSelectedCompositions()
    }

    fun onSelectAllButtonClicked() {
        selectedCompositions.clear() //reselect previous feature
        selectedCompositions.addAll(compositions)
        viewState.showSelectionMode(compositions.size)
        viewState.setItemsSelected(true)
    }

    fun onPlayNextSelectedCompositionsClicked() {
        addCompositionsToPlayNext(ArrayList(selectedCompositions))
        closeSelectionMode()
    }

    fun onAddToQueueSelectedCompositionsClicked() {
        addCompositionsToEnd(ArrayList(selectedCompositions))
        closeSelectionMode()
    }

    fun onPlayActionSelected(position: Int) {
        playerInteractor.startPlaying(compositions, position)
    }

    fun onSearchTextChanged(text: String?) {
        if (!TextUtils.equals(searchText, text)) {
            searchText = text
            subscribeOnCompositions()
        }
    }

    fun getSelectedCompositions(): HashSet<Composition> {
        return selectedCompositions
    }

    fun getSearchText() = searchText

    protected fun subscribeOnCompositions() {
        if (compositions.isEmpty()) {
            viewState.showLoading()
        }
        RxUtils.dispose(compositionsDisposable, presenterDisposable)
        compositionsDisposable = getCompositionsObservable(searchText)
                .observeOn(uiScheduler)
                .subscribe(this::onCompositionsReceived, this::onCompositionsReceivingError)
        presenterDisposable.add(compositionsDisposable!!)
    }

    protected fun onDefaultError(throwable: Throwable?) {
        val errorCommand = errorParser.parseError(throwable)
        viewState.showErrorMessage(errorCommand)
    }

    private fun onDeleteCompositionError(throwable: Throwable) {
        val errorCommand = errorParser.parseError(throwable)
        viewState.showDeleteCompositionError(errorCommand)
    }

    private fun onAddingToPlayListError(throwable: Throwable) {
        val errorCommand = errorParser.parseError(throwable)
        viewState.showAddingToPlayListError(errorCommand)
    }

    private fun onAddingToPlayListCompleted(playList: PlayList) {
        viewState.showAddingToPlayListComplete(playList, compositionsForPlayList)
        compositionsForPlayList.clear()
        if (selectedCompositions.isNotEmpty()) {
            closeSelectionMode()
        }
    }

    private fun addCompositionsToPlayNext(compositions: List<Composition>) {
        playerInteractor.addCompositionsToPlayNext(compositions)
                .subscribeOnUi(viewState::onCompositionsAddedToPlayNext, this::onDefaultError)
    }

    private fun addCompositionsToEnd(compositions: List<Composition>) {
        playerInteractor.addCompositionsToEnd(compositions)
                .subscribeOnUi(viewState::onCompositionsAddedToQueue, this::onDefaultError)
    }

    private fun playSelectedCompositions() {
        playerInteractor.startPlaying(ArrayList(selectedCompositions))
        closeSelectionMode()
    }

    private fun closeSelectionMode() {
        selectedCompositions.clear()
        viewState.showSelectionMode(0)
        viewState.setItemsSelected(false)
    }

    private fun deletePreparedCompositions() {
        playerInteractor.deleteCompositions(compositionsToDelete)
                .subscribeOnUi(this::onDeleteCompositionsSuccess, this::onDeleteCompositionError)
    }

    private fun onDeleteCompositionsSuccess() {
        viewState.showDeleteCompositionMessage(compositionsToDelete)
        compositionsToDelete.clear()
        if (selectedCompositions.isNotEmpty()) {
            closeSelectionMode()
        }
    }

    private fun subscribeOnCurrentComposition() {
        currentCompositionDisposable = playerInteractor.currentCompositionObservable
                .observeOn(uiScheduler)
                .subscribe(this::onCurrentCompositionReceived, errorParser::logError)
        presenterBatterySafeDisposable.add(currentCompositionDisposable!!)
    }

    private fun onCompositionsReceivingError(throwable: Throwable) {
        val errorCommand = errorParser.parseError(throwable)
        viewState.showLoadingError(errorCommand)
    }

    private fun onCompositionsReceived(compositions: List<Composition>) {
        this.compositions = compositions
        viewState.updateList(compositions)
        if (compositions.isEmpty()) {
            if (TextUtils.isEmpty(searchText)) {
                viewState.showEmptyList()
            } else {
                viewState.showEmptySearchResult()
            }
        } else {
            viewState.showList()
            if (RxUtils.isInactive(currentCompositionDisposable)) {
                subscribeOnCurrentComposition()
            }
        }
    }

    private fun onCurrentCompositionReceived(currentComposition: CurrentComposition) {
        this.currentComposition = currentComposition.composition
        viewState.showCurrentComposition(currentComposition)
    }

    private fun subscribeOnUiSettings() {
        presenterDisposable.add(displaySettingsInteractor.coversEnabledObservable
                .observeOn(uiScheduler)
                .subscribe(this::onUiSettingsReceived, errorParser::logError))
    }

    private fun onUiSettingsReceived(isCoversEnabled: Boolean) {
        viewState.setDisplayCoversEnabled(isCoversEnabled)
    }

    protected abstract fun getCompositionsObservable(searchText: String?): Observable<List<Composition>>

}