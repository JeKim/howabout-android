package io.recom.howabout;

import io.recom.howabout.category.adult.fragment.AdultCategoryWrapFragment;
import io.recom.howabout.category.music.activity.MusicPlaylistActivity;
import io.recom.howabout.category.music.activity.SearchedTrackListActivity;
import io.recom.howabout.category.music.fragment.MusicCategoryWrapFragment;
import io.recom.howabout.category.music.service.MusicPlayerService;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;

public class MainActivity extends RoboSherlockFlurryAdlibSpiceFragmentActivity {

	private String[] categoryStrings;

	private String musicPlaylistTitle;
	private String exit;

	AdultCategoryWrapFragment adultCategoryWrapFragment;
	MusicCategoryWrapFragment musicCategoryWrapFragment;

	private MenuItem searchMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		categoryStrings = getResources().getStringArray(R.array.category_list);
		musicPlaylistTitle = getResources().getString(
				R.string.title_activity_music_playlist);
		exit = getResources().getString(R.string.exit);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setDisplayShowTitleEnabled(false);

		// dropdown menu in actionBar.
		final SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(
				this, R.array.category_list,
				android.R.layout.simple_list_item_1);
		actionBar.setListNavigationCallbacks(spinnerAdapter,
				new HowaboutDropdownNavigationListener());

		// set framgents.
		adultCategoryWrapFragment = new AdultCategoryWrapFragment();
		Bundle adultCategoryWrapFragmentBundle = new Bundle();
		adultCategoryWrapFragmentBundle.putString("category", "adult");
		adultCategoryWrapFragment.setArguments(adultCategoryWrapFragmentBundle);

		musicCategoryWrapFragment = new MusicCategoryWrapFragment();
		Bundle musicCategoryWrapFragmentBundle = new Bundle();
		musicCategoryWrapFragmentBundle.putString("category", "music");
		musicCategoryWrapFragment.setArguments(musicCategoryWrapFragmentBundle);

		setAdsContainer(R.id.adView);
	}

	@Override
	public void onDestroy() {
		stopService(new Intent(this, MusicPlayerService.class));

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);

		// Place an action bar item for searching.
		final SearchView searchView = new SearchView(getSupportActionBar()
				.getThemedContext());
		searchView.setQueryHint("Search");
		searchView.setIconified(true);

		searchMenu = menu.getItem(0);
		searchMenu.setActionView(searchView).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_ALWAYS
						| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

		searchView
				.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View view,
							boolean queryTextFocused) {
						if (!queryTextFocused) {
							Log.d("searchMenu.collapseActionView();", Boolean
									.toString(searchMenu.collapseActionView()));
							searchView.setQuery("", false);
						}
					}
				});

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String searchKeyword) {
				Intent intent = new Intent(MainActivity.this,
						SearchedTrackListActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				Bundle bundle = new Bundle();
				bundle.putString("category", "music");
				bundle.putString("method", "search");
				bundle.putString("searchKeyword", searchKeyword);
				intent.putExtras(bundle);

				startActivity(intent);

				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return true;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals(musicPlaylistTitle)) {
			Intent intent = new Intent(this, MusicPlaylistActivity.class);
			Bundle bundle = new Bundle();
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);

			return true;
		} else if (item.getTitle().equals(exit)) {
			finish();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
	}

	public class HowaboutDropdownNavigationListener implements
			OnNavigationListener {

		@Override
		public boolean onNavigationItemSelected(int position, long itemId) {
			if (searchMenu != null) {
				searchMenu.setVisible(false);
			}

			Fragment categoryWrapFragment;

			if (position == 0) {
				if (searchMenu != null) {
					searchMenu.setVisible(true);
				}
				categoryWrapFragment = MainActivity.this.musicCategoryWrapFragment;
			} else {
				categoryWrapFragment = MainActivity.this.adultCategoryWrapFragment;
			}

			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.mainContentView, categoryWrapFragment,
							categoryStrings[position]).commit();

			return true;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
