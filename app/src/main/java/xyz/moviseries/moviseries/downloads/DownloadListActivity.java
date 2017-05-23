package xyz.moviseries.moviseries.downloads;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.tonyodev.fetch.Fetch;
import com.tonyodev.fetch.request.Request;
import com.tonyodev.fetch.request.RequestInfo;

import java.util.ArrayList;
import java.util.List;

import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.models.VideoDownload;

public class DownloadListActivity extends AppCompatActivity implements ActionListener {

    private static final int STORAGE_PERMISSION_CODE = 200;
    private Context context;

    private RecyclerView recyclerView;
    private SwitchCompat networkSwitch;

    private FileAdapter fileAdapter;
    private Fetch fetch;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fetch = Fetch.getInstance(this);
        clearAllDownloads();
        try {

            String link = getIntent().getStringExtra("video link");
            String video_name = getIntent().getStringExtra("video name");
            String video_quality = getIntent().getStringExtra("video quality");

            String filename = video_name + "_" + video_quality + ".mp4";

            com.tonyodev.fetch.request.Request request = new com.tonyodev.fetch.request.Request(link,
                    Environment.getExternalStorageDirectory() + "/Moviseries/", filename);
            long downloadId = fetch.enqueue(request);
            Toast.makeText(context, "Descarga Iniciada", Toast.LENGTH_SHORT).show();

            Data data = new Data(context);
            data.addDownload(new VideoDownload(downloadId + "", filename, link));

        } catch (Exception e) {
            Log.i("derror", e.getMessage());
        }


        data = new Data(this);
        setViews();


    }

    /*Removes all downloads managed by Fetch*/
    private void clearAllDownloads() {

        Fetch fetch = Fetch.getInstance(this);
        fetch.removeAll();

        createNewRequests();
        fetch.release();
    }

    private void setViews() {

        networkSwitch = (SwitchCompat) findViewById(R.id.networkSwitch);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        networkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    fetch.setAllowedNetwork(Fetch.NETWORK_WIFI);
                } else {
                    fetch.setAllowedNetwork(Fetch.NETWORK_ALL);
                }
            }
        });

        fileAdapter = new FileAdapter(this);
        recyclerView.setAdapter(fileAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<RequestInfo> infos = fetch.get();

        for (RequestInfo info : infos) {

            fileAdapter.onUpdate(info.getId(), info.getStatus()
                    , info.getProgress(), info.getDownloadedBytes(), info.getFileSize(), info.getError());
        }

        fetch.addFetchListener(fileAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        fetch.removeFetchListener(fileAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fetch.release();
    }

    private void createNewRequests() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            enqueueDownloads();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE || grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            enqueueDownloads();

        } else {
            Toast.makeText(this, R.string.permission_not_enabled, Toast.LENGTH_SHORT).show();
        }
    }

    private void enqueueDownloads() {


        data.readDownloads();


        List<Request> requests = new ArrayList<>();

        for (VideoDownload v : Data.videoDownloads) {

            Request request = new Request(v.getUrl(), getFilePath(v.getUrl()));
            requests.add(request);
        }

        List<Long> ids = fetch.enqueue(requests);

        for (int i = 0; i < requests.size(); i++) {

            Request request = requests.get(i);
            long id = ids.get(i);

            Download download = new Download();
            download.setId(id);
            download.setUrl(request.getUrl());
            download.setFilePath(request.getFilePath());
            download.setError(Fetch.DEFAULT_EMPTY_VALUE);
            download.setProgress(0);
            download.setStatus(Fetch.STATUS_QUEUED);
            download.setFileName(Data.videoDownloads.get(i).getFileName());

            fileAdapter.addDownload(download);
        }
    }


    public static String getFilePath(String url) {

        Uri uri = Uri.parse(url);

        String fileName = uri.getLastPathSegment();

        String dir = getSaveDir();

        return (dir + System.nanoTime() + "_" + fileName);
    }


    public static String getSaveDir() {
        return Environment.getExternalStorageDirectory() + "/Moviseries/";
    }

    @Override
    public void onPauseDownload(long id) {
        fetch.pause(id);
    }

    @Override
    public void onResumeDownload(long id) {
        fetch.resume(id);
    }

    @Override
    public void onRemoveDownload(long id) {
        fetch.remove(id);
    }

    @Override
    public void onRetryDownload(long id) {
        fetch.retry(id);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}