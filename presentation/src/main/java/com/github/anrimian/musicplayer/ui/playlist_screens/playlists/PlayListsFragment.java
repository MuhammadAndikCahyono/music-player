package com.github.anrimian.musicplayer.ui.playlist_screens.playlists;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.anrimian.musicplayer.R;
import com.github.anrimian.musicplayer.databinding.FragmentPlayListsBinding;
import com.github.anrimian.musicplayer.di.Components;
import com.github.anrimian.musicplayer.domain.models.playlist.PlayList;
import com.github.anrimian.musicplayer.domain.models.utils.ListPosition;
import com.github.anrimian.musicplayer.ui.common.dialogs.DialogUtils;
import com.github.anrimian.musicplayer.ui.common.error.ErrorCommand;
import com.github.anrimian.musicplayer.ui.common.format.MessagesUtils;
import com.github.anrimian.musicplayer.ui.common.serialization.PlaylistSerializer;
import com.github.anrimian.musicplayer.ui.common.toolbar.AdvancedToolbar;
import com.github.anrimian.musicplayer.ui.common.view.ViewUtils;
import com.github.anrimian.musicplayer.ui.playlist_screens.create.CreatePlayListDialogFragment;
import com.github.anrimian.musicplayer.ui.playlist_screens.playlist.PlayListFragment;
import com.github.anrimian.musicplayer.ui.playlist_screens.playlists.adapter.PlayListsAdapter;
import com.github.anrimian.musicplayer.ui.playlist_screens.rename.RenamePlayListDialogFragment;
import com.github.anrimian.musicplayer.ui.utils.dialogs.menu.MenuDialogFragment;
import com.github.anrimian.musicplayer.ui.utils.fragments.DialogFragmentRunner;
import com.github.anrimian.musicplayer.ui.utils.fragments.navigation.FragmentLayerListener;
import com.github.anrimian.musicplayer.ui.utils.fragments.navigation.FragmentNavigation;
import com.github.anrimian.musicplayer.ui.utils.views.recycler_view.RecyclerViewUtils;
import com.github.anrimian.musicplayer.ui.utils.wrappers.ProgressViewWrapper;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import moxy.MvpAppCompatFragment;
import moxy.presenter.InjectPresenter;
import moxy.presenter.ProvidePresenter;

import static com.github.anrimian.musicplayer.Constants.Tags.PLAY_LIST_MENU;

public class PlayListsFragment extends MvpAppCompatFragment
        implements PlayListsView, FragmentLayerListener {

    @InjectPresenter
    PlayListsPresenter presenter;

    private FragmentPlayListsBinding viewBinding;

    private PlayListsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ProgressViewWrapper progressViewWrapper;

    private DialogFragmentRunner<MenuDialogFragment> menuDialogRunner;

    @ProvidePresenter
    PlayListsPresenter providePresenter() {
        return Components.getAppComponent().playListsPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentPlayListsBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressViewWrapper = new ProgressViewWrapper(view);
        progressViewWrapper.hideAll();

        layoutManager = new LinearLayoutManager(getContext());
        viewBinding.recyclerView.setLayoutManager(layoutManager);

        RecyclerViewUtils.attachFastScroller(viewBinding.recyclerView);

        adapter = new PlayListsAdapter(
                viewBinding.recyclerView,
                this::goToPlayListScreen,
                presenter::onPlayListLongClick
        );
        viewBinding.recyclerView.setAdapter(adapter);

        viewBinding.fab.setOnClickListener(v -> onCreatePlayListButtonClicked());

        menuDialogRunner = new DialogFragmentRunner<>(getChildFragmentManager(),
                PLAY_LIST_MENU,
                fragment -> fragment.setComplexCompleteListener((menuItem, extra) -> {
                    PlayList playList = PlaylistSerializer.deserialize(extra);
                    onPlayListMenuItemSelected(menuItem, playList);
                }));
    }

    @Override
    public void onFragmentMovedOnTop() {
        AdvancedToolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.play_lists);
        toolbar.setSubtitle(null);
        toolbar.setTitleClickListener(null);
        toolbar.clearOptionsMenu();

        presenter.onFragmentMovedToTop();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop(ViewUtils.getListPosition(layoutManager));
    }

    @Override
    public void showEmptyList() {
        progressViewWrapper.hideAll();
        progressViewWrapper.showMessage(R.string.play_lists_on_device_not_found, false);
    }

    @Override
    public void showList() {
        progressViewWrapper.hideAll();
    }

    @Override
    public void showLoading() {
        progressViewWrapper.showProgress();
    }

    @Override
    public void updateList(List<PlayList> lists) {
        adapter.submitList(lists);
    }

    @Override
    public void restoreListPosition(ListPosition listPosition) {
        ViewUtils.scrollToPosition(layoutManager, listPosition);
    }

    @Override
    public void showPlayListMenu(PlayList playList) {
        Bundle extra = PlaylistSerializer.serialize(playList);
        MenuDialogFragment fragment = MenuDialogFragment.newInstance(R.menu.play_list_menu,
                playList.getName(),
                extra);
        menuDialogRunner.show(fragment);
    }

    @Override
    public void showConfirmDeletePlayListDialog(PlayList playList) {
        DialogUtils.showConfirmDeleteDialog(requireContext(),
                playList,
                () -> presenter.onDeletePlayListDialogConfirmed(playList));
    }

    @Override
    public void showPlayListDeleteSuccess(PlayList playList) {
        MessagesUtils.makeSnackbar(viewBinding.listContainer,
                getString(R.string.play_list_deleted, playList.getName()),
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showDeletePlayListError(ErrorCommand errorCommand) {
        MessagesUtils.makeSnackbar(viewBinding.listContainer,
                getString(R.string.play_list_delete_error, errorCommand.getMessage()),
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @Override
    public void showEditPlayListNameDialog(PlayList playList) {
        RenamePlayListDialogFragment fragment =
                RenamePlayListDialogFragment.newInstance(playList.getId());
        fragment.show(getChildFragmentManager(), null);
    }

    private void onPlayListMenuItemSelected(MenuItem menuItem, PlayList playList) {
        switch (menuItem.getItemId()) {
            case R.id.menu_change_play_list_name: {
                presenter.onChangePlayListNameButtonClicked(playList);
                break;
            }
            case R.id.menu_delete_play_list: {
                presenter.onDeletePlayListButtonClicked(playList);
                break;
            }
        }
    }

    private void onCreatePlayListButtonClicked() {
        CreatePlayListDialogFragment fragment = new CreatePlayListDialogFragment();
        fragment.show(getChildFragmentManager(), null);
    }

    private void goToPlayListScreen(PlayList playList) {
        FragmentNavigation.from(getParentFragmentManager())
                .addNewFragment(PlayListFragment.newInstance(playList.getId()));
    }
}
