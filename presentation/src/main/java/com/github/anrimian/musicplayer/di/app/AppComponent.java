package com.github.anrimian.musicplayer.di.app;


import com.github.anrimian.musicplayer.data.controllers.music.equalizer.EqualizerController;
import com.github.anrimian.musicplayer.data.storage.providers.albums.StorageAlbumsProvider;
import com.github.anrimian.musicplayer.data.storage.source.CompositionSourceProvider;
import com.github.anrimian.musicplayer.di.app.editor.album.AlbumEditorComponent;
import com.github.anrimian.musicplayer.di.app.editor.album.AlbumEditorModule;
import com.github.anrimian.musicplayer.di.app.editor.composition.CompositionEditorComponent;
import com.github.anrimian.musicplayer.di.app.editor.composition.CompositionEditorModule;
import com.github.anrimian.musicplayer.di.app.external_player.ExternalPlayerComponent;
import com.github.anrimian.musicplayer.di.app.external_player.ExternalPlayerModule;
import com.github.anrimian.musicplayer.di.app.library.LibraryComponent;
import com.github.anrimian.musicplayer.di.app.library.LibraryModule;
import com.github.anrimian.musicplayer.di.app.play_list.PlayListComponent;
import com.github.anrimian.musicplayer.di.app.play_list.PlayListModule;
import com.github.anrimian.musicplayer.di.app.settings.SettingsComponent;
import com.github.anrimian.musicplayer.domain.interactors.player.LibraryPlayerInteractor;
import com.github.anrimian.musicplayer.domain.interactors.player.MusicServiceInteractor;
import com.github.anrimian.musicplayer.domain.interactors.player.PlayerInteractor;
import com.github.anrimian.musicplayer.domain.interactors.settings.DisplaySettingsInteractor;
import com.github.anrimian.musicplayer.domain.repositories.MediaScannerRepository;
import com.github.anrimian.musicplayer.domain.repositories.UiStateRepository;
import com.github.anrimian.musicplayer.ui.common.error.parser.ErrorParser;
import com.github.anrimian.musicplayer.ui.common.images.CoverImageLoader;
import com.github.anrimian.musicplayer.ui.common.theme.ThemeController;
import com.github.anrimian.musicplayer.ui.notifications.NotificationsDisplayer;
import com.github.anrimian.musicplayer.ui.playlist_screens.choose.ChoosePlayListPresenter;
import com.github.anrimian.musicplayer.ui.playlist_screens.create.CreatePlayListPresenter;
import com.github.anrimian.musicplayer.ui.playlist_screens.playlists.PlayListsPresenter;
import com.github.anrimian.musicplayer.ui.widgets.WidgetUpdater;
import com.github.anrimian.musicplayer.utils.filelog.FileLog;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created on 11.02.2017.
 */

@Singleton
@Component(modules = {
        AppModule.class,
        SchedulerModule.class,
        ErrorModule.class,
        MusicModule.class,
        DbModule.class,
        StorageModule.class,
        SettingsModule.class,
        PlayListsModule.class
})
public interface AppComponent {

    LibraryComponent libraryComponent(LibraryModule libraryModule);
    PlayListComponent playListComponent(PlayListModule module);
    SettingsComponent settingsComponent();
    CompositionEditorComponent compositionEditorComponent(CompositionEditorModule module);
    AlbumEditorComponent albumEditorComponent(AlbumEditorModule module);
    ExternalPlayerComponent externalPlayerComponent(ExternalPlayerModule module);

    LibraryPlayerInteractor musicPlayerInteractor();
    DisplaySettingsInteractor displaySettingsInteractor();
    PlayerInteractor playerInteractor();
    MusicServiceInteractor musicServiceInteractor();

    PlayListsPresenter playListsPresenter();
    CreatePlayListPresenter createPlayListsPresenter();
    ChoosePlayListPresenter choosePlayListPresenter();

    UiStateRepository uiStateRepository();
    MediaScannerRepository mediaScannerRepository();
    CompositionSourceProvider sourceRepository();
    StorageAlbumsProvider storageAlbumsProvider();

    CoverImageLoader imageLoader();
    WidgetUpdater widgetUpdater();
    NotificationsDisplayer notificationDisplayer();
    ErrorParser errorParser();
    FileLog fileLog();

    ThemeController themeController();
    EqualizerController equalizerController();

}