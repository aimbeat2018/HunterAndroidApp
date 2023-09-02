package ott.hunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.ArrayList;
import java.util.List;

import ott.hunter.models.single_details.Genre;
import ott.hunter.utils.ItemAnimation;


public class SearchGridAdapter extends RecyclerView.Adapter<SearchGridAdapter.OriginalViewHolder> {

    private List<Genre> items = new ArrayList<>();
    private Context ctx;


    //        int selectedIndex = 0;
    public static int selectedIndex = -1;
    public static int return_size = 9;


    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;
    public static int selected_id = 0;

    public SearchGridAdapter(Context context, List<Genre> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public SearchGridAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        SearchGridAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_grid_albums, parent, false);
        vh = new SearchGridAdapter.OriginalViewHolder(v);
        return vh;

    }


    @Override
    public void onBindViewHolder(SearchGridAdapter.OriginalViewHolder holder, final int position) {
        final Genre obj = items.get(position);

        holder.name.setText(obj.getName());


        //  Picasso.get().load(obj.getImageUrl()).placeholder(R.drawable.poster_placeholder).into(holder.image);

         /*   holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PreferenceUtils.isMandatoryLogin(ctx)){
                        if (PreferenceUtils.isLoggedIn(ctx)){
                            goToDetailsActivity(obj);
                        }else {
                            ctx.startActivity(new Intent(ctx, LoginActivity.class));
                        }
                    }else {
                        goToDetailsActivity(obj);
                    }
                }
            });
*/


        setAnimation(holder.itemView, position);


        if (selectedIndex == position) {

            holder.name.setTextColor(ctx.getResources().getColor(R.color.red));
          //  selected_id = Integer.parseInt(obj.getGenreId());
            SearchActivity.selectedGenreId = Integer.parseInt(obj.getGenreId());

            SearchActivity.genreSpinner.setText("All genres");

        } else {
            holder.name.setTextColor(ctx.getResources().getColor(R.color.white));
        }


        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedIndex = position;


                notifyDataSetChanged();


//                rec_episod.setLayoutManager(new LinearLayoutManager(context,
//                        RecyclerView.HORIZONTAL, false));
//                EpisodeAdapter episodeAdapter = new EpisodeAdapter(castCrew, context,
//                        listServer.get(position).getListEpi());
//                rec_episod.setAdapter(episodeAdapter);

            }
        });


    }


    @Override
    public int getItemCount() {
//            if(items.size()<=9){


        try {
            if (items.size() <= return_size) {
                return items.size();

            } else {
                return return_size;
            }

        } catch (NullPointerException e) {


        }

        return 0;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView name, qualityTv, releaseDateTv;
        public MaterialRippleLayout lyt_parent;

        public View view;

        public CardView cardView;

        public OriginalViewHolder(View v) {
            super(v);
            view = v;
            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);


            lyt_parent = v.findViewById(R.id.lyt_parent);
            qualityTv = v.findViewById(R.id.quality_tv);
            releaseDateTv = v.findViewById(R.id.release_date_tv);
            cardView = v.findViewById(R.id.top_layout);

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