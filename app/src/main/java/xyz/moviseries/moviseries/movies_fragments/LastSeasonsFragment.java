package xyz.moviseries.moviseries.movies_fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.SeasonActivity;
import xyz.moviseries.moviseries.adapters.SeasonAdapter;
import xyz.moviseries.moviseries.adapters.SeriesAdapter;
import xyz.moviseries.moviseries.api_clients.MoviseriesApiClient;
import xyz.moviseries.moviseries.api_services.MoviseriesApiService;
import xyz.moviseries.moviseries.models.Season;
import xyz.moviseries.moviseries.models.Serie;

/**
 * Created by DARWIN on 7/5/2017.
 */

public class LastSeasonsFragment extends Fragment implements SeasonAdapter.OnSeasonClickListener {
    private Context context;
    private RecyclerView recyclerView;
    private SeasonAdapter adapter;
    private ArrayList<Season> seasons = new ArrayList<>();

    private ProgressBar progressBar;
    private LinearLayout home;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        home = (LinearLayout) rootView.findViewById(R.id.home);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        adapter = new SeasonAdapter(context, seasons);
        adapter.setOnSeasonClickListener(this);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait Mode
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        } else {
            // Landscape Mode
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        }

        recyclerView.setAdapter(adapter);

        new Load().execute();
        return rootView;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        }

    }

    @Override
    public void OnCLickSeason(Season season) {
        Intent intent = new Intent(context, SeasonActivity.class);
        intent.putExtra(SeasonActivity.SEASON_ID, season.getSeason_id());
        intent.putExtra(SeasonActivity.SEASON_NUMBER, season.getNumber());
        intent.putExtra(SeasonActivity.SERIE_NAME, season.getSerie_name());
        intent.putExtra(SeasonActivity.COVER, season.getCover());
        intent.putExtra(SeasonActivity.TRAILER, season.getTrailer());

        startActivity(intent);
    }

    private class Load extends AsyncTask<Void, Void, Void> implements Callback<List<Season>> {
        private String url = "http://moviseries.xyz/android/last-seasons";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            seasons.clear();
            home.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MoviseriesApiService apiService = MoviseriesApiClient.getClient().create(MoviseriesApiService.class);
            Call<List<Season>> call = apiService.getLastSeasons(url);
            call.enqueue(this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }


        @Override
        public void onResponse(Call<List<Season>> call, Response<List<Season>> response) {

            if (response != null) {
                seasons.addAll(response.body());
                int n = seasons.size();
                //Log.i("apimoviseries","tam:"+n);
                if (n > 0) {
                    adapter.notifyItemRangeInserted(0, n);
                    adapter.notifyDataSetChanged();
                }
            }

            progressBar.setVisibility(View.GONE);
            home.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFailure(Call<List<Season>> call, Throwable t) {
            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
