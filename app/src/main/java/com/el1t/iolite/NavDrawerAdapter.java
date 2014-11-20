package com.el1t.iolite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by El1t on 11/18/14.
 */
public class NavDrawerAdapter extends ArrayAdapter<NavDrawerItem>
{
	private LayoutInflater mLayoutInflater;

	private static class NavMenuItemHolder {
		private TextView labelView;
		private ImageView iconView;
	}

//	private class NavMenuSectionHolder {
//		private TextView labelView;
//	}

	public NavDrawerAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final NavDrawerItem menuItem = this.getItem(position);
		// if (menuItem.getType() == NavMenuItem.ITEM_TYPE) {
		if (menuItem != null) {
			return getItemView(convertView, parent, menuItem);
		}
		return getSeparatorView(convertView, parent);
	}

	public View getItemView(View convertView, ViewGroup parentView, NavDrawerItem navDrawerItem) {
		final NavMenuItem menuItem = (NavMenuItem) navDrawerItem;
		final NavMenuItemHolder navMenuItemHolder;

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.nav_item, parentView, false);

			// Use view holder
			navMenuItemHolder = new NavMenuItemHolder();
			navMenuItemHolder.labelView = (TextView) convertView.findViewById(R.id.nav_label);
			navMenuItemHolder.iconView = (ImageView) convertView.findViewById(R.id.nav_icon);
			convertView.setTag(navMenuItemHolder);
		} else {
			navMenuItemHolder = (NavMenuItemHolder) convertView.getTag();
		}

		navMenuItemHolder.labelView.setText(menuItem.getLabel());
		navMenuItemHolder.iconView.setImageResource(menuItem.getIcon());
		navMenuItemHolder.iconView.setVisibility(View.VISIBLE);

		return convertView ;
	}

	public View getSeparatorView(View convertView, ViewGroup parentView) {
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.nav_separator, parentView, false);
		}
		return convertView;
	}

//	public View getSectionView(View convertView, ViewGroup parentView,
//	                           NavDrawerItem navDrawerItem) {
//		NavMenuSection menuSection = (NavMenuSection) navDrawerItem;
//		NavMenuSectionHolder navMenuItemHolder = null;
//
//		if (convertView == null) {
//			convertView = mLayoutInflater.inflate(R.layout.navdrawer_section, parentView, false);
//			TextView labelView = (TextView) convertView
//					.findViewById(R.id.nav_label);
//
//			navMenuItemHolder = new NavMenuSectionHolder();
//			navMenuItemHolder.labelView = labelView;
//			convertView.setTag(navMenuItemHolder);
//		}
//
//		if (navMenuItemHolder == null) {
//			navMenuItemHolder = (NavMenuSectionHolder) convertView.getTag();
//		}
//
//		navMenuItemHolder.labelView.setText(menuSection.getLabel());
//
//		return convertView;
//	}

	public void setItems(NavDrawerItem[] data) {
		if (data != null) {
			clear();
			addAll(data);
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return this.getItem(position) == null ? 1 : 0;
		// this.getItem(position).getType()
	}

	@Override
	public boolean isEnabled(int position) {
		return getItem(position).isEnabled();
	}
}