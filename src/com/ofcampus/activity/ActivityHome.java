/*
 * This is the source code of OfCampus for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.ofcampus.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.ofcampus.OfCampusApplication;
import com.ofcampus.R;
import com.ofcampus.Util;
import com.ofcampus.activity.FragmentJobs.JobsFrgInterface;
import com.ofcampus.activity.FragmentNewsFeeds.FragmentNewsInterface;
import com.ofcampus.adapter.SlideMenuAdapter;
import com.ofcampus.adapter.SlideMenuAdapter.viewCLickEvent;
import com.ofcampus.component.PagerSlidingTabStrip;
import com.ofcampus.model.FilterDataSets;
import com.ofcampus.model.JobDetails;
import com.ofcampus.model.UserDetails;
import com.ofcampus.parser.FilterJobParser;
import com.ofcampus.parser.SearchParser;
import com.ofcampus.parser.SearchParser.SearchParserInterface;
import com.ofcampus.ui.FilterDialog;

public class ActivityHome extends ActionBarActivity implements OnClickListener, viewCLickEvent, OnPageChangeListener, JobsFrgInterface, FragmentNewsInterface {

	private String NAME = "";
	private String EMAIL = "";
	private String tocken = "";
	private String picUrl = "";
	// private int PROFILE = R.drawable.ic_profilepic;

	private Toolbar toolbar;
	private RecyclerView mRecyclerView;
	private SlideMenuAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private DrawerLayout Drawer;
	private SearchView searchView = null;
	private ImageView img_composejob;

	private ActionBarDrawerToggle mDrawerToggle;
	private Context mContext;

	/* Pager section */
	private int currentSelection = 1;
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private FragmentNewsFeeds fragmentNewsFeeds;
	private FragmentJobs fragmentJobs;
	private FragmentClassifieds mClassifiedsFragment;
	private FragmentMeetups fragmentMeetups;

	private TextView txt_countJobs, txt_countNews, txt_countmetup;

	/** Filter Data ***/
	private FilterDataSets mFilterDataSets = null;
	private boolean isChangedhideList = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mContext = ActivityHome.this;
		loadProfileData();
		initilizActionBarDrawer();
		initilizePagerview();
		loadFilterData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			stopservice();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try {
			stopservice();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			if (((OfCampusApplication) getApplication()).isHidePostModify || ((OfCampusApplication) getApplication()).editPostSuccessForHome) {
				fragmentJobs.loadData(false);
				((OfCampusApplication) getApplication()).isHidePostModify = false;
				((OfCampusApplication) getApplication()).editPostSuccessForHome = false;
			}

			if (((OfCampusApplication) getApplication()).editPostSuccessForNews) {
				fragmentNewsFeeds.loadData();
				((OfCampusApplication) getApplication()).editPostSuccessForNews = false;
			}

			if (fragmentJobs.mJobListAdapter != null) {
				fragmentJobs.mJobListAdapter.notifyDataSetChanged();
			}

			if (((OfCampusApplication) getApplication()).profileEditSuccess) {
				updateProfileData();
				((OfCampusApplication) getApplication()).profileEditSuccess = false;
			}
			stopservice();
			startService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		MenuItem searchItem = menu.findItem(R.id.action_search);
		if (searchItem != null) {
			searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		}

		if (searchView != null) {
			searchView.setIconifiedByDefault(true);
			searchView.setQueryHint("Search job");
			searchView.setOnQueryTextListener(new OnQueryTextListener() {

				@Override
				public boolean onQueryTextSubmit(String s) {
					searchView.clearFocus();
					searchEvent(s); 
					return true;
				}

				@Override
				public boolean onQueryTextChange(String arg0) {
					return false;
				}
			});

			searchView.setOnSearchClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					closeDraware();
					img_composejob.setVisibility(View.GONE);
				}
			});

			MenuItemCompat.setOnActionExpandListener(searchItem, new OnActionExpandListener() {
				@Override
				public boolean onMenuItemActionCollapse(MenuItem item) {
					img_composejob.setVisibility(View.VISIBLE);
					return true;
				}

				@Override
				public boolean onMenuItemActionExpand(MenuItem item) {
					return true;
				}
			});

			searchView.setOnCloseListener(new OnCloseListener() {

				@Override
				public boolean onClose() {
					img_composejob.setVisibility(View.VISIBLE);
					return false;
				}
			});
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_search) {
			return true;
		} else if (id == R.id.action_filter) {
			if (mFilterDataSets != null) {
				FilterDialog mDialog = new FilterDialog(mContext, mFilterDataSets);
				mDialog.showDialog();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_home_img_composejob:
			Intent mIntent = new Intent(ActivityHome.this, ActivityCreatePost.class);
			Bundle mBundle = new Bundle();
			if (currentSelection == 0) {
				mBundle.putString("ToolBarTitle", "Create News");
				mBundle.putInt("createFor", currentSelection);
			} else {
				mBundle.putString("ToolBarTitle", "Create Job");
				mBundle.putInt("createFor", currentSelection);
			}
			mIntent.putExtras(mBundle);
			startActivity(mIntent);
			overridePendingTransition(0, 0);
			break;

		default:
			break;
		}
	}

	@Override
	public void OnViewItemClick(final int position) {
		closeDraware();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				switch (position) {

				case 1:
					startActivity(new Intent(ActivityHome.this, ActivityMyProfile.class));
					overridePendingTransition(0, 0);
					break;
				case 2:
					startActivity(new Intent(ActivityHome.this, ActivityMyPost.class));
					overridePendingTransition(0, 0);
					break;
				case 3:
					startActivity(new Intent(ActivityHome.this, ActivityImportantmail.class));
					overridePendingTransition(0, 0);
					break;
				case 4:
					startActivity(new Intent(ActivityHome.this, ActivityHidePost.class));
					overridePendingTransition(0, 0);
					break;
				case 5:
					startActivity(new Intent(ActivityHome.this, ActivityCircle.class));
					overridePendingTransition(0, 0);
					break;
				case 6:
					startActivity(new Intent(ActivityHome.this, ActivityResetPassword.class));
					overridePendingTransition(0, 0);
					break;
				case 7:
					showLogutDialog();
					break;

				default:
					break;
				}
			}
		}, 200);
	}

	/**
	 * Pager Page Selected.
	 */
	@Override
	public void onPageSelected(int position) {
		switch (position) {
		case 0:
			img_composejob.setVisibility(View.VISIBLE);
			break;
		case 1:
			img_composejob.setVisibility(View.VISIBLE);
			break;
		case 2:
			img_composejob.setVisibility(View.GONE);
			break;

		default:
			break;
		}
		currentSelection = position;
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void pullToRefreshCallCompleteForNews() {
		count[0] = "";
		txt_countNews.setVisibility(View.GONE);
	}

	@Override
	public void pullToRefreshCallCompleteForJob() {
		count[1] = "";
		txt_countJobs.setVisibility(View.GONE);
	}

	private void initilizActionBarDrawer() {
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("OfCampus");
		setSupportActionBar(toolbar);

		mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
		mRecyclerView.setHasFixedSize(true);
		img_composejob = (ImageView) findViewById(R.id.activity_home_img_composejob);
		img_composejob.setOnClickListener(this);

		mAdapter = new SlideMenuAdapter(ActivityHome.this, Util.TITLES, Util.ICONS, NAME, EMAIL, picUrl);
		mAdapter.setViewclickevent(this);
		mRecyclerView.setAdapter(mAdapter);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);

		Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
		mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

		};
		Drawer.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
	}

	private void loadProfileData() {
		UserDetails mUserDetails = UserDetails.getLoggedInUser(mContext);
		EMAIL = mUserDetails.getEmail();
		NAME = mUserDetails.getName();
		tocken = mUserDetails.getAuthtoken();
		picUrl = mUserDetails.getImage();
	}

	private void updateProfileData() {
		UserDetails mUserDetails = UserDetails.getLoggedInUser(mContext);
		EMAIL = mUserDetails.getEmail();
		NAME = mUserDetails.getName();
		tocken = mUserDetails.getAuthtoken();
		picUrl = mUserDetails.getImage();
		mAdapter = new SlideMenuAdapter(ActivityHome.this, Util.TITLES, Util.ICONS, NAME, EMAIL, picUrl);
		mAdapter.setViewclickevent(this);
		mRecyclerView.setAdapter(mAdapter);
	}

	private void initilizePagerview() {
		GradientDrawable bgShape = null;

		txt_countJobs = (TextView) findViewById(R.id.activity_home_jobcount);
		bgShape = (GradientDrawable) txt_countJobs.getBackground();
		bgShape.setColor(Color.parseColor("#5498C7"));

		txt_countNews = (TextView) findViewById(R.id.activity_home_classcount);
		bgShape = (GradientDrawable) txt_countNews.getBackground();
		bgShape.setColor(Color.parseColor("#E84C3D"));

		txt_countmetup = (TextView) findViewById(R.id.activity_home_meetcount);
		bgShape = (GradientDrawable) txt_countmetup.getBackground();
		bgShape.setColor(Color.parseColor("#18BC9A"));

		txt_countJobs.setVisibility(View.GONE);
		txt_countNews.setVisibility(View.GONE);
		txt_countmetup.setVisibility(View.GONE);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		tabs.setViewPager(pager);
		pager.setOffscreenPageLimit(3);
		pager.setCurrentItem(1);
		tabs.setOnPageChangeListener(this);
	}

	private void closeDraware() {
		if (Drawer.isDrawerOpen(GravityCompat.START)) {
			Drawer.closeDrawers();
		}
	}

	private void showLogutDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		alert.setTitle("Logout");
		alert.setMessage("Do you want to logout?");
		alert.setPositiveButton("No", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Util.ShowToast(mContext, "Successfully logout.");
				UserDetails.logoutUser(mContext);
				startActivity(new Intent(ActivityHome.this, ActivitySplash.class));
				overridePendingTransition(0, 0);
				finish();
			}
		});
		alert.create();
		alert.show();
	}

	public class MyPagerAdapter extends FragmentStatePagerAdapter {

		private final String[] TITLES = { "Newsfeed", "Jobs", "Classifieds" };

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				fragmentNewsFeeds = FragmentNewsFeeds.newInstance(position, ActivityHome.this);
				fragmentNewsFeeds.setFragmentnewsinterface(ActivityHome.this);
				return fragmentNewsFeeds;
			case 1:
				fragmentJobs = FragmentJobs.newInstance(position, ActivityHome.this);
				fragmentJobs.setJobsfrginterface(ActivityHome.this);
				return fragmentJobs;
			case 2:
				mClassifiedsFragment = FragmentClassifieds.newInstance(position, ActivityHome.this);
				return mClassifiedsFragment;
			}
			return null;
		}
	}

	/********************************************** Sync service ***********************************************************/

	private Timer timer;
	private MyTask mTask;
	private String[] count = { "", "", "" };// News,Jobs,MeetUp.

	public void stopservice() {
		try {
			if (timer != null) {
				timer.cancel();
				timer.purge();
				timer = null;
			}
			if (mTask != null) {
				mTask.cancel();
				mTask = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startService() {
		try {
			if (timer != null) {
				timer.cancel();
				timer.purge();
				timer = null;
				timer = new Timer();
				mTask = new MyTask();
				timer.scheduleAtFixedRate(mTask, Util.delay, Util.period);
			}
			if (timer == null) {
				timer = new Timer();
				mTask = new MyTask();
				timer.scheduleAtFixedRate(mTask, Util.delay, Util.period);
			}
		} catch (Exception e) {
			Log.i("TaskTimerNullcheck", "TaskTimerNullcheck_excep");
			e.printStackTrace();
		}
	}

	private class MyTask extends TimerTask {
		@Override
		public void run() {
			try {

				if (Util.hasConnection(mContext)) {

					try {
						mFilterDataSets = new FilterJobParser().parse(mContext, tocken);
					} catch (Exception e) {
						e.printStackTrace();
					}

					/*** For News Feed Sync **/
					count[0] = (fragmentNewsFeeds != null) ? fragmentNewsFeeds.getUpdateNewsCount() : "";
					/*** For News Feed Sync **/

					/*** For Jobs Feed Sync **/
					count[1] = (fragmentJobs != null) ? fragmentJobs.getUpdateJobsCount() : "";
					/*** For Jobs Feed Sync **/

					count[2] = "0";

					handler.sendEmptyMessage(0);
				}

			} catch (Exception e) {
				e.getMessage();
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				if (count != null) {
					String newsCount = count[0];
					String jobCount = count[1];
					String meetupcount = count[2];

					if (newsCount != null && !newsCount.equals("") && !newsCount.equals("0")) {
						txt_countNews.setVisibility(View.VISIBLE);
						txt_countNews.setText(newsCount);
					} else {
						txt_countNews.setVisibility(View.GONE);
					}

					if (jobCount != null && !jobCount.equals("") && !jobCount.equals("0")) {
						txt_countJobs.setVisibility(View.VISIBLE);
						txt_countJobs.setText(jobCount);
					} else {
						txt_countJobs.setVisibility(View.GONE);
					}

					if (meetupcount != null && !meetupcount.equals("") && !meetupcount.equals("0")) {
						txt_countmetup.setVisibility(View.VISIBLE);
						txt_countmetup.setText(meetupcount);
					} else {
						txt_countmetup.setVisibility(View.GONE);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/********************************************** End Sync service ***********************************************************/

	private void loadFilterData() {
		if (Util.hasConnection(mContext)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						mFilterDataSets = new FilterJobParser().parse(mContext, tocken);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
	
	/**
	 * Searche Event
	 */
	private SearchParser mParser=null;
	private void searchEvent(String searchString){
		
		if (searchString.length()==0) {
			Util.ShowToast(mContext, "Please enter some character.");
			return;
		}
		
		if (!Util.hasConnection(mContext)) {
			Util.ShowToast(mContext, getResources().getString(R.string.internetconnection_msg));
			return;
		}
		
		if (mParser==null) {
			mParser=new SearchParser();
		}
		mParser.setSearchparserinterface(new SearchParserInterface() {
			
			@Override
			public void OnSuccess(ArrayList<JobDetails> jobList) {
				
			}
			
			@Override
			public void OnError() {
				
			}
		});
		mParser.parse(mContext, mParser.getBody(searchString),tocken, true);
	}
}