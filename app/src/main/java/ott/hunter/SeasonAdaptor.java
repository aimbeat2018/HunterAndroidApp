package ott.hunter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ott.hunter.adapters.EpisodeAdapter;
//import ott.hunter.adapters.EpisodeAdapterKo;
import ott.hunter.models.CommonModels;
import ott.hunter.utils.ItemAnimation;

public class SeasonAdaptor extends RecyclerView.Adapter<SeasonAdaptor.ViewHolder> {

    private Activity context;
    private List<String> seasonData;
    int selectedIndex = 0;

    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;
    List<CommonModels> listServer;
    RecyclerView rec_episod;

    public SeasonAdaptor(Activity context, List<String> seasonData, List<CommonModels> listServer, RecyclerView rec_episod) {
        this.context = context;
        this.seasonData = seasonData;
        this.listServer = listServer;
        this.rec_episod = rec_episod;
    }


    @NonNull
    @Override
    public SeasonAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.layout_season_title_item, parent,
                false);
        return new SeasonAdaptor.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonAdaptor.ViewHolder holder, int position) {

        String castCrew = seasonData.get(position);


        if (castCrew != null) {
            holder.castNameTv.setText(castCrew);

        }
        setAnimation(holder.itemView, position);


        if (selectedIndex == position) {
            holder.castNameTv.setTextColor(context.getResources().getColor(R.color.red));

            rec_episod.setLayoutManager(new LinearLayoutManager(context,
                    RecyclerView.HORIZONTAL, false));
            EpisodeAdapter episodeAdapter = new EpisodeAdapter(castCrew, context, listServer.get(position).getListEpi());
//            EpisodeAdapterKo episodeAdapter = new EpisodeAdapterKo(castCrew, context, listServer.get(position).getListEpi());
            rec_episod.setAdapter(episodeAdapter);

//            episodeAdapter.setOnEmbedItemClickListener((EpisodeAdapterKo.OnTVSeriesEpisodeItemClickListener) context);
            episodeAdapter.setOnEmbedItemClickListener((EpisodeAdapter.OnTVSeriesEpisodeItemClickListener) context);


        } else {
            holder.castNameTv.setTextColor(context.getResources().getColor(R.color.white));
        }
/*
        EpisodeAdapter episodeAdapter = new EpisodeAdapter(context,
                listServer.get(position).getListEpi());
        holder.rvserverlist.setLayoutManager(new LinearLayoutManager(context,
                RecyclerView.HORIZONTAL, false));

        holder.rvserverlist.setAdapter(episodeAdapter);
        holder.rvserverlist.setNestedScrollingEnabled(true);
        holder.rvserverlist.setHasFixedSize(true);*/


        holder.castNameTv.setOnClickListener(new View.OnClickListener() {
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


//        holder.rvserverlist.setNestedScrollingEnabled(true);

        //  episodeAdapter.setOnEmbedItemClickListener();


    }


    @Override
    public int getItemCount() {
        return seasonData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView castIv;
        TextView castNameTv;
        LinearLayout castCrewLayout;
        public RecyclerView rvserverlist;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // castIv          = itemView.findViewById(R.id.cast_iv);
            castNameTv = itemView.findViewById(R.id.crew_name_tv);
            //  castCrewLayout = itemView.findViewById(R.id.cast_crew_layout);
            rvserverlist = itemView.findViewById(R.id.rv_server_list);


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
