package com.github.anrimian.musicplayer.ui.common.toolbar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.anrimian.musicplayer.R;
import com.github.anrimian.musicplayer.domain.utils.java.Callback;
import com.github.anrimian.musicplayer.ui.utils.fragments.navigation.FragmentNavigation;
import com.github.anrimian.musicplayer.ui.utils.fragments.navigation.FragmentStackListener;
import com.github.anrimian.musicplayer.ui.utils.views.text_view.SimpleTextWatcher;
import com.github.anrimian.musicplayer.utils.AndroidUtils;

import static android.animation.ObjectAnimator.ofFloat;
import static android.text.TextUtils.isEmpty;
import static com.github.anrimian.musicplayer.Constants.Animation.TOOLBAR_ARROW_ANIMATION_TIME;

public class AdvancedToolbar extends Toolbar {

    private static final String IS_IN_SEARCH_MODE = "is_in_search_mode";
    private static final String IS_KEYBOARD_SHOWN = "is_keyboard_shown";

    private final FragmentStackListener stackChangeListener = new StackChangeListenerImpl();

    private View clTitleContainer;
    private TextView tvTitle;
    private TextView tvSubtitle;
    private View actionIcon;
    private EditText etSearch;
    private ActionMenuView actionMenuView;
    private FrameLayout flTitleArea;

    private FragmentNavigation navigation;
    private DrawerArrowDrawable drawerArrowDrawable;
    private LockArrowInBackStateFunction lockArrowFunction;

    private Callback<String> textChangeListener;
    private Callback<String> textConfirmListener;
    private Callback<Boolean> searchModeListener;

    private boolean isInSearchMode;

    public AdvancedToolbar(Context context) {
        super(context);
    }

    public AdvancedToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvancedToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initializeViews() {
        clTitleContainer = findViewById(R.id.title_container);
        tvTitle = findViewById(R.id.tv_title);
        tvSubtitle = findViewById(R.id.tv_subtitle);
        actionIcon = findViewById(R.id.action_icon);
        etSearch = findViewById(R.id.et_search);
        flTitleArea = findViewById(R.id.fl_title_area);
        etSearch.addTextChangedListener(new SimpleTextWatcher(this::onSearchTextChanged));
        etSearch.setOnEditorActionListener(this::onSearchTextViewAction);
        etSearch.setVisibility(INVISIBLE);
        actionIcon.setVisibility(GONE);
    }

