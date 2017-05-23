package xyz.moviseries.moviseries.streaming;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tonyodev.fetch.Fetch;

import xyz.moviseries.moviseries.Exoplayer2Activity;
import xyz.moviseries.moviseries.downloads.Data;
import xyz.moviseries.moviseries.downloads.DownloadListActivity;
import xyz.moviseries.moviseries.models.UrlOnline;
import xyz.moviseries.moviseries.models.VideoDownload;

/**
 * Created by DARWIN on 10/5/2017.
 */

public class StreamMoe {
    private Context context;
    private DownloadLink stream_task;


    public StreamMoe(Context context) {
        this.context = context;
    }


    public void initStreaming(UrlOnline urlOnline, String video_name, boolean isDownload) {
        if (stream_task != null) {
            if (stream_task.getStatus() == AsyncTask.Status.PENDING || stream_task.getStatus() == AsyncTask.Status.RUNNING) {
                stream_task.cancel(true);
            }
            stream_task = null;
        }
        stream_task = new DownloadLink(urlOnline, video_name, isDownload);
        stream_task.execute();
    }

    private class DownloadLink extends AsyncTask<Void, Void, Void> implements com.android.volley.Response.ErrorListener, com.android.volley.Response.Listener<String> {
        private UrlOnline urlOnline;
        private String video_name;
        private boolean isDownload;


        public DownloadLink(UrlOnline urlOnline, String video_name, boolean isDownload) {
            this.urlOnline = urlOnline;
            this.video_name = video_name;
            this.isDownload = isDownload;
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
                firtsString = response.substring(response.lastIndexOf("Filename:"));
                firtsString = firtsString.substring(firtsString.indexOf("<a href=\""));
                firtsString = firtsString.replace("<a href=\"", "");
            } catch (StringIndexOutOfBoundsException e) {
                Log.i("firts", firtsString);
                Toast.makeText(context, "No se pudo obtener el enlace", Toast.LENGTH_SHORT).show();
                return;
            }



            String link = firtsString.substring(0, firtsString.indexOf("\""));
            Log.i("link", link);

            if (isDownload) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Debe conceder los permisos de escritura en la SD Card", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                Intent intent=new Intent(context, DownloadListActivity.class);
                intent.putExtra("video link",link);
                intent.putExtra("video name",video_name);
                intent.putExtra("video quality",urlOnline.getQuality());
                context.startActivity(intent);

               } else {
               /*





                */



                Intent intent = new Intent(context, Exoplayer2Activity.class);
                intent.putExtra(Exoplayer2Activity.LINK, link);
                intent.putExtra(Exoplayer2Activity.TITLE, video_name + " - " + urlOnline.getQuality());

                context.startActivity(intent);




            }


        }
    }

}
