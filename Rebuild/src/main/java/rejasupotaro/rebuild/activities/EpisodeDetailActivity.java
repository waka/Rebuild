package rejasupotaro.rebuild.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import rejasupotaro.rebuild.R;
import rejasupotaro.rebuild.adapters.EpisodeDetailPagerAdapter;
import rejasupotaro.rebuild.fragments.EpisodeMediaFragment;
import rejasupotaro.rebuild.models.Episode;
import rejasupotaro.rebuild.tools.MenuDelegate;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

public class EpisodeDetailActivity extends RoboActionBarActivity {

    private static final String EXTRA_EPISODE_ID = "extra_episode_id";

    private Menu menu;

    @InjectExtra(value = EXTRA_EPISODE_ID)
    private int episodeId;

    private Episode episode;

    @InjectView(R.id.episode_detail_view_pager)
    private ViewPager viewPager;

    @InjectView(R.id.pager_tab_strip)
    private PagerTabStrip pagerTabStrip;

    @Inject
    private MenuDelegate menuDelegate;

    public static Intent createIntent(Context context, int episodeId) {
        Intent intent = new Intent(context, EpisodeDetailActivity.class);
        intent.putExtra(EXTRA_EPISODE_ID, episodeId);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);

        episode = Episode.findById(episodeId);
        setupActionBar(episode);
        EpisodeMediaFragment episodeMediaFragment =
                (EpisodeMediaFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_episode_media);
        episodeMediaFragment.setup(episode);
        setupViewPager(episode);
    }

    private void setupActionBar(Episode episode) {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        String originalTitle = episode.getTitle();
        int startIndex = originalTitle.indexOf(':');
        actionBar.setTitle("Episode " + originalTitle.substring(0, startIndex));
    }

    private void setupViewPager(Episode episode) {
        viewPager.setAdapter(new EpisodeDetailPagerAdapter(
                getSupportFragmentManager(),
                episode));

        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.WHITE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.episode_detail, menu);
        this.menu = menu;
        updateMenuTitles(episode);
        return true;
    }

    private void updateMenuTitles(Episode episode) {
        MenuItem downloadOrClearCacheMenuItem = menu.findItem(R.id.action_download_or_clear_cache);
        if (episode.isDownloaded()) {
            downloadOrClearCacheMenuItem.setTitle(getString(R.string.clear_cache));
        } else {
            downloadOrClearCacheMenuItem.setTitle(getString(R.string.download));
        }
    }

    public void close() {
        finish();
        overridePendingTransition(R.anim.zoom_in, R.anim.slide_down_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        switch (item.getItemId()) {
            case android.R.id.home:
                close();
                break;
            case R.id.action_settings:
                menuDelegate.pressSettings();
                break;
            case R.id.action_share:
                menuDelegate.pressShare(episode);
                break;
            case R.id.action_download_or_clear_cache:
                menuDelegate.pressDownloadOrClearCache(episode);
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
    }
}
