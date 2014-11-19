package com.el1t.iolite;

import android.content.Context;

/**
 * Created by El1t on 11/18/14.
 */
public class NavMenuItem implements NavDrawerItem
{
	public static final int ITEM_TYPE = 1;

	private int id;
	private String label;
	private int icon;
	private boolean updateActionBarTitle;

	private NavMenuItem() { }

	// Create without icon
	public static NavMenuItem create(int id, String label, boolean updateActionBarTitle) {
		NavMenuItem item = new NavMenuItem();
		item.setId(id);
		item.setLabel(label);
		item.setUpdateActionBarTitle(updateActionBarTitle);
		// Disable icon
		item.setIcon(-1);
		return item;
	}

	public static NavMenuItem create(int id, String label, String icon, boolean updateActionBarTitle, Context context) {
		NavMenuItem item = new NavMenuItem();
		item.setId(id);
		item.setLabel(label);
		item.setIcon(context.getResources().getIdentifier(icon, "drawable", context.getPackageName()));
		item.setUpdateActionBarTitle(updateActionBarTitle);
		return item;
	}

	@Override
	public int getType() {
		return ITEM_TYPE;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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