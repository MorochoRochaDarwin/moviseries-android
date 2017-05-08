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
import xyz.moviseries.moviseries.models.MEGAUrl;

/**
 * Created by DARWIN on 8/5/2017.
 */

public class EnlacesMegaAdapter extends RecyclerView.Adapter<EnlacesMegaAdapter.MViewHolder> {

    private Context context;
    private ArrayList<MEGAUrl> megaUrls;


    public EnlacesMegaAdapter(Context context, ArrayList<MEGAUrl> megaUrls) {
        this.context = context;
        this.megaUrls = megaUrls;
    }

    @Override
    public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_enlace_mega, parent, false);
        return new MViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        MEGAUrl megaUrl = megaUrls.get(position);
        holder.title.setText(megaUrl.getName());
        holder.note.setText(megaUrl.getNote());
    }

    @Override
    public int getItemCount() {
        return megaUrls.size();
    }

    class MViewHolder extends RecyclerView.ViewHolder {
        TextView title, note;
        ImageButton mega, copy;

        public MViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            note = (TextView) itemView.findViewById(R.id.note);
            mega = (ImageButton) itemView.findViewById(R.id.mega);
            copy = (ImageButton) itemView.findViewById(R.id.copy);
        }
    }
}
