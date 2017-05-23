package xyz.moviseries.moviseries.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.custom_views.DMTextView;
import xyz.moviseries.moviseries.models.Season;
import xyz.moviseries.moviseries.models.Serie;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.SeasonHolder> {

    private Context context;
    private ArrayList<Season> seasons;

    public SeasonAdapter(Context context, ArrayList<Season> series) {
        this.context = context;
        this.seasons = series;
    }

    @Override
    public SeasonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_season, parent, false);
        return new SeasonHolder(v);
    }

    @Override
    public void onBindViewHolder(SeasonHolder holder, int position) {
        final Season season = seasons.get(position);

        holder.name.setText(season.getSerie_name());
        holder.number.setText("Temporada " + season.getNumber());
        Uri uri = Uri.parse(season.getCover());
        holder.cover.setImageURI(uri);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSeasonClickListener.OnCLickSeason(season);
            }
        });

    }

    @Override
    public int getItemCount() {
        return seasons.size();
    }


    class SeasonHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView cover;
        DMTextView name, number;

        public SeasonHolder(View itemView) {
            super(itemView);
            name = (DMTextView) itemView.findViewById(R.id.name);
            cover = (SimpleDraweeView) itemView.findViewById(R.id.cover);
            number = (DMTextView) itemView.findViewById(R.id.number);
        }
    }


    public interface OnSeasonClickListener {
        void OnCLickSeason(Season season);
    }

    private OnSeasonClickListener onSeasonClickListener;

    public void setOnSeasonClickListener(OnSeasonClickListener onSeasonClickListener) {
        this.onSeasonClickListener = onSeasonClickListener;
    }
}
