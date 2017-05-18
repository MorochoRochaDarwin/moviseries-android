package xyz.moviseries.moviseries.streaming;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import xyz.moviseries.moviseries.Exoplayer2Activity;
import xyz.moviseries.moviseries.JWplayerActivity;
import xyz.moviseries.moviseries.models.UrlOnline;

/**
 * Created by DARWIN on 14/5/2017.
 */

public class RapidVideo {
    private Context context;
    private DownloadLink stream_task;


    public RapidVideo(Context context) {
        this.context = context;
    }


    public void initStreaming(UrlOnline urlOnline, String video_name) {
        if (stream_task != null) {
            if (stream_task.getStatus() == AsyncTask.Status.PENDING || stream_task.getStatus() == AsyncTask.Status.RUNNING) {
                stream_task.cancel(true);
            }
            stream_task = null;
        }
        stream_task = new DownloadLink(urlOnline, video_name);
        stream_task.execute();
    }

    private class DownloadLink extends AsyncTask<Void, Void, Void> implements com.android.volley.Response.ErrorListener, com.android.volley.Response.Listener<String> {
        private UrlOnline urlOnline;
        private String video_name;

        DownloadLink(UrlOnline url, String video_name) {
            this.urlOnline = url;
            this.video_name = video_name;
        }

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "Obteniendo enlace", "por favor espere", true);
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(context);

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.rapidvideo.com/v/" + urlOnline.getFile_id(), this, this);
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
            return null;
        }

        @Override
        public void onErrorResponse(VolleyError error) {

        }

        @Override
        public void onResponse(String response) {
            progressDialog.hide();
            String firtsString = null;
            try {

                firtsString = response.substring(response.indexOf("www7"));
                firtsString = firtsString.substring(0, firtsString.indexOf("."));

                firtsString = response.substring(response.lastIndexOf("https:\\/\\/"+firtsString+".playercdn.net\\/85\\/0\\/"));
            } catch (StringIndexOutOfBoundsException e) {
                Toast.makeText(context, "No se pudo obtener el enlace", Toast.LENGTH_SHORT).show();
                return;
            }

            String link = firtsString.substring(0, firtsString.indexOf("\""));

            link = link.replace("\\", "");


            Intent intent = new Intent(context, JWplayerActivity.class);
            intent.putExtra(JWplayerActivity.LINK, link);
            intent.putExtra(JWplayerActivity.TITLE, urlOnline.getQuality());

            context.startActivity(intent);


        }
    }

}
