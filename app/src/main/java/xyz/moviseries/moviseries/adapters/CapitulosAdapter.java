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
import xyz.moviseries.moviseries.models.Capitulo;

/**
 * Created by DARWIN on 11/5/2017.
 */

public class CapitulosAdapter extends RecyclerView.Adapter<CapitulosAdapter.CViewHolder> {

    private Context context;
    private ArrayList<Capitulo> capitulos;

    public CapitulosAdapter(Context context, ArrayList<Capitulo> capitulos) {
        this.context = context;
        this.capitulos = capitulos;
    }

    @Override
    public CViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_capitulo, parent, false);
        return new CViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CViewHolder holder, int position) {
        final Capitulo capitulo = capitulos.get(position);
        holder.episode_number.setText(capitulo.getEpisode());
        holder.episode_name.setText(capitulo.getEpisode_name());
        holder.audio.setText(capitulo.getLanguage_name());
        holder.server.setText(capitulo.getServer());
        holder.quality.setText(capitulo.getQuality());

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCapituloListener.onCapituloClickPlay(capitulo,false);
            }
        });

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCapituloListener.onCapituloClickPlay(capitulo,true);
            }
        });

    }

    @Override
    public int getItemCount() {
        return capitulos.size();
    }

    class CViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout item;
        private TextView audio, quality, server, episode_number, episode_name;

        private ImageButton play, download;

        public CViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView.findViewById(R.id.item);
            episode_name = (TextView) itemView.findViewById(R.id.episode_name);
            episode_number = (TextView) itemView.findViewById(R.id.episode);
            audio = (TextView) itemView.findViewById(R.id.audio);
            quality = (TextView) itemView.findViewById(R.id.quality);
            server = (TextView) itemView.findViewById(R.id.server);


            play = (ImageButton) itemView.findViewById(R.id.play);
            download = (ImageButton) itemView.findViewById(R.id.download);

        }
    }


    public interface OnClickCapituloListener {
        void onCapituloClickPlay(Capitulo capitulo,boolean isDownload);
    }


    private OnClickCapituloListener onClickCapituloListener;

    public void setOnClickCapituloListener(OnClickCapituloListener onClickCapituloListener) {
        this.onClickCapituloListener = onClickCapituloListener;
    }
}
