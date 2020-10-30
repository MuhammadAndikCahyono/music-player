package com.github.anrimian.musicplayer.ui.editor.common;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.github.anrimian.musicplayer.ui.common.error.ErrorCommand;

public class EditorErrorHandler {

    private static final int EDIT_REQUEST_CODE = 1;
    private static final String EDITOR_REQUEST_FRAGMENT_TAG = "editor_request_fragment";

    public EditorErrorHandler(AppCompatActivity activity,
                              Runnable onPermissionGranted,
                              Runnable onPermissionDenied) {
        FragmentManager fm =  activity.getSupportFragmentManager();
        EditRequestFragment fragment = (EditRequestFragment) fm.findFragmentByTag(EDITOR_REQUEST_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new EditRequestFragment();
            fm.beginTransaction()
                    .add(fragment, EDITOR_REQUEST_FRAGMENT_TAG)
                    .commit();
        }
        fragment.setOnPermissionGranted(onPermissionGranted);
        fragment.setOnPermissionDenied(onPermissionDenied);
    }

    public void handleEditorError(Activity activity,
                                  ErrorCommand errorCommand,
                                  Runnable defaultAction) {
        if (errorCommand instanceof EditorErrorCommand) {
            try {
                activity.startIntentSenderForResult(
                        ((EditorErrorCommand) errorCommand).getIntentSender(),
                        EDIT_REQUEST_CODE,
                        null,
                        0,
                        0,
                        0,
                        null
                );
            } catch (IntentSender.SendIntentException e) {
                Toast.makeText(activity, "can not start request activity", Toast.LENGTH_LONG).show();
            }
        } else {
            defaultAction.run();
        }
    }

    public static class EditRequestFragment extends Fragment {

        private Runnable onPermissionGranted;
        private Runnable onPermissionDenied;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            switch (requestCode) {
                case EditorErrorHandler.EDIT_REQUEST_CODE: {
                    if (resultCode == Activity.RESULT_OK) {
                        onPermissionGranted.run();//seems not called after recreation
                    } else {
                        onPermissionDenied.run();//seems not called
                    }
                }
                default: super.onActivityResult(requestCode, resultCode, data);
            }
        }

        public void setOnPermissionGranted(Runnable onPermissionGranted) {
            this.onPermissionGranted = onPermissionGranted;
        }

        public void setOnPermissionDenied(Runnable onPermissionDenied) {
            this.onPermissionDenied = onPermissionDenied;
        }
    }
}
