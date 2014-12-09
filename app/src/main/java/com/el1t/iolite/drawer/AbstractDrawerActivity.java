package com.el1t.iolite.drawer;

import android.content.res.Configuration;
import android.os.Bundle;
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
import android.widget.RelativeLayout;

import com.el1t.iolite.R;

/**
 * Created by El1t on 11/17/14.
 */
public abstract class AbstractDrawerActivity extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private RelativeLayout mDrawerContainer;
	private ListView mDrawerList;
	private NavDrawerAdapter mDrawerAdapter;
	private NavDrawerActivityConfig navConf;
	private int lastItemChecked;

	public abstract NavDrawerActivityConfig getNavDrawerConfiguration();

	public abstract void onNavItemSelected(int id);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		navConf = getNavDrawerConfiguration();
		setContentView(navConf.getMainLayout());

		if (savedInstanceState == null) {
			lastItemChecked = navConf.getCheckedPosition();
		} else {
			lastItemChecked = savedInstanceState.getInt("lastItemChecked");
		}

		mDrawerLayout = (DrawerLayout) findViewById(navConf.getDrawerLayoutId());
		mDrawerContainer = (RelativeLayout) findViewById(navConf.getDrawerContainerId());
		mDrawerList = (ListView) findViewById(navConf.getLeftDrawerId());

		mDrawerAdapter = navConf.getAdapter();
		mDrawerAdapter.check(lastItemChecked);
		mDrawerList.setAdapter(mDrawerAdapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		this.initDrawerShadow();

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
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// Do this last for it to show
		if (lastItemChecked != -1) {
			mDrawerList.setItemChecked(lastItemChecked, true);
		}
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
		if (navConf.getActionMenuItemsToHideWhenDrawerOpen() != null) {
			final boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerContainer);
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
			if (mDrawerLayout.isDrawerOpen(mDrawerContainer)) {
				mDrawerLayout.closeDrawer(mDrawerContainer);
			}
			else {
				mDrawerLayout.openDrawer(mDrawerContainer);
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
			final NavDrawerItem selectedItem = mDrawerAdapter.getItem(position);
			onNavItemSelected(selectedItem.getListId());
			if (selectedItem.isCheckable()) {
				mDrawerAdapter.check(position);
				// Checking is automatically done
				lastItemChecked = position;
			} else if (lastItemChecked != -1) {
				// Check other position instead
				mDrawerList.setItemChecked(lastItemChecked, true);
			}

			if (mDrawerLayout.isDrawerOpen(mDrawerContainer)) {
				mDrawerLayout.closeDrawer(mDrawerContainer);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if(mDrawerLayout.isDrawerOpen(Gravity.START| Gravity.LEFT)){
			mDrawerLayout.closeDrawers();
			return;
		}
		super.onBackPressed();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("lastItemChecked", this.lastItemChecked);
	}
}