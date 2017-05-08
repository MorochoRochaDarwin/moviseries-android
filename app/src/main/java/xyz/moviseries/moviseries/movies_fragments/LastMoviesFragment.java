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
import android.widget.Button;
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

    private int gridsP = 1, gridsL = 2;
    private boolean loading;
    private GridLayoutManager glm;

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



        new Load().execute();


        Button more=(Button)rootView.findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loading) {//si no hay una tarea pendiente
                    offset += 10;
                    new Load().execute();
                }
            }
        });






        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            glm=new GridLayoutManager(context, gridsL);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            glm=new GridLayoutManager(context, gridsP);

        }
        recyclerView.setLayoutManager(glm);

    }

    @Override
    public void MovieOptionsClick(MovieQualities movie,String qualities) {
        Bundle args = new Bundle();
        args.putString(BottomSheetMovieOptions.MOVIE_ID, movie.getMovie().getMovie_id());
        args.putString(BottomSheetMovieOptions.NAME, movie.getMovie().getName());
        args.putString(BottomSheetMovieOptions.TRAILER, movie.getMovie().getTrailer());
        args.putString(BottomSheetMovieOptions.COVER, movie.getMovie().getCover());
        args.putString(BottomSheetMovieOptions.UPDATE_AT, movie.getMovie().getUpdated_at());
        args.putString(BottomSheetMovieOptions.DESCRIPTION, movie.getMovie().getShort_description());
        args.putString(BottomSheetMovieOptions.QUALITIES, qualities);
        BottomSheetDialogFragment bottomSheet = BottomSheetMovieOptions.newInstance(args);
        bottomSheet.show(getActivity().getSupportFragmentManager(), "BSDialog");
    }

    private int limit = 10, offset = 0;

    private class Load extends AsyncTask<Void, Void, Void> implements Callback<List<MovieQualities>> {
        private String url = "http://moviseries.xyz/android/last-movies/" + limit + "/" + offset;

        private int prev_size = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prev_size = movies.size();
            loading = true;
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
                if(response.isSuccessful()){
                    if(response.body()!=null){
                        movies.addAll(response.body());
                        int n = movies.size();
                        //Log.i("apimoviseries","tam:"+n);
                        if (n > prev_size) {
                            adapter.notifyItemRangeInserted(prev_size - 1, n);
                            adapter.notifyDataSetChanged();

                        }
                    }
                }

            }


            progressBar.setVisibility(View.GONE);
            home.setVisibility(View.VISIBLE);

            loading = false;

        }

        @Override
        public void onFailure(Call<List<MovieQualities>> call, Throwable t) {
            loading = false;
        }
    }
}
