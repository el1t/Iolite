package com.el1t.iolite;

/**
 * Created by El1t on 11/18/14.
 */
public interface NavDrawerItem
{
	public int getId();

	public String getLabel();

	public int getType();

	public boolean isEnabled();

	public boolean updateActionBarTitle();
}