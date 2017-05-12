package xyz.moviseries.moviseries.streaming;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import xyz.moviseries.moviseries.models.UrlOnline;

/**
 * Created by DARWIN on 10/5/2017.
 */

public class StreamMoe {
    private Context context;
    private DownloadLink stream_task;


    public StreamMoe(Context context) {
        this.context = context;
    }


    public void initStreaming(UrlOnline urlOnline){
        if (stream_task != null) {
            if (stream_task.getStatus() == AsyncTask.Status.PENDING || stream_task.getStatus() == AsyncTask.Status.RUNNING) {
                stream_task.cancel(true);
            }
            stream_task = null;
        }
        stream_task = new DownloadLink(urlOnline);
        stream_task.execute();
    }

    private class DownloadLink extends AsyncTask<Void, Void, Void> implements com.android.volley.Response.ErrorListener, com.android.volley.Response.Listener<String> {
        private UrlOnline urlOnline;

        DownloadLink(UrlOnline url) {
            this.urlOnline = url;
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
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://stream.moe/" + urlOnline.getFile_id(), this, this);
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
                firtsString = response.substring(response.lastIndexOf("https://wabbit.moecdn.io/"));
            }catch (StringIndexOutOfBoundsException e){
                try {
                    firtsString = response.substring(response.lastIndexOf("https://clank.stream.moe/"));
                }catch (StringIndexOutOfBoundsException e2){
                    Toast.makeText(context, "No se pudo obtener el enlace", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            String link = firtsString.substring(0, firtsString.indexOf("\""));

            // Intent intent = new Intent(context, Exoplayer2Activity.class);
            // intent.putExtra(Exoplayer2Activity.LINK,link);
            // intent.putExtra(Exoplayer2Activity.TITLE,movie.getName()+" - "+urlOnline.getQuality());

            //startActivity(intent);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(link), "video/mp4");
            context.startActivity(intent);


        }
    }

}
