package xyz.moviseries.moviseries;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.moviseries.moviseries.api_clients.MoviseriesApiClient;
import xyz.moviseries.moviseries.api_services.MoviseriesApiService;

public class LoginActivity extends AppCompatActivity {

    private Context context;
    private EditText editTextEmail, editTextPassword;
    private Login task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText) findViewById(R.id.edit_email);
        editTextPassword = (EditText) findViewById(R.id.edit_password);


    }


    public void checkLogin(View v) {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        if (!email.contains("@")) {
            editTextEmail.setError("El e-mail ingresado es invalido");
            editTextEmail.requestFocus();
            return;
        }


        if (password.trim().length() == 0) {
            editTextPassword.setError("Este campo es obligatorio");
            editTextPassword.requestFocus();
            return;
        }

        if (task != null) {
            if (task.getStatus() == AsyncTask.Status.RUNNING) {
                task.cancel(true);
                task = null;
            }
        }
        task = null;
        task = new Login(email, password);
        task.execute();


    }


    private class Login extends AsyncTask<Void, Void, Void> implements Callback<JsonObject> {
        private String email, password;
        private ProgressDialog progressDialog;

        public Login(String email, String password) {
            this.email = email;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "Verificando Información", "For favor espere ...", true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            MoviseriesApiService apiService = MoviseriesApiClient.getClient().create(MoviseriesApiService.class);
            Call<JsonObject> call = apiService.login(email, password);
            call.enqueue(this);

            return null;
        }

        @Override
        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
            progressDialog.hide();
            Log.i("apimoviseries", response.body().toString());
            JsonPrimitive resultJson = response.body().getAsJsonPrimitive("result_code");
            int resultCode = resultJson.getAsInt();
            if (resultCode == 202) {
                Toast.makeText(context, response.body().getAsJsonPrimitive("error").getAsString(), Toast.LENGTH_SHORT).show();
            } else if (resultCode == 200) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.preferencias_login), "moviseries");
                editor.putString(getString(R.string.usuario_nombre), response.body().getAsJsonPrimitive("username").getAsString());
                editor.putString(getString(R.string.usuario_email), response.body().getAsJsonPrimitive("email").getAsString());
                editor.putString(getString(R.string.usuario_id), response.body().getAsJsonPrimitive("user_id").getAsString());
                editor.putString(getString(R.string.usuario_tipo), response.body().getAsJsonPrimitive("user_type").getAsString());
                editor.apply();

                startActivity(new Intent(context,DashboardActivity.class));
                finish();

            }

        }

        @Override
        public void onFailure(Call<JsonObject> call, Throwable t) {
            Log.i("apimoviseries", t.getMessage());
        }
    }


    public void resetpass(View v){
        String url = "http://moviseries.xyz";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }



}
