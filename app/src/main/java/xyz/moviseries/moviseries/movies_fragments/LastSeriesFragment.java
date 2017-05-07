package xyz.moviseries.moviseries.movies_fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.moviseries.moviseries.MovieQualities;
import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.adapters.MoviesAdapter;
import xyz.moviseries.moviseries.adapters.SeriesAdapter;
import xyz.moviseries.moviseries.api_clients.MoviseriesApiClient;
import xyz.moviseries.moviseries.api_services.MoviseriesApiService;
import xyz.moviseries.moviseries.models.Serie;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class LastSeriesFragment extends Fragment {
    private Context context;
    private RecyclerView recyclerView;
    private SeriesAdapter adapter;
    private ArrayList<Serie> series = new ArrayList<>();

    private ProgressBar progressBar;

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
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        adapter = new SeriesAdapter(context, series);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait Mode
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        } else {
            // Landscape Mode
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL));
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
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        }

    }

    private class Load extends AsyncTask<Void, Void, Void> implements Callback<List<Serie>> {
        private String url = "http://moviseries.xyz/android/last-series";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            series.clear();
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MoviseriesApiService apiService = MoviseriesApiClient.getClient().create(MoviseriesApiService.class);
            Call<List<Serie>> call = apiService.getLastSeries(url);
            call.enqueue(this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }


        @Override
        public void onResponse(Call<List<Serie>> call, Response<List<Serie>> response) {

            if(response!=null){
                series.addAll(response.body());
                int n = series.size();
                //Log.i("apimoviseries","tam:"+n);
                if (n > 0) {
                    adapter.notifyItemRangeInserted(0, n);
                    adapter.notifyDataSetChanged();
                }
            }

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFailure(Call<List<Serie>> call, Throwable t) {

        }
    }
}
