package xyz.moviseries.moviseries.bottom_sheets;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.moviseries.moviseries.DeveloperKey;
import xyz.moviseries.moviseries.Exoplayer2Activity;
import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.adapters.EnlacesAdapter;
import xyz.moviseries.moviseries.adapters.EnlacesMegaAdapter;
import xyz.moviseries.moviseries.api_clients.MoviseriesApiClient;
import xyz.moviseries.moviseries.api_services.MoviseriesApiService;
import xyz.moviseries.moviseries.custom_views.DMTextView;
import xyz.moviseries.moviseries.models.MEGAUrl;
import xyz.moviseries.moviseries.models.MovieScore;
import xyz.moviseries.moviseries.models.OpenLoadTicket;
import xyz.moviseries.moviseries.models.UrlOnline;
import xyz.moviseries.moviseries.models.ViewMovie;

/**
 * Created by DARWIN on 7/5/2017.
 */

public class BottomSheetMovieOptions extends BottomSheetDialogFragment implements EnlacesAdapter.OnClickEnlaceListener {
    public static final String MOVIE_ID = "BottomSheetOpcionesPelicula.movie_id";
    public static final String NAME = "BottomSheetOpcionesPelicula.name";
    public static final String TRAILER = "BottomSheetOpcionesPelicula.trailer";
    public static final String COVER = "BottomSheetOpcionesPelicula.cover";
    public static final String DESCRIPTION = "BottomSheetOpcionesPelicula.description";
    public static final String QUALITIES = "BottomSheetOpcionesPelicula.qualities";
    public static final String UPDATE_AT = "BottomSheetOpcionesPelicula.update_at";
    public static final String YEAR = "BottomSheetOpcionesPelicula.year";

    private String name, movie_id, year, trailer, cover, description, qualities, update_at;


    private Context context;
    private BottomSheetBehavior mBehavior;

    private ImageView imageViewCover;
    private TextView textViewName, textViewUpdateAt, textViewQualities, textViewVotos;
    private DMTextView textViewDescription;
    private SmileRating smileRating;


    private MovieScore movie;
    private ArrayList<UrlOnline> urls = new ArrayList<>();
    private ArrayList<MEGAUrl> mega_urls = new ArrayList<>();
    private RecyclerView recyclerViewEnlaces, recyclerViewEnlacesMega;
    private EnlacesAdapter enlacesAdapter;
    private EnlacesMegaAdapter enlacesMegaAdapter;


    private OpenLoadDownloadLink openload_task;
    private DownloadLink stream_task;

    private OpenLoadTicket openLoadTicket;
    private AlertDialog alertDialog;

