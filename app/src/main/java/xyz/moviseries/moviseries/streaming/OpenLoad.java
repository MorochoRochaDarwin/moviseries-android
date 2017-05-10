package xyz.moviseries.moviseries.streaming;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.models.OpenLoadTicket;
import xyz.moviseries.moviseries.models.UrlOnline;

/**
 * Created by DARWIN on 10/5/2017.
 */

public class OpenLoad {

    private Context context;
    private OpenLoadTicket openLoadTicket;
    private AlertDialog alertDialog;
    private OpenLoadDownloadLink openload_task;

    public OpenLoad(Context context) {
        this.context = context;
    }


    public void initStreaming(UrlOnline urlOnline){
        if (openload_task != null) {
            if (openload_task.getStatus() == AsyncTask.Status.PENDING || openload_task.getStatus() == AsyncTask.Status.RUNNING) {
                openload_task.cancel(true);
            }
            openload_task = null;
        }
        openload_task = new OpenLoadDownloadLink(urlOnline);
        openload_task.execute();
    }

    private class OpenLoadDownloadLink extends AsyncTask<Void, Void, Void> implements com.android.volley.Response.ErrorListener, com.android.volley.Response.Listener<String> {

        private UrlOnline urlOnline;
        private ProgressDialog progressDialog;

        OpenLoadDownloadLink(UrlOnline url) {
            this.urlOnline = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "Obteniendo Captcha", "por favor espere", true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = "https://api.openload.co/1/file/dlticket?file=" + urlOnline.getFile_id();
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, this, this);
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

            return null;
        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }

        @Override
        public void onResponse(String response) {

            progressDialog.dismiss();

            try {
                JSONObject json = new JSONObject(response);

                if (json.getString("status").equals("200")) {
                    JSONObject json_result = json.getJSONObject("result");
                    String ticket = json_result.getString("ticket");
                    String captcha_url = json_result.getString("captcha_url");
                    String captcha_w = json_result.getString("captcha_w");
                    String captcha_h = json_result.getString("captcha_h");
                    String wait_time = json_result.getString("wait_time");
                    String valid_until = json_result.getString("valid_until");
                    openLoadTicket = new OpenLoadTicket(ticket, captcha_url, captcha_w, captcha_h, wait_time, valid_until);


                    dialogOpenload(openLoadTicket, urlOnline);
                } else {
                    Toast.makeText(context, "Enlace no valido, por favor notifiquelo", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    private void dialogOpenload(final OpenLoadTicket openLoadTicket, final UrlOnline urlOnline) {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.dialog_openload, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);


        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final ImageView captcha = (ImageView) promptsView.findViewById(R.id.captcha);
        final EditText editTextCaptcha = (EditText) promptsView.findViewById(R.id.edit_text_captcha);
        final Button btn_ok = (Button) promptsView.findViewById(R.id.btn_ok);
        final Button btn_cancel = (Button) promptsView.findViewById(R.id.btn_cancel);

        Picasso.with(context)
                .load(openLoadTicket.getCaptcha_url())
                .resize(Integer.parseInt(openLoadTicket.getCaptcha_w()), Integer.parseInt(openLoadTicket.getCaptcha_h()))
                .centerCrop()
                .into(captcha);

        // create alert dialog
        alertDialog = alertDialogBuilder.create();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_captcha = editTextCaptcha.getText().toString();
                new ValidadeCaptcha(urlOnline, openLoadTicket.getTicket(), txt_captcha).execute();

            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // show it
        alertDialog.show();
    }


    private class ValidadeCaptcha extends AsyncTask<Void, Void, Void> implements com.android.volley.Response.Listener<String>, com.android.volley.Response.ErrorListener {

        private UrlOnline urlOnline;
        private String ticket, captcha_response;
        private ProgressDialog progressDialog;


        public ValidadeCaptcha(UrlOnline urlOnline, String ticket, String captcha_response) {
            this.urlOnline = urlOnline;
            this.ticket = ticket;
            this.captcha_response = captcha_response;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "Validando Captcha", "por favor espere", true);
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(context);

            String url = "https://api.openload.co/1/file/dl?file=" + urlOnline.getFile_id() + "&ticket=" + ticket + "&captcha_response=" + captcha_response;
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, this, this);
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            return null;
        }

        @Override
        public void onResponse(String response) {
            progressDialog.dismiss();

            try {
                JSONObject json = new JSONObject(response);

                if (json.getString("status").equals("200")) {

                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }

                    JSONObject json_result = json.getJSONObject("result");

                    String url_video = json_result.getString("url");


                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(url_video), "video/mp4");
                    context.startActivity(intent);


                    // Intent intent = new Intent(context, Exoplayer2Activity.class);
                    // intent.putExtra(Exoplayer2Activity.LINK,link);
                    // intent.putExtra(Exoplayer2Activity.TITLE,movie.getName()+" - "+urlOnline.getQuality());


                    dialogOpenload(openLoadTicket, urlOnline);
                } else {
                    Toast.makeText(context, "Error no se pudo obtener el enlace", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                Log.i("openload", e.getMessage());
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }
    }

}
