package com.el1t.iolite.decoration;

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by El1t on 8/11/15.
 */
public class CardDecoration extends RecyclerView.ItemDecoration {
	private int space;

	public CardDecoration() {
		this(16);
	}

	public CardDecoration(int dp) {
		final DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		this.space = (int) (dp * (metrics.densityDpi / 160f) + 0.5);
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		outRect.left = space / 2;
		outRect.right = space / 2;
		outRect.bottom = space;

		// Add top margin only for the first item to avoid double space between items
		if (parent.getChildLayoutPosition(view) == 0) {
			outRect.top = space;
		}
	}
}