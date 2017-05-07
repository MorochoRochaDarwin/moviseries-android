package xyz.moviseries.moviseries.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        Season season=seasons.get(position);

        holder.name.setText(season.getSerie_name());
        holder.number.setText("Temporada "+season.getNumber());

        try {
            Picasso.with(context)
                    .load(season.getCover())
                    .resize(351, 526)
                    .centerCrop()
                    .into(holder.cover);
        } catch (Exception e) {
            Log.i("apimoviseries", " err: " + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return seasons.size();
    }


    class SeasonHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        DMTextView name,number;
        public SeasonHolder(View itemView) {
            super(itemView);
            name=(DMTextView) itemView.findViewById(R.id.name);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            number = (DMTextView) itemView.findViewById(R.id.number);
        }
    }
}
