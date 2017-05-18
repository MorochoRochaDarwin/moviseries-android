package xyz.moviseries.moviseries.movies_fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.moviseries.moviseries.MovieQualities;
import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.adapters.MoviesAdapter;
import xyz.moviseries.moviseries.api_clients.MoviseriesApiClient;
import xyz.moviseries.moviseries.api_services.MoviseriesApiService;
import xyz.moviseries.moviseries.bottom_sheets.BottomSheetMovie;

/**
 * Created by DARWIN on 12/5/2017.
 */

public class SearchMovieFragment extends Fragment implements MoviesAdapter.MovieOnclickListener {

    public static final String CATEGORY_NAME = "movies.category_name";
    private String category;

    private Context context;
    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private ArrayList<MovieQualities> movies = new ArrayList<>();

    private ProgressBar progressBar;
    private LinearLayout home;


    private Load load_task;

    private String query = "";


    public void search(String query) {
        movies.clear();
        if (adapter.getItemCount() > 0) {
            adapter.notifyItemRangeRemoved(0, adapter.getItemCount());
            adapter.notifyDataSetChanged();
        }
        if (query.trim().length() > 2) {
            this.query = query;
            if (load_task != null) {
                if (load_task.getStatus() == AsyncTask.Status.RUNNING) {
                    load_task.cancel(true);
                    load_task = null;
                }
            }
            load_task = new Load();
            load_task.execute();
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    private int gridsP = 1, gridsL = 2;
    private boolean loading;
    private GridLayoutManager glm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_last_movies, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        home = (LinearLayout) rootView.findViewById(R.id.home);
        adapter = new MoviesAdapter(context, movies);
        adapter.setMovieOnclickListener(this);

        progressBar.setVisibility(View.GONE);


        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;


        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                gridsL = 5;
                gridsP = 4;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                gridsL = 4;
                gridsP = 3;
                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                gridsL = 3;
                gridsP = 2;
                break;
            default:
                gridsL = 2;
                gridsP = 1;
        }


        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            glm = new GridLayoutManager(context, gridsP);
        } else {
            // Landscape Mode
            glm = new GridLayoutManager(context, gridsL);

        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(glm);


        Button more = (Button) rootView.findViewById(R.id.more);
        more.setVisibility(View.GONE);


        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            glm = new GridLayoutManager(context, gridsL);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            glm = new GridLayoutManager(context, gridsP);

        }
        recyclerView.setLayoutManager(glm);

    }

    @Override
    public void MovieOptionsClick(MovieQualities movie, String qualities) {
        Bundle args = new Bundle();
        args.putString(BottomSheetMovie.MOVIE_ID, movie.getMovie().getMovie_id());
        args.putString(BottomSheetMovie.NAME, movie.getMovie().getName());
        args.putString(BottomSheetMovie.TRAILER, movie.getMovie().getTrailer());
        args.putString(BottomSheetMovie.COVER, movie.getMovie().getCover());
        args.putString(BottomSheetMovie.UPDATE_AT, movie.getMovie().getUpdated_at());
        args.putString(BottomSheetMovie.DESCRIPTION, movie.getMovie().getShort_description());
        args.putString(BottomSheetMovie.QUALITIES, qualities);
        args.putString(BottomSheetMovie.YEAR, movie.getMovie().getYear());
        BottomSheetDialogFragment bottomSheet = BottomSheetMovie.newInstance(args);
        bottomSheet.show(getActivity().getSupportFragmentManager(), "BSDialog");
    }


    private class Load extends AsyncTask<Void, Void, Void> implements Callback<List<MovieQualities>> {

        private String url;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);
            home.setVisibility(View.GONE);
            try {
                url = "http://moviseries.xyz/android/search-movie/" + URLEncoder.encode(query, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return;
            }

            Log.i("search url", url);
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
            Log.i("search response", response.body().toString());

            if (response != null) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        movies.addAll(response.body());
                        int n = movies.size();
                        //Log.i("apimoviseries","tam:"+n);
                        if (n > 0) {
                            adapter.notifyItemRangeInserted(0, n);
                            adapter.notifyDataSetChanged();

                        }
                    }
                }

            }


            progressBar.setVisibility(View.GONE);
            home.setVisibility(View.VISIBLE);


        }

        @Override
        public void onFailure(Call<List<MovieQualities>> call, Throwable t) {

        }
    }
}
