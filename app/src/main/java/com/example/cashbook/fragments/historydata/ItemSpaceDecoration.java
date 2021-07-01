package com.example.cashbook.fragments.historydata;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ItemSpaceDecoration extends RecyclerView.ItemDecoration {

    private int mSpace = 0;

    public ItemSpaceDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = SizeUtils.dp2px(view.getContext(), mSpace);

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.set(0, SizeUtils.dp2px(view.getContext(), mSpace), 0, SizeUtils.dp2px(view.getContext(), mSpace));
        } else {
            outRect.set(0, 0, 0, SizeUtils.dp2px(view.getContext(), mSpace));
        }

    }
}