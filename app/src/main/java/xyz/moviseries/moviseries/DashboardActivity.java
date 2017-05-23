package xyz.moviseries.moviseries;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.moviseries.moviseries.adapters.AlfabetoAdapter;
import xyz.moviseries.moviseries.adapters.CategoriasAdapter;
import xyz.moviseries.moviseries.api_clients.MoviseriesApiClient;
import xyz.moviseries.moviseries.api_services.MoviseriesApiService;
import xyz.moviseries.moviseries.custom_views.DMTextView;
import xyz.moviseries.moviseries.downloads.DownloadListActivity;
import xyz.moviseries.moviseries.models.Category;
import xyz.moviseries.moviseries.movies_fragments.LastMoviesFragment;
import xyz.moviseries.moviseries.movies_fragments.LastSeasonsFragment;
import xyz.moviseries.moviseries.movies_fragments.LastSeriesFragment;
import xyz.moviseries.moviseries.movies_fragments.SearchMovieFragment;
import xyz.moviseries.moviseries.movies_fragments.SearchSerieFragment;
import xyz.moviseries.moviseries.movies_fragments.TopMoviesFragment;

public class DashboardActivity extends BaseActivity
        implements
        CategoriasAdapter.OnCategoryClickListener, AlfabetoAdapter.OnClickLetraListener, View.OnClickListener {

    private String letra = "none";

    private Toolbar toolbar;
    private ArrayList<Category> categories = new ArrayList<>();
    private CategoriasAdapter categoriasAdapter;
    private boolean isLoadCategories;


    private static int SEE = 0;
    private static final int SEE_MOVIES = 0;
    private static final int SEE_SERIES = 1;
    private String category = "Todas las categorias";
    private DMTextView textViewToolbar;

    private boolean buscando;

    private Fragment fragment;

    private LinearLayout content_search, content_title;
    private ImageButton btn_search, btn_close_search;
    private EditText editTextSearch;
    private RecyclerView recyclerViewAlfabeto;


    private AlfabetoAdapter alfabetoAdapter;

    private LinearLayout btn_movies, btn_series, btn_topmovies, btn_topseries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        textViewToolbar = (DMTextView) findViewById(R.id.text_toolbar);
        content_search = (LinearLayout) findViewById(R.id.content_search);
        content_title = (LinearLayout) findViewById(R.id.content_title);
        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_close_search = (ImageButton) findViewById(R.id.btn_close_search);
        editTextSearch = (EditText) findViewById(R.id.edit_search);
        recyclerViewAlfabeto = (RecyclerView) findViewById(R.id.recyclerView);

        btn_movies = (LinearLayout) findViewById(R.id.movies);
        btn_topmovies = (LinearLayout) findViewById(R.id.topMovies);
        btn_series = (LinearLayout) findViewById(R.id.series);
        btn_topseries = (LinearLayout) findViewById(R.id.topSeries);

        alfabetoAdapter = new AlfabetoAdapter(context);
        alfabetoAdapter.setOnClickLetraListener(this);
        recyclerViewAlfabeto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewAlfabeto.setAdapter(alfabetoAdapter);

        categories.add(new Category("Todas las categorias"));
        categoriasAdapter = new CategoriasAdapter(context, categories);
        categoriasAdapter.setOnCategoryClickListener(this);

        RecyclerView recyclerViewCategorias = (RecyclerView) findViewById(R.id.menuList);
        recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewCategorias.setAdapter(categoriasAdapter);


        btn_movies.setOnClickListener(this);
        btn_topmovies.setOnClickListener(this);
        btn_series.setOnClickListener(this);
        btn_topseries.setOnClickListener(this);


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscando = true;
                content_title.setVisibility(View.GONE);
                content_search.setVisibility(View.VISIBLE);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (SEE == SEE_MOVIES) {
                    editTextSearch.setHint("Buscar en películas");
                    fragment = new SearchMovieFragment();
                    transaction.replace(R.id.fragment_content, fragment, "searchMovie");
                } else {
                    editTextSearch.setHint("Buscar en series");
                    fragment = new SearchSerieFragment();
                    transaction.replace(R.id.fragment_content, fragment, "searchSerie");
                }
                transaction.commit();

            }
        });


        btn_close_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscando = false;

                content_search.setVisibility(View.GONE);
                content_title.setVisibility(View.VISIBLE);

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (SEE == SEE_MOVIES) {
                    if (category.equals("Todas las categorias"))
                        textViewToolbar.setText("Ultimas Películas");
                    else
                        textViewToolbar.setText("Películas - " + category);
                    Bundle bundle = new Bundle();
                    bundle.putString(LastMoviesFragment.CATEGORY_NAME, category);
                    transaction.replace(R.id.fragment_content, LastMoviesFragment.newInstance(bundle), "movies");
                } else {
                    if (category.equals("Todas las categorias"))
                        textViewToolbar.setText("Ultimas Series");
                    else
                        textViewToolbar.setText("Series - " + category);
                    Bundle bundle = new Bundle();
                    bundle.putString(LastSeriesFragment.CATEGORY_NAME, category);
                    transaction.replace(R.id.fragment_content, LastSeriesFragment.newInstance(bundle), "series");
                }

                transaction.commit();

            }
        });


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (buscando) {
                    if (SEE == SEE_MOVIES) {

                        SearchMovieFragment fragment = (SearchMovieFragment) getSupportFragmentManager().findFragmentByTag("searchMovie");
                        fragment.search(s.toString());
                    } else {
                        SearchSerieFragment fragment = (SearchSerieFragment) getSupportFragmentManager().findFragmentByTag("searchSerie");
                        fragment.search(s.toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initDrawer();

        checkWriteSDCard();

        if (!isLoadCategories) {
            new LoadCategories().execute();
        }


    }

    private int REQUEST_CODE_W = 100;

    private void checkFolder() {
        File f = new File(Environment.getExternalStorageDirectory() + "/Moviseries/");
        if (!f.exists())
            f.mkdirs();
    }

    private void checkWriteSDCard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_W);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_W) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v("P60", "Permission: " + permissions[0] + "was " + grantResults[0]);

            } else {
                Toast.makeText(context, "ERROR no podra realizar descargas", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_downloads) {
            startActivity(new Intent(context, DownloadListActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initDrawer() {

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setScrimColor(getResources().getColor(android.R.color.transparent));

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                coordinatorLayout.setTranslationX(slideOffset * drawerView.getWidth());
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        toggle.syncState();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String nombre_usuario = preferences.getString(getString(R.string.usuario_nombre), "none");
        String email_usuario = preferences.getString(getString(R.string.usuario_email), "none");
        String login_usuario = preferences.getString(getString(R.string.preferencias_login), "none");
        String tipo = preferences.getString(getString(R.string.usuario_tipo), "free");
        String usuario_id = preferences.getString(getString(R.string.usuario_id), "-1");


        TextView textViewUserName = (TextView) findViewById(R.id.username);
        TextView textViewUserEmail = (TextView) findViewById(R.id.email);
        TextView textViewUserType = (TextView) findViewById(R.id.type);
        TextView textViewUserID = (TextView) findViewById(R.id.user_id);

        textViewUserName.setText(nombre_usuario);
        textViewUserEmail.setText(email_usuario);
        textViewUserID.setText("Usuario ID: " + usuario_id);
        textViewUserType.setText("Cuenta: " + tipo);


        Bundle bundle = new Bundle();
        if (category.equals("Todas las categorias"))
            textViewToolbar.setText("Ultimas Películas");
        else
            textViewToolbar.setText("Películas - " + category);
        SEE = SEE_MOVIES;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        bundle.putString(LastMoviesFragment.CATEGORY_NAME, this.category);
        bundle.putString(LastMoviesFragment.LETRA, this.letra);
        transaction.replace(R.id.fragment_content, LastMoviesFragment.newInstance(bundle), "movies");
        transaction.commit();
    }


    /**
     * cuando se da click en una categoria del nav
     *
     * @param category
     */
    @Override
    public void onCategoryClick(Category category) {


        this.category = category.getCategory_name();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (SEE == SEE_MOVIES) {
            if (this.category.equals("Todas las categorias"))
                textViewToolbar.setText("Ultimas Películas");
            else
                textViewToolbar.setText("Películas - " + this.category);

            Bundle bundle = new Bundle();
            bundle.putString(LastMoviesFragment.CATEGORY_NAME, this.category);
            bundle.putString(LastMoviesFragment.LETRA, this.letra);
            transaction.replace(R.id.fragment_content, LastMoviesFragment.newInstance(bundle), "movies");
        } else {
            if (this.category.equals("Todas las categorias"))
                textViewToolbar.setText("Ultimas Series");
            else
                textViewToolbar.setText("Series - " + this.category);

            Bundle bundle = new Bundle();
            bundle.putString(LastSeriesFragment.CATEGORY_NAME, this.category);
            bundle.putString(LastSeriesFragment.LETRA, this.letra);
            transaction.replace(R.id.fragment_content, LastSeriesFragment.newInstance(bundle), "series");
        }
        transaction.commit();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onClickLetra(String letra) {

        if (alfabetoAdapter.isSelect()) {
            this.letra = letra;
        } else {
            this.letra = "none";
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (SEE == SEE_MOVIES) {
            if (this.category.equals("Todas las categorias"))
                textViewToolbar.setText("Ultimas Películas");
            else
                textViewToolbar.setText("Películas - " + this.category);

            Bundle bundle = new Bundle();
            bundle.putString(LastMoviesFragment.CATEGORY_NAME, this.category);
            bundle.putString(LastMoviesFragment.LETRA, this.letra);
            transaction.replace(R.id.fragment_content, LastMoviesFragment.newInstance(bundle), "movies");
        } else {
            if (this.category.equals("Todas las categorias"))
                textViewToolbar.setText("Ultimas Series");
            else
                textViewToolbar.setText("Series - " + this.category);

            Bundle bundle = new Bundle();
            bundle.putString(LastSeriesFragment.CATEGORY_NAME, this.category);
            bundle.putString(LastSeriesFragment.LETRA, this.letra);
            transaction.replace(R.id.fragment_content, LastSeriesFragment.newInstance(bundle), "series");
        }
        transaction.commit();


    }

    @Override
    public void onClick(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int id = view.getId();
        Bundle bundle = new Bundle();
        switch (id) {
            case R.id.movies:
                if (category.equals("Todas las categorias"))
                    textViewToolbar.setText("Ultimas Películas");
                else
                    textViewToolbar.setText("Películas - " + category);
                SEE = SEE_MOVIES;

                bundle.putString(LastMoviesFragment.CATEGORY_NAME, this.category);
                bundle.putString(LastMoviesFragment.LETRA, this.letra);
                transaction.replace(R.id.fragment_content, LastMoviesFragment.newInstance(bundle), "movies");
                break;
            case R.id.series:
                if (category.equals("Todas las categorias"))
                    textViewToolbar.setText("Ultimas Series");
                else
                    textViewToolbar.setText("Series - " + category);
                SEE = SEE_SERIES;

                bundle.putString(LastSeriesFragment.CATEGORY_NAME, this.category);
                bundle.putString(LastSeriesFragment.LETRA, this.letra);
                transaction.replace(R.id.fragment_content, LastSeriesFragment.newInstance(bundle), "series");
                break;

            case R.id.topMovies:
                textViewToolbar.setText("Top 20 Películas");
                transaction.replace(R.id.fragment_content, new TopMoviesFragment(), "topMovies");
                break;

            case R.id.topSeries:
                textViewToolbar.setText("Series Actualizadas");
                transaction.replace(R.id.fragment_content, new LastSeasonsFragment(), "SeriesActua");
                break;
        }

        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    private class LoadCategories extends AsyncTask<Void, Void, Void> implements Callback<List<Category>> {
        private String url = "http://moviseries.xyz/android/categories";

        @Override
        protected Void doInBackground(Void... voids) {
            MoviseriesApiService apiService = MoviseriesApiClient.getClient().create(MoviseriesApiService.class);
            Call<List<Category>> call = apiService.getCategories(url);
            call.enqueue(this);
            return null;
        }

        @Override
        public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

            isLoadCategories = true;
            if (response.body() != null) {
                categories.addAll(response.body());
                int n = categories.size();
                if (n > 0) {
                    categoriasAdapter.notifyItemRangeInserted(0, n);
                    categoriasAdapter.notifyDataSetChanged();
                }

            }
        }

        @Override
        public void onFailure(Call<List<Category>> call, Throwable t) {
            isLoadCategories = true;
        }
    }


}
