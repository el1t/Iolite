package com.el1t.iolite.drawer;

import java.util.ArrayList;

/**
 * Created by El1t on 11/19/14.
 */

public class NavMenuBuilder {

	private ArrayList<NavDrawerItem> mMenu = new ArrayList<NavDrawerItem>();

	public NavMenuBuilder addItem(NavDrawerItem customItem) {
		mMenu.add(customItem);
		return this;
	}

	public NavMenuBuilder addItemAtIndex(NavDrawerItem customItem, int index) {
		mMenu.add(index, customItem);
		return this;
	}

	public NavMenuBuilder addSeparator() {
		mMenu.add(null);
		return this;
	}

	public NavMenuBuilder addSeparatorAtIndex(int index) {
		mMenu.add(index, null);
		return this;
	}

	public NavDrawerItem[] build() {
		return mMenu.toArray(new NavDrawerItem[mMenu.size()]);
	}
}