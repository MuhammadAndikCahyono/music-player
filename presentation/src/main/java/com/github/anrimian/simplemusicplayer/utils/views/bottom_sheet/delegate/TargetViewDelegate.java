package com.github.anrimian.simplemusicplayer.utils.views.bottom_sheet.delegate;

import android.util.Log;
import android.view.View;

import static android.support.v4.view.ViewCompat.isLaidOut;

/**
 * Created on 13.01.2018.
 */

public class TargetViewDelegate implements BottomSheetDelegate {

    private static final float UNDEFINED = -1;

    private float startX = UNDEFINED;
    private float startY = UNDEFINED;
    private float startPadding = UNDEFINED;
    private float endX = UNDEFINED;
    private float endY = UNDEFINED;
    private float endPadding = UNDEFINED;

    private View view;
    private View targetView;

    public TargetViewDelegate(View view, View targetView) {
        this.view = view;
        this.targetView = targetView;
    }

    @Override
    public void onSlide(float slideOffset) {
        if (isLaidOut(view) && isLaidOut(targetView)) {
            moveView(slideOffset);
        } else {
            view.post(() -> moveView(slideOffset));
        }
    }

    private void moveView(float slideOffset) {
        if (startX == UNDEFINED) {
            startX = view.getX();
            startY = view.getY();
            startPadding = view.getPaddingBottom();
            endX = targetView.getX();
            endY = targetView.getY();
            endPadding = targetView.getPaddingBottom();
        }
        float deltaX = endX - startX;
        view.setX(startX + (deltaX * slideOffset));

        float deltaY = endY - startY;
        view.setY(startY + (deltaY * slideOffset));

        float deltaPadding = endPadding - startPadding;
        int resultPadding = (int) (startPadding + (deltaPadding * slideOffset));
        Log.d("resultPadding", "resultPadding: " + resultPadding);
        view.setPadding(resultPadding, resultPadding, resultPadding, resultPadding);
    }
}