    public void setupWithActivity(AppCompatActivity activity) {
        setTitle("");//replace null to prevent auto title setting from action bar
        activity.setSupportActionBar(this);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    public void setupWithNavigation(FragmentNavigation navigation,
                                    DrawerArrowDrawable drawerArrowDrawable,
                                    LockArrowInBackStateFunction lockArrowFunction) {
        this.navigation = navigation;
        this.drawerArrowDrawable = drawerArrowDrawable;
        this.lockArrowFunction = lockArrowFunction;

        onFragmentStackChanged(navigation.getScreensCount(), true);
        navigation.addStackChangeListener(stackChangeListener);
    }

    public void setSearchModeEnabled(boolean enabled) {
        setSearchModeEnabled(enabled, true, false);
    }

    public void setSearchModeEnabled(boolean enabled,
                                     boolean showKeyboard,
                                     boolean jumpToState) {
        isInSearchMode = enabled;
        if (searchModeListener != null) {
            searchModeListener.call(enabled);
        }
        etSearch.setVisibility(enabled? VISIBLE: GONE);
        clTitleContainer.post(() -> clTitleContainer.setVisibility(enabled? INVISIBLE: VISIBLE));
        clTitleContainer.setVisibility(enabled? INVISIBLE: VISIBLE);
        getActionMenuView().setVisibility(enabled? GONE: VISIBLE);
        if (!lockArrowFunction.isLocked() && navigation.getScreensCount() <= 1) {
            setCommandButtonMode(!enabled, !jumpToState);
        }
        if (enabled) {
            etSearch.requestFocus();
            if (showKeyboard){
                AndroidUtils.showKeyboard(etSearch.getContext());
            }
        } else {
            etSearch.setText(null);
            AndroidUtils.hideKeyboard(etSearch);
        }
    }

    public void release() {
        navigation.removeStackChangeListener(stackChangeListener);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putBoolean(IS_IN_SEARCH_MODE, isInSearchMode);
        bundle.putBoolean(IS_KEYBOARD_SHOWN, AndroidUtils.isKeyboardWasShown(etSearch));
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            boolean isInSearchMode = bundle.getBoolean(IS_IN_SEARCH_MODE);
            boolean isKeyboardShown = bundle.getBoolean(IS_KEYBOARD_SHOWN);
            setSearchModeEnabled(isInSearchMode, isKeyboardShown, true);

            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public CharSequence getTitle() {
        return tvTitle.getText();
    }

    @Override
    public void setTitle(CharSequence title) {
        tvTitle.setVisibility(isEmpty(title) ? GONE : VISIBLE);
        tvTitle.setText(title);
    }

    @Override
    public CharSequence getSubtitle() {
        return tvSubtitle.getText();
    }

    @Override
    public void setSubtitle(CharSequence subtitle) {
        tvSubtitle.setVisibility(isEmpty(subtitle) ? GONE : VISIBLE);
        tvSubtitle.setText(subtitle);
    }

    public void setTitleClickListener(View.OnClickListener listener) {
        actionIcon.setVisibility(listener == null? GONE : VISIBLE);
        flTitleArea.setEnabled(listener != null);
        flTitleArea.setOnClickListener(listener);
    }

    public void onStackFragmentSlided(float offset) {
        if (navigation.getScreensCount() >= 2) {
            drawerArrowDrawable.setProgress(offset);
        }
    }

    public boolean isInSearchMode() {
        return isInSearchMode;
    }

    public ActionMenuView getActionMenuView() {
        if (actionMenuView == null) {
            actionMenuView = findActionMenuView();
            if (actionMenuView == null) {
                inflateMenu(R.menu.empty_stub_menu);
            }
            actionMenuView = findActionMenuView();
        }
        return actionMenuView;
    }

    public void setTextChangeListener(Callback<String> textChangeListener) {
        this.textChangeListener = textChangeListener;
    }

    public void setTextConfirmListener(Callback<String> textConfirmListener) {
        this.textConfirmListener = textConfirmListener;
    }

    public void setSearchModeListener(Callback<Boolean> searchModeListener) {
        this.searchModeListener = searchModeListener;
    }

    private void onFragmentStackChanged(int stackSize, boolean jumpToState) {
        boolean isRoot = stackSize <= 1;
        if (isRoot && lockArrowFunction.isLocked()) {
            return;
        }

        setCommandButtonMode(isRoot, !jumpToState);
    }

    private void setCommandButtonMode(boolean isNormal, boolean animate) {
        float end = isNormal? 0f : 1f;
        if (animate) {
            float start = drawerArrowDrawable.getProgress();
            ObjectAnimator objectAnimator = ofFloat(drawerArrowDrawable, "progress", start, end);
            objectAnimator.setDuration(TOOLBAR_ARROW_ANIMATION_TIME);
            objectAnimator.start();
        } else {
            drawerArrowDrawable.setProgress(end);
        }
    }

    @Nullable
    private ActionMenuView findActionMenuView() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ActionMenuView) {
                return (ActionMenuView) child;
            }
        }
        return null;
    }

    private boolean onSearchTextViewAction(TextView v, int actionId, KeyEvent event) {
        if (textConfirmListener != null) {
            textConfirmListener.call(v.getText().toString());
            return true;
        }
        return true;
    }

    private void onSearchTextChanged(String text) {
        if (textChangeListener != null) {
            textChangeListener.call(text);
        }
    }

    public interface LockArrowInBackStateFunction {
        boolean isLocked();
    }

    private class StackChangeListenerImpl implements FragmentStackListener {

        @Override
        public void onStackChanged(int stackSize) {
            onFragmentStackChanged(navigation.getScreensCount(), false);
        }
    }
}