package xyz.moviseries.moviseries;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.Locale;

/**
 * Created by Darwin Morocho on 27/3/2017.
 */

public class InitActivity extends Activity {

    SharedPreferences preferences;//para obtener las preferencias de la aplicacion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //preferencias de la aplicacion
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String login = preferences.getString(getString(R.string.preferencias_login), "none");
        if (!login.equals("none")) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }
}
