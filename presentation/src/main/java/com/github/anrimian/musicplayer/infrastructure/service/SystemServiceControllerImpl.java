package com.github.anrimian.musicplayer.infrastructure.service;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.github.anrimian.musicplayer.domain.controllers.SystemServiceController;
import com.github.anrimian.musicplayer.infrastructure.service.music.MusicService;

public class SystemServiceControllerImpl implements SystemServiceController {

    private final Context context;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public SystemServiceControllerImpl(Context context) {
        this.context = context;
    }

    @Override
    public void startMusicService() {
        handler.post(() -> {
            Intent intent = new Intent(context, MusicService.class);
            intent.putExtra(MusicService.START_FOREGROUND_SIGNAL, 1);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        });
    }
}
