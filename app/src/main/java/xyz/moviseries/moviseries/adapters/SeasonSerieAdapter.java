package xyz.moviseries.moviseries.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.custom_views.DMTextView;
import xyz.moviseries.moviseries.models.SeasonSerie;

/**
 * Created by DARWIN on 10/5/2017.
 */

public class SeasonSerieAdapter extends RecyclerView.Adapter<SeasonSerieAdapter.SeasonHolder> {
    private Context context;
    private ArrayList<SeasonSerie> seasons;

    public SeasonSerieAdapter(Context context, ArrayList<SeasonSerie> seasons) {
        this.context = context;
        this.seasons = seasons;
    }

    @Override
    public SeasonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_season, parent, false);
        return new SeasonHolder(v);
    }

    @Override
    public void onBindViewHolder(SeasonHolder holder, int position) {
        final SeasonSerie season = seasons.get(position);

        holder.number.setText("Temporada " + season.getNumber());

        Uri uri = Uri.parse(season.getCover());
        holder.cover.setImageURI(uri);

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSeasonSerieClickListener.onSeasonSerieClick(season);
            }
        });

    }

    @Override
    public int getItemCount() {
        return seasons.size();
    }


    class SeasonHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        SimpleDraweeView cover;
        DMTextView name, number;

        public SeasonHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView.findViewById(R.id.item);
            name = (DMTextView) itemView.findViewById(R.id.name);
            cover = (SimpleDraweeView) itemView.findViewById(R.id.cover);
            number = (DMTextView) itemView.findViewById(R.id.number);
            name.setVisibility(View.GONE);
        }
    }


    public interface OnSeasonSerieClickListener {
        void onSeasonSerieClick(SeasonSerie seasonSerie);
    }

    private OnSeasonSerieClickListener onSeasonSerieClickListener;

    public void setOnSeasonSerieClickListener(OnSeasonSerieClickListener onSeasonSerieClickListener) {
        this.onSeasonSerieClickListener = onSeasonSerieClickListener;
    }
}
