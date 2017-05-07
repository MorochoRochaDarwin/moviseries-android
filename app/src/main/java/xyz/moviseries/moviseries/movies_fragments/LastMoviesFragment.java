package xyz.moviseries.moviseries.movies_fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
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
import xyz.moviseries.moviseries.bottom_sheets.BottomSheetMovieOptions;
import xyz.moviseries.moviseries.models.Movie;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class LastMoviesFragment extends Fragment implements MoviesAdapter.MovieOnclickListener {
    private Context context;
    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private ArrayList<MovieQualities> movies = new ArrayList<>();

    private ProgressBar progressBar;
    private LinearLayout home;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    private  int gridsP=1, gridsL=2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_last_movies, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        home = (LinearLayout) rootView.findViewById(R.id.home);
        adapter = new MoviesAdapter(context, movies);
        adapter.setMovieOnclickListener(this);


        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;


        switch(screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                gridsL=3;
                gridsP=2;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                gridsL=2;
                gridsP=1;
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                gridsL=2;
                gridsP=1;
                break;
            default:
                gridsL=2;
                gridsP=1;
        }

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait Mode
            recyclerView.setLayoutManager(new GridLayoutManager(context, gridsP));
        } else {
            // Landscape Mode
            recyclerView.setLayoutManager(new GridLayoutManager(context, gridsL));
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
            recyclerView.setLayoutManager(new GridLayoutManager(context, gridsL));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(context, gridsP));
        }

    }

    @Override
    public void MovieOptionsClick(MovieQualities movie) {
        Bundle args = new Bundle();
        args.putString(BottomSheetMovieOptions.MOVIE_ID, movie.getMovie().getMovie_id());
        args.putString(BottomSheetMovieOptions.TRAILER, movie.getMovie().getTrailer());
        BottomSheetDialogFragment bottomSheet = BottomSheetMovieOptions.newInstance(args);
        bottomSheet.show(getActivity().getSupportFragmentManager(), "BSDialog");
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
