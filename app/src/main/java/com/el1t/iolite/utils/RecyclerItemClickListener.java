package com.el1t.iolite.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by El1t on 8/7/15.
 */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
	private OnItemClickListener mListener;
	private GestureDetector mGestureDetector;

	public interface OnItemClickListener {
		void onItemClick(View view, int position);
	}

	public interface OnItemLongClickListener {
		void onItemLongClick(View view, int position);
	}

	public static void attachTo(RecyclerView view, Context context, OnItemClickListener listener) {
		view.addOnItemTouchListener(new RecyclerItemClickListener(context, listener));
	}

	public static void attachTo(RecyclerView view, Context context, OnItemClickListener clickListener,
	                            OnItemLongClickListener longClickListener) {
		view.addOnItemTouchListener(new RecyclerItemClickListener(context, clickListener, longClickListener, view));
	}

	public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
		mListener = listener;
		mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return true;
			}
		});
		mGestureDetector.setIsLongpressEnabled(false);
	}

	public RecyclerItemClickListener(Context context, OnItemClickListener clickListener,
	                                 final OnItemLongClickListener longClickListener, final RecyclerView view) {
		mListener = clickListener;
		mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return true;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				final View childView = view.findChildViewUnder(e.getX(), e.getY());
				if (childView != null && mListener != null) {
					longClickListener.onItemLongClick(childView, view.getChildAdapterPosition(childView));
				}
			}
		});
	}

	@Override
	public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
		final View childView;
		if (mListener != null) {
			if (mGestureDetector.onTouchEvent(e) &&
					(childView = view.findChildViewUnder(e.getX(), e.getY())) != null) {
				mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
				return true;
			}
		}
		return false;
	}

	@Override
	public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
		// Empty
	}

	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		// Empty
	}
}