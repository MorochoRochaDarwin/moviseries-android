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
import xyz.moviseries.moviseries.adapters.SeriesAdapter;
import xyz.moviseries.moviseries.api_clients.MoviseriesApiClient;
import xyz.moviseries.moviseries.api_services.MoviseriesApiService;
import xyz.moviseries.moviseries.bottom_sheets.BottomSheetSerie;
import xyz.moviseries.moviseries.models.Serie;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class LastSeriesFragment extends Fragment implements SeriesAdapter.OnCLickSerieListener {
    public static final String CATEGORY_NAME = "series.category_name";
    public static final String LETRA = "series.category_letra";
    private String category, letra;
    private Context context;
    private RecyclerView recyclerView;
    private SeriesAdapter adapter;
    private ArrayList<Serie> series = new ArrayList<>();

    private ProgressBar progressBar;
    private int gridsP = 1, gridsL = 2;
    private LinearLayout home;

    private Bundle savedInstanceState;
    private boolean initLoad = false;


    public static LastSeriesFragment newInstance(Bundle bundle) {
        LastSeriesFragment fragment = new LastSeriesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = getActivity();
        category = getArguments().getString(CATEGORY_NAME, "Todas las categorias");
        letra = getArguments().getString(LETRA, "none");
        this.savedInstanceState = savedInstanceState;
    }

    private boolean loading;
    private int limit = 12, offset = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        home = (LinearLayout) rootView.findViewById(R.id.home);
        adapter = new SeriesAdapter(context, series);
        adapter.setOnCLickSerieListener(this);

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
            // Portrait Mode
            recyclerView.setLayoutManager(new GridLayoutManager(context, gridsP));
        } else {
            // Landscape Mode
            recyclerView.setLayoutManager(new GridLayoutManager(context, gridsL));
        }

        recyclerView.setAdapter(adapter);


        Button more = (Button) rootView.findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loading) {//si no hay una tarea pendiente
                    offset += limit;
                    new Load().execute();
                }
            }
        });

        if (initLoad) {
            progressBar.setVisibility(View.GONE);
            home.setVisibility(View.VISIBLE);

        } else {
            new Load().execute();
        }

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
    public void onClickSerie(Serie serie) {
        Bundle args = new Bundle();
        args.putString(BottomSheetSerie.SERIE_ID, serie.getSerie_id());
        args.putString(BottomSheetSerie.NAME, serie.getSerie_name());
        args.putString(BottomSheetSerie.COVER, serie.getCover());
        args.putString(BottomSheetSerie.UPDATE_AT, serie.getCreated_at());
        args.putString(BottomSheetSerie.YEAR, serie.getYear());
        args.putString(BottomSheetSerie.DESCRIPTION, serie.getShort_description());
        BottomSheetDialogFragment bottomSheet = BottomSheetSerie.newInstance(args);
        bottomSheet.show(getActivity().getSupportFragmentManager(), "BSDialog");
    }

    private class Load extends AsyncTask<Void, Void, Void> implements Callback<List<Serie>> {
        private String url;
        private int prev_size = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if (category.equals("Todas las categorias") && category.equals("Todas las categorias") && letra.equals("none")) {
                url = "http://moviseries.xyz/android/last-series/" + limit + "/" + offset;
            } else if (category.equals("Todas las categorias") && !letra.equals("none")) {
                url = "http://moviseries.xyz/android/last-series/" + limit + "/" + offset + "/" + letra;
            } else if (letra.equals("none")) {
                url = "http://moviseries.xyz/android/series/category/" + category.replace(" ", "+") + "/limit_offset/" + limit + "/" + offset;
            } else {
                url = "http://moviseries.xyz/android/series/category/" + category.replace(" ", "+") + "/limit_offset/" + limit + "/" + offset+"/letra/"+letra;
            }





            prev_size = series.size();
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

            if (response != null) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        series.addAll(response.body());
                        int n = series.size();
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
            initLoad = true;
        }

        @Override
        public void onFailure(Call<List<Serie>> call, Throwable t) {
            loading = false;
        }
    }


}
