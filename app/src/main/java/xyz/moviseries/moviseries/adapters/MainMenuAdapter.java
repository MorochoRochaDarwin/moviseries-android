package xyz.moviseries.moviseries.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.models.MainMenuItem;

/**
 * Created by DARWIN on 19/5/2017.
 */

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.MViewHolder> {

    private Context context;
    private ArrayList<MainMenuItem> items = new ArrayList<>();

    public MainMenuAdapter(Context context) {
        this.context = context;
        items.add(new MainMenuItem("PELICULAS",R.mipmap.ic_movie,true));
        items.add(new MainMenuItem("SERIES",R.mipmap.ic_dc,false));
        items.add(new MainMenuItem("SERIES ACTUALIZ.",R.mipmap.ic_dc,false));
        items.add(new MainMenuItem("TOP PELICULAS",R.mipmap.ic_start,false));
        items.add(new MainMenuItem("TOP SERIES",R.mipmap.ic_start,false));
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_main_menu, parent, false);
        return new MViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        MainMenuItem item = items.get(position);
        holder.imageView.setImageDrawable(context.getResources().getDrawable(item.getIcon()));
        holder.textView.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView imageView;

        public MViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
