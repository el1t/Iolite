package com.el1t.iolite;

import android.content.Context;

/**
 * Created by El1t on 11/18/14.
 */
public class NavMenuItem implements NavDrawerItem
{
	public static final int ITEM_TYPE = 1;

	private int resId;
	private String label;
	private int icon;
	private boolean checkable;
	private boolean updateActionBarTitle;

	private NavMenuItem() { }

	// Create without icon, temporary
	public static NavMenuItem create(int resId, String label) {
		NavMenuItem item = new NavMenuItem();
		item.setResId(resId);
		item.setLabel(label);
		item.setCheckable(true);
		// Disable icon
		item.setIcon(-1);
		return item;
	}

	public static NavMenuItem createButton(int resId, String label) {
		NavMenuItem item = new NavMenuItem();
		item.setResId(resId);
		item.setLabel(label);
		item.setCheckable(false);
		// Disable icon
		item.setIcon(-1);
		return item;
	}

	// Create with icon, is checkable
	public static NavMenuItem create(Context context, int resId, String label, String icon, boolean updateActionBarTitle) {
		NavMenuItem item = new NavMenuItem();
		item.setResId(resId);
		item.setLabel(label);
		item.setIcon(context.getResources().getIdentifier(icon, "drawable", context.getPackageName()));
		item.setCheckable(true);
		item.setUpdateActionBarTitle(updateActionBarTitle);
		return item;
	}

	// Create with icon, not checkable
	public static NavMenuItem createButton(Context context, int resId, String label, String icon, boolean updateActionBarTitle) {
		NavMenuItem item = new NavMenuItem();
		item.setResId(resId);
		item.setLabel(label);
		item.setIcon(context.getResources().getIdentifier(icon, "drawable", context.getPackageName()));
		item.setCheckable(false);
		item.setUpdateActionBarTitle(updateActionBarTitle);
		return item;
	}

	@Override
	public int getType() {
		return ITEM_TYPE;
	}

	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public boolean isCheckable() {
		return checkable;
	}

	public void setCheckable(boolean checkable) {
		this.checkable = checkable;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean updateActionBarTitle() {
		return updateActionBarTitle;
	}

	public void setUpdateActionBarTitle(boolean updateActionBarTitle) {
		updateActionBarTitle = updateActionBarTitle;
	}
}