package com.el1t.iolite.drawer;

/**
 * Created by El1t on 11/18/14.
 */
public interface NavDrawerItem
{
	int getListId();

	String getLabel();

	int getType();

	boolean isCheckable();

	boolean isEnabled();

//	public boolean updateActionBarTitle();
}