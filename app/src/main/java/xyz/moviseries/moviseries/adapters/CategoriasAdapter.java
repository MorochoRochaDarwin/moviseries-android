package xyz.moviseries.moviseries.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import xyz.moviseries.moviseries.R;
import xyz.moviseries.moviseries.models.Category;

/**
 * Created by DARWIN on 12/5/2017.
 */

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.CViewHolder> {

    private Context context;
    private ArrayList<Category> categories;

    private String letraAnt = "";
    private int color, color1, color2;

    public CategoriasAdapter(Context context, ArrayList<Category> categories) {
        this.context = context;
        this.categories = categories;
        color1 = Color.parseColor("#0453d2");
        color2 = Color.parseColor("#de054e");
        color = color1;
    }

    @Override
    public CViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CViewHolder holder, int position) {
        final Category category = categories.get(position);
        holder.cateory.setText(category.getCategory_name());
        String letter = String.valueOf(category.getCategory_name().charAt(0));
        holder.letra.setText(letter);
        holder.letra2.setText(letter);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCategoryClickListener.onCategoryClick(category);
            }
        });

        if (letter.equals(letraAnt)) {
            holder.letra.setBackgroundColor(color);
            holder.letra2.setBackgroundColor(color);
        } else {
            if (color == color1) {
                color = color2;
            } else {
                color = color1;
            }
            holder.letra.setBackgroundColor(color);
            holder.letra2.setBackgroundColor(color);
        }

        letraAnt = letter;

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    class CViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        TextView cateory, letra, letra2;

        public CViewHolder(View itemView) {
            super(itemView);
            item = (LinearLayout) itemView.findViewById(R.id.item);
            cateory = (TextView) itemView.findViewById(R.id.textView);
            letra = (TextView) itemView.findViewById(R.id.textViewletra);
            letra2 = (TextView) itemView.findViewById(R.id.textViewletra2);


        }
    }


    public interface OnCategoryClickListener {
        /**
         * cuando se da click en una categoria del nav
         *
         * @param category
         */
        void onCategoryClick(Category category);
    }


    private OnCategoryClickListener onCategoryClickListener;

    public void setOnCategoryClickListener(OnCategoryClickListener onCategoryClickListener) {
        this.onCategoryClickListener = onCategoryClickListener;
    }
}
