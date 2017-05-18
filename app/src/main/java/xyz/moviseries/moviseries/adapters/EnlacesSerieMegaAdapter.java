package xyz.moviseries.moviseries.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.models.MEGAUrlSerie;

/**
 * Created by DARWIN on 10/5/2017.
 */

public class EnlacesSerieMegaAdapter  extends RecyclerView.Adapter<EnlacesSerieMegaAdapter.MViewHolder>{

    private Context context;
    private ArrayList<MEGAUrlSerie> megaUrls;

    public EnlacesSerieMegaAdapter(Context context, ArrayList<MEGAUrlSerie> megaUrls) {
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
        final MEGAUrlSerie megaUrl = megaUrls.get(position);
        holder.title.setText(megaUrl.getName());
        holder.note.setText(megaUrl.getNote());

        holder.mega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(megaUrl.getUrl()));
                context.startActivity(intent);
            }
        });


        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(megaUrl.getUrl(), megaUrl.getUrl());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Enlace de MEGA copiado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return megaUrls.size();
    }

    class MViewHolder extends RecyclerView.ViewHolder {
        TextView title, note;
        Button mega, copy;

        public MViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            note = (TextView) itemView.findViewById(R.id.note);
            mega = (Button) itemView.findViewById(R.id.mega);
            copy = (Button) itemView.findViewById(R.id.copy);
        }
    }

}
