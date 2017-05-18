package xyz.moviseries.moviseries.downloads;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.Request;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import xyz.moviseries.moviseries.models.VideoDownload;

import static android.content.Context.MODE_WORLD_READABLE;

/**
 * Created by tonyofrancis on 1/24/17.
 */

public final class Data {

    private Context context;
    public static List<VideoDownload> videoDownloads = new ArrayList<>();

    public Data(Context context) {
        this.context = context;
        checkFolder();
    }

    private void checkFolder() {
        File f = new File(Environment.getExternalStorageDirectory() + "/Moviseries");
        if (!f.exists())
            f.mkdirs();

    }

    private void saveDownloads() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("DOWNLOADS", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(videoDownloads);

        editor.putString("DOWNLOADS_FILES", json);
        editor.commit();

    }


    public void addDownload(VideoDownload videoDownload) {
        videoDownloads.add(videoDownload);
        saveDownloads();
    }

    public void removeDownload(int pos){
        videoDownloads.remove(pos);
        saveDownloads();
    }




    public void readDownloads() {

        videoDownloads.clear();
        SharedPreferences sharedPreferences = context.getSharedPreferences("DOWNLOADS", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("DOWNLOADS_FILES", "[]");

        Gson gson = new Gson();

        VideoDownload[] videos = gson.fromJson(json, VideoDownload[].class);
        if (videos != null) {
            if (videos.length > 0) {
                for (int i = 0; i < videos.length; i++) {
                    videoDownloads.add(videos[i]);
                }
            }
        }


    }


    public static String getFilePath(String url) {

        Uri uri = Uri.parse(url);

        String fileName = uri.getLastPathSegment();

        String dir = getSaveDir();

        return (dir + "/Moviseries/" + System.nanoTime() + "_" + fileName);
    }


    public static String getSaveDir() {
        return Environment.getExternalStorageDirectory() + "/Moviseries/";
    }
}
