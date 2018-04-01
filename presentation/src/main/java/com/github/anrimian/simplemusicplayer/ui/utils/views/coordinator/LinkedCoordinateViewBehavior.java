package com.github.anrimian.simplemusicplayer.ui.utils.views.coordinator;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created on 21.10.2017.
 */

public class LinkedCoordinateViewBehavior extends AppBarLayout.ScrollingViewBehavior {

    private static final float UNDEFINED = Float.MAX_VALUE;

    private float dependencyStartY = 0;

    public LinkedCoordinateViewBehavior() {
    }

    public LinkedCoordinateViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        return dependency.getId() == params.getAnchorId();
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if (dependencyStartY == UNDEFINED) {
            dependencyStartY = dependency.getY();
        }
        int dependencyY = (int) dependency.getY();
        int height = dependency.getMeasuredHeight();
        float deltaY = dependencyY - dependencyStartY;
        if (deltaY < 0) {
            deltaY *= -1;
        }

        int offset = height - (int) deltaY;
        child.setPadding(child.getPaddingLeft(), offset, child.getPaddingRight(), child.getPaddingBottom());
        return true;
    }
}