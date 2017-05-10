package xyz.moviseries.moviseries.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hsalf.smilerating.SmileRating;
import com.squareup.picasso.Picasso;

import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.custom_views.DMTextView;

/**
 * Created by DARWIN on 10/5/2017.
 */

public class SerieFragment extends Fragment {
    public static final String SERIE_ID = "SerieFragment.seriee_id";
    public static final String NAME = "SerieFragment.name";
    public static final String YEAR = "SerieFragment.year";
    public static final String COVER = "SerieFragment.cover";
    public static final String DESCRIPTION = "SerieFragment.description";
    public static final String UPDATE_AT = "SerieFragment.update_at";

    private String name, serie_id, year, cover, description, update_at;
    private Context context;
    private ImageView imageViewCover;
    private TextView textViewName, textViewUpdateAt, textViewVotos;
    private DMTextView textViewDescription;
    private SmileRating smileRating;
    private RecyclerView recyclerViewEnlaces, recyclerViewEnlacesMega;


    public static SerieFragment newInstance(Bundle args) {
        SerieFragment serieFragment = new SerieFragment();
        serieFragment.setArguments(args);
        return serieFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        Bundle args = getArguments();
        name = args.getString(NAME);
        serie_id = args.getString(SERIE_ID);
        year = args.getString(YEAR);
        cover = args.getString(COVER);
        description = args.getString(DESCRIPTION);
        update_at = args.getString(UPDATE_AT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_serie, container, false);
        imageViewCover = (ImageView) contentView.findViewById(R.id.cover);
        textViewName = (TextView) contentView.findViewById(R.id.name);
        textViewUpdateAt = (TextView) contentView.findViewById(R.id.timestamp);
        textViewDescription = (DMTextView) contentView.findViewById(R.id.short_description);
        textViewVotos = (TextView) contentView.findViewById(R.id.votos);
        smileRating = (SmileRating) contentView.findViewById(R.id.ratingView);
        recyclerViewEnlaces = (RecyclerView) contentView.findViewById(R.id.enlaces);
        recyclerViewEnlacesMega = (RecyclerView) contentView.findViewById(R.id.enlaces_mega);

        Picasso.with(context)
                .load(cover)
                .resize(351, 526)
                .centerCrop()
                .into(imageViewCover);
        textViewName.setText(name);
        textViewUpdateAt.setText(update_at);
        textViewDescription.setText(description);

        return contentView;
    }
}
