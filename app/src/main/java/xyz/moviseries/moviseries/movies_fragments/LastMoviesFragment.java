package xyz.moviseries.moviseries.movies_fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;
import xyz.moviseries.moviseries.MovieQualities;
import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.adapters.MoviesAdapter;
import xyz.moviseries.moviseries.api_clients.MoviseriesApiClient;
import xyz.moviseries.moviseries.api_services.MoviseriesApiService;
import xyz.moviseries.moviseries.models.Movie;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class LastMoviesFragment extends Fragment {
    private Context context;
    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private ArrayList<MovieQualities> movies = new ArrayList<>();

    private ProgressBar progressBar;
    private LinearLayout home;


    private FragmentTabHost mTabHost;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_last_movies, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        home = (LinearLayout) rootView.findViewById(R.id.home);
        adapter = new MoviesAdapter(context, movies);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(context, getActivity().getSupportFragmentManager(), R.id.realtabcontent);

        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Ultimas Series"),
                LastSeriesFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Series Actualizadas"),
                LastSeasonsFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator("Top Peliculas"),
                TopMoviesFragment.class, null);


        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            mTabHost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.drawable.tab_unselected); // unselected
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(Color.parseColor("#ffffff"));
            tv.setTextSize(10);
        }
        mTabHost.getTabWidget().getChildAt(0)
                .setBackgroundResource(R.drawable.tab_selected); // unselected



        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            public void onTabChanged(String arg0) {

                for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                    mTabHost.getTabWidget().getChildAt(i)
                            .setBackgroundResource(R.drawable.tab_unselected); // unselected
                }

                mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab())
                        .setBackgroundResource(R.drawable.tab_selected); // selected

            }
        });


        new Load().execute();
        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }

    private class Load extends AsyncTask<Void, Void, Void> implements Callback<List<MovieQualities>> {
        private String url = "http://moviseries.xyz/android/last-movies";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            movies.clear();
            home.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MoviseriesApiService apiService = MoviseriesApiClient.getClient().create(MoviseriesApiService.class);
            Call<List<MovieQualities>> call = apiService.getLastMovies(url);
            call.enqueue(this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }


        @Override
        public void onResponse(Call<List<MovieQualities>> call, Response<List<MovieQualities>> response) {


            if (response != null) {
                movies.addAll(response.body());
                int n = movies.size();
                //Log.i("apimoviseries","tam:"+n);
                if (n > 0) {
                    adapter.notifyItemRangeInserted(0, n);
                    adapter.notifyDataSetChanged();

                }
            }


            progressBar.setVisibility(View.GONE);
            home.setVisibility(View.VISIBLE);

            recyclerView.scrollToPosition(0);


        }

        @Override
        public void onFailure(Call<List<MovieQualities>> call, Throwable t) {

        }
    }
}
