package xyz.moviseries.moviseries.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.models.UrlOnline;

/**
 * Created by DARWIN on 8/5/2017.
 */

public class EnlacesAdapter extends RecyclerView.Adapter<EnlacesAdapter.MViewHolder> {

    private Context context;
    private ArrayList<UrlOnline> urls;

    public EnlacesAdapter(Context context, ArrayList<UrlOnline> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_enlace_pelicula, parent, false);
        return new MViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        UrlOnline url = urls.get(position);
        holder.audio.setText(url.getLanguage_name());
        holder.quality.setText(url.getQuality());
        holder.server.setText(url.getServer());
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    class MViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout item;
        private TextView audio, quality, server;

        public MViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView.findViewById(R.id.item);
            audio = (TextView) itemView.findViewById(R.id.audio);
            quality = (TextView) itemView.findViewById(R.id.quality);
            server = (TextView) itemView.findViewById(R.id.server);

        }
    }
}
