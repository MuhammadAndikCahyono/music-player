package com.github.anrimian.musicplayer.ui.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;

import com.github.anrimian.musicplayer.R;
import com.github.anrimian.musicplayer.domain.models.composition.Composition;
import com.github.anrimian.musicplayer.domain.models.composition.folders.FolderFileSource;

import java.io.File;
import java.util.List;

import static com.github.anrimian.musicplayer.domain.utils.TextUtils.getLastPathSegment;
import static com.github.anrimian.musicplayer.ui.common.format.FormatUtils.formatCompositionName;

public class DialogUtils {

    public static void showConfirmDeleteDialog(Context context,
                                        List<Composition> compositions,
                                        Runnable deleteCallback) {
        String message = compositions.size() == 1?
                context.getString(R.string.delete_composition_template, formatCompositionName(compositions.get(0))):
                context.getString(R.string.delete_template, getDativCompositionsMessage(context, compositions.size()));
        showConfirmDeleteDialog(context, message, deleteCallback);
    }

    public static void showConfirmDeleteDialog(Context context,
                                               FolderFileSource folderFileSource,
                                               Runnable deleteCallback) {
        String message = context.getString(R.string.delete_folder_template,
                getLastPathSegment(folderFileSource.getFullPath()),
                getDativCompositionsMessage(context, folderFileSource.getFilesCount()));
        showConfirmDeleteDialog(context, message, deleteCallback);
    }

    public static void showConfirmDeleteDialog(Context context,
                                               String message,
                                               Runnable deleteCallback) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.deleting)
                .setMessage(message)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteCallback.run())
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static void shareFile(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");
        Uri fileUri = FileProvider.getUriForFile(context,
                context.getString(R.string.file_provider_authorities),
                new File(filePath));
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
    }

    private static String getDativCompositionsMessage(Context context, int count) {
        return context.getResources().getQuantityString(R.plurals.compositions_count_dativ, count, count);
    }
}
