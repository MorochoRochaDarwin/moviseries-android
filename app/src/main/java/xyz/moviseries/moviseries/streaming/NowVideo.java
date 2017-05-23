package xyz.moviseries.moviseries.streaming;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import xyz.moviseries.moviseries.JWplayerActivity;
import xyz.moviseries.moviseries.downloads.Data;
import xyz.moviseries.moviseries.models.UrlOnline;
import xyz.moviseries.moviseries.models.VideoDownload;

/**
 * Created by DARWIN on 18/5/2017.
 */

public class NowVideo {
    private Context context;
    private DownloadLink stream_task;


    public NowVideo(Context context) {
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
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://www.nowvideo.sx/mobile/video.php?id=" + urlOnline.getFile_id(), this, this);
            // Add the request to the RequestQueue.

            queue.add(stringRequest);
            return null;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            progressDialog.hide();
            Toast.makeText(context, "No se pudo obtener el enlace", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(String response) {

            progressDialog.hide();
            String firtsString = null;
            try {
                firtsString = response.substring(response.lastIndexOf("source src="));
                firtsString = firtsString.replace("source src=\"", "");
                Log.i("resp first", firtsString);
            } catch (StringIndexOutOfBoundsException e) {
                Toast.makeText(context, "No se pudo obtener el enlace", Toast.LENGTH_SHORT).show();
                return;
            }

            String link = firtsString.substring(0, firtsString.indexOf("\""));


            if (isDownload) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(context, "Debe conceder los permisos de escritura en la SD Card", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                Fetch fetch = Fetch.getInstance(context);

                com.tonyodev.fetch.request.Request request = new com.tonyodev.fetch.request.Request(link,
                        Environment.getExternalStorageDirectory() + "/Moviseries/", video_name + " " + urlOnline.getQuality() + ".mp4");
                long downloadId = fetch.enqueue(request);
                Toast.makeText(context, "Descarga Iniciada", Toast.LENGTH_SHORT).show();

                Data data = new Data(context);
                data.addDownload(new VideoDownload(downloadId + "", video_name + " " + urlOnline.getQuality() + ".mp4", link));
            } else {


                Intent intent = new Intent(context, JWplayerActivity.class);
                intent.putExtra(JWplayerActivity.LINK, link);
                intent.putExtra(JWplayerActivity.TITLE, urlOnline.getQuality());

                context.startActivity(intent);

            }


        }
    }


}
