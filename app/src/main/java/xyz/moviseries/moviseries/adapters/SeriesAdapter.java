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
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
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
        final Serie serie = series.get(position);

        holder.name.setText(serie.getSerie_name());

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCLickSerieListener.onClickSerie(serie);
            }
        });

        Uri uri = Uri.parse(serie.getCover());
        holder.cover.setImageURI(uri);

    }

    @Override
    public int getItemCount() {
        return series.size();
    }


    class SerieHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView cover;
        TextView name;
        LinearLayout item;

        public SerieHolder(View itemView) {
            super(itemView);
            cover = (SimpleDraweeView) itemView.findViewById(R.id.cover);
            name = (TextView) itemView.findViewById(R.id.name);
            item = (LinearLayout) itemView.findViewById(R.id.item);
        }
    }


    public interface OnCLickSerieListener {
        void onClickSerie(Serie serie);
    }

    private OnCLickSerieListener onCLickSerieListener;

    public void setOnCLickSerieListener(OnCLickSerieListener onCLickSerieListener) {
        this.onCLickSerieListener = onCLickSerieListener;
    }
}
