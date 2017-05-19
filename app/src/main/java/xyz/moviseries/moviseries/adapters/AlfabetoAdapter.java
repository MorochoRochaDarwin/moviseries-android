package xyz.moviseries.moviseries.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xyz.moviseries.moviseries.R;

/**
 * Created by DARWIN on 18/5/2017.
 */

public class AlfabetoAdapter extends RecyclerView.Adapter<AlfabetoAdapter.AViewHolder> {

    private Context context;
    private String[] alfabeto;

    private boolean select;
    private int pos;
    private String lastLetra = "";

    public boolean isSelect() {
        return select;
    }

    public AlfabetoAdapter(Context context) {
        this.context = context;
        alfabeto = context.getResources().getStringArray(R.array.alfabeto);
    }

    @Override
    public AViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.intem_letra, parent, false);
        return new AViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AViewHolder holder, final int position) {
        final String letra = alfabeto[position];
        holder.letra.setText(letra);


        if (select) {
            if (this.pos == position) {
                holder.letra.setBackgroundColor(Color.parseColor("#de054e"));

            } else {
                holder.letra.setBackgroundColor(Color.parseColor("#00FFFFFF"));
            }

        } else {
            holder.letra.setBackgroundColor(Color.parseColor("#00FFFFFF"));
        }
        holder.letra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pos == position && select) {
                    select = false;
                } else {
                    select = true;
                }


                pos = position;
                lastLetra = letra;
                updateSelect();
                onClickLetraListener.onClickLetra(letra);


            }
        });
    }

    private void updateSelect() {
        notifyItemRangeChanged(0, alfabeto.length);
    }

    @Override
    public int getItemCount() {
        return alfabeto.length;
    }

    class AViewHolder extends RecyclerView.ViewHolder {
        TextView letra;

        public AViewHolder(View itemView) {
            super(itemView);
            letra = (TextView) itemView.findViewById(R.id.letra);
        }
    }


    public interface OnClickLetraListener {
        void onClickLetra(String letra);
    }


    private OnClickLetraListener onClickLetraListener;

    public void setOnClickLetraListener(OnClickLetraListener onClickLetraListener) {
        this.onClickLetraListener = onClickLetraListener;
    }
}
