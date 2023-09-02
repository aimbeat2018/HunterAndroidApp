package ott.hunter.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ott.hunter.GoldDetailsActivity;
import ott.hunter.LoginActivity;
import ott.hunter.R;
import ott.hunter.models.CommonModels;
import ott.hunter.utils.ItemAnimation;
import ott.hunter.utils.PreferenceUtils;

public class CommonGoldAdapter extends RecyclerView.Adapter<CommonGoldAdapter.OriginalViewHolder> {

    private List<CommonModels> items = new ArrayList<>();
    private Context ctx;

    private int lastPosition = -1;
    private boolean on_attach = true;
    private int animation_type = 2;


    public CommonGoldAdapter(Context context, List<CommonModels> items) {
        this.items = items;
        ctx = context;
    }


    @Override
    public CommonGoldAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        CommonGoldAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gold_image_albums, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(CommonGoldAdapter.OriginalViewHolder holder, final int position) {
        final CommonModels obj = items.get(position);

        holder.qualityTv.setText("Rs.");
        holder.releaseDateTv.setText(obj.getVideo_plan());
//        holder.name.setText(obj.getTitle());

        if (obj.getIs_gold_paid().equals("1")) {
            Glide.with(ctx).load(R.drawable.ic_baseline_lock_open_24).into(holder.img_lock);
            holder.img_lock.setColorFilter(ContextCompat.getColor(ctx, R.color.green_500), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            Glide.with(ctx).load(R.drawable.ic_baseline_lock_24).into(holder.img_lock);
            holder.img_lock.setColorFilter(ContextCompat.getColor(ctx, R.color.red_600), android.graphics.PorterDuff.Mode.SRC_IN);
        }


        Picasso.get().load(obj.getImageUrl()).placeholder(R.drawable.poster_placeholder).into(holder.image);

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(obj.getIs_gold_paid().equals("1")) {
                    if (PreferenceUtils.isMandatoryLogin(ctx)) {
                        if (PreferenceUtils.isLoggedIn(ctx)) {
                            goToDetailsActivity(obj);
                        } else {
                            ctx.startActivity(new Intent(ctx, LoginActivity.class));
                        }
                    } else {
                        goToDetailsActivity(obj);
                    }
                }else{
                    Intent intent = new Intent(ctx, GoldRazorPayActivity.class);
                    intent.putExtra("videoId", obj.getId());
                    intent.putExtra("videoAmount", obj.getVideo_plan());
                    ctx.startActivity(intent);
                }*/
                if (PreferenceUtils.isMandatoryLogin(ctx)) {
                    if (PreferenceUtils.isLoggedIn(ctx)) {
                        Intent intent = new Intent(ctx, GoldDetailsActivity.class);
                        intent.putExtra("vType", obj.getVideoType());
                        intent.putExtra("id", obj.getId());
                        intent.putExtra("is_gold", obj.getIs_gold_paid());
                        intent.putExtra("amt", obj.getVideo_plan());

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        ctx.startActivity(intent);
                    } else {
                        ctx.startActivity(new Intent(ctx, LoginActivity.class));
                    }
                } else {
                    Intent intent = new Intent(ctx, GoldDetailsActivity.class);
                    intent.putExtra("vType", obj.getVideoType());
                    intent.putExtra("id", obj.getId());
                    intent.putExtra("is_gold", obj.getIs_gold_paid());
                    intent.putExtra("amt", obj.getVideo_plan());

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ctx.startActivity(intent);
                }
            }
        });

        setAnimation(holder.itemView, position);


    }



    private void goToDetailsActivity(CommonModels obj) {
        Intent intent = new Intent(ctx, GoldDetailsActivity.class);
        intent.putExtra("vType", obj.getVideoType());
        intent.putExtra("id", obj.getId());
        intent.putExtra("is_gold", obj.getIs_gold_paid());

//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ctx.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public ImageView image,img_lock;
        public TextView name, qualityTv, releaseDateTv;
        public MaterialRippleLayout lyt_parent;

        public View view;

        public CardView cardView;

        public OriginalViewHolder(View v) {
            super(v);
            view = v;
            image = v.findViewById(R.id.image);
            img_lock = v.findViewById(R.id.img_lock);
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