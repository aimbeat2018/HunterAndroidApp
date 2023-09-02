package ott.hunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ott.hunter.utils.ItemAnimation;


public class LangAdapter extends RecyclerView.Adapter<LangAdapter.ViewHolder> {

    private Context context;
    private List<LangModels> langModels;
    private String type;
    private String layout;
    private int c;

    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;

    public LangAdapter(Context context, List<LangModels> langModels, String type, String layout) {
        this.context = context;
        this.langModels= langModels;
        this.type = type;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (layout.equals("home")) {
            //  v = LayoutInflater.from(context).inflate(R.layout.layout_genre_item, parent,
           // v = LayoutInflater.from(context).inflate(R.layout.generw_item_layout, parent,
            v = LayoutInflater.from(context).inflate(R.layout.lang_item_layout, parent,
                    false);
        } else {
            v = LayoutInflater.from(context).inflate(R.layout.layout_genre_item_2, parent,
                    false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final LangModels langModel = langModels.get(position);

     /*
        if (langModel != null) {
            holder.cardView.requestFocus();
            holder.nameTv.setText(langModel.getTitle());
            Picasso.get()
                    .load(langModel.getImageUrl())
                    .centerCrop()
                    .fit()
                    .placeholder(R.drawable.poster_placeholder)
                    .into(holder.icon);

            holder.cardView.setBackgroundResource(getColor());



            holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ItemMovieActivity.class);
                  //  intent.putExtra("id",commonModel.getId());
                  //  intent.putExtra("title",commonModel.getTitle());
                    intent.putExtra("type",type);
                    context.startActivity(intent);

                }
            });

        }
*/
        setAnimation(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return langModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        ImageView icon;
        CardView cardView;
        LinearLayout itemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.genre_name_tv);
            icon = itemView.findViewById(R.id.icon);
            cardView = itemView.findViewById(R.id.card_view);
            itemLayout = itemView.findViewById(R.id.item_layout);

        }
    }






    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }

        });



        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}

