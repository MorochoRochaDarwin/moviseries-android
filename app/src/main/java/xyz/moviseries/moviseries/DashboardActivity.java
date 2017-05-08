package xyz.moviseries.moviseries;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import xyz.moviseries.moviseries.custom_views.DMTextView;
import xyz.moviseries.moviseries.fragments.HomeFragment;
import xyz.moviseries.moviseries.movies_fragments.LastMoviesFragment;
import xyz.moviseries.moviseries.movies_fragments.LastSeriesFragment;
import xyz.moviseries.moviseries.movies_fragments.TopMoviesFragment;

public class DashboardActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    private Toolbar toolbar;
    private ViewPager pager;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, new LastMoviesFragment());
        transaction.commit();


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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
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
        int id = item.getItemId();
        Fragment fragment = null;

        switch (id) {
            case R.id.nav_topm:
                fragment = new TopMoviesFragment();
                break;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, fragment);
        transaction.commit();


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


        View headerView = navigationView.getHeaderView(0);

        TextView textViewUserName = (TextView) headerView.findViewById(R.id.username);
        TextView textViewUserEmail = (TextView) headerView.findViewById(R.id.email);
        TextView textViewUserType = (TextView) headerView.findViewById(R.id.type);
        TextView textViewUserID = (TextView) headerView.findViewById(R.id.user_id);

        textViewUserName.setText(nombre_usuario);
        textViewUserEmail.setText(email_usuario);
        textViewUserID.setText("Usuario ID: " + usuario_id);
        textViewUserType.setText("Cuenta: " + tipo);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (i == 0) {
            transaction.replace(R.id.fragment_content, new LastMoviesFragment());

        } else {
            transaction.replace(R.id.fragment_content, new LastSeriesFragment());
        }
        transaction.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private class MPagerAdapter extends FragmentPagerAdapter {


        public MPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new LastMoviesFragment();
                    break;
                case 1:
                    fragment = new LastSeriesFragment();
                    break;
                default:
                    fragment = new LastMoviesFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 5;
        }


    }
}
