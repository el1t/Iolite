package com.el1t.iolite.drawer;

/**
 * Created by El1t on 11/18/14.
 */
public class NavMenuItem implements NavDrawerItem
{
	public static final int ITEM_TYPE = 1;

	private int listId;
	private String label;
	private int icon;
	private boolean checkable;

	private NavMenuItem() { }

	// Create with icon, is checkable
	public static NavMenuItem create(int listId, String label, int icon) {
		NavMenuItem item = new NavMenuItem();
		item.setListId(listId);
		item.setLabel(label);
		item.setIcon(icon);
		item.setCheckable(true);
		return item;
	}

	// Create with icon, not checkable
	public static NavMenuItem createButton(int listId, String label, int icon) {
		NavMenuItem item = new NavMenuItem();
		item.setListId(listId);
		item.setLabel(label);
		item.setIcon(icon);
		item.setCheckable(false);
		return item;
	}

	@Override
	public int getType() {
		return ITEM_TYPE;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
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

//	@Override
//	public boolean updateActionBarTitle() {
//		return updateActionBarTitle;
//	}

	public void setUpdateActionBarTitle(boolean updateActionBarTitle) {
		updateActionBarTitle = updateActionBarTitle;
	}
}