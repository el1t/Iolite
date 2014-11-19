package com.el1t.iolite;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * Created by El1t on 11/17/14.
 */
public abstract class AbstractDrawerActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private ListView mDrawerList;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private NavDrawerActivityConfig navConf;

	protected abstract NavDrawerActivityConfig getNavDrawerConfiguration();

	protected abstract void onNavItemSelected(int id);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		navConf = getNavDrawerConfiguration();

		setContentView(navConf.getMainLayout());

		mTitle = mDrawerTitle = getTitle();

		mDrawerLayout = (DrawerLayout) findViewById(navConf.getDrawerLayoutId());
		mDrawerList = (ListView) findViewById(navConf.getLeftDrawerId());
		mDrawerList.setAdapter(navConf.getBaseAdapter());
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

//		this.initDrawerShadow();

		// Use material design toolbar
		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, navConf.getDrawerOpenDesc(),
												  navConf.getDrawerCloseDesc()) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	protected void initDrawerShadow() {
		mDrawerLayout.setDrawerShadow(navConf.getDrawerShadow(), GravityCompat.START);
	}

	protected int getDrawerIcon() {
		return R.drawable.ic_launcher;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (navConf.getActionMenuItemsToHideWhenDrawerOpen() != null ) {
			boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
			for(int iItem : navConf.getActionMenuItemsToHideWhenDrawerOpen()) {
				menu.findItem(iItem).setVisible(!drawerOpen);
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return mDrawerToggle.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
				this.mDrawerLayout.closeDrawer(this.mDrawerList);
			}
			else {
				this.mDrawerLayout.openDrawer(this.mDrawerList);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected DrawerLayout getDrawerLayout() {
		return mDrawerLayout;
	}

	protected ActionBarDrawerToggle getDrawerToggle() {
		return mDrawerToggle;
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	public void selectItem(int position) {
		NavDrawerItem selectedItem = navConf.getNavItems()[position];

		this.onNavItemSelected(selectedItem.getId());
		mDrawerList.setItemChecked(position, true);

		if (selectedItem.updateActionBarTitle()) {
			setTitle(selectedItem.getLabel());
		}

		if (this.mDrawerLayout.isDrawerOpen(this.mDrawerList)) {
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	public void onBackPressed() {
		if(mDrawerLayout.isDrawerOpen(Gravity.START| Gravity.LEFT)){
			mDrawerLayout.closeDrawers();
			return;
		}
		super.onBackPressed();
	}
}