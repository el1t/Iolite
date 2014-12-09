package com.el1t.iolite.drawer;

/**
 * Created by El1t on 11/18/14.
 */
public interface NavDrawerItem
{
	public int getListId();

	public String getLabel();

	public int getType();

	public boolean isCheckable();

	public boolean isEnabled();

//	public boolean updateActionBarTitle();
}