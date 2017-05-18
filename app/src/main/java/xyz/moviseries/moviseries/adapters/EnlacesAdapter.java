package xyz.moviseries.moviseries.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
        final UrlOnline url = urls.get(position);
        holder.audio.setText(url.getLanguage_name());
        holder.quality.setText(url.getQuality());
        holder.server.setText(url.getServer());
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEnlaceListener.onClickEnlace(url,false);
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEnlaceListener.onClickEnlace(url,true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return urls.size();
    }

    class MViewHolder extends RecyclerView.ViewHolder {
        private TextView audio, quality, server;
        ImageButton play, download;

        public MViewHolder(View itemView) {
            super(itemView);
            audio = (TextView) itemView.findViewById(R.id.audio);
            quality = (TextView) itemView.findViewById(R.id.quality);
            server = (TextView) itemView.findViewById(R.id.server);
            play = (ImageButton) itemView.findViewById(R.id.play);
            download = (ImageButton) itemView.findViewById(R.id.download);

        }
    }


    public interface OnClickEnlaceListener {
        void onClickEnlace(UrlOnline url, boolean isDownload);
    }

    private OnClickEnlaceListener onClickEnlaceListener;

    public void setOnClickEnlaceListener(OnClickEnlaceListener onClickEnlaceListener) {
        this.onClickEnlaceListener = onClickEnlaceListener;
    }
}
