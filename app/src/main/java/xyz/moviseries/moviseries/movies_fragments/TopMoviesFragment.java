package xyz.moviseries.moviseries.movies_fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.adapters.TopMoviesAdapter;
import xyz.moviseries.moviseries.api_clients.MoviseriesApiClient;
import xyz.moviseries.moviseries.api_services.MoviseriesApiService;
import xyz.moviseries.moviseries.bottom_sheets.BottomSheetMovie;
import xyz.moviseries.moviseries.models.TopMovie;

/**
 * Created by DARWIN on 7/5/2017.
 */

public class TopMoviesFragment extends Fragment implements TopMoviesAdapter.OnTopMovieListener {
    private Context context;
    private RecyclerView recyclerView;
    private TopMoviesAdapter adapter;
    private ArrayList<TopMovie> movies = new ArrayList<>();
    private LinearLayout home;
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
        recyclerView.setNestedScrollingEnabled(false);
        home = (LinearLayout) rootView.findViewById(R.id.home);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        adapter = new TopMoviesAdapter(context, movies);
        adapter.setOnTopMovieListener(this);
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait Mode
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        } else {
            // Landscape Mode
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        }
        recyclerView.setAdapter(adapter);
        Button more = (Button) rootView.findViewById(R.id.more);
        more.setVisibility(View.GONE);


        new Load().execute();
        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        }
    }

    @Override
    public void onClickTopMovie(TopMovie movie) {
        Bundle args = new Bundle();
        args.putString(BottomSheetMovie.MOVIE_ID, movie.getMovie_id());
        args.putString(BottomSheetMovie.NAME, movie.getName());
        args.putString(BottomSheetMovie.TRAILER, movie.getTrailer());
        args.putString(BottomSheetMovie.COVER, movie.getCover());
        args.putString(BottomSheetMovie.UPDATE_AT, movie.getUpdated_at());
        args.putString(BottomSheetMovie.DESCRIPTION, movie.getShort_description());
        args.putString(BottomSheetMovie.YEAR, movie.getYear());
        BottomSheetDialogFragment bottomSheet = BottomSheetMovie.newInstance(args);
        bottomSheet.show(getActivity().getSupportFragmentManager(), "BSDialog");
    }

    private class Load extends AsyncTask<Void, Void, Void> implements Callback<List<TopMovie>> {
        private String url = "http://moviseries.xyz/android/top-movies";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            movies.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MoviseriesApiService apiService = MoviseriesApiClient.getClient().create(MoviseriesApiService.class);
            Call<List<TopMovie>> call = apiService.getTopMovies(url);
            call.enqueue(this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }


        @Override
        public void onResponse(Call<List<TopMovie>> call, Response<List<TopMovie>> response) {


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


        }

        @Override
        public void onFailure(Call<List<TopMovie>> call, Throwable t) {

        }
    }
}
