package net.devdome.bhu.app.ui.components;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class CustomLinearLayoutManager extends LinearLayoutManager {
    private boolean isScrollable = true;

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public void setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollable;
    }
}
