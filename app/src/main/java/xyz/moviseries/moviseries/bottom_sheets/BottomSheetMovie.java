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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.moviseries.moviseries.DeveloperKey;
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
import xyz.moviseries.moviseries.streaming.NowVideo;
import xyz.moviseries.moviseries.streaming.OpenLoad;
import xyz.moviseries.moviseries.streaming.RapidVideo;
import xyz.moviseries.moviseries.streaming.StreamMoe;

/**
 * Created by DARWIN on 7/5/2017.
 */

public class BottomSheetMovie extends BottomSheetDialogFragment implements EnlacesAdapter.OnClickEnlaceListener {
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

    private SimpleDraweeView imageViewCover;
    private TextView textViewName, textViewUpdateAt, textViewQualities, textViewVotos;
    private DMTextView textViewDescription;
    private SmileRating smileRating;


    private MovieScore movie;
    private ArrayList<UrlOnline> urls = new ArrayList<>();
    private ArrayList<MEGAUrl> mega_urls = new ArrayList<>();
    private RecyclerView recyclerViewEnlaces, recyclerViewEnlacesMega;
    private EnlacesAdapter enlacesAdapter;
    private EnlacesMegaAdapter enlacesMegaAdapter;


    private OpenLoadTicket openLoadTicket;
    private AlertDialog alertDialog;


    private OpenLoad openLoad;
    private StreamMoe streamMoe;
    private RapidVideo rapidVideo;
    private NowVideo nowVideo;

    public static BottomSheetDialogFragment newInstance(Bundle args) {
        BottomSheetMovie bottomSheetNuevoEvento = new BottomSheetMovie();
        bottomSheetNuevoEvento.setArguments(args);
        return bottomSheetNuevoEvento;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        openLoad = new OpenLoad(context);
        streamMoe = new StreamMoe(context);
        rapidVideo = new RapidVideo(context);
        nowVideo = new NowVideo(context);


        Bundle args = getArguments();
        name = args.getString(NAME);
        movie_id = args.getString(MOVIE_ID);
        trailer = args.getString(TRAILER);
        cover = args.getString(COVER);
        description = args.getString(DESCRIPTION);
        qualities = args.getString(QUALITIES,"");
        update_at = args.getString(UPDATE_AT);
        year = args.getString(YEAR);


    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        //setStatusBarColorIfPossible(R.color.colorPrimary);

        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_opciones_pelicula, null);
        dialog.setContentView(contentView);


        Button btn_trailer = (Button) contentView.findViewById(R.id.btn_trailer);
        Button btn_share = (Button) contentView.findViewById(R.id.btn_share);

        imageViewCover = (SimpleDraweeView) contentView.findViewById(R.id.cover);
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


        Uri uri = Uri.parse(cover);
        imageViewCover.setImageURI(uri);


        textViewName.setText(name);
        textViewQualities.setText(qualities);
        textViewUpdateAt.setText(update_at);
        textViewDescription.setText(description);


        mBehavior = BottomSheetBehavior.from((View) contentView.getParent());
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }


        btn_trailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                        getActivity(), DeveloperKey.DEVELOPER_KEY, trailer, 0, true, false);
                startActivity(intent);
            }
        });


        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, "http://moviseries.xyz/peliculas/"+movie_id);
                startActivity(Intent.createChooser(i, "Compartir URL"));
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
    public void onClickEnlace(UrlOnline url, boolean isDownload) {

        if (url.getServer().equals("openload") && isDownload) {
            String link = "https://openload.co/f/" + url.getFile_id();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            context.startActivity(intent);
        }else {
            switch (url.getServer()) {
                case "stream.moe":
                    streamMoe.initStreaming(url, name, isDownload);
                    break;
                case "openload":
                    openLoad.initStreaming(url);
                    break;
                case "rapidvideo":
                    rapidVideo.initStreaming(url, name);
                    break;
                case "nowvideo":
                    nowVideo.initStreaming(url, name, isDownload);
                    break;
            }
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


}
