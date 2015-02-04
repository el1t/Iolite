package com.el1t.iolite.drawer;

/**
 * Created by El1t on 11/17/14.
 */
public class NavDrawerActivityConfig {

	private int mainLayout;
	private int drawerShadow;
	private int drawerLayoutId;
	private int drawerContainerId;
	private int leftDrawerId;
	private int[] actionMenuItemsToHideWhenDrawerOpen;
	private NavDrawerItem[] navItems;
	private int drawerOpenDesc;
	private int drawerCloseDesc;
	private NavDrawerAdapter adapter;
	private int checkedPosition;

	private NavDrawerActivityConfig() {
		mainLayout = drawerShadow = drawerLayoutId = drawerContainerId = leftDrawerId = drawerOpenDesc =
				drawerCloseDesc = checkedPosition = -1;
	}

	public int getMainLayout() {
		return mainLayout;
	}

	void setMainLayout(int mainLayout) {
		this.mainLayout = mainLayout;
	}

	public int getDrawerShadow() {
		return drawerShadow;
	}

	void setDrawerShadow(int drawerShadow) {
		this.drawerShadow = drawerShadow;
	}

	public int getDrawerLayoutId() {
		return drawerLayoutId;
	}

	void setDrawerLayoutId(int drawerLayoutId) {
		this.drawerLayoutId = drawerLayoutId;
	}

	public int getDrawerContainerId() {
		return drawerContainerId;
	}

	void setDrawerContainerId(int drawerContainerId) {
		this.drawerContainerId = drawerContainerId;
	}

	public int getLeftDrawerId() {
		return leftDrawerId;
	}

	void setLeftDrawerId(int leftDrawerId) {
		this.leftDrawerId = leftDrawerId;
	}

	public int[] getActionMenuItemsToHideWhenDrawerOpen() {
		return actionMenuItemsToHideWhenDrawerOpen;
	}

	public void setActionMenuItemsToHideWhenDrawerOpen(int[] actionMenuItemsToHideWhenDrawerOpen) {
		this.actionMenuItemsToHideWhenDrawerOpen = actionMenuItemsToHideWhenDrawerOpen;
	}

	public NavDrawerItem[] getNavItems() {
		return navItems;
	}

	public void setNavItems(NavDrawerItem[] navItems) {
		this.navItems = navItems;
	}

	public int getDrawerOpenDesc() {
		return drawerOpenDesc;
	}

	void setDrawerOpenDesc(int drawerOpenDesc) {
		this.drawerOpenDesc = drawerOpenDesc;
	}

	public int getDrawerCloseDesc() {
		return drawerCloseDesc;
	}

	void setDrawerCloseDesc(int drawerCloseDesc) {
		this.drawerCloseDesc = drawerCloseDesc;
	}

	public NavDrawerAdapter getAdapter() {
		return adapter;
	}

	void setAdapter(NavDrawerAdapter adapter) {
		this.adapter = adapter;
	}

	public int getCheckedPosition() {
		return checkedPosition;
	}

	void setCheckedPosition(int checkedPosition) {
		this.checkedPosition = checkedPosition;
	}

	public static class Builder {

		private NavDrawerActivityConfig mConf = new NavDrawerActivityConfig();

		public Builder() { }

		public Builder mainLayout(int mainLayout) {
			mConf.setMainLayout(mainLayout);
			return this;
		}

		public NavDrawerActivityConfig build() {
			return mConf;
		}

		public Builder drawerLayoutId(int drawerLayoutId) {
			mConf.setDrawerLayoutId(drawerLayoutId);
			return this;
		}

		public Builder drawerContainerId(int drawerContainerId) {
			mConf.setDrawerContainerId(drawerContainerId);
			return this;
		}

		public Builder leftDrawerId(int leftDrawerId) {
			mConf.setLeftDrawerId(leftDrawerId);
			return this;
		}

		public Builder checkedPosition(int checkedPosition) {
			mConf.setCheckedPosition(checkedPosition);
			return this;
		}

		public Builder drawerShadow(int drawerShadowId) {
			mConf.setDrawerShadow(drawerShadowId);
			return this;
		}

		public Builder drawerOpenDesc(int drawerOpenId) {
			mConf.setDrawerOpenDesc(drawerOpenId);
			return this;
		}

		public Builder drawerCloseDesc(int drawerCloseId) {
			mConf.setDrawerCloseDesc(drawerCloseId);
			return this;
		}

		public Builder adapter( NavDrawerAdapter adapter) {
			mConf.setAdapter(adapter);
			return this;
		}
	}
}