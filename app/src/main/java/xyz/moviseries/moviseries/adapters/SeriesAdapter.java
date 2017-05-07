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
import xyz.moviseries.moviseries.models.Serie;

/**
 * Created by DARWIN on 6/5/2017.
 */

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SerieHolder> {

    private Context context;
    private ArrayList<Serie> series;

    public SeriesAdapter(Context context, ArrayList<Serie> series) {
        this.context = context;
        this.series = series;
    }

    @Override
    public SerieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_serie, parent, false);
        return new SerieHolder(v);
    }

    @Override
    public void onBindViewHolder(SerieHolder holder, int position) {
        Serie serie=series.get(position);

        holder.name.setText(serie.getSerie_name());

        try {
            Picasso.with(context)
                    .load(serie.getCover())
                    .resize(351, 526)
                    .centerCrop()
                    .into(holder.cover);
        } catch (Exception e) {
            Log.i("apimoviseries", " err: " + e.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return series.size();
    }


    class SerieHolder extends RecyclerView.ViewHolder{
        ImageView cover;
        DMTextView name;
        public SerieHolder(View itemView) {
            super(itemView);
            name=(DMTextView) itemView.findViewById(R.id.name);
            cover = (ImageView) itemView.findViewById(R.id.cover);
        }
    }
}
