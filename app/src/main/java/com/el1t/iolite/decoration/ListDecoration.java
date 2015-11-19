package com.el1t.iolite.decoration;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.el1t.iolite.R;

public class ListDecoration extends RecyclerView.ItemDecoration {
	private Drawable mDivider;
	private int mLeftPadding;

	public ListDecoration(Context context, AttributeSet attrs) {
		final TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.listDivider});
		mDivider = a.getDrawable(0);
		a.recycle();
	}

	public ListDecoration(Drawable divider) {
		mDivider = divider;
		mDivider.setColorFilter(Color.parseColor("#15000000"), PorterDuff.Mode.SRC);
		final DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		mLeftPadding = (int) (72 * (metrics.densityDpi / 160f) + 0.5);
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		super.getItemOffsets(outRect, view, parent, state);
		if (mDivider == null || parent.getChildLayoutPosition(view) < 1) {
			return;
		}
		if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
			outRect.top = mDivider.getIntrinsicHeight();
		} else {
			outRect.left = mDivider.getIntrinsicWidth();
		}
	}

	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		if (mDivider == null || parent.getChildCount() <= 0) {
			super.onDraw(c, parent, state);
			return;
		}
		if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
			final int left = parent.getPaddingLeft() + mLeftPadding;
			final int right = parent.getWidth() - parent.getPaddingRight();
			final int childCount = parent.getChildCount();

			for (int i = parent.getChildAt(0).getId() == R.id.title ? 2 : 1; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				if (child.getId() == R.id.title) {
					i++;
					continue;
				}
				final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
				final int size = mDivider.getIntrinsicHeight();
				final int top = child.getTop() - params.topMargin - size;
				final int bottom = top + size;
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		} else { //horizontal
			final int top = parent.getPaddingTop();
			final int bottom = parent.getHeight() - parent.getPaddingBottom();
			final int childCount = parent.getChildCount();

			for (int i = 1; i < childCount; i++) {
				final View child = parent.getChildAt(i);
				final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
				final int size = mDivider.getIntrinsicWidth();
				final int left = child.getLeft() - params.leftMargin;
				final int right = left + size;
				mDivider.setBounds(left, top, right, bottom);
				mDivider.draw(c);
			}
		}
	}

	private int getOrientation(RecyclerView parent) {
		if (parent.getLayoutManager() instanceof LinearLayoutManager) {
			final LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
			return layoutManager.getOrientation();
		} else {
			throw new IllegalStateException("ListDecoration can only be used with a LinearLayoutManager.");
		}
	}
}