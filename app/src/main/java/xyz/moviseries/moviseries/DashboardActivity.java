package xyz.moviseries.moviseries;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.moviseries.moviseries.adapters.CategoriasAdapter;
import xyz.moviseries.moviseries.api_clients.MoviseriesApiClient;
import xyz.moviseries.moviseries.api_services.MoviseriesApiService;
import xyz.moviseries.moviseries.custom_views.DMTextView;
import xyz.moviseries.moviseries.fragments.BottomSheetSerie;
import xyz.moviseries.moviseries.models.Category;
import xyz.moviseries.moviseries.models.Serie;
import xyz.moviseries.moviseries.movies_fragments.LastMoviesFragment;
import xyz.moviseries.moviseries.movies_fragments.LastSeriesFragment;
import xyz.moviseries.moviseries.movies_fragments.SearchMovieFragment;
import xyz.moviseries.moviseries.movies_fragments.TopMoviesFragment;

public class DashboardActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener,
        CategoriasAdapter.OnCategoryClickListener {

    private Toolbar toolbar;
    private ArrayList<Category> categories = new ArrayList<>();
    private CategoriasAdapter categoriasAdapter;
    private boolean isLoadCategories;


    private static int SEE = 0;
    private static final int SEE_MOVIES = 0;
    private static final int SEE_SERIES = 1;
    private static final int SEARCH_MOVIES = 2;
    private static final int SEARCH_SERIES = 3;
    private String category = "Todas las categorias";
    private DMTextView textViewToolbar;

    private Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        textViewToolbar = (DMTextView) findViewById(R.id.text_toolbar);

        categories.add(new Category("Todas las categorias"));
        categoriasAdapter = new CategoriasAdapter(context, categories);
        categoriasAdapter.setOnCategoryClickListener(this);

        RecyclerView recyclerViewCategorias = (RecyclerView) findViewById(R.id.menuList);
        recyclerViewCategorias.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewCategorias.setAdapter(categoriasAdapter);
        if (!isLoadCategories) {
            new LoadCategories().execute();
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_home, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        initDrawer();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                checkFolder();
            }
        } else {
            checkFolder();
        }

    }

    private int REQUEST_CODE = 100;

    private void checkFolder() {
        File f = new File(Environment.getExternalStorageDirectory() + "/Moviseries/");
        if (!f.exists())
            f.mkdirs();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("P60", "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
            checkFolder();

        } else {
            Toast.makeText(context, "ERROR no podra realizar descargas", Toast.LENGTH_SHORT).show();
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

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }


        // Get the search close button image view
        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);

        // Set on click listener
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });


        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                if (b) {
                    if (SEE == SEE_MOVIES) {
                        fragment = new SearchMovieFragment();
                        transaction.replace(R.id.fragment_content, fragment,"searchMovie");
                    } else {

                    }
                    transaction.commit();
                } else {

                    if (SEE == SEE_MOVIES) {
                        if (category.equals("Todas las categorias"))
                            textViewToolbar.setText("Ultimas Películas");
                        else
                            textViewToolbar.setText("Películas - " + category);
                        Bundle bundle = new Bundle();
                        bundle.putString(LastMoviesFragment.CATEGORY_NAME, category);
                        transaction.replace(R.id.fragment_content, LastMoviesFragment.newInstance(bundle),"movies");
                    } else {
                        if (category.equals("Todas las categorias"))
                            textViewToolbar.setText("Ultimas Series");
                        else
                            textViewToolbar.setText("Series - " + category);
                        Bundle bundle = new Bundle();
                        bundle.putString(LastSeriesFragment.CATEGORY_NAME, category);
                        transaction.replace(R.id.fragment_content, LastSeriesFragment.newInstance(bundle),"series");
                    }

                    transaction.commit();
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (SEE == SEE_MOVIES) {

                    SearchMovieFragment fragment = (SearchMovieFragment) getSupportFragmentManager().findFragmentByTag("searchMovie");
                    fragment.search(newText);
                } else {

                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void initDrawer() {

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setScrimColor(getResources().getColor(android.R.color.transparent));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (i == 0) {
            if (category.equals("Todas las categorias"))
                textViewToolbar.setText("Ultimas Películas");
            else
                textViewToolbar.setText("Películas - " + category);
            SEE = SEE_MOVIES;
            Bundle bundle = new Bundle();
            bundle.putString(LastMoviesFragment.CATEGORY_NAME, this.category);
            transaction.replace(R.id.fragment_content, LastMoviesFragment.newInstance(bundle),"movies");
        } else {
            if (category.equals("Todas las categorias"))
                textViewToolbar.setText("Ultimas Series");
            else
                textViewToolbar.setText("Series - " + category);
            SEE = SEE_SERIES;
            Bundle bundle = new Bundle();
            bundle.putString(LastSeriesFragment.CATEGORY_NAME, this.category);
            transaction.replace(R.id.fragment_content, LastSeriesFragment.newInstance(bundle),"series");
        }
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
            transaction.replace(R.id.fragment_content, LastMoviesFragment.newInstance(bundle),"movies");
        } else {
            if (this.category.equals("Todas las categorias"))
                textViewToolbar.setText("Ultimas Series");
            else
                textViewToolbar.setText("Series - " + this.category);

            Bundle bundle = new Bundle();
            bundle.putString(LastSeriesFragment.CATEGORY_NAME, this.category);
            transaction.replace(R.id.fragment_content, LastSeriesFragment.newInstance(bundle),"series");
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
