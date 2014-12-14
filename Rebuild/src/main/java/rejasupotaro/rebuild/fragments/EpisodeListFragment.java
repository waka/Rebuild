package rejasupotaro.rebuild.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rejasupotaro.rebuild.R;
import rejasupotaro.rebuild.api.RssFeedClient;
import rejasupotaro.rebuild.data.adapters.EpisodeListAdapter;
import rejasupotaro.rebuild.data.models.Episode;
import rejasupotaro.rebuild.dialogs.EpisodeDownloadDialog;
import rejasupotaro.rebuild.events.BusProvider;
import rejasupotaro.rebuild.events.ClearEpisodeCacheEvent;
import rejasupotaro.rebuild.events.DownloadEpisodeCompleteEvent;
import rejasupotaro.rebuild.events.LoadEpisodeListCompleteEvent;
import rejasupotaro.rebuild.tools.MainThreadExecutor;
import uk.me.lewisdeane.ldialogs.CustomDialog;

public class EpisodeListFragment extends Fragment {

    @InjectView(R.id.episode_list)
    RecyclerView episodeListView;

    private RssFeedClient rssFeedClient = new RssFeedClient();
    private MainThreadExecutor mainThreadExecutor = new MainThreadExecutor();
    private EpisodeListAdapter episodeListAdapter;
    private OnEpisodeSelectListener listener;

    public static interface OnEpisodeSelectListener {

        public void onSelect(Episode episode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (OnEpisodeSelectListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BusProvider.getInstance().register(this);
        View view = inflater.inflate(R.layout.fragment_episode_list, container);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupListView();
        requestFeed();
    }

    @Override
    public void onDestroyView() {
        BusProvider.getInstance().unregister(this);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void setupListView() {
        episodeListView.setHasFixedSize(false);
        episodeListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        episodeListAdapter = new EpisodeListAdapter(new EpisodeListAdapter.OnItemClickListener() {
            @Override
            public void onClick(Episode episode) {
                listener.onSelect(episode);
            }

            @Override
            public void onDownloadButtonClick(Episode episode) {
                CustomDialog dialog = EpisodeDownloadDialog.newInstance(getActivity(), episode);
                dialog.show();
            }
        });
        episodeListView.setAdapter(episodeListAdapter);
    }

    private void requestFeed() {
        rssFeedClient.request(new RssFeedClient.EpisodeClientResponseHandler() {
            @Override
            public void onSuccess(final List<Episode> episodeList) {
                mainThreadExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        BusProvider.getInstance()
                                .post(new LoadEpisodeListCompleteEvent(episodeList));
                        setupEpisodeListView(episodeList);
                    }
                });
            }

            @Override
            public void onFailure() {
                // do nothing
            }
        });
    }

    public void setupEpisodeListView(List<Episode> episodes) {
        episodeListAdapter.setEpisodes(episodes);
    }

    @Subscribe
    public void onEpisodeDownloadComplete(final DownloadEpisodeCompleteEvent event) {
        mainThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                requestFeed();
            }
        });
    }

    @Subscribe
    public void onEpisodeCacheCleared(final ClearEpisodeCacheEvent event) {
        mainThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                episodeListAdapter.notifyDataSetChanged();
            }
        });
    }
}
