package com.el1t.iolite.drawer;

/**
 * Created by El1t on 11/18/14.
 */
public class NavMenuItem implements NavDrawerItem
{
	private static final int ITEM_TYPE = 1;

	private int listId;
	private String label;
	private int icon;
	private boolean checkable;
	private boolean updateActionBarTitle;

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

	void setListId(int listId) {
		this.listId = listId;
	}

	public String getLabel() {
		return label;
	}

	void setLabel(String label) {
		this.label = label;
	}

	public int getIcon() {
		return icon;
	}

	void setIcon(int icon) {
		this.icon = icon;
	}

	public boolean isCheckable() {
		return checkable;
	}

	void setCheckable(boolean checkable) {
		this.checkable = checkable;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

//	@Override
//	public boolean updateActionBarTitle() {
//		return this.updateActionBarTitle;
//	}

	public void setUpdateActionBarTitle(boolean updateActionBarTitle) {
		this.updateActionBarTitle = updateActionBarTitle;
	}
}