    public static BottomSheetDialogFragment newInstance(Bundle args) {
        BottomSheetMovieOptions bottomSheetNuevoEvento = new BottomSheetMovieOptions();
        bottomSheetNuevoEvento.setArguments(args);
        return bottomSheetNuevoEvento;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();


        Bundle args = getArguments();
        name = args.getString(NAME);
        movie_id = args.getString(MOVIE_ID);
        trailer = args.getString(TRAILER);
        cover = args.getString(COVER);
        description = args.getString(DESCRIPTION);
        qualities = args.getString(QUALITIES);
        update_at = args.getString(UPDATE_AT);
        year = args.getString(YEAR);


    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        setStatusBarColorIfPossible(R.color.colorPrimary);

        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_opciones_pelicula, null);
        dialog.setContentView(contentView);


        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }


        Button btn_trailer = (Button) contentView.findViewById(R.id.btn_trailer);

        imageViewCover = (ImageView) contentView.findViewById(R.id.cover);
        textViewName = (TextView) contentView.findViewById(R.id.name);
        textViewQualities = (TextView) contentView.findViewById(R.id.qualities);
        textViewUpdateAt = (TextView) contentView.findViewById(R.id.timestamp);
        textViewDescription = (DMTextView) contentView.findViewById(R.id.short_description);
        textViewVotos = (TextView) contentView.findViewById(R.id.votos);
        smileRating = (SmileRating) contentView.findViewById(R.id.ratingView);
        recyclerViewEnlaces = (RecyclerView) contentView.findViewById(R.id.enlaces);
        recyclerViewEnlacesMega = (RecyclerView) contentView.findViewById(R.id.enlaces_mega);

        enlacesAdapter = new EnlacesAdapter(context, urls);
        enlacesAdapter.setOnClickEnlaceListener(this);
        enlacesMegaAdapter = new EnlacesMegaAdapter(context, mega_urls);
        recyclerViewEnlaces.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewEnlacesMega.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewEnlaces.setAdapter(enlacesAdapter);
        recyclerViewEnlacesMega.setAdapter(enlacesMegaAdapter);

        smileRating.setNameForSmile(BaseRating.TERRIBLE, "Terrible");
        smileRating.setNameForSmile(BaseRating.BAD, "Mala");
        smileRating.setNameForSmile(BaseRating.OKAY, "Regular");
        smileRating.setNameForSmile(BaseRating.GOOD, "Buena");
        smileRating.setNameForSmile(BaseRating.GREAT, "Excelente");


        Picasso.with(context)
                .load(cover)
                .resize(351, 526)
                .centerCrop()
                .into(imageViewCover);
        textViewName.setText(name);
        textViewQualities.setText(qualities);
        textViewUpdateAt.setText(update_at);
        textViewDescription.setText(description);


        mBehavior = BottomSheetBehavior.from((View) contentView.getParent());


        btn_trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                        getActivity(), DeveloperKey.DEVELOPER_KEY, trailer, 0, true, false);
                startActivity(intent);
            }
        });

        new Load().execute();

    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };


    /**
     * cambia el color del staus bar
     *
     * @param color
     */
    private void setStatusBarColorIfPossible(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                // getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getDialog().getWindow().setStatusBarColor(color);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }


    private int navigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Resources resources = context.getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }

        return 0;
    }

    @Override
    public void onClickEnlace(UrlOnline url) {
        if (url.getServer().equals("stream.moe")) {
            new DownloadLink(url).execute();
            if (stream_task != null) {
                if (stream_task.getStatus() == AsyncTask.Status.PENDING || stream_task.getStatus() == AsyncTask.Status.RUNNING) {
                    stream_task.cancel(true);
                }
                stream_task = null;
            }
            stream_task = new DownloadLink(url);
            stream_task.execute();
        } else if (url.getServer().equals("openload")) {
            if (openload_task != null) {
                if (openload_task.getStatus() == AsyncTask.Status.PENDING || openload_task.getStatus() == AsyncTask.Status.RUNNING) {
                    openload_task.cancel(true);
                }
                openload_task = null;
            }
            openload_task = new OpenLoadDownloadLink(url);
            openload_task.execute();
        }
    }


    private class Load extends AsyncTask<Void, Void, Void> implements Callback<ViewMovie> {
        private String url = "http://moviseries.xyz/android/movie/" + movie_id;

        @Override
        protected Void doInBackground(Void... voids) {
            MoviseriesApiService apiService = MoviseriesApiClient.getClient().create(MoviseriesApiService.class);
            Call<ViewMovie> call = apiService.getMovie(url);
            call.enqueue(this);
            return null;
        }

        @Override
        public void onResponse(Call<ViewMovie> call, Response<ViewMovie> response) {
            if (response != null) {
                if (response.body() != null) {

                    if (response.body().getMovie() != null) {
                        movie = response.body().getMovie();
                        float score = Float.parseFloat(movie.getScore());
                        if (score >= 0 && score < 2) {
                            smileRating.setSelectedSmile(BaseRating.TERRIBLE);
                        } else if (score >= 2 && score < 4) {
                            smileRating.setSelectedSmile(BaseRating.BAD);
                        } else if (score >= 4 && score < 6) {
                            smileRating.setSelectedSmile(BaseRating.OKAY);
                        } else if (score >= 6 && score < 8) {
                            smileRating.setSelectedSmile(BaseRating.GOOD);
                        } else {
                            smileRating.setSelectedSmile(BaseRating.GREAT);
                        }

                        textViewVotos.setText("# votos: " + movie.getVotos());

                    } else {
                        Log.i("apimovi", "null movie");
                    }


                    if (response.body().getMega_urls() != null) {
                        mega_urls.addAll(response.body().getMega_urls());
                        if (mega_urls.size() > 0) {
                            enlacesMegaAdapter.notifyItemRangeInserted(0, mega_urls.size());
                            enlacesMegaAdapter.notifyDataSetChanged();
                        }
                    }

                    if (response.body().getUrls() != null) {
                        urls.addAll(response.body().getUrls());
                        if (urls.size() > 0) {
                            enlacesAdapter.notifyItemRangeInserted(0, urls.size());
                            enlacesAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        @Override
        public void onFailure(Call<ViewMovie> call, Throwable t) {
            Log.i("apimovi", t.getMessage());
        }
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
            String firtsString = response.substring(response.lastIndexOf("https://wabbit.moecdn.io/"));
            String link = firtsString.substring(0, firtsString.indexOf("\""));

            // Intent intent = new Intent(context, Exoplayer2Activity.class);
            // intent.putExtra(Exoplayer2Activity.LINK,link);
            // intent.putExtra(Exoplayer2Activity.TITLE,movie.getName()+" - "+urlOnline.getQuality());

            //startActivity(intent);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(link), "video/mp4");
            startActivity(intent);


        }
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
                    startActivity(intent);


